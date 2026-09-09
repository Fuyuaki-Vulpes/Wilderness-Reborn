package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.mixin.accessor.ChunkMapAccessor;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import com.fuyuaki.r_wilderness.world.generation.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import com.fuyuaki.r_wilderness.world.level.levelgen.carver.WildCarverContext;
import com.fuyuaki.r_wilderness.world.level.levelgen.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.level.levelgen.feature.RAquifer;
import com.fuyuaki.r_wilderness.world.level.levelgen.terrain.WRSurfaceSystemUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class WildChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension {
    public static final MapCodec<WildChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            p_255585_ -> p_255585_.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(chunkGenerator -> chunkGenerator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("vanilla_settings").forGetter(chunkGenerator -> chunkGenerator.noiseSettings),
                            WildGeneratorSettings.CODEC.fieldOf("generator_settings").forGetter(chunkGenerator -> chunkGenerator.settings)
                    )
                    .apply(p_255585_, p_255585_.stable(WildChunkGenerator::new))
    );
    private final NoiseBasedChunkGenerator vanillaChunkGenerator;

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private final Holder<NoiseGeneratorSettings> noiseSettings;

    public static final int DECORATION_STEPS = GenerationStep.Decoration.values().length;
    public static final int SEA_LEVEL_Y = WildernessConstants.SEA_LEVEL;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    private Seed seed;
    private WildGeneratorSettings settings;
    private boolean complete = false;

    public WildChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseSettings, WildGeneratorSettings generatorSettings) {
        super(biomeSource);
        this.noiseSettings = noiseSettings;
        this.settings = generatorSettings;
        this.vanillaChunkGenerator = new NoiseBasedChunkGenerator(biomeSource, this.noiseSettings);
        this.globalFluidPicker = Suppliers.memoize(WildChunkGenerator::createFluidPicker);


    }
    private static Aquifer.FluidPicker createFluidPicker() {
        Aquifer.FluidStatus lavaAquifer = new Aquifer.FluidStatus(WildernessConstants.CAVES_BOTTOM + 16, Blocks.LAVA.defaultBlockState());
        int i = WildernessConstants.SEA_LEVEL;
        Aquifer.FluidStatus oceanAquifer = new Aquifer.FluidStatus(i, Blocks.WATER.defaultBlockState());
        Aquifer.FluidStatus disabledAquifer = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (x, y, z) -> {
            if (SharedConstants.DEBUG_DISABLE_FLUID_GENERATION) {
                return disabledAquifer;
            } else {
                return y < WildernessConstants.CAVES_BOTTOM + 32 ? lavaAquifer : oceanAquifer;
            }
        };
    }
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            this.doCreateBiomes(randomState, blender, structureManager, chunk);
            return chunk;
        }, Util.backgroundExecutor().forName("init_biomes"));
    }

    private void doCreateBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        RebornBiomeSource source = (RebornBiomeSource) this.biomeSource;
        chunk.fillBiomesFromNoise(source,randomState.sampler());
    }


    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {

        if (!SharedConstants.DEBUG_DISABLE_CARVERS && !WildernessConstants.DISABLE_CARVERS) {
            BiomeManager correctBiomeManager = biomeManager.withDifferentSource(
                    (quartX, quartY, quartZ) -> this.biomeSource.getNoiseBiome(quartX, quartY, quartZ, randomState.sampler())
            );
            WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            int range = 8;
            ChunkPos pos = chunk.getPos();
            WRNoiseChunk noiseChunk = this.createWRNoiseChunk(chunk, structureManager, Blender.of(region), randomState);
            Aquifer aquifer = new RAquifer(noiseChunk,pos,randomState.aquiferRandom(), WildernessConstants.WORLD_BOTTOM,WildernessConstants.WORLD_HEIGHT,this.globalFluidPicker.get());
            CarvingContext context = new WildCarverContext(
                    this, region.registryAccess(), chunk.getHeightAccessorForGeneration(), noiseChunk, randomState, this.settings.surfaceRule()
            );
            CarvingMask mask = ((ProtoChunk)chunk).getOrCreateCarvingMask();

            for (int dx = -8; dx <= 8; dx++) {
                for (int dz = -8; dz <= 8; dz++) {
                    ChunkPos sourcePos = new ChunkPos(pos.x() + dx, pos.z() + dz);
                    ChunkAccess carverCenterChunk = region.getChunk(sourcePos.x(), sourcePos.z());
                    BiomeGenerationSettings sourceBiomeGenerationSettings = carverCenterChunk.carverBiome(
                            () -> this.getBiomeGenerationSettings(
                                    this.biomeSource
                                            .getNoiseBiome(
                                                    QuartPos.fromBlock(sourcePos.getMinBlockX()), 0, QuartPos.fromBlock(sourcePos.getMinBlockZ()), randomState.sampler()
                                            )
                            )
                    );
                    Iterable<Holder<ConfiguredWorldCarver<?>>> carvers = sourceBiomeGenerationSettings.getCarvers();
                    int index = 0;

                    for (Holder<ConfiguredWorldCarver<?>> carverHolder : carvers) {
                        ConfiguredWorldCarver<?> carver = carverHolder.value();
                        random.setLargeFeatureSeed(seed + index, sourcePos.x(), sourcePos.z());
                        if (carver.isStartChunk(random)) {
                            carver.carve(context, chunk, correctBiomeManager::getBiome, random, aquifer, sourcePos, mask);
                        }

                        index++;
                    }
                }
            }
        }

    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess protoChunk) {
        WorldGenerationContext context = new WorldGenerationContext(this, region);
        Set<Holder<Biome>> possibleBiomes = collectPossibleBiomes(region, 1);
        this.buildSurface(protoChunk, context, randomState, structureManager, region.getBiomeManager(), Blender.of(region), possibleBiomes);

    }
    private static Set<Holder<Biome>> collectPossibleBiomes(WorldGenRegion region, int chunkRadius) {
        Set<Holder<Biome>> chunkBiomes = new ReferenceOpenHashSet<>();
        ChunkPos center = region.getCenter();

        for (int z = center.z() - chunkRadius; z <= center.z() + chunkRadius; z++) {
            for (int x = center.x() - chunkRadius; x <= center.x() + chunkRadius; x++) {
                region.getChunk(x, z).collectBiomesInPalette(chunkBiomes);
            }
        }

        return chunkBiomes;
    }

    @VisibleForTesting
    public void buildSurface(
            ChunkAccess protoChunk,
            WorldGenerationContext context,
            RandomState randomState,
            StructureManager structureManager,
            BiomeManager biomeManager,
            Blender blender,
            @Nullable Set<Holder<Biome>> possibleBiomes
    ) {
        WRNoiseChunk noiseChunk = createWRNoiseChunk(protoChunk, structureManager, blender, randomState);
        WRSurfaceSystemUtil.buildSurface(randomState, biomeManager, context, protoChunk, noiseChunk, settings.surfaceRule(), possibleBiomes);
    }


    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        ChunkPos chunkpos = level.getCenter();
        Holder<Biome> holder = level.getBiome(chunkpos.getWorldPosition().atY(level.getMaxY()));
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        worldgenrandom.setDecorationSeed(level.getSeed(), chunkpos.getMinBlockX(), chunkpos.getMinBlockZ());
        NaturalSpawner.spawnMobsForChunkGeneration(level, holder, chunkpos, worldgenrandom);
    }

    @Override
    public int getGenDepth() {
        return this.settings.noiseSettings().height();
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        NoiseSettings noisesettings = this.settings.noiseSettings().clampToHeightAccessor(chunk.getHeightAccessorForGeneration());


        int minY = noisesettings.minY();
        int minCellY = Mth.floorDiv(minY, noisesettings.getCellHeight());
        int maxCellY = Mth.floorDiv(noisesettings.height(), noisesettings.getCellHeight());
        final WRNoiseChunk noiseChunk = createWRNoiseChunk(chunk, structureManager, blender, randomState);

        return CompletableFuture.supplyAsync(() -> {
            int cellRange = chunk.getSectionIndex(maxCellY * noisesettings.getCellHeight() - 1 + minY);
            int minYCell = chunk.getSectionIndex(minY);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for (int index = cellRange; index >= minYCell; index--) {
                LevelChunkSection levelchunksection = chunk.getSection(index);
                levelchunksection.acquire();
                set.add(levelchunksection);
            }


            ChunkAccess chunkaccess;

            chunkaccess = noiseChunk.buildNoise(chunk, minCellY, this.settings.defaultBlock());
            for (LevelChunkSection levelchunksection1 : set) {
                levelchunksection1.release();

            }
            return chunkaccess;
        }, Util.backgroundExecutor().forName("wgen_fill_noise"));
    }

    private WRNoiseChunk createWRNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random) {
        return WRNoiseChunk.forChunk(
                chunk,
                random,
                Beardifier.forStructuresInChunk(structureManager, chunk.getPos()),
                this.settings,
                blender,
                getSeaLevel(),
                (RebornBiomeSource) this.biomeSource,
                this.globalFluidPicker.get()
        );
    }

    @Override
    public int getSeaLevel() {
        return SEA_LEVEL_Y;
    }

    @Override
    public int getMinY() {
        return WildernessConstants.WORLD_BOTTOM;

    }


    @Override
    public WildGeneratorSettings settings() {
        return this.settings;
    }

    @Override
    public synchronized void prepare(ChunkPos pos) {

    }

    @Override
    public void applySettings(UnaryOperator<WildGeneratorSettings> settings) {
        this.settings = settings.apply(this.settings);

    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initRandomState(ChunkMap chunkMap, ServerLevel level) {


        if (complete) {
            final WildChunkGenerator copy = copy();

            ((ChunkMapBridge) chunkMap).updateGenerator(copy);
            copy.initRandomState(chunkMap, level);
            return;
        }
        final Seed seed = Seed.of(level.getSeed());

        this.seed = seed;

        if (this.biomeSource instanceof RebornBiomeSource) {
            List<BiomePlacementData> knownBiomes = level.registryAccess().lookupOrThrow(WildRegistries.REBORN_BIOME_PLACEMENT_KEY).stream().toList();

            ((RebornBiomeSource)this.biomeSource).setMemory(RegionMemory.getInstance(level));
            ((RebornBiomeSource)this.biomeSource).setKnownBiomes(knownBiomes);

        }

        // Update the cached chunk generator extension on the RandomState
        // This is done here when we initialize this chunk generator, and have ensured we are unique to this state and chunk map
        // We do this to be able to access the chunk generator through the random state later, i.e. in structure generation

        ((RandomStateExtension) (Object) ((ChunkMapAccessor) chunkMap).accessor$getRandomState()).wildernessReborn$setChunkGeneratorExtension(this);
        this.complete = true;
    }

    @Override
    public void initBiomeSource(ServerLevel level) {
        List<BiomePlacementData> knownBiomes = level.registryAccess().lookupOrThrow(WildRegistries.REBORN_BIOME_PLACEMENT_KEY).stream().toList();
        this.featuresPerStep = Lazy.of(
                () -> FeatureSorter.buildFeaturesPerStep(List.copyOf(knownBiomes.stream().map(BiomePlacementData::biome).toList()), biomeHolder -> generationSettingsGetter.apply(biomeHolder).features(), true)
        );
    }


    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return iterateNoiseColumn(level, random, x, z, null).orElse(level.getMinY());
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        MutableObject<NoiseColumn> mutableobject = new MutableObject<>();
        this.iterateNoiseColumn(height, random, x, z, mutableobject);
        return mutableobject.getValue();
    }


    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        RegionMemory memory = RegionMemory.getInstance(Minecraft.getInstance().getSingleplayerServer().overworld());
        int x = pos.getX();
        int z = pos.getZ();
        WorldRegion region = memory.getRegionAt(x, z);
        RegionLayers layers = region.layers();

        String continentalness = decimalformat.format(layers.getContinentalness().getUpsampled(x,z,memory));
        String erosion = decimalformat.format(layers.getErodability().getUpsampled(x,z,memory));
        String plateau = decimalformat.format(layers.getPlateaus().getUpsampled(x,z,memory));
        String hill = decimalformat.format(layers.getHilliness().getUpsampled(x,z,memory));
        String tectonicStress = decimalformat.format(layers.getTectonicStress().getUpsampled(x,z,memory));
        String volcanicness = decimalformat.format(layers.getVolcanicness().getUpsampled(x,z,memory));
        String plateAge = decimalformat.format(layers.getAge().getUpsampled(x,z,memory));
        String plateSwell = decimalformat.format(layers.getSwelling().getUpsampled(x,z,memory));
        String plateWeight = decimalformat.format(layers.getPlateWeights().getUpsampled(x,z,memory));
        String plateDistToParent = decimalformat.format(layers.getDistToParent().getUpsampled(x,z,memory));
        String plateEdges = decimalformat.format(layers.getPlateEdges().getUpsampled(x,z,memory));
        String plateBuoyancy = decimalformat.format(layers.getBuoyancy().getUpsampled(x,z,memory));
        String rockiness = decimalformat.format(layers.getRockiness().getUpsampled(x,z,memory));
        String humidity = decimalformat.format(layers.getHumidity().getUpsampled(x,z,memory));
        String temperature = decimalformat.format(layers.getTemperature().getUpsampled(x,z,memory));
        String atmosPressure = decimalformat.format(layers.getAtmospherePressure().getUpsampled(x,z,memory));
        String plateElevOff = decimalformat.format(layers.getPlateElevationOffsets().getUpsampled(x,z,memory));
        String heightmap = decimalformat.format(layers.getHeightmapBasic().getUpsampled(x,z,memory));
        String rivers = decimalformat.format(layers.getRivers().getUpsampled(x,z,memory));
        String elevation = decimalformat.format(layers.getElevation().getUpsampled(x,z,memory));
        String magic = decimalformat.format(layers.getMagicalness().getUpsampled(x,z,memory));
        String vegetation = decimalformat.format(layers.getVegetation().getUpsampled(x,z,memory));

        info.add("Elevations: ");
        info.add("P Elev Off: " + plateElevOff + " Base Height: " + heightmap + " Final Elev: " + elevation);
        info.add("Terrain Exclusive: ");
        info.add("St: " + tectonicStress + " Age: " + plateAge + " Edg: " + plateEdges);
        info.add(" We: " + plateWeight + " Buoy: " + plateBuoyancy);
        info.add("Dist: " + plateDistToParent + " Sw: " + plateSwell);
        info.add("Hill: " + hill + " Plat: " + plateau);
        info.add("Rivr: " + rivers + " Atmos Pres: " + atmosPressure);
        info.add("All Generation: ");
        info.add("Temp: " + temperature + " Cont: " + continentalness + " Erod: " + erosion);
        info.add("Hum: " + humidity + " Veg: " + vegetation + " Rock: " + rockiness);
        info.add("Vol: " + volcanicness + " Mag: " + magic);
    }



    private WildChunkGenerator copy() {
        return new WildChunkGenerator(biomeSource, noiseSettings, settings);
    }

    protected OptionalInt iterateNoiseColumn(
            LevelHeightAccessor level,
            RandomState random,
            int x,
            int z,
            MutableObject<NoiseColumn> column
    ) {
        NoiseSettings noisesettings = this.settings.noiseSettings().clampToHeightAccessor(level);
        int cellHeight = noisesettings.getCellHeight();
        int heightDiv = Mth.floorDiv(noisesettings.height(), cellHeight);
        if (heightDiv <= 0) {
            return OptionalInt.empty();
        } else {
            BlockState[] ablockstate;
            int cellWidth = noisesettings.getCellWidth();
            int cellX = Math.floorDiv(x, cellWidth);
            int cellZ = Math.floorDiv(z, cellWidth);
            int firstCellX = cellX * cellWidth;
            int firstCellZ = cellZ * cellWidth;
            int cellXPos = Math.floorMod(x, cellWidth);
            int cellZPos = Math.floorMod(z, cellWidth);
            double cellXPercent = (double) cellXPos / cellWidth;
            double cellZPercent = (double) cellZPos / cellWidth;

            int minY = noisesettings.minY();
            int bottomCellY = Mth.floorDiv(minY, cellHeight);
            int cellYCount = Mth.floorDiv(noisesettings.height(), cellHeight);
            WRNoiseChunk noisechunk = new WRNoiseChunk(
                    1,
                    random,
                    firstCellX,
                    firstCellZ,
                    null,
                    noisesettings,
                    SEA_LEVEL_Y,
                    DensityFunctions.BeardifierMarker.INSTANCE,
                    Blender.empty(),
                    this.settings,
                    (RebornBiomeSource) biomeSource,
                    this.globalFluidPicker.get());

            if (column != null) {
                ablockstate = new BlockState[noisesettings.height()];
                column.setValue(new NoiseColumn(minY, ablockstate));
                for (int cellY = cellYCount - 1; cellY >= 0; cellY--) {
                    noisechunk.selectCellYZ(cellY, 0);

                    for (int cellBlock = cellHeight - 1; cellBlock >= 0; cellBlock--) {
                        int yLevel = (bottomCellY + cellY) * cellHeight + cellBlock;
                        double cellYPercent = (double) cellBlock / cellHeight;

                        noisechunk.updateForY(yLevel, cellYPercent);
                        noisechunk.updateForX(x, cellXPercent);
                        noisechunk.updateForZ(z, cellZPercent);

                        int yCellIndex = cellY * cellHeight + cellBlock;
                        BlockState blockstate = noisechunk.testBlockAt(x,cellBlock,z,settings.defaultBlock());
                        BlockState blockstate1 = blockstate == null ? this.settings.defaultBlock() : blockstate;
                        ablockstate[yCellIndex] = blockstate1;
                    }
                }
            }


            int height = noisechunk.getSurfaceY(x, z);
            return OptionalInt.of(height);
        }
    }

}
