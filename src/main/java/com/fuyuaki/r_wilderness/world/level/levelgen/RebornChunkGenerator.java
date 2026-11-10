package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.mixin.accessor.ChunkMapAccessor;
import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import com.fuyuaki.r_wilderness.world.generation.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.generation.features.plates.TectonicPlateNode;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RebornChunkGenerator extends ChunkGenerator implements ChunkGeneratorExtension {
    public static final MapCodec<RebornChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(RebornChunkGenerator::getBiomeSource),
                            RebornWorldSettings.HOLDER_CODEC.fieldOf("generator_settings").forGetter(RebornChunkGenerator::settings)
                    )
                    .apply(instance, instance.stable(RebornChunkGenerator::new))
    );

    private static final DecimalFormat DEBUG_MENU_FORMAT = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final DecimalFormat DEBUG_MENU_FORMAT_SHORT = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();


    private RegionMemory regionMemory;
    private final Holder<RebornWorldSettings> settings;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    private Seed seed;
    private boolean complete = false;

    public RebornChunkGenerator(BiomeSource biomeSource, Holder<RebornWorldSettings> settings) {
        super(biomeSource, biome -> biome.value().getGenerationSettings());
        this.settings = settings;
        this.globalFluidPicker = Suppliers.memoize(() -> createFluidPicker(settings.value()));
    }

    private static Aquifer.FluidPicker createFluidPicker(RebornWorldSettings settings) {
        Aquifer.FluidStatus lavaStatus = new Aquifer.FluidStatus(settings.lavaLevel(), Blocks.LAVA.defaultBlockState());
        int seaLevel = settings.seaLevel();
        Aquifer.FluidStatus seaStatus = new Aquifer.FluidStatus(seaLevel, settings.defaultFluid());
        Aquifer.FluidStatus emptyStatus = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (x, y, z) -> {
            if (WildernessConstants.DEBUG_DISABLE_FLUID_GENERATION) {
                return emptyStatus;
            } else {
                return y < Math.min(settings.lavaLevel(), seaLevel) ? lavaStatus : seaStatus;
            }
        };
    }

    public Holder<RebornWorldSettings> settings() {
        return settings;
    }


    private RebornNoiseChunk createNoiseChunk(
            ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState randomState, NoiseSettings noiseSettings
    ) {
        Beardifier beardifier = Beardifier.forStructuresInChunk(structureManager, chunk.getPos());
        return new RebornNoiseChunk(this.regionMemory,randomState, beardifier, this.settings.value(), this.globalFluidPicker.get(), blender, chunkVolume(chunk, noiseSettings));
    }

    private static DensityVolume chunkVolume(ChunkAccess chunk, NoiseSettings noiseSettings) {
        ChunkPos pos = chunk.getPos();
        return new DensityVolume(16, noiseSettings.height(), 16, pos.getMinBlockX(), noiseSettings.minY(), pos.getMinBlockZ());
    }

    @Override protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override public CompletableFuture<ChunkAccess> buildTerrain(ChunkAccess chunk, Blender blender, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, @Nullable WorldGenRegion carverBiomeRegion, Set<Holder<Biome>> possibleBiomes) {
        this.regionMemory.getRegionAt(chunk.getPos().getMiddleBlockPosition(0));
        WorldRegion region = null;
        while (!regionMemory.readyForChunkReading(chunk.getPos().getMiddleBlockPosition(0))){
            try{
                region = regionMemory.requestImmediately(chunk.getPos().getMiddleBlockPosition(0));
                Thread.sleep(10L);
            } catch (Exception e) {
                LogUtils.getLogger().warn("Sleep failed for chunk {}",chunk, e);
            }
        }
        if (region == null){
            region = regionMemory.requestImmediately(chunk.getPos().getMiddleBlockPosition(0));
        }

        WorldRegion region1 = region;

        NoiseSettings noiseSettings = this.settings.value().noiseSettings().clampToHeightAccessor(chunk.getHeightAccessorForGeneration());
        return noiseSettings.height() > 0 && !SharedConstants.debugVoidTerrain(chunk.getPos()) ? CompletableFuture.supplyAsync(() -> {
            ProfilerFiller profiler = Profiler.get();

            try (RebornNoiseChunk noiseChunk = this.createNoiseChunk(chunk, structureManager, blender, randomState, noiseSettings)) {
                DensityVolume volume = noiseChunk.volume();
                int topSectionIndex = chunk.getSectionIndex(volume.maxBlockY());
                int bottomSectionIndex = chunk.getSectionIndex(volume.minBlockY());
                Set<LevelChunkSection> sections = Sets.newHashSet();

                for (int sectionIndex = topSectionIndex; sectionIndex >= bottomSectionIndex; sectionIndex--) {
                    LevelChunkSection section = chunk.getSection(sectionIndex);
                    section.acquire();
                    sections.add(section);
                }

                try (Zone var39 = profiler.zone("doFill")) {
                    this.doFill(noiseChunk, chunk,region1);
                } finally {
                    for (LevelChunkSection section : sections) {
                        section.release();
                    }
                }

                MaterialRule materialRule = this.settings.value().materialRule().value();

                try (Zone var41 = profiler.zone("buildSurface")) {
                    this.buildSurface(region1, chunk, noiseChunk, randomState, biomeManager, possibleBiomes, materialRule);
                }

                try (Zone var42 = profiler.zone("generateCarvers")) {
                    this.generateCarvers(region1, chunk, blender, noiseChunk, randomState, biomeManager, carverBiomeRegion, materialRule);
                }

                return chunk;
            }
        }, Util.backgroundExecutor().forName("buildTerrain")) : CompletableFuture.completedFuture(chunk);
    }

    @Override public void addDebugScreenInfo(List<String> result, RandomState randomState, BlockPos feetPos, SamplerContext samplerContext) {
        RegionMemory memory = RegionMemory.getInstance(Minecraft.getInstance().getSingleplayerServer().overworld());
        int x = feetPos.getX();
        int z = feetPos.getZ();
        WorldRegion region = memory.getRegionAt(x, z);
        RegionLayers layers = region.layers();

        String continentalness = DEBUG_MENU_FORMAT.format(layers.getContinentalness().getUpsampled(x,z,memory));
        String erosion = DEBUG_MENU_FORMAT.format(layers.getErodability().getUpsampled(x,z,memory));
        String plateau = DEBUG_MENU_FORMAT.format(layers.getPlateaus().getUpsampled(x,z,memory));
        String hill = DEBUG_MENU_FORMAT.format(layers.getHilliness().getUpsampled(x,z,memory));
        String tectonicStress = DEBUG_MENU_FORMAT.format(layers.getTectonicStress().getUpsampled(x,z,memory));
        String volcanicness = DEBUG_MENU_FORMAT.format(layers.getVolcanicness().getUpsampled(x,z,memory));
        String plateAge = DEBUG_MENU_FORMAT.format(layers.getAge().getUpsampled(x,z,memory));
        String plateSwell = DEBUG_MENU_FORMAT.format(layers.getSwelling().getUpsampled(x,z,memory));
        String plateWeight = DEBUG_MENU_FORMAT.format(layers.getPlateWeights().getUpsampled(x,z,memory));
        String plateDistToParent = DEBUG_MENU_FORMAT.format(layers.getDistToParent().getUpsampled(x,z,memory));
        String plateEdges = DEBUG_MENU_FORMAT.format(layers.getPlateEdges().getUpsampled(x,z,memory));
        String plateBuoyancy = DEBUG_MENU_FORMAT.format(layers.getBuoyancy().getUpsampled(x,z,memory));
        String rockiness = DEBUG_MENU_FORMAT.format(layers.getRockiness().getUpsampled(x,z,memory));
        String humidity = DEBUG_MENU_FORMAT.format(layers.getHumidity().getUpsampled(x,z,memory));
        String temperature = DEBUG_MENU_FORMAT.format(layers.getTemperature().getUpsampled(x,z,memory));
        String atmosPressure = DEBUG_MENU_FORMAT.format(layers.getAtmospherePressure().getUpsampled(x,z,memory));
        String plateElevOff = DEBUG_MENU_FORMAT_SHORT.format(layers.getPlateElevationOffsets().getUpsampled(x,z,memory));
        String heightmap = DEBUG_MENU_FORMAT_SHORT.format(layers.getHeightmapBasic().getUpsampled(x,z,memory));
        String rivers = DEBUG_MENU_FORMAT.format(layers.getRivers().getUpsampled(x,z,memory));
        String elevation = DEBUG_MENU_FORMAT_SHORT.format(layers.getElevation().getUpsampled(x,z,memory));
        String magic = DEBUG_MENU_FORMAT.format(layers.getMagicalness().getUpsampled(x,z,memory));
        String vegetation = DEBUG_MENU_FORMAT.format(layers.getVegetation().getUpsampled(x,z,memory));


        result.add("EP: " + plateElevOff + " EB: " + heightmap + " EF: " + elevation);

        result.add("PST: " + tectonicStress + " PA: " + plateAge + " PE: " + plateEdges);
        result.add("PW: " + plateWeight + " PB: " + plateBuoyancy);
        result.add("PD: " + plateDistToParent + " PSW: " + plateSwell);

        result.add("GH: " + hill + " GP: " + plateau);
        result.add("GR: " + rivers + " GA: " + atmosPressure);

        result.add("GT: " + temperature + " GC: " + continentalness + " GE: " + erosion);
        result.add("GH: " + humidity + " GF: " + vegetation + " GR: " + rockiness);
        result.add("GV: " + volcanicness + " GM: " + magic);

        result.add("");

        //CURRENT PLATE
        TectonicPlateNode node = region.getNbtData().plates().closestNode(x,z);
        String parentX = DEBUG_MENU_FORMAT_SHORT.format(node.getParentX());
        String parentZ = DEBUG_MENU_FORMAT_SHORT.format(node.getParentZ());
        TectonicPlateNode.TerrainMaker maker = node.getTerrainMaker();
        String type = node.getType().getSerializedName();
        String age = DEBUG_MENU_FORMAT.format(maker.age());
        String weight = DEBUG_MENU_FORMAT.format(maker.weight());

        Vec2 velocity = node.getVelocity();
        String vX = DEBUG_MENU_FORMAT_SHORT.format(velocity.x);
        String vZ = DEBUG_MENU_FORMAT_SHORT.format(velocity.y);

        result.add("Current Plate:");
        result.add("X:" + parentX + " Z:" + parentZ);
        result.add("Type: " + type);
        result.add("Age: " + age + " Weight: " + weight);
        result.add("Movement: X: " + vX + " Z:" + vZ);
        result.add("Len: " + velocity.length() + " Dir: " + HorizontalDirection.fromVector(velocity).getSerializedName());
    }

    //GENERATORS


    @Override public void prepare(ChunkPos pos) {

    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initRandomState(ChunkMap chunkMap, ServerLevel level) {
        RegionMemory regionMemory = RegionMemory.getInstance(level);
        this.regionMemory = regionMemory;
        if (complete) {
            final RebornChunkGenerator copy = copy();

            ((ChunkMapBridge) chunkMap).updateGenerator(copy);
            copy.initRandomState(chunkMap, level);
            return;
        }
        final Seed seed = Seed.of(level.getSeed());

        this.seed = seed;
        RandomState randomState = ((ChunkMapAccessor) chunkMap).accessor$getRandomState();

        if (this.biomeSource instanceof RebornBiomeSource) {
            ((RebornBiomeSource) this.biomeSource).setMemory(RegionMemory.getInstance(level));

        }

        // Update the cached chunk generator extension on the RandomState
        // This is done here when we initialize this chunk generator, and have ensured we are unique to this state and chunk map
        // We do this to be able to access the chunk generator through the random state later, i.e. in structure generation

        ((RandomStateExtension) (Object) randomState).setChunkGeneratorExtension(this);
        this.complete = true;
    }

    @Override
    public void initBiomeSource(ServerLevel level) {
        List<BiomePlacementData> knownBiomes = ((RebornBiomeSource)this.biomeSource).getPlacementList();
        this.featuresPerStep = Lazy.of(
                () -> FeatureSorter.buildFeaturesPerStep(List.copyOf(knownBiomes.stream().map(BiomePlacementData::biome).toList()), biomeHolder -> generationSettingsGetter.apply(biomeHolder).features(), true)
        );
    }

    private RebornChunkGenerator copy() {
        return new RebornChunkGenerator(biomeSource, settings);
    }


    private void doFill(RebornNoiseChunk noiseChunk, ChunkAccess chunk, WorldRegion region) {
        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        Aquifer aquifer = noiseChunk.aquifer();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        DensityVolume volume = noiseChunk.volume();
        DensitySampler.Bound finalDensity = noiseChunk.cachingSamplers().get(this.settings.value().noiseRouter().finalDensity());

        try (ScopedDensityBuffer densityBuffer = finalDensity.sampleVolume(volume)) {
            for (int z = 0; z < volume.sizeZ(); z++) {
                int blockZ = volume.blockZ(z);

                for (int x = 0; x < volume.sizeX(); x++) {
                    int blockX = volume.blockX(x);
                    float yLevel = region.layers().getElevation().getUpsampled(blockX,blockZ,this.regionMemory);
                    for (int y = volume.sizeY() - 1; y >= 0; y--) {
                        int blockY = volume.blockY(y);
                        LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(blockY));
                        float densityPre = densityBuffer.get(volume.indexUnchecked(x, y, z));
                        float density = this.adaptDensity(densityPre, yLevel,blockY);
                        BlockState state = aquifer.computeSubstance(blockX, blockY, blockZ, density);
                        if (state == null) {
                            state = this.settings.value().defaultBlock();
                        }

                        state = this.debugPreliminarySurfaceLevel(noiseChunk, blockX, blockY, blockZ, state,yLevel);
                        if (state != AIR) {
                            section.setBlockState(x, SectionPos.sectionRelative(blockY), z, state, false);
                            oceanFloor.update(x, blockY, z, state);
                            worldSurface.update(x, blockY, z, state);
                            if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                                blockPos.set(blockX, blockY, blockZ);
                                chunk.markPosForPostProcessing(blockPos);
                            }
                        }
                    }
                }
            }
        }
    }

    private float adaptDensity(float densityPre, float yLevel, int y) {
        if (y > yLevel) {
            float l = Math.max((y - yLevel) / 32,0.0f);
            return -0.3f - Math.min(l,1.3f) ;
        }
        float l = 1 - Math.min((yLevel - y) / 12,1.0f);
        float d = densityPre + l;
        return d > 0.0f ? Math.max(d,0.3f) : d;
    }


    private void buildSurface(
            WorldRegion region,
            ChunkAccess protoChunk,
            RebornNoiseChunk noiseChunk,
            RandomState randomState,
            BiomeManager biomeManager,
            Set<Holder<Biome>> possibleBiomes,
            MaterialRule materialRule
    ) {
        if (!SharedConstants.debugVoidTerrain(protoChunk.getPos()) && !SharedConstants.DEBUG_DISABLE_SURFACE) {
            WorldGenerationContext context = new WorldGenerationContext(this, protoChunk.getHeightAccessorForGeneration());
            ((RandomStateExtension) (Object) randomState).getMaterialSystemExtension().buildSurface(this.regionMemory, region, randomState, biomeManager, context, protoChunk, noiseChunk, materialRule, possibleBiomes);
        }
    }

    private void generateCarvers(
            WorldRegion region,
            ChunkAccess chunk,
            Blender blender,
            RebornNoiseChunk noiseChunk,
            RandomState randomState,
            BiomeManager biomeManager,
            @Nullable WorldGenRegion carverBiomeRegion,
            MaterialRule materialRule
    ) {
        if (!SharedConstants.DEBUG_DISABLE_CARVERS && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
            BiomeResolver biomeResolver = this.biomeSource.createUncachedResolver(randomState);
            WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            int range = 8;
            ChunkPos pos = chunk.getPos();
            WorldGenerationContext context = new WorldGenerationContext(this, chunk.getHeightAccessorForGeneration());
            int protectedBlocksOnTop = chunk.isUpgrading() ? 0 : 7;
            int maxY = context.getMinGenY() + context.getGenDepth() - 1 - protectedBlocksOnTop;
            CarvingMask mask = new CarvingMask(context.getMinGenY() + 1, maxY);

            for (int dx = -range; dx <= range; dx++) {
                for (int dz = -range; dz <= range; dz++) {
                    ChunkPos sourcePos = new ChunkPos(pos.x() + dx, pos.z() + dz);
                    BiomeGenerationSettings sourceBiomeGenerationSettings;
                    if (carverBiomeRegion != null) {
                        ChunkAccess carverCenterChunk = carverBiomeRegion.getChunk(sourcePos.x(), sourcePos.z());
                        sourceBiomeGenerationSettings = carverCenterChunk.carverBiome(() -> this.getBiomeGenerationSettingsForCarver(biomeResolver, sourcePos));
                    } else {
                        sourceBiomeGenerationSettings = this.getBiomeGenerationSettingsForCarver(biomeResolver, sourcePos);
                    }

                    Iterable<Holder<WorldCarver>> carvers = sourceBiomeGenerationSettings.getCarvers();
                    int index = 0;

                    for (Holder<WorldCarver> carverHolder : carvers) {
                        WorldCarver carver = carverHolder.value();
                        random.setLargeFeatureSeed(randomState.seed() + index, sourcePos.x(), sourcePos.z());
                        if (carver.isStartChunk(random)) {
                            carver.carve(context, random, chunk.getPos(), sourcePos, mask);
                        }

                        index++;
                    }
                }
            }

            if (!mask.isEmpty()) {
                try (Zone var27 = Profiler.get().zone("applyCarvingMask")) {
                    BiomeManager correctBiomeManager = biomeManager.withDifferentSource(biomeResolver);
                    this.applyCarvingMask(
                            chunk, mask, randomState, materialRule, context, noiseChunk, correctBiomeManager::getBiome, blender.getCarvingFilter()
                    );
                }
            }
        }
    }

    private void applyCarvingMask(
            ChunkAccess chunk,
            CarvingMask mask,
            RandomState randomState,
            MaterialRule materialRule,
            WorldGenerationContext context,
            RebornNoiseChunk noiseChunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            CarvingMask.@Nullable Filter filter
    ) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos helperPos = new BlockPos.MutableBlockPos();
        Aquifer aquifer = noiseChunk.aquifer();
        mask.visit(
                (x, z, bottomY, topY) -> {
                    boolean hasGrass = false;
                    int worldX = chunkPos.getBlockX(x);
                    int worldZ = chunkPos.getBlockZ(z);

                    for (int worldY = topY; worldY >= bottomY; worldY--) {
                        if (filter == null || filter.test(x, worldY, z)) {
                            blockPos.set(worldX, worldY, worldZ);
                            BlockState blockState = chunk.getBlockState(blockPos);
                            if (!blockState.is(BlockTags.UNCARVABLE)) {
                                if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.MYCELIUM)) {
                                    hasGrass = true;
                                }

                                BlockState state = aquifer.computeSubstance(worldX, worldY, worldZ, 0.0);
                                if (state != null) {
                                    chunk.setBlockState(blockPos, state);
                                    if (aquifer.shouldScheduleFluidUpdate() && !state.getFluidState().isEmpty()) {
                                        chunk.markPosForPostProcessing(blockPos);
                                    }

                                    if (hasGrass) {
                                        helperPos.setWithOffset(blockPos, Direction.DOWN);
                                        if (chunk.getBlockState(helperPos).is(Blocks.DIRT)) {
                                            randomState.surfaceSystem()
                                                    .topMaterial(
                                                            materialRule,
                                                            randomState,
                                                            context,
                                                            biomeGetter,
                                                            chunk,
                                                            noiseChunk.cachingSamplers(),
                                                            helperPos,
                                                            !state.getFluidState().isEmpty()
                                                    )
                                                    .ifPresent(topMaterial -> {
                                                        chunk.setBlockState(helperPos, topMaterial);
                                                        if (!topMaterial.getFluidState().isEmpty()) {
                                                            chunk.markPosForPostProcessing(helperPos);
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    private BiomeGenerationSettings getBiomeGenerationSettingsForCarver(BiomeResolver biomeResolver, ChunkPos sourcePos) {
        int quartX = QuartPos.fromBlock(sourcePos.getMinBlockX());
        int quartZ = QuartPos.fromBlock(sourcePos.getMinBlockZ());
        return this.getBiomeGenerationSettings(biomeResolver.getNoiseBiome(quartX, 0, quartZ));
    }

    private BlockState debugPreliminarySurfaceLevel(RebornNoiseChunk noiseChunk, int posX, int posY, int posZ, BlockState state, float yLevel) {
        if (SharedConstants.DEBUG_AQUIFERS && posZ >= 0 && posZ % 4 == 0) {
            if (posY == yLevel) {
                state = yLevel < this.getSeaLevel() ? Blocks.SLIME_BLOCK.defaultBlockState() : Blocks.HONEY_BLOCK.defaultBlockState();
            }
        }

        return state;
    }
    // CHUNK GENERATOR METHODS

    @Override
    public int getGenDepth() {
        return this.settings.value().noiseSettings().height();
    }

    @Override
    public int getSeaLevel() {
        return this.settings.value().seaLevel();
    }

    @Override
    public int getMinY() {
        return this.settings.value().noiseSettings().minY();
    }


    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return this.iterateNoiseColumn(heightAccessor, randomState, x, z, null, type.isOpaque()).orElse(heightAccessor.getMinY());
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        MutableObject<NoiseColumn> result = new MutableObject<>();
        this.iterateNoiseColumn(heightAccessor, randomState, x, z, result, null);
        return result.get();
    }

    protected OptionalInt iterateNoiseColumn(
            LevelHeightAccessor heightAccessor,
            RandomState randomState,
            int blockX,
            int blockZ,
            @Nullable MutableObject<NoiseColumn> columnReference,
            @Nullable Predicate<BlockState> tester
    ) {
        NoiseSettings noiseSettings = this.settings.value().noiseSettings().clampToHeightAccessor(heightAccessor);
        if (noiseSettings.height() <= 0) {
            return OptionalInt.empty();
        }

        this.regionMemory.getRegionAt(blockX,blockZ);
        WorldRegion region = null;
        while (!regionMemory.readyForChunkReading(blockX,blockZ)){
            try{
                region = regionMemory.requestImmediately(blockX,blockZ);
                Thread.sleep(10L);
            } catch (Exception e) {
                LogUtils.getLogger().warn("Sleep failed for position {}, {}",blockX,blockZ, e);
            }
        }
        if (region == null){
            region = regionMemory.requestImmediately(blockX,blockZ);
        }


        DensityVolume volume = new DensityVolume(1, noiseSettings.height(), 1, blockX, noiseSettings.minY(), blockZ);
        BlockState[] writeTo;
        if (columnReference == null) {
            writeTo = null;
        } else {
            writeTo = new BlockState[volume.sizeY()];
            columnReference.setValue(new NoiseColumn(volume.minBlockY(), writeTo));
        }

        try (RebornNoiseChunk noiseChunk = new RebornNoiseChunk(this.regionMemory, randomState, null, this.settings.value(), this.globalFluidPicker.get(), Blender.empty(), volume)) {
            Aquifer aquifer = noiseChunk.aquifer();
            BlockState defaultState = this.settings.value().defaultBlock();
            DensitySampler.Bound finalDensity = noiseChunk.cachingSamplers().get(this.settings.value().noiseRouter().finalDensity());
            float yLevel = region.layers().getElevation().getUpsampled(blockX,blockZ,this.regionMemory);

            try (ScopedDensityBuffer densityBuffer = finalDensity.sampleVolume(noiseChunk.volume())) {
                for (int y = volume.sizeY() - 1; y >= 0; y--) {
                    int blockY = volume.blockY(y);
                    float densityPre = densityBuffer.get(volume.indexUnchecked(0, y, 0));
                    float density = this.adaptDensity(densityPre, yLevel,blockY);
                    BlockState baseState = aquifer.computeSubstance(blockX, blockY, blockZ, density);
                    BlockState state = baseState == null ? defaultState : baseState;
                    if (writeTo != null) {
                        writeTo[y] = state;
                    }

                    if (tester != null && tester.test(state)) {
                        return OptionalInt.of(blockY + 1);
                    }
                }
            }
        }

        return OptionalInt.empty();
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
        ChunkPos center = worldGenRegion.getCenter();
        BlockPos sourcePos = center.getWorldPosition().atY(worldGenRegion.getMaxY());
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        random.setDecorationSeed(worldGenRegion.getSeed(), center.getMinBlockX(), center.getMinBlockZ());
        NaturalSpawner.spawnMobsForChunkGeneration(worldGenRegion, sourcePos, center, random);

    }

}
