package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.mixin.accessor.ChunkMapAccessor;
import com.fuyuaki.r_wilderness.world.generation.aquifer.WRAquifer;
import com.fuyuaki.r_wilderness.world.generation.chunk.ChunkData;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.noise.ChunkNoiseSamplingSettings;
import com.fuyuaki.r_wilderness.world.generation.noise.NoiseSampler;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
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
    public static final int SEA_LEVEL_Y = ModWorldGenConstants.SEA_LEVEL;

    private Seed seed;
    private WildGeneratorSettings settings;

    private final FastConcurrentCache<WRAquifer> aquiferCache;

    private TerrainParameters parameters;
    private NoiseSampler noiseSampler;

    public WildChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseSettings, WildGeneratorSettings generatorSettings) {
        super(biomeSource);
        this.noiseSettings = noiseSettings;
        this.settings = generatorSettings;
        this.vanillaChunkGenerator = new NoiseBasedChunkGenerator(biomeSource, this.noiseSettings);
        this.aquiferCache = new FastConcurrentCache<>(256);

    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


    @Override
    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            return chunk;
        }, Util.backgroundExecutor().forName("init_biomes"));
    }


    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {

    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

    }


    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override
    public int getGenDepth() {
        return this.settings.noiseSettings().height();
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        final ChunkPos chunkPos = chunk.getPos();
        NoiseSettings noisesettings = this.settings.noiseSettings().clampToHeightAccessor(chunk.getHeightAccessorForGeneration());


        int minY = noisesettings.minY();
        int minCellY = Mth.floorDiv(minY, noisesettings.getCellHeight());
        int maxCellY = Mth.floorDiv(noisesettings.height(), noisesettings.getCellHeight());
        final WRNoiseChunk noiseChunk = createWRNoiseChunk(chunk,structureManager,blender,randomState,settings);

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

    private WRNoiseChunk createWRNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random, ChunkNoiseSamplingSettings settings) {
        return WRNoiseChunk.forChunk(
                this.parameters,
                chunk,
                random,
                Beardifier.forStructuresInChunk(structureManager, chunk.getPos()),
                this.settings,
                blender,
                noiseSampler,
                settings,
                getSeaLevel()
        );
    }

    @Override
    public int getSeaLevel() {
        return SEA_LEVEL_Y;
    }

    @Override
    public int getMinY() {
        return ModWorldGenConstants.WORLD_BOTTOM;

    }


    @Override
    public WildGeneratorSettings settings() {
        return this.settings;
    }

    @Override
    public void applySettings(UnaryOperator<WildGeneratorSettings> settings) {
        this.settings = settings.apply(this.settings);

    }

    @Override
    public Aquifer getOrCreateAquifer(ChunkAccess chunk) {

        final ChunkNoiseSamplingSettings settings = createNoiseSamplingSettingsForChunk(chunk);
        return getOrCreateAquifer(chunk, settings);
    }

    private WRAquifer getOrCreateAquifer(ChunkAccess chunk, ChunkNoiseSamplingSettings settings) {

        final ChunkPos chunkPos = chunk.getPos();

        WRAquifer aquifer = aquiferCache.getIfPresent(chunkPos.x, chunkPos.z);
        if (aquifer == null)
        {
            final ChunkData chunkData = ChunkData.get(chunk);

            aquifer = new WRAquifer(chunkPos, settings, getSeaLevel(), noiseSampler.positionalRandomFactory, noiseSampler.barrierNoise);
            aquifer.setSurfaceHeights(chunkData.getAquiferSurfaceHeight());

            aquiferCache.set(chunkPos.x, chunkPos.z, aquifer);
        }
        return aquifer;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initRandomState(ChunkMap chunkMap, ServerLevel level)
    {

        final Seed seed = Seed.of(level.getSeed());

        this.seed = seed;
        this.noiseSampler = new NoiseSampler(seed.next(), level.registryAccess().lookupOrThrow(Registries.NOISE), level.registryAccess().lookupOrThrow(Registries.DENSITY_FUNCTION));

        this.parameters = new TerrainParameters(seed,this.settings);

        // Update the cached chunk generator extension on the RandomState
        // This is done here when we initialize this chunk generator, and have ensured we are unique to this state and chunk map
        // We do this to be able to access the chunk generator through the random state later, i.e. in structure generation
        ((RandomStateExtension) (Object) ((ChunkMapAccessor) chunkMap).accessor$getRandomState()).wildernessReborn$setChunkGeneratorExtension(this);
    }


    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return iterateNoiseColumn(level,random,x,z,null,type.isOpaque()).orElse(level.getMinY());
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
        MutableObject<NoiseColumn> mutableobject = new MutableObject<>();
        this.iterateNoiseColumn(height,random,x,z, mutableobject, null);
        return mutableobject.getValue();
    }


    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        DecimalFormat decimalformat = new DecimalFormat("0.000");
        TerrainParameters.Sampled sampled = this.parameters.samplerAt(pos.getX(),pos.getZ());

        String continentalness = decimalformat.format(sampled.continentalness());
        String terrainOffset = decimalformat.format(sampled.terrainOffset());
        String terrainOffsetLarge = decimalformat.format(sampled.terrainOffsetLarge());
        String erosion = decimalformat.format(sampled.erosion());
        String tectonicActivity = decimalformat.format((1 - Math.abs(sampled.tectonicActivity())));
        String mountainCore = decimalformat.format(sampled.mountainsCore());
        String mountain = decimalformat.format(sampled.mountains());
        String mountainDetail = decimalformat.format(sampled.mountainDetails());
        String plateau = decimalformat.format( Math.pow(Math.clamp(sampled.plateauMap(),0,1),5));
        String hill = decimalformat.format(sampled.hills());
        String terrainTypeA = decimalformat.format(sampled.terrainTypeA());
        String terrainTypeB = decimalformat.format(sampled.terrainTypeB());
        String postTerrainA = decimalformat.format(Math.clamp((sampled.terrainTypeA() + 1) /2,0,1));
        String postTerrainB = decimalformat.format(Math.clamp((sampled.terrainTypeB() + 1) /2,0,1));

        info.add("TO: " + terrainOffset + " TOL: " + terrainOffsetLarge);
        info.add("C: " + continentalness + " E: " + erosion + " TA: " +  tectonicActivity);
        info.add("Mountain: M: " +  mountain + " C: " + mountainCore + " D: " + mountainDetail);
        info.add("H: " +  hill + " P: " + plateau);
        info.add("Type: A: " + terrainTypeA  + " B: " + terrainTypeB  + " Post: A: " + postTerrainA  + " B: " + postTerrainB );


    }


    //Borrowed from TerraFirmaCraft
    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkAccess chunk) {
        return createNoiseSamplingSettingsForChunk(chunk.getPos(), chunk.getHeightAccessorForGeneration());
    }

    //Borrowed from TerraFirmaCraft
    private ChunkNoiseSamplingSettings createNoiseSamplingSettingsForChunk(ChunkPos pos, LevelHeightAccessor level) {
        final NoiseSettings noiseSettings = this.settings.noiseSettings();

        final int cellWidth = noiseSettings.getCellWidth();
        final int cellHeight = noiseSettings.getCellHeight();

        final int minY = Math.max(noiseSettings.minY(), level.getMinY());
        final int maxY = Math.min(noiseSettings.minY() + noiseSettings.height(), level.getMaxY());

        final int cellCountY = Math.floorDiv(maxY - minY, noiseSettings.getCellHeight());

        final int firstCellX = Math.floorDiv(pos.getMinBlockX(), cellWidth);
        final int firstCellY = Math.floorDiv(minY, cellHeight);
        final int firstCellZ = Math.floorDiv(pos.getMinBlockZ(), cellWidth);

        return new ChunkNoiseSamplingSettings(minY, 16 / cellWidth, cellCountY, cellWidth, cellHeight, firstCellX, firstCellY, firstCellZ);
    }

    private WildChunkGenerator copy() {
        return new WildChunkGenerator(biomeSource, noiseSettings, settings);
    }
    protected OptionalInt iterateNoiseColumn(
            LevelHeightAccessor level,
            RandomState random,
            int x,
            int z,
            @Nullable MutableObject<NoiseColumn> column,
            @Nullable Predicate<BlockState> stoppingState
    ) {
        NoiseSettings noisesettings = this.settings.noiseSettings().clampToHeightAccessor(level);
        int cellHeight = noisesettings.getCellHeight();
        int minY = noisesettings.minY();
        int heightDiv = Mth.floorDiv(noisesettings.height(), cellHeight);
        if (heightDiv <= 0) {
            return OptionalInt.empty();
        } else {
            BlockState[] ablockstate;
            if (column != null) {
                ablockstate = new BlockState[noisesettings.height()];
                column.setValue(new NoiseColumn(minY, ablockstate));
            }

            int i1 = noisesettings.getCellWidth();
            int j1 = Math.floorDiv(x, i1);
            int k1 = Math.floorDiv(z, i1);
            int j2 = j1 * i1;
            int k2 = k1 * i1;
            WRNoiseChunk noisechunk = new WRNoiseChunk(
                    this.parameters,
                    1,
                    random,
                    j2,
                    k2,
                    null,
                    noiseSampler,
                    noisesettings,
                    createNoiseSamplingSettingsForChunk( new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)), level),
                    SEA_LEVEL_Y,
                    DensityFunctions.BeardifierMarker.INSTANCE,
                    Blender.empty()
            );

                    int height = (int) noisechunk.getTerrainPeakAt(x,z);
                    return OptionalInt.of(height);
        }
    }
}
