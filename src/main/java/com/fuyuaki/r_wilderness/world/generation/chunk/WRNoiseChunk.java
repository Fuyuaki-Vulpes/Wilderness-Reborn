package com.fuyuaki.r_wilderness.world.generation.chunk;

import com.fuyuaki.r_wilderness.util.math.SplinePoint;
import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
import com.fuyuaki.r_wilderness.world.generation.aquifer.LazyAquifer;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildOreVeins;
import com.google.common.collect.Lists;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static net.minecraft.world.level.block.Blocks.WATER;

public class WRNoiseChunk implements DensityFunction.ContextProvider, DensityFunction.FunctionContext {

    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(3);

    private final NoiseSettings noiseSettings;
    final int cellCountXZ;
    final int cellCountY;
    final int cellNoiseMinY;
    private final int firstCellX;
    private final int firstCellZ;
    final int firstNoiseX;
    final int firstNoiseZ;
    final List<WRNoiseChunk.NoiseInterpolator> interpolators;
    final List<WRNoiseChunk.CacheAllInCell> cellCaches;
    protected final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

    protected final Blender blender;
    protected final WRNoiseChunk.FlatCache blendAlpha;
    protected final WRNoiseChunk.FlatCache blendOffset;
    private final TerrainParameters terrainParameters;
    protected long lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
    protected Blender.BlendingOutput lastBlendingOutput = new Blender.BlendingOutput(1.0, 0.0);
    final int noiseSizeXZ;
    final int cellWidth;
    final int cellHeight;
    boolean interpolating;
    boolean fillingCell;
    private int cellStartBlockX;
    int cellStartBlockY;
    private int cellStartBlockZ;
    int inCellX;
    int inCellY;
    int inCellZ;
    long interpolationCounter;
    long arrayInterpolationCounter;
    int arrayIndex;
    private final DensityFunction.ContextProvider sliceFillingContextProvider = new DensityFunction.ContextProvider() {
        @Override
        public DensityFunction.FunctionContext forIndex(int p_209253_) {
            WRNoiseChunk.this.cellStartBlockY = (p_209253_ + WRNoiseChunk.this.cellNoiseMinY) * WRNoiseChunk.this.cellHeight;
            WRNoiseChunk.this.interpolationCounter++;
            WRNoiseChunk.this.inCellY = 0;
            WRNoiseChunk.this.arrayIndex = p_209253_;
            return WRNoiseChunk.this;
        }

        @Override
        public void fillAllDirectly(double[] p_209255_, DensityFunction p_209256_) {
            for (int i2 = 0; i2 < WRNoiseChunk.this.cellCountY + 1; i2++) {
                WRNoiseChunk.this.cellStartBlockY = (i2 + WRNoiseChunk.this.cellNoiseMinY) * WRNoiseChunk.this.cellHeight;
                WRNoiseChunk.this.interpolationCounter++;
                WRNoiseChunk.this.inCellY = 0;
                WRNoiseChunk.this.arrayIndex = i2;
                p_209255_[i2] = p_209256_.compute(WRNoiseChunk.this);
            }
        }
    };



    final ProtoChunk chunk;
    final int chunkMinX, chunkMinZ; // Min block positions for the chunk

    final CarvingMask airCarvingMask; // Only air carving mask is marked

    private final DensityFunctions.BeardifierOrMarker  beardifier;


    protected final int seaLevel;

    private final BlockStateFiller veinFiller;

    public static WRNoiseChunk forChunk(
            TerrainParameters parameters,
            ChunkAccess chunk,
            RandomState state,
            DensityFunctions.BeardifierOrMarker beardifierOrMarker,
            WildGeneratorSettings noiseGeneratorSettings,
            Blender blender,
            int seaLevel
    ) {
        NoiseSettings noisesettings = noiseGeneratorSettings.noiseSettings().clampToHeightAccessor(chunk);
        ChunkPos chunkpos = chunk.getPos();
        int i = 16 / noisesettings.getCellWidth();
        return new WRNoiseChunk(parameters, i, state, chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(),(ProtoChunk) chunk, noisesettings, seaLevel,beardifierOrMarker, blender, noiseGeneratorSettings);
    }

