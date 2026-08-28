package com.fuyuaki.r_wilderness.world.level.levelgen.chunk;

import com.electronwill.nightconfig.core.io.Utils;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.util.RUtil;
import com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.WaterFlowData;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPhase;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildGeneratorSettings;
import com.fuyuaki.r_wilderness.world.level.levelgen.hydrology.RAquifer;
import com.fuyuaki.r_wilderness.world.level.levelgen.terrain.TerrainParameters;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildOreVeins;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static net.minecraft.world.level.block.Blocks.WATER;

public class WRNoiseChunk implements DensityFunction.ContextProvider, DensityFunction.FunctionContext {

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
    private final Long2IntMap preliminarySurfaceLevelCache = new Long2IntOpenHashMap();
    private final Long2IntMap preliminaryTerrainLevelCache = new Long2IntOpenHashMap();

    protected final Blender blender;
    protected final WRNoiseChunk.FlatCache blendAlpha;
    protected final WRNoiseChunk.FlatCache blendOffset;
    private final TerrainParameters terrainParameters;
    private final WildGeneratorSettings generator;
    private final Aquifer aquifer;
    protected long lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
    protected Blender.BlendingOutput lastBlendingOutput = new Blender.BlendingOutput(1.0, 0.0);
    protected Double[][] heightsAtCells;

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
    private final RebornBiomeSource biomeSource;

    public static WRNoiseChunk forChunk(
            TerrainParameters parameters,
            ChunkAccess chunk,
            RandomState state,
            DensityFunctions.BeardifierOrMarker beardifierOrMarker,
            WildGeneratorSettings noiseGeneratorSettings,
            Blender blender,
            int seaLevel,
            RebornBiomeSource biomeSource, final Aquifer.FluidPicker globalFluidPicker
    ) {
        NoiseSettings noisesettings = noiseGeneratorSettings.noiseSettings().clampToHeightAccessor(chunk);
        ChunkPos chunkpos = chunk.getPos();
        int i = 16 / noisesettings.getCellWidth();
        return new WRNoiseChunk(parameters, i, state, chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(),(ProtoChunk) chunk, noisesettings, seaLevel,beardifierOrMarker, blender, noiseGeneratorSettings, biomeSource,globalFluidPicker);
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
            WildGeneratorSettings generator,
            RebornBiomeSource biomeSource, final Aquifer.FluidPicker globalFluidPicker){
        this.terrainParameters = parameters;
        this.biomeSource = biomeSource;
        this.noiseSettings = noiseSettings;
        this.cellWidth = noiseSettings.getCellWidth();
        this.cellHeight = noiseSettings.getCellHeight();
        this.cellCountXZ = cellCountXZ;
        this.heightsAtCells = new Double[cellCountXZ+2][cellCountXZ+2];
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
        this.generator = generator;
        if (chunk != null) {
            this.chunk = chunk;
            this.chunkMinX = chunk.getPos().getMinBlockX();
            this.chunkMinZ = chunk.getPos().getMinBlockZ();
            this.airCarvingMask = chunk.getOrCreateCarvingMask();
        }else {
            int x = SectionPos.blockToSectionCoord(firstNoiseX);
            int z = SectionPos.blockToSectionCoord(firstNoiseZ);

            this.chunk = null;
            this.chunkMinX = x;
            this.chunkMinZ = z;
            this.airCarvingMask = new CarvingMask(0,0);
        }
        if (!blendifier.isEmpty()) {
            for (int qX = 0; qX <= this.noiseSizeXZ; qX++) {
                int gX = this.firstNoiseX + qX;
                int x = QuartPos.toBlock(gX);

                for (int qZ = 0; qZ <= this.noiseSizeXZ; qZ++) {
                    int gZ = this.firstNoiseZ + qZ;
                    int z = QuartPos.toBlock(gZ);
                    Blender.BlendingOutput blender$blendingoutput = blendifier.blendOffsetAndFactor(x, z);
                    this.blendAlpha.values[qX + qZ * this.blendAlpha.sizeXZ] = blender$blendingoutput.alpha();
                    this.blendOffset.values[qX + qZ * this.blendOffset.sizeXZ ] = blender$blendingoutput.blendingOffset();
                }
            }
        } else {
            Arrays.fill(this.blendAlpha.values, 1.0D);
            Arrays.fill(this.blendOffset.values, 0.0);
        }
        if (this.chunk != null) {
            this.aquifer = new RAquifer(this, this.chunk.getPos(), random.aquiferRandom(), WildernessConstants.WORLD_BOTTOM, WildernessConstants.WORLD_HEIGHT, globalFluidPicker);
        }
        else{
            this.aquifer = new RAquifer(this, new ChunkPos(0,0), random.aquiferRandom(), WildernessConstants.WORLD_BOTTOM, WildernessConstants.WORLD_HEIGHT, globalFluidPicker);

        }
        NoiseRouter noiserouter = random.router();
        NoiseRouter wrappedRouter = noiserouter.mapAll(this::wrap);
        this.seaLevel = seaLevel;
        this.veinFiller = WildOreVeins.create(wrappedRouter.veinToggle(), wrappedRouter.veinRidged(), wrappedRouter.veinGap(), random.oreRandom());
    }


