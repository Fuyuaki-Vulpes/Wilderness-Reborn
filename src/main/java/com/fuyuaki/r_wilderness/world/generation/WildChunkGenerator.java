package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.mixin.accessor.ChunkMapAccessor;
import com.fuyuaki.r_wilderness.world.generation.carvers.WildCarverContext;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.hydrology.RAquifer;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.fuyuaki.r_wilderness.world.generation.terrain.WRSurfaceSystemUtil;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomePlacement;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.ChunkAccessModifier;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
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


    private TerrainParameters parameters;

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
        ((ChunkAccessModifier)chunk).fillBiomesReborn(source,randomState);
    }


    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {
        if (!SharedConstants.DEBUG_DISABLE_CARVERS) {
            BiomeManager biomemanager = biomeManager.withDifferentSource(
                    (x, y, z) -> this.biomeSource.getNoiseBiome(x, y, z, random.sampler())
            );
            WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            int i = 8;
            ChunkPos chunkpos = chunk.getPos();
            WRNoiseChunk noisechunk = this.createWRNoiseChunk(chunk, structureManager, Blender.of(level), random);
            Aquifer aquifer = new RAquifer(noisechunk,chunkpos,random.aquiferRandom(), WildernessConstants.WORLD_BOTTOM,WildernessConstants.WORLD_HEIGHT,this.globalFluidPicker.get());
            CarvingContext carvingcontext = new WildCarverContext(
                    this, level.registryAccess(), chunk.getHeightAccessorForGeneration(), noisechunk, random, this.settings.surfaceRule()
            );
            CarvingMask carvingmask = ((ProtoChunk)chunk).getOrCreateCarvingMask();

            for (int j = -i; j <= i; j++) {
                for (int k = -i; k <= i; k++) {
                    ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
                    ChunkAccess chunkaccess = level.getChunk(chunkpos1.x, chunkpos1.z);
                    BiomeGenerationSettings biomegenerationsettings = chunkaccess.carverBiome(
                            () -> this.getBiomeGenerationSettings(
                                    this.biomeSource
                                            .getNoiseBiome(
                                                    chunkpos1.getMinBlockX(), 0, chunkpos1.getMinBlockZ(), random.sampler()
                                            )
                            )
                    );
                    Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers();
                    int l = 0;

                    for (Holder<ConfiguredWorldCarver<?>> holder : iterable) {
                        ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
                        worldgenrandom.setLargeFeatureSeed(seed + l, chunkpos1.x, chunkpos1.z);
                        if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                            configuredworldcarver.carve(carvingcontext, chunk, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, carvingmask);
                        }

                        l++;
                    }
                }
            }
        }

    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

        WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, level);
        this.buildSurface(
                chunk,
                worldgenerationcontext,
                random,
                structureManager,
                level.getBiomeManager(),
                level.registryAccess().lookupOrThrow(Registries.BIOME),
                Blender.of(level)
        );

    }

    public void buildSurface(
            ChunkAccess chunk,
            WorldGenerationContext context,
            RandomState random,
            StructureManager structureManager,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            Blender blender
    ) {
        WRSurfaceSystemUtil
                .buildSurface(
                        random,
                        biomeManager,
                        biomes,
                        context,
                        chunk,
                        createWRNoiseChunk(chunk, structureManager, blender, random),
                        this.settings.surfaceRule()
                );
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
                this.parameters,
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
    public TerrainParameters terrainParameters() {
        return this.parameters;
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


        if (parameters != null) {
            final WildChunkGenerator copy = copy();

            ((ChunkMapBridge) chunkMap).updateGenerator(copy);
            copy.initRandomState(chunkMap, level);
            return;
        }
        final Seed seed = Seed.of(level.getSeed());

        this.seed = seed;

        this.parameters = new TerrainParameters(seed, this.settings);
        if (this.biomeSource instanceof RebornBiomeSource) {
            List<RebornBiomePlacement> knownBiomes = level.registryAccess().lookupOrThrow(WildRegistries.REBORN_BIOME_PLACEMENT_KEY).stream().toList();

            ((RebornBiomeSource)this.biomeSource).setParameters(parameters);
            ((RebornBiomeSource)this.biomeSource).setKnownBiomes(knownBiomes);

        }

        // Update the cached chunk generator extension on the RandomState
        // This is done here when we initialize this chunk generator, and have ensured we are unique to this state and chunk map
        // We do this to be able to access the chunk generator through the random state later, i.e. in structure generation
        ((RandomStateExtension) (Object) ((ChunkMapAccessor) chunkMap).accessor$getRandomState()).wildernessReborn$setChunkGeneratorExtension(this);
    }

    @Override
    public void initBiomeSource(ServerLevel level) {
        List<RebornBiomePlacement> knownBiomes = level.registryAccess().lookupOrThrow(WildRegistries.REBORN_BIOME_PLACEMENT_KEY).stream().toList();
        this.featuresPerStep = Lazy.of(
                () -> FeatureSorter.buildFeaturesPerStep(List.copyOf(knownBiomes.stream().map(RebornBiomePlacement::biome).toList()), biomeHolder -> generationSettingsGetter.apply(biomeHolder).features(), true)
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
        TerrainParameters.Sampled sampled = this.parameters.samplerAtCached(pos.getX(), pos.getZ());

        String continentalness = decimalformat.format(sampled.continentalness());
        String terrainOffset = decimalformat.format(sampled.terrainOffset());
        String erosion = decimalformat.format(sampled.erosion());
        String tectonicActivity = decimalformat.format((1 - Math.abs(sampled.tectonicActivity())));
        String mountainCore = decimalformat.format(sampled.mountainsCore());
        String mountain = decimalformat.format(sampled.mountains());
        String mountainDetail = decimalformat.format(sampled.mountainDetails());
        String mountainNoise = decimalformat.format(sampled.mountainNoise());
        String plateau = decimalformat.format(Math.pow(Math.clamp(sampled.highlandsMap(), 0, 1), 5));
        String hill = decimalformat.format(sampled.hills());
        String terrainTypeA = decimalformat.format(sampled.terrainTypeA());
        String terrainTypeB = decimalformat.format(sampled.terrainTypeB());
        String postTerrainA = decimalformat.format(sampled.terrainType().a());
        String postTerrainB = decimalformat.format(sampled.terrainType().b());

        info.add("C: " + continentalness + " E: " + erosion + " TA: " + tectonicActivity);
        info.add("Mountain: M: " + mountain + " C: " + mountainCore + " D: " + mountainDetail + " N: " + mountainNoise);
        info.add("H: " + hill + " P: " + plateau + " TO: " + terrainOffset);
        info.add("Type: A: " + terrainTypeA + " B: " + terrainTypeB + " Post: A: " + postTerrainA + " B: " + postTerrainB);
        info.add("X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ());


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
                    this.parameters,
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