    public WRNoiseChunk(
            TerrainParameters parameters,
            int cellCountXZ,
            RandomState random,
            int firstNoiseX,
            int firstNoiseZ,
            ProtoChunk chunk,
            NoiseSettings noiseSettings,
            int seaLevel,
            DensityFunctions.BeardifierOrMarker  beardifier,
            Blender blendifier,
            WildGeneratorSettings noiseGeneratorSettings
    ){
        this.terrainParameters = parameters;
        this.noiseSettings = noiseSettings;
        this.cellWidth = noiseSettings.getCellWidth();
        this.cellHeight = noiseSettings.getCellHeight();
        this.cellCountXZ = cellCountXZ;
        this.cellCountY = Mth.floorDiv(noiseSettings.height(), this.cellHeight);
        this.cellNoiseMinY = Mth.floorDiv(noiseSettings.minY(), this.cellHeight);
        this.firstCellX = Math.floorDiv(firstNoiseX, this.cellWidth);
        this.firstCellZ = Math.floorDiv(firstNoiseZ, this.cellWidth);
        this.interpolators = Lists.newArrayList();
        this.cellCaches = Lists.newArrayList();
        this.firstNoiseX = QuartPos.fromBlock(firstNoiseX);
        this.firstNoiseZ = QuartPos.fromBlock(firstNoiseZ);
        this.noiseSizeXZ = QuartPos.fromBlock(cellCountXZ * this.cellWidth);
        this.blender = blendifier;
        this.beardifier = beardifier;
        this.blendAlpha = new FlatCache(new BlendAlpha(), false);
        this.blendOffset = new FlatCache(new BlendOffset(), false);
        if (chunk != null) {
            this.chunk = chunk;
            this.chunkMinX = chunk.getPos().getMinBlockX();
            this.chunkMinZ = chunk.getPos().getMinBlockZ();
            this.airCarvingMask = chunk.getOrCreateCarvingMask();
        }else {
            int x = SectionPos.blockToSectionCoord(firstNoiseX);
            int z = SectionPos.blockToSectionCoord(firstNoiseZ);

            this.chunk = null;
            this.chunkMinX = 0;
            this.chunkMinZ = 0;
            this.airCarvingMask = new CarvingMask(0,0);
        }
        NoiseRouter noiserouter = random.router();
        NoiseRouter noiserouter1 = noiserouter.mapAll(this::wrap);



        this.seaLevel = seaLevel;
        this.veinFiller = WildOreVeins.create(noiserouter1.veinToggle(), noiserouter1.veinRidged(), noiserouter1.veinGap(), random.oreRandom());


    }

    public ChunkAccess buildNoise(ChunkAccess chunk, int minCellY, BlockState defaultBlock) {

        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        this.initializeForFirstCellX();
        int cellWidth = this.cellWidth();
        int cellHeights = this.cellHeight();
        int cellCountX = 16 / cellWidth;
        int cellCountZ = 16 / cellWidth;

        for (int cellX= 0; cellX < cellCountX; cellX++) {
            this.advanceCellX(cellX);

            for (int cellZ = 0; cellZ < cellCountZ; cellZ++) {
                int chunkSectionsCount = chunk.getSectionsCount() - 1;
                LevelChunkSection levelchunksection = chunk.getSection(chunkSectionsCount);


                for (int cellFractionX = 0; cellFractionX < cellWidth; cellFractionX++) {
                    int x = this.chunkMinX + cellX * cellWidth + cellFractionX;
                    int regionalX = x & 15;
                    double d1 = (double) cellFractionX / cellWidth;
                    this.updateForX(x, d1);

                    for (int cellFractionZ = 0; cellFractionZ < cellWidth; cellFractionZ++) {
                        int z = this.chunkMinZ + cellZ * cellWidth + cellFractionZ;
                        int regionalZ = z & 15;
                        double d2 = (double) cellFractionZ / cellWidth;
                        this.updateForZ(z, d2);
                        int yLevelGen = Mth.floor(this.terrainParameters.yLevelAt(x, z,false));
                        TerrainParameters.Sampled sampled = this.terrainParameters.samplerAt(x,z);

                        for (int cellY = cellCountY - 1; cellY >= 0; cellY--) {
                            this.selectCellYZ(cellY, cellZ);

                            for (int verticalCell = cellHeights - 1; verticalCell >= 0; verticalCell--) {
                                int y = (minCellY + cellY) * cellHeights + verticalCell;
                                int regionalY = y & 15;
                                int cellIndex = chunk.getSectionIndex(y);
                                if (chunkSectionsCount != cellIndex) {
                                    chunkSectionsCount = cellIndex;
                                    levelchunksection = chunk.getSection(cellIndex);
                                }

                                BlockState finalState = getBlockStateForY(defaultBlock, verticalCell, cellHeights, y, yLevelGen, sampled, x, z);


                                if (finalState != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
                                    levelchunksection.setBlockState(regionalX, regionalY, regionalZ, finalState, false);
                                    oceanFloor.update(regionalX, y, regionalZ, finalState);
                                    worldSurface.update(regionalX, y, regionalZ, finalState);
                                }
                            }
                        }
                    }
                }
            }

            this.swapSlices();
        }

        this.stopInterpolation();
        return chunk;
    }