    public ChunkAccess buildNoise(ChunkAccess chunk, int minCellY, BlockState defaultBlock) {
        RegionMemory regionMemory = RegionMemory.getInstance(Minecraft.getInstance().getSingleplayerServer().overworld());
        while (!regionMemory.readyForChunkReading(chunk.getPos().getMiddleBlockPosition(0))){
            try{
                Thread.sleep(10L);
            } catch (Exception e) {
                LogUtils.getLogger().warn("Sleep failed for chunk {}",chunk, e);
            }
        }
        WorldRegion region = regionMemory.getRegionAt(chunk.getPos().getMiddleBlockPosition(0));

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

                        /*
                        int ca = Math.floorMod(regionMemory.nearestPlate(x, z).getId(), 1024);
                        int[] cp = new int[]{0, 1, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
                        int cOf = Math.floorDiv(ca, 64);
                        int c = Math.floorMod(ca + cp[cOf], 64);
                        BlockState tectState;
                        if (c < 16) {
                            tectState = Blocks.CONCRETE.pick(DyeColor.byId((c) % DyeColor.values().length)).defaultBlockState();
                        } else if (c < 32) {
                            tectState = Blocks.DYED_TERRACOTTA.pick(DyeColor.byId((c + 3) % DyeColor.values().length)).defaultBlockState();
                        } else if (c < 48) {
                            tectState = Blocks.GLAZED_TERRACOTTA.pick(DyeColor.byId((c + 5) % DyeColor.values().length)).defaultBlockState();
                        } else {
                            tectState = Blocks.WOOL.pick(DyeColor.byId((c + 7) % DyeColor.values().length)).defaultBlockState();
                        }*/
                        float heightAt = region.layers().getElevation().getUpsampled(x, z,regionMemory);

                        float riverness = region.layers().getRivers().getUpsampled(x, z, regionMemory);
                        boolean riverWater = riverness > 0.15f;

                        WaterFlowData data  = RegionFeatureGenerator.makeChunkFlowRiverData(x,z,region.getPos(),region,regionMemory);
                        float waterHeight;
                        Vec2 waterFlow;

                        waterHeight = data.waterLevel();
                        waterFlow = data.flow();

                        int yLevelGen = Math.round(heightAt);
                        int waterElevation = Mth.floor(waterHeight);
                        BlockState waterBlock = RUtil.pickWaterBlockStateFor(riverWater,waterFlow);

                        for (int cellY = cellCountY - 1; cellY >= 0; cellY--) {
                            this.selectCellYZ(cellY, cellZ);

                            for (int cellFractionY = cellHeights - 1; cellFractionY >= 0; cellFractionY--) {
                                int y = (minCellY + cellY) * cellHeights + cellFractionY;
                                int regionalY = y & 15;
                                int cellIndex = chunk.getSectionIndex(y);
                                if (chunkSectionsCount != cellIndex) {
                                    chunkSectionsCount = cellIndex;
                                    levelchunksection = chunk.getSection(cellIndex);
                                }

                                BlockState finalState = getBlockStateForY(defaultBlock,
                                        cellFractionY,
                                        cellHeights, y, yLevelGen,
                                        x, z,waterElevation, waterBlock);


                                if (finalState != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
                                    levelchunksection.setBlockState(regionalX, regionalY, regionalZ, finalState, false);
                                    oceanFloor.update(regionalX, y, regionalZ, finalState);
                                    worldSurface.update(regionalX, y, regionalZ, finalState);
                                    if ((aquifer.shouldScheduleFluidUpdate() || waterElevation > seaLevel) && !finalState.getFluidState().isEmpty()) {
                                        chunk.markPosForPostProcessing(new BlockPos(x,y,z));
                                    }
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
        /*
        double t = this.terrainParameters.samplerAt(chunkMinX + 8, chunkMinZ + 8).tectonicActivity();
        boolean tectonicFlag = t > 0;





        if (!tectonicFlag) {
            for (int cellX = -1; cellX < cellCountX + 1; cellX++) {
                int x = this.chunkMinX + cellX * cellWidth;
                for (int cellZ = -1; cellZ < cellCountZ + 1; cellZ++) {
                    int cX = cellX + 1;
                    int cZ = cellZ + 1;
                    int z = this.chunkMinZ + cellZ * cellWidth;
                    if (this.heightsAtCells[cX][cZ] == null) {
                                    double y = this.terrainParameters.yLevelAt(x, z, false);
                                    this.heightsAtCells[cX][cZ] = y;


                    }
                }
            }
        }

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

                        int yLevelGen;
                        if (tectonicFlag){
                            yLevelGen = Mth.floor(this.terrainParameters.yLevelAt(x,z,false));

                        }else {
                            yLevelGen = Mth.floor(blendYLevelsFor(cellX + 1, cellZ + 1, cellFractionX, cellFractionZ));

                        }

                        for (int cellY = cellCountY - 1; cellY >= 0; cellY--) {
                            this.selectCellYZ(cellY, cellZ);

                            for (int cellFractionY = cellHeights - 1; cellFractionY >= 0; cellFractionY--) {
                                int y = (minCellY + cellY) * cellHeights + cellFractionY;
                                int regionalY = y & 15;
                                int cellIndex = chunk.getSectionIndex(y);
                                if (chunkSectionsCount != cellIndex) {
                                    chunkSectionsCount = cellIndex;
                                    levelchunksection = chunk.getSection(cellIndex);
                                }

                                BlockState finalState = getBlockStateForY(defaultBlock,
                                        cellFractionY,
                                        cellHeights, y, yLevelGen,
                                        x, z);
                                


                                if (finalState != Blocks.AIR.defaultBlockState() && !SharedConstants.debugVoidTerrain(chunk.getPos())) {
                                    levelchunksection.setBlockState(regionalX, regionalY, regionalZ, finalState, false);
                                    oceanFloor.update(regionalX, y, regionalZ, finalState);
                                    worldSurface.update(regionalX, y, regionalZ, finalState);
                                    if (aquifer.shouldScheduleFluidUpdate() && !finalState.getFluidState().isEmpty()) {
                                        chunk.markPosForPostProcessing(new BlockPos(x,y,z));
                                    }
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
                 */
    }

    private double blendYLevelsFor(int cellX, int cellZ, int cellFractionX, int cellFractionZ) {
        if (cellFractionX == 0 && cellFractionZ == 0){
            return this.heightsAtCells[cellX][cellZ];
        }
        double y1 = this.heightsAtCells[cellX][cellZ];
        double y2 = this.heightsAtCells[cellX+1][cellZ];
        double y3 = this.heightsAtCells[cellX][cellZ+1];
        double y4 = this.heightsAtCells[cellX+1][cellZ+1];

        double delta1 = (double) cellFractionX / (double) this.cellWidth;
        double delta2 = (double) cellFractionZ / (double) this.cellWidth;

        return Mth.lerp2(delta1,delta2,y1,y2,y3,y4);

    }


    private BlockState getBlockStateForY(BlockState defaultBlock,
                                         int cellFractionY,
                                         int cellHeight, int y, int yLevelGen, int x, int z, int waterHeight, BlockState waterBlock) {
        if (defaultBlock == null) defaultBlock = Blocks.STONE.defaultBlockState();


        DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(x,y,z);

        double cellY1 = (double) cellFractionY / cellHeight;
        this.updateForY(y, cellY1);

        BlockState finalState;
        BlockState worldState;
        BlockState veinState =
                veinFiller
                        .calculate(context);

        boolean caveAir = false;
        if (y <= yLevelGen) {
            double caveDensity = 1.0;
            if (caveDensity < 0.0f){
                caveAir = true;
                worldState = Blocks.AIR.defaultBlockState();
            }else{
                worldState = veinState == null ? defaultBlock : veinState;
            }

        }else {
            worldState = Blocks.AIR.defaultBlockState();
        }

        if (worldState.isAir()){
            double substance = -0.15;
            if (y > this.preliminarySurfaceLevel(x,z)){
                substance = -0.5;
            }
//            worldState = this.aquifer.computeSubstance(context,substance);

            if (y <= waterHeight && y >= yLevelGen){
                worldState =  waterBlock;
            }

        }else{
//            this.aquifer.computeSubstance(context,substance(y,yLevelGen));
        }

        finalState = worldState;
        if (worldState != WATER.defaultBlockState()) {
            double t = 0;
            double c = 0;
            for (int xO = -1; xO < 1; xO++){
                for (int zO = -1; zO < 1; zO++){
                    double v = this.beardifier.compute(
                            new DensityFunction.SinglePointContext(x + xO, y, z + zO)
                    );
                    if (Math.abs(v) > 0.025){
                        t += v;
                        c++;
                    }
                }
            }
            t = t/c;
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

    private double substance(int y, int yLevelGen) {
        double threshold = yLevelGen - 16;
        double substance = 0;
        if (y > threshold){
            substance = Math.max((double) (yLevelGen - y)/16.0D,-0.458);
        }else{
            substance = Math.max(1 - Math.min((threshold - y) / 16.0D,1),0.250);
        }
        return substance;
    }

    public int maxPreliminarySurfaceLevel(final int minBlockX, final int minBlockZ, final int maxBlockX, final int maxBlockZ) {
        int maxY = Integer.MIN_VALUE;

        for(int blockZ = minBlockZ; blockZ <= maxBlockZ; blockZ += 4) {
            for(int blockX = minBlockX; blockX <= maxBlockX; blockX += 4) {
                int surfaceLevel = this.preliminarySurfaceLevel(blockX, blockZ);
                if (surfaceLevel > maxY) {
                    maxY = surfaceLevel;
                }
            }
        }

        return maxY;
    }

    public int preliminarySurfaceLevel(final int sampleX, final int sampleZ) {
        int quantizedX = QuartPos.toBlock(QuartPos.fromBlock(sampleX));
        int quantizedZ = QuartPos.toBlock(QuartPos.fromBlock(sampleZ));
        return this.preliminarySurfaceLevelCache.computeIfAbsent(ColumnPos.asLong(quantizedX, quantizedZ), this::computePreliminarySurfaceLevel);
    }

    private int computePreliminarySurfaceLevel(final long key) {
        int blockX = ColumnPos.getX(key);
        int blockZ = ColumnPos.getZ(key);
        return Mth.floor(getSurfaceYSurface(blockX,blockZ));
    }


    public int maxPreliminaryTerrainLevel(final int minBlockX, final int minBlockZ, final int maxBlockX, final int maxBlockZ) {
        int maxY = Integer.MIN_VALUE;

        for(int blockZ = minBlockZ; blockZ <= maxBlockZ; blockZ += 4) {
            for(int blockX = minBlockX; blockX <= maxBlockX; blockX += 4) {
                int surfaceLevel = this.preliminaryTerrainLevel(blockX, blockZ);
                if (surfaceLevel > maxY) {
                    maxY = surfaceLevel;
                }
            }
        }

        return maxY;
    }

    public int preliminaryTerrainLevel(final int sampleX, final int sampleZ) {
        int quantizedX = QuartPos.toBlock(QuartPos.fromBlock(sampleX));
        int quantizedZ = QuartPos.toBlock(QuartPos.fromBlock(sampleZ));
        return this.preliminaryTerrainLevelCache.computeIfAbsent(ColumnPos.asLong(quantizedX, quantizedZ), this::computeTerrainSurfaceLevel);
    }

    private int computeTerrainSurfaceLevel(final long key) {
        int blockX = ColumnPos.getX(key);
        int blockZ = ColumnPos.getZ(key);
        return Mth.floor(getSurfaceY(blockX,blockZ));
    }

    public BlockState testBlockAt(int x, int y, int z, BlockState defaultBlock) {
        if (defaultBlock == null) defaultBlock = Blocks.STONE.defaultBlockState();
        int yLevelGen = Mth.floor(this.terrainParameters.yLevelAt(x, z));


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
        if (worldState.isAir()){
//            worldState = this.aquifer.computeSubstance(new DensityFunction.SinglePointContext(x,y,z),-0.15);

        }else{
//            this.aquifer.computeSubstance(new DensityFunction.SinglePointContext(x,y,z),substance(y,yLevelGen));
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
        long i = ChunkPos.pack(chunkX, chunkZ);
        if (this.lastBlendingDataPos == i) {
            return this.lastBlendingOutput;
        } else {
            this.lastBlendingDataPos = i;
            Blender.BlendingOutput blender$blendingoutput = this.blender.blendOffsetAndFactor(chunkX, chunkZ);
            this.lastBlendingOutput = blender$blendingoutput;
            return blender$blendingoutput;
        }
    }
    public DensityFunction wrap(DensityFunction densityFunction) {
        return this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
    }

    private DensityFunction wrapNew(DensityFunction function) {
        return switch (function) {
            case DensityFunctions.Marker(DensityFunctions.Marker.Type type, DensityFunction wrapped) -> {
                switch (type) {
                    case Interpolated:
                        yield new NoiseInterpolator(wrapped);
                    case FlatCache:
                        yield new FlatCache(wrapped, true);
                    case Cache2D:
                        yield new Cache2D(wrapped);
                    case CacheOnce:
                        yield new CacheOnce(wrapped);
                    case CacheAllInCell:
                        yield new CacheAllInCell(wrapped);
                    case BlendDensity:
                        yield !this.blender.isEmpty() ? new BlendDensity(wrapped) : wrapped;
                    default:
                        throw new MatchException(null, null);
                }
            }
            case DensityFunctions.HolderHolder(Holder<DensityFunction> holder) -> (DensityFunction)holder.value();
            default -> function == DensityFunctions.BlendAlpha.INSTANCE && this.blendAlpha != null
                    ? this.blendAlpha
                    : (
                    function == DensityFunctions.BlendOffset.INSTANCE && this.blendOffset != null
                    ? this.blendOffset
                    : (function == DensityFunctions.BeardifierMarker.INSTANCE ? this.beardifier : function)
            );
        };
    }

    public int getSurfaceY(int x, int z) {
        return Mth.floor(this.terrainParameters.yLevelAt(x,z,false));
    }
    public int getSurfaceYSurface(int x, int z) {
        return Mth.floor(this.terrainParameters.yLevelAt(x,z,true));
    }

    public TerrainParameters getTerrainParameters() {
        return this.terrainParameters;
    }

    public RebornBiomeSource biomeSource() {
        return this.biomeSource;
    }


    private class BlendAlpha implements NoiseChunkDensityFunction {
        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendAlpha.INSTANCE;
        }

        @Override
        public DensityFunction mapChildren(DensityFunction.Visitor visitor) {
            return visitor.apply(this.wrapped());
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return WRNoiseChunk.this.getOrComputeBlendingOutput(context.blockX(), context.blockZ()).alpha();
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
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

    private class BlendDensity implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private final DensityFunction input;

        private BlendDensity(DensityFunction input) {
            this.input = input;
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return WRNoiseChunk.this.blender.blendDensity(context, this.input.compute(context));
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            this.input.fillArray(output, contextProvider);

            for (int i = 0; i < output.length; i++) {
                DensityFunction.FunctionContext context = contextProvider.forIndex(i);
                output[i] = WRNoiseChunk.this.blender.blendDensity(context, output[i]);
            }
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
        public DensityFunction wrapped() {
            return this.input;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.BlendDensity;
        }
    }

    private class BlendOffset implements NoiseChunkDensityFunction {
        @Override
        public DensityFunction wrapped() {
            return DensityFunctions.BlendOffset.INSTANCE;
        }

        @Override
        public DensityFunction mapChildren(DensityFunction.Visitor visitor) {
            return visitor.apply(this.wrapped());
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return WRNoiseChunk.this.getOrComputeBlendingOutput(context.blockX(), context.blockZ()).blendingOffset();
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
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

    @FunctionalInterface
    public interface BlockStateFiller {
        @Nullable BlockState calculate(final DensityFunction.FunctionContext context);
    }

    private static class Cache2D implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private final DensityFunction function;
        private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
        private double lastValue;

        private Cache2D(DensityFunction function) {
            this.function = function;
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            int blockX = context.blockX();
            int blockZ = context.blockZ();
            long pos2D = ChunkPos.pack(blockX, blockZ);
            if (this.lastPos2D == pos2D) {
                return this.lastValue;
            }

            this.lastPos2D = pos2D;
            double value = this.function.compute(context);
            this.lastValue = value;
            return value;
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            this.function.fillArray(output, contextProvider);
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

    private class CacheAllInCell implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private final DensityFunction noiseFiller;
        private final double[] values;

        private CacheAllInCell(DensityFunction noiseFiller) {
            this.noiseFiller = noiseFiller;
            this.values = new double[WRNoiseChunk.this.cellWidth * WRNoiseChunk.this.cellWidth * WRNoiseChunk.this.cellHeight];
            WRNoiseChunk.this.cellCaches.add(this);
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            if (context != WRNoiseChunk.this) {
                return this.noiseFiller.compute(context);
            }

            if (!WRNoiseChunk.this.interpolating) {
                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
            }

            int x = WRNoiseChunk.this.inCellX;
            int y = WRNoiseChunk.this.inCellY;
            int z = WRNoiseChunk.this.inCellZ;
            return x >= 0 && y >= 0 && z >= 0 && x < WRNoiseChunk.this.cellWidth && y < WRNoiseChunk.this.cellHeight && z < WRNoiseChunk.this.cellWidth
                    ? this.values[((WRNoiseChunk.this.cellHeight - 1 - y) * WRNoiseChunk.this.cellWidth + x) * WRNoiseChunk.this.cellWidth + z]
                    : this.noiseFiller.compute(context);
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
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

    private class CacheOnce implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private final DensityFunction function;
        private long lastCounter;
        private long lastArrayCounter;
        private double lastValue;
        private double @Nullable [] lastArray;

        private CacheOnce(DensityFunction function) {
            this.function = function;
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            if (context != WRNoiseChunk.this) {
                return this.function.compute(context);
            }

            if (this.lastArray != null && this.lastArrayCounter == WRNoiseChunk.this.arrayInterpolationCounter) {
                return this.lastArray[WRNoiseChunk.this.arrayIndex];
            }

            if (this.lastCounter == WRNoiseChunk.this.interpolationCounter) {
                return this.lastValue;
            }

            this.lastCounter = WRNoiseChunk.this.interpolationCounter;
            double value = this.function.compute(context);
            this.lastValue = value;
            return value;
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            if (this.lastArray != null && this.lastArrayCounter == WRNoiseChunk.this.arrayInterpolationCounter) {
                System.arraycopy(this.lastArray, 0, output, 0, output.length);
            } else {
                this.wrapped().fillArray(output, contextProvider);
                if (this.lastArray != null && this.lastArray.length == output.length) {
                    System.arraycopy(output, 0, this.lastArray, 0, output.length);
                } else {
                    this.lastArray = (double[])output.clone();
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

    private class FlatCache implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private final DensityFunction noiseFiller;
        private final double[] values;
        private final int sizeXZ;

        private FlatCache(DensityFunction noiseFiller, boolean fill) {
            this.noiseFiller = noiseFiller;
            this.sizeXZ = WRNoiseChunk.this.noiseSizeXZ + 1;
            this.values = new double[this.sizeXZ * this.sizeXZ];
            if (fill) {
                for (int x = 0; x <= WRNoiseChunk.this.noiseSizeXZ; x++) {
                    int quartX = WRNoiseChunk.this.firstNoiseX + x;
                    int blockX = QuartPos.toBlock(quartX);

                    for (int z = 0; z <= WRNoiseChunk.this.noiseSizeXZ; z++) {
                        int quartZ = WRNoiseChunk.this.firstNoiseZ + z;
                        int blockZ = QuartPos.toBlock(quartZ);
                        this.values[x + z * this.sizeXZ] = noiseFiller.compute(new DensityFunction.SinglePointContext(blockX, 0, blockZ));
                    }
                }
            }
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            int quartX = QuartPos.fromBlock(context.blockX());
            int quartZ = QuartPos.fromBlock(context.blockZ());
            int x = quartX - WRNoiseChunk.this.firstNoiseX;
            int z = quartZ - WRNoiseChunk.this.firstNoiseZ;
            return x >= 0 && z >= 0 && x < this.sizeXZ && z < this.sizeXZ ? this.values[x + z * this.sizeXZ] : this.noiseFiller.compute(context);
        }

        @Override
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
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

    private interface NoiseChunkDensityFunction extends DensityFunction {
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

    public class NoiseInterpolator implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
        private double[][] slice0;
        private double[][] slice1;
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

        private NoiseInterpolator(DensityFunction noiseFiller) {
            this.noiseFiller = noiseFiller;
            this.slice0 = this.allocateSlice(WRNoiseChunk.this.cellCountY, WRNoiseChunk.this.cellCountXZ);
            this.slice1 = this.allocateSlice(WRNoiseChunk.this.cellCountY, WRNoiseChunk.this.cellCountXZ);
            WRNoiseChunk.this.interpolators.add(this);
        }

        private double[][] allocateSlice(int cellCountY, int cellCountZ) {
            int sizeZ = cellCountZ + 1;
            int sizeY = cellCountY + 1;
            double[][] result = new double[sizeZ][sizeY];

            for (int cellZIndex = 0; cellZIndex < sizeZ; cellZIndex++) {
                result[cellZIndex] = new double[sizeY];
            }

            return result;
        }

        private void selectCellYZ(int cellYIndex, int cellZIndex) {
            this.noise000 = this.slice0[cellZIndex][cellYIndex];
            this.noise001 = this.slice0[cellZIndex + 1][cellYIndex];
            this.noise100 = this.slice1[cellZIndex][cellYIndex];
            this.noise101 = this.slice1[cellZIndex + 1][cellYIndex];
            this.noise010 = this.slice0[cellZIndex][cellYIndex + 1];
            this.noise011 = this.slice0[cellZIndex + 1][cellYIndex + 1];
            this.noise110 = this.slice1[cellZIndex][cellYIndex + 1];
            this.noise111 = this.slice1[cellZIndex + 1][cellYIndex + 1];
        }

        private void updateForY(double factorY) {
            this.valueXZ00 = Mth.lerp(factorY, this.noise000, this.noise010);
            this.valueXZ10 = Mth.lerp(factorY, this.noise100, this.noise110);
            this.valueXZ01 = Mth.lerp(factorY, this.noise001, this.noise011);
            this.valueXZ11 = Mth.lerp(factorY, this.noise101, this.noise111);
        }

        private void updateForX(double factorX) {
            this.valueZ0 = Mth.lerp(factorX, this.valueXZ00, this.valueXZ10);
            this.valueZ1 = Mth.lerp(factorX, this.valueXZ01, this.valueXZ11);
        }

        private void updateForZ(double factorZ) {
            this.value = Mth.lerp(factorZ, this.valueZ0, this.valueZ1);
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            if (context != WRNoiseChunk.this) {
                return this.noiseFiller.compute(context);
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
        public void fillArray(double[] output, DensityFunction.ContextProvider contextProvider) {
            if (WRNoiseChunk.this.fillingCell) {
                contextProvider.fillAllDirectly(output, this);
            } else {
                this.wrapped().fillArray(output, contextProvider);
            }
        }

        @Override
        public DensityFunction wrapped() {
            return this.noiseFiller;
        }

        private void swapSlices() {
            double[][] tmp = this.slice0;
            this.slice0 = this.slice1;
            this.slice1 = tmp;
        }

        @Override
        public DensityFunctions.Marker.Type type() {
            return DensityFunctions.Marker.Type.Interpolated;
        }
    }

}