    private @Nullable BlockState getBlockStateForY(BlockState defaultBlock, double verticalCell, int cellHeight1, int y, int yLevelGen, TerrainParameters.Sampled sampled, int x, int z) {
        double cellY1 = verticalCell / cellHeight1;
        this.updateForY(y, cellY1);

        BlockState finalState;
        BlockState worldState;
        BlockState veinState =
                veinFiller
                        .calculate(this);
        if (y <= yLevelGen){
            if (sampled.continentalness() < 0.2 && y > yLevelGen - 3){
                worldState = veinState == null ? defaultBlock : veinState;

            }else {
                worldState = this.terrainParameters.cavesAt(x, y, z, yLevelGen) ? Blocks.AIR.defaultBlockState() : veinState == null ? defaultBlock : veinState;
            }
        }
        else {
            worldState = Blocks.AIR.defaultBlockState();
        }

        if (sampled.continentalness() < 0.2 && worldState.isAir() && y <= seaLevel && y >= yLevelGen){
            worldState = WATER.defaultBlockState();
        }
        finalState = worldState;
        if (worldState != WATER.defaultBlockState()) {
            double t;
            if (x % 4 == 0 || z % 4 == 0) {
                t = this.beardifier.compute(
                        new DensityFunction.SinglePointContext(x + 1, y, z + 1)
                );
            } else {
                t = this.beardifier.compute(this);

            }

            if (t > 0.05) {
                finalState = defaultBlock;
            } else if (t < -0.05) {
                finalState = Blocks.AIR.defaultBlockState();
            }
        }
        if (finalState == null){
            finalState = defaultBlock;
        }
        return finalState;
    }

    public BlockState testBlockAt(int x, int y, int z, BlockState defaultBlock) {
        if (defaultBlock == null) defaultBlock = Blocks.STONE.defaultBlockState();
        int yLevelGen = Mth.floor(this.terrainParameters.yLevelAtWithCache(x, z));
        TerrainParameters.Sampled sampled = this.terrainParameters.samplerAtCached(x,z);


        BlockState finalState;
        BlockState worldState;
        BlockState veinState =
                veinFiller
                        .calculate(this);
        if (y <= yLevelGen){
            worldState = veinState == null ? defaultBlock : veinState;
        }else {
            worldState = Blocks.AIR.defaultBlockState();
        }

        if (sampled.continentalness() < 0.2 && worldState.isAir() && y <= seaLevel){
            worldState = WATER.defaultBlockState();
        }

        finalState = worldState;

        if (finalState == null){
            finalState = defaultBlock;
        }

        return finalState;
    }




    @Override
    public int blockX() {
        return this.cellStartBlockX + this.inCellX;
    }

    @Override
    public int blockY() {
        return this.cellStartBlockY + this.inCellY;
    }

    @Override
    public int blockZ() {
        return this.cellStartBlockZ + this.inCellZ;
    }

    @Override
    public Blender getBlender() {
        return this.blender;
    }

    private void fillSlice(boolean isSlice0, int start) {
        this.cellStartBlockX = start * this.cellWidth;
        this.inCellX = 0;

        for (int i = 0; i < this.cellCountXZ + 1; i++) {
            int j = this.firstCellZ + i;
            this.cellStartBlockZ = j * this.cellWidth;
            this.inCellZ = 0;
            this.arrayInterpolationCounter++;

            for (WRNoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
                double[] adouble = (isSlice0 ? noisechunk$noiseinterpolator.slice0 : noisechunk$noiseinterpolator.slice1)[i];
                noisechunk$noiseinterpolator.fillArray(adouble, this.sliceFillingContextProvider);
            }
        }

        this.arrayInterpolationCounter++;
    }

    public void initializeForFirstCellX() {
        if (this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        } else {
            this.interpolating = true;
            this.interpolationCounter = 0L;
            this.fillSlice(true, this.firstCellX);
        }
    }

    public void advanceCellX(int increment) {
        this.fillSlice(false, this.firstCellX + increment + 1);
        this.cellStartBlockX = (this.firstCellX + increment) * this.cellWidth;
    }

    public WRNoiseChunk forIndex(int p_209240_) {
        int i = Math.floorMod(p_209240_, this.cellWidth);
        int j = Math.floorDiv(p_209240_, this.cellWidth);
        int k = Math.floorMod(j, this.cellWidth);
        int l = this.cellHeight - 1 - Math.floorDiv(j, this.cellWidth);
        this.inCellX = k;
        this.inCellY = l;
        this.inCellZ = i;
        this.arrayIndex = p_209240_;
        return this;
    }

    @Override
    public void fillAllDirectly(double[] p_209224_, DensityFunction p_209225_) {
        this.arrayIndex = 0;

        for (int i = this.cellHeight - 1; i >= 0; i--) {
            this.inCellY = i;

            for (int j = 0; j < this.cellWidth; j++) {
                this.inCellX = j;

                for (int k = 0; k < this.cellWidth; k++) {
                    this.inCellZ = k;
                    p_209224_[this.arrayIndex++] = p_209225_.compute(this);
                }
            }
        }
    }

    /*
    public double[] createSlopeMap()
    {
        final int[] quartSurfaceHeight = new int[7 * 7]; // 7x7, quart pos resolution

        // Interior points - record from the existing positions in the chunk
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                // Copy from surface height, where possible
                quartSurfaceHeight[(x + 1) + 7 * (z + 1)] = surfaceHeight[(x << 2) + 16 * (z << 2)];
            }
        }

        // Exterior points
        for (int i = 0; i < EXTERIOR_POINTS_COUNT; i++)
        {
            int x = EXTERIOR_POINTS[i << 1];
            int z = EXTERIOR_POINTS[(i << 1) | 1];

            int x0 = chunkMinX + ((x - 1) << 2);
            int z0 = chunkMinZ + ((z - 1) << 2);

            setupColumn(x0, z0);
            quartSurfaceHeight[x + 7 * z] = (int) sampleColumnHeightAndBiome(sampledBiomeWeights[x + z * 7], false);
        }

        double[] slopeMap = new double[6 * 6];
        for (int x = 0; x < 6; x++)
        {
            for (int z = 0; z < 6; z++)
            {
                // Math people (including myself) cry at what I'm calling 'the derivative'
                final double nw = quartSurfaceHeight[(x + 0) + 7 * (z + 0)];
                final double ne = quartSurfaceHeight[(x + 1) + 7 * (z + 0)];
                final double sw = quartSurfaceHeight[(x + 0) + 7 * (z + 1)];
                final double se = quartSurfaceHeight[(x + 1) + 7 * (z + 1)];

                final double center = (nw + ne + sw + se) / 4;
                final double slope = Math.abs(nw - center) + Math.abs(ne - center) + Math.abs(sw - center) + Math.abs(se - center);
                slopeMap[x + 6 * z] = slope;
            }
        }
        return slopeMap;
    }*/


    public void selectCellYZ(int y, int z) {
        for (WRNoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
            noisechunk$noiseinterpolator.selectCellYZ(y, z);
        }

        this.fillingCell = true;
        this.cellStartBlockY = (y + this.cellNoiseMinY) * this.cellHeight;
        this.cellStartBlockZ = (this.firstCellZ + z) * this.cellWidth;
        this.arrayInterpolationCounter++;

        for (WRNoiseChunk.CacheAllInCell noisechunk$cacheallincell : this.cellCaches) {
            noisechunk$cacheallincell.noiseFiller.fillArray(noisechunk$cacheallincell.values, this);
        }

        this.arrayInterpolationCounter++;
        this.fillingCell = false;
    }

    public void updateForY(int cellEndBlockY, double y) {
        this.inCellY = cellEndBlockY - this.cellStartBlockY;
        this.interpolationCounter++;

        for (WRNoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
            noisechunk$noiseinterpolator.updateForY(y);
        }
    }

    public void updateForX(int cellEndBlockX, double x) {
        this.inCellX = cellEndBlockX - this.cellStartBlockX;

        for (WRNoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
            noisechunk$noiseinterpolator.updateForX(x);
        }
    }

    public void updateForZ(int cellEndBlockZ, double z) {
        this.inCellZ = cellEndBlockZ - this.cellStartBlockZ;

        for (WRNoiseChunk.NoiseInterpolator noisechunk$noiseinterpolator : this.interpolators) {
            noisechunk$noiseinterpolator.updateForZ(z);
        }
    }
    public void stopInterpolation() {
        if (!this.interpolating) {
            throw new IllegalStateException("Staring interpolation twice");
        } else {
            this.interpolating = false;
        }
    }

    public void swapSlices() {
        this.interpolators.forEach(WRNoiseChunk.NoiseInterpolator::swapSlices);
    }

    protected int cellWidth() {
        return this.cellWidth;
    }

    protected int cellHeight() {
        return this.cellHeight;
    }

    Blender.BlendingOutput getOrComputeBlendingOutput(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);
        if (this.lastBlendingDataPos == i) {
            return this.lastBlendingOutput;
        } else {
            this.lastBlendingDataPos = i;
            Blender.BlendingOutput blender$blendingoutput = this.blender.blendOffsetAndFactor(chunkX, chunkZ);
            this.lastBlendingOutput = blender$blendingoutput;
            return blender$blendingoutput;
        }
    }
    protected DensityFunction wrap(DensityFunction densityFunction) {
        return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
    }

    private DensityFunction wrapNew(DensityFunction densityFunction) {
        if (densityFunction instanceof DensityFunctions.Marker densityfunctions$marker) {
            return (DensityFunction)(switch (densityfunctions$marker.type()) {
                case Interpolated -> new WRNoiseChunk.NoiseInterpolator(densityfunctions$marker.wrapped());
                case FlatCache -> new WRNoiseChunk.FlatCache(densityfunctions$marker.wrapped(), true);
                case Cache2D -> new WRNoiseChunk.Cache2D(densityfunctions$marker.wrapped());
                case CacheOnce -> new WRNoiseChunk.CacheOnce(densityfunctions$marker.wrapped());
                case CacheAllInCell -> new WRNoiseChunk.CacheAllInCell(densityfunctions$marker.wrapped());
            });
        } else {
            if (this.blender != Blender.empty()) {
                if (densityFunction == DensityFunctions.BlendAlpha.INSTANCE) {
                    return this.blendAlpha;
                }

                if (densityFunction == DensityFunctions.BlendOffset.INSTANCE) {
                    return this.blendOffset;
                }
            }

            if (densityFunction == DensityFunctions.BeardifierMarker.INSTANCE) {
                return this.beardifier;
            } else {
                return densityFunction instanceof DensityFunctions.HolderHolder densityfunctions$holderholder
                        ? densityfunctions$holderholder.function().value()
                        : densityFunction;
            }
        }
    }

    public int getSurfaceY(int x, int z) {
        return Mth.floor(this.terrainParameters.yLevelAtWithCache(x,z));
    }
    public int getSurfaceYSurface(int x, int z) {
        return Mth.floor(this.terrainParameters.yLevelAt(x,z,true));
    }
    public int getSurfaceYNoCache(int x, int z,boolean surfaceOnly) {
        return Mth.floor(this.terrainParameters.yLevelAt(x,z,surfaceOnly,false));
    }

    public Aquifer lazyAquifer() {
        return new LazyAquifer(terrainParameters);

    }


    @FunctionalInterface
    public interface BlockStateFiller {
        @Nullable
        BlockState calculate(DensityFunction.FunctionContext context);
    }

    class BlendAlpha implements WRNoiseChunk.NoiseChunkDensityFunction {
        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendAlpha.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor p_224365_) {
            return this.wrapped().mapAll(p_224365_);
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209264_) {
            return WRNoiseChunk.this.getOrComputeBlendingOutput(p_209264_.blockX(), p_209264_.blockZ()).alpha();
        }

        @Override
        public void fillArray(double[] p_209266_, DensityFunction.ContextProvider p_209267_) {
            p_209267_.fillAllDirectly(p_209266_, this);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return DensityFunctions.BlendAlpha.CODEC;
        }
    }

    class BlendOffset implements WRNoiseChunk.NoiseChunkDensityFunction {
        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendOffset.INSTANCE;
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor p_224368_) {
            return this.wrapped().mapAll(p_224368_);
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209276_) {
            return WRNoiseChunk.this.getOrComputeBlendingOutput(p_209276_.blockX(), p_209276_.blockZ()).blendingOffset();
        }

        @Override
        public void fillArray(double[] p_209278_, DensityFunction.ContextProvider p_209279_) {
            p_209279_.fillAllDirectly(p_209278_, this);
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return DensityFunctions.BlendOffset.CODEC;
        }
    }

    static class Cache2D implements DensityFunctions.MarkerOrMarked, WRNoiseChunk.NoiseChunkDensityFunction {
        private final DensityFunction function;
        private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
        private double lastValue;

        Cache2D(DensityFunction function) {
            this.function = function;
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209290_) {
            int i = p_209290_.blockX();
            int j = p_209290_.blockZ();
            long k = ChunkPos.asLong(i, j);
            if (this.lastPos2D == k) {
                return this.lastValue;
            } else {
                this.lastPos2D = k;
                double d0 = this.function.compute(p_209290_);
                this.lastValue = d0;
                return d0;
            }
        }

        @Override
        public void fillArray(double[] p_209292_, DensityFunction.ContextProvider p_209293_) {
            this.function.fillArray(p_209292_, p_209293_);
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.Cache2D;
        }
    }

    class CacheAllInCell implements DensityFunctions.MarkerOrMarked, WRNoiseChunk.NoiseChunkDensityFunction {
        final DensityFunction noiseFiller;
        final double[] values;

        CacheAllInCell(DensityFunction noiseFilter) {
            this.noiseFiller = noiseFilter;
            this.values = new double[WRNoiseChunk.this.cellWidth * WRNoiseChunk.this.cellWidth * WRNoiseChunk.this.cellHeight];
            WRNoiseChunk.this.cellCaches.add(this);
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209303_) {
            if (p_209303_ != WRNoiseChunk.this) {
                return this.noiseFiller.compute(p_209303_);
            } else if (!WRNoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            } else {
                int i = WRNoiseChunk.this.inCellX;
                int j = WRNoiseChunk.this.inCellY;
                int k = WRNoiseChunk.this.inCellZ;
                return i >= 0 && j >= 0 && k >= 0 && i < WRNoiseChunk.this.cellWidth && j < WRNoiseChunk.this.cellHeight && k < WRNoiseChunk.this.cellWidth
                        ? this.values[((WRNoiseChunk.this.cellHeight - 1 - j) * WRNoiseChunk.this.cellWidth + i) * WRNoiseChunk.this.cellWidth + k]
                        : this.noiseFiller.compute(p_209303_);
            }
        }

        @Override
        public void fillArray(double[] p_209305_, DensityFunction.ContextProvider p_209306_) {
            p_209306_.fillAllDirectly(p_209305_, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.CacheAllInCell;
        }
    }

    class CacheOnce implements DensityFunctions.MarkerOrMarked, WRNoiseChunk.NoiseChunkDensityFunction {
        private final DensityFunction function;
        private long lastCounter;
        private long lastArrayCounter;
        private double lastValue;
        @Nullable
        private double[] lastArray;

        CacheOnce(DensityFunction function) {
            this.function = function;
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209319_) {
            if (p_209319_ != WRNoiseChunk.this) {
                return this.function.compute(p_209319_);
            } else if (this.lastArray != null && this.lastArrayCounter == WRNoiseChunk.this.arrayInterpolationCounter) {
                return this.lastArray[WRNoiseChunk.this.arrayIndex];
            } else if (this.lastCounter == WRNoiseChunk.this.interpolationCounter) {
                return this.lastValue;
            } else {
                this.lastCounter = WRNoiseChunk.this.interpolationCounter;
                double d0 = this.function.compute(p_209319_);
                this.lastValue = d0;
                return d0;
            }
        }

        @Override
        public void fillArray(double[] p_209321_, DensityFunction.ContextProvider p_209322_) {
            if (this.lastArray != null && this.lastArrayCounter == WRNoiseChunk.this.arrayInterpolationCounter) {
                System.arraycopy(this.lastArray, 0, p_209321_, 0, p_209321_.length);
            } else {
                this.wrapped().fillArray(p_209321_, p_209322_);
                if (this.lastArray != null && this.lastArray.length == p_209321_.length) {
                    System.arraycopy(p_209321_, 0, this.lastArray, 0, p_209321_.length);
                } else {
                    this.lastArray = (double[])p_209321_.clone();
                }

                this.lastArrayCounter = WRNoiseChunk.this.arrayInterpolationCounter;
            }
        }

        @Override
        public DensityFunction wrapped() {
            return this.function;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.CacheOnce;
        }
    }

    class FlatCache implements DensityFunctions.MarkerOrMarked, WRNoiseChunk.NoiseChunkDensityFunction {
        private final DensityFunction noiseFiller;
        final double[][] values;

        FlatCache(DensityFunction noiseFiller, boolean computeValues) {
            this.noiseFiller = noiseFiller;
            this.values = new double[WRNoiseChunk.this.noiseSizeXZ + 1][WRNoiseChunk.this.noiseSizeXZ + 1];
            if (computeValues) {
                for (int i = 0; i <= WRNoiseChunk.this.noiseSizeXZ; i++) {
                    int j = WRNoiseChunk.this.firstNoiseX + i;
                    int k = QuartPos.toBlock(j);

                    for (int l = 0; l <= WRNoiseChunk.this.noiseSizeXZ; l++) {
                        int i1 = WRNoiseChunk.this.firstNoiseZ + l;
                        int j1 = QuartPos.toBlock(i1);
                        this.values[i][l] = noiseFiller.compute(new DensityFunction.SinglePointContext(k, 0, j1));
                    }
                }
            }
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209333_) {
            int i = QuartPos.fromBlock(p_209333_.blockX());
            int j = QuartPos.fromBlock(p_209333_.blockZ());
            int k = i - WRNoiseChunk.this.firstNoiseX;
            int l = j - WRNoiseChunk.this.firstNoiseZ;
            int i1 = this.values.length;
            return k >= 0 && l >= 0 && k < i1 && l < i1 ? this.values[k][l] : this.noiseFiller.compute(p_209333_);
        }

        @Override
        public void fillArray(double[] p_209335_, DensityFunction.ContextProvider p_209336_) {
            p_209336_.fillAllDirectly(p_209335_, this);
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.FlatCache;
        }
    }

    interface NoiseChunkDensityFunction extends DensityFunction {
        DensityFunction wrapped();

        @Override
        default double minValue() {
            return this.wrapped().minValue();
        }

        @Override
        default double maxValue() {
            return this.wrapped().maxValue();
        }
    }

    public class NoiseInterpolator implements DensityFunctions.MarkerOrMarked, WRNoiseChunk.NoiseChunkDensityFunction {
        double[][] slice0;
        double[][] slice1;
        private final DensityFunction noiseFiller;
        private double noise000;
        private double noise001;
        private double noise100;
        private double noise101;
        private double noise010;
        private double noise011;
        private double noise110;
        private double noise111;
        private double valueXZ00;
        private double valueXZ10;
        private double valueXZ01;
        private double valueXZ11;
        private double valueZ0;
        private double valueZ1;
        private double value;

        NoiseInterpolator(DensityFunction noiseFilter) {
            this.noiseFiller = noiseFilter;
            this.slice0 = this.allocateSlice(WRNoiseChunk.this.cellCountY, WRNoiseChunk.this.cellCountXZ);
            this.slice1 = this.allocateSlice(WRNoiseChunk.this.cellCountY, WRNoiseChunk.this.cellCountXZ);
            WRNoiseChunk.this.interpolators.add(this);
        }

        private double[][] allocateSlice(int cellCountY, int cellCountXZ) {
            int i = cellCountXZ + 1;
            int j = cellCountY + 1;
            double[][] adouble = new double[i][j];

            for (int k = 0; k < i; k++) {
                adouble[k] = new double[j];
            }

            return adouble;
        }

        void selectCellYZ(int y, int z) {
            this.noise000 = this.slice0[z][y];
            this.noise001 = this.slice0[z + 1][y];
            this.noise100 = this.slice1[z][y];
            this.noise101 = this.slice1[z + 1][y];
            this.noise010 = this.slice0[z][y + 1];
            this.noise011 = this.slice0[z + 1][y + 1];
            this.noise110 = this.slice1[z][y + 1];
            this.noise111 = this.slice1[z + 1][y + 1];
        }

        void updateForY(double y) {
            this.valueXZ00 = Mth.lerp(y, this.noise000, this.noise010);
            this.valueXZ10 = Mth.lerp(y, this.noise100, this.noise110);
            this.valueXZ01 = Mth.lerp(y, this.noise001, this.noise011);
            this.valueXZ11 = Mth.lerp(y, this.noise101, this.noise111);
        }

        void updateForX(double x) {
            this.valueZ0 = Mth.lerp(x, this.valueXZ00, this.valueXZ10);
            this.valueZ1 = Mth.lerp(x, this.valueXZ01, this.valueXZ11);
        }

        void updateForZ(double z) {
            this.value = Mth.lerp(z, this.valueZ0, this.valueZ1);
        }

        @Override
        public double compute(DensityFunction.FunctionContext p_209347_) {
            if (p_209347_ != WRNoiseChunk.this) {
                return this.noiseFiller.compute(p_209347_);
            } else if (!WRNoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            } else {
                return WRNoiseChunk.this.fillingCell
                        ? Mth.lerp3(
                        (double)WRNoiseChunk.this.inCellX / WRNoiseChunk.this.cellWidth,
                        (double)WRNoiseChunk.this.inCellY / WRNoiseChunk.this.cellHeight,
                        (double)WRNoiseChunk.this.inCellZ / WRNoiseChunk.this.cellWidth,
                        this.noise000,
                        this.noise100,
                        this.noise010,
                        this.noise110,
                        this.noise001,
                        this.noise101,
                        this.noise011,
                        this.noise111
                )
                        : this.value;
            }
        }

        @Override
        public void fillArray(double[] p_209349_, DensityFunction.ContextProvider p_209350_) {
            if (WRNoiseChunk.this.fillingCell) {
                p_209350_.fillAllDirectly(p_209349_, this);
            } else {
                this.wrapped().fillArray(p_209349_, p_209350_);
            }
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        private void swapSlices() {
            double[][] adouble = this.slice0;
            this.slice0 = this.slice1;
            this.slice1 = adouble;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.Interpolated;
        }
    }
}
