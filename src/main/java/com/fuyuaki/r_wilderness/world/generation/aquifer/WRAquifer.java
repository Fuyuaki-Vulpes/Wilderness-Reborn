package com.fuyuaki.r_wilderness.world.generation.aquifer;

import com.fuyuaki.r_wilderness.util.ModUtil;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.neoforged.neoforge.common.Tags;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class WRAquifer implements Aquifer {

    private static final int X_RANGE = 10;
    private static final int Y_RANGE = 9;
    private static final int Z_RANGE = 10;
    private static final int X_SEPARATION = 6;
    private static final int Y_SEPARATION = 3;
    private static final int Z_SEPARATION = 6;
    private static final int X_SPACING = 16;
    private static final int Y_SPACING = 12;
    private static final int Z_SPACING = 16;
    private static final int X_SPACING_SHIFT = 4;
    private static final int Z_SPACING_SHIFT = 4;
    private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
    private static final int SAMPLE_OFFSET_X = -5;
    private static final int SAMPLE_OFFSET_Y = 1;
    private static final int SAMPLE_OFFSET_Z = -5;
    private static final int MIN_CELL_SAMPLE_X = 0;
    private static final int MIN_CELL_SAMPLE_Y = -1;
    private static final int MIN_CELL_SAMPLE_Z = 0;
    private static final int MAX_CELL_SAMPLE_X = 1;
    private static final int MAX_CELL_SAMPLE_Y = 1;
    private static final int MAX_CELL_SAMPLE_Z = 1;

    private static final int GRID_WIDTH = 16;
    private static final int GRID_HEIGHT = 12;

    private static final int XZ_RANGE = 10;

    private static final double FLOWING_UPDATE_SIMILARITY = similarity(Mth.square(10), Mth.square(11));
    private final PerlinNoise fluidLevelFloodednessNoise;
    private final PerlinNoise fluidLevelSpreadNoise;
    private final PerlinNoise lavaNoise;
    private final PerlinNoise barrierNoise;

    private static double similarity(int firstDistance, int secondDistance)
    {
        return 1.0 - Math.abs(secondDistance - firstDistance) / 25.0;
    }

    private final int minGridX, minGridY, minGridZ;
    private final int gridSizeX, gridSizeZ;
    private final int minY;
    private final PositionalRandomFactory fork;


    private final FluidStatus lavaLevelAquifer;
    private final FluidStatus seaLevelAquifer;
    private final FluidPicker globalBasePicker;
    private final FluidStatus[] aquiferCache;
    private final WRNoiseChunk chunk;
    private final long[] aquiferLocations;

    private boolean shouldScheduleFluidUpdate;
    private int maxSampledY;

    private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{
            {0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
    };

    public WRAquifer(WRNoiseChunk chunk, ChunkPos chunkPos, int seaLevel, PositionalRandomFactory fork, TerrainParameters parameters) {
        this.chunk = chunk;

        int maxGridX = 0;
        int maxGridY = 0;
        int maxGridZ = 0;

        this.minGridX = 0;
        this.minGridY = 0;
        this.minGridZ = 0;

        this.gridSizeX = maxGridX - minGridX + 1;
        int gridSizeY = maxGridY - minGridY + 1;
        this.gridSizeZ = maxGridZ - minGridZ + 1;

        this.minY = 0;

        this.fork = fork;
//        this.fluidLevelFloodednessNoise = parameters.aquifers.floodedness();
//        this.fluidLevelSpreadNoise = parameters.aquifers.spread();
//        this.lavaNoise = parameters.aquifers.lava();
//        this.barrierNoise = parameters.aquifers.barrier();

        this.fluidLevelFloodednessNoise = null;
        this.fluidLevelSpreadNoise = null;
        this.lavaNoise = null;
        this.barrierNoise = null;


        this.lavaLevelAquifer = new FluidStatus(minY + 32,Blocks.LAVA.defaultBlockState());
        this.seaLevelAquifer = new FluidStatus(seaLevel, Blocks.WATER.defaultBlockState() );

        this.aquiferCache = new FluidStatus[gridSizeX * gridSizeY * gridSizeZ];
        this.aquiferLocations = new long[gridSizeX * gridSizeY * gridSizeZ];

        Arrays.fill(aquiferLocations, Long.MAX_VALUE);

        this.globalBasePicker = (x, y, z) -> y < minY + 32 ? this.lavaLevelAquifer : this.seaLevelAquifer;

        int yLimit = this.adjustSurfaceLevel(
                Math.max(chunk.getSurfaceY(fromGridX(this.minGridX, 0), fromGridZ(this.minGridZ, 0))
                , chunk.getSurfaceY(fromGridX(maxGridX, 9), fromGridZ(maxGridZ, 9)))
        );
        int yLimitToGrid = gridY(yLimit + 12) + 1;
        this.maxSampledY = fromGridY(yLimitToGrid, 11) - 1;
    }

    @Nullable
    public BlockState computeSubstance(DensityFunction.FunctionContext context, double baseNoise)
    {
        // Only used directly by carvers, where it passes in baseNoise = 0, modifiedNoise = 0
        return sampleState(context.blockX(), context.blockY(), context.blockZ(),Mth.clamp(baseNoise,-1,1));
    }


    protected static int gridX(int x) {
        return x >> 4;
    }

    private static int fromGridX(int gridX, int offset) {
        return (gridX << 4) + offset;
    }

    protected static int gridY(int y) {
        return Math.floorDiv(y, 12);
    }

    private static int fromGridY(int gridY, int offset) {
        return gridY * 12 + offset;
    }

    protected static int gridZ(int z) {
        return z >> 4;
    }

    private static int fromGridZ(int gridZ, int offset) {
        return (gridZ << 4) + offset;
    }


    @Nullable
    public BlockState sampleState(int x, int y, int z, double baseNoise) {
        BlockState defaultState = Blocks.GLOWSTONE.defaultBlockState();
        if (baseNoise > 0.0) {
            this.shouldScheduleFluidUpdate = false;
            return null;
        }
        if (y > this.maxSampledY) {
            this.shouldScheduleFluidUpdate = false;
        } else {
            FluidStatus global = this.globalBasePicker.computeFluid(x, y, z);

            double aquiferNoiseContribution; // The contribution from aquifer borders to the noise
            BlockState state = null; // The result aquifer state
            boolean isSurfaceLevelAquifer; // If the aquifer is a surface/sea level one, which needs to be affected by the water type

            if (ModUtil.isBlock(global.at(y), Blocks.LAVA)) {
                // Always lava below lava level, and don't generate adjacent borders.
                state = Blocks.LAVA.defaultBlockState();
                aquiferNoiseContribution = 0;
                isSurfaceLevelAquifer = false;
                shouldScheduleFluidUpdate = false;
            } else {
                int lowerGridX = Math.floorDiv(x - XZ_RANGE / 2, GRID_WIDTH);
                int lowerGridY = Math.floorDiv(y, GRID_HEIGHT);
                int lowerGridZ = Math.floorDiv(z - XZ_RANGE / 2, GRID_WIDTH);

                // The closest three aquifers, by distance
                int distance1 = Integer.MAX_VALUE, distance2 = Integer.MAX_VALUE, distance3 = Integer.MAX_VALUE;
                long aquifer1 = 0L, aquifer2 = 0L, aquifer3 = 0L;

                // Iterate nearby aquifers
                for (int offsetGridX = 0; offsetGridX <= 1; ++offsetGridX) {
                    for (int offsetGridY = -1; offsetGridY <= 1; ++offsetGridY) {
                        for (int offsetGridZ = 0; offsetGridZ <= 1; ++offsetGridZ) {
                            int adjGridX = lowerGridX + offsetGridX;
                            int adjGridY = lowerGridY + offsetGridY;
                            int adjGridZ = lowerGridZ + offsetGridZ;
                            int adjIndex = getIndex(adjGridX, adjGridY, adjGridZ);

                            long adjAquifer = aquiferLocations[adjIndex];
                            if (adjAquifer == Long.MAX_VALUE) {
                                // Compute and cache the aquifer location at this index
                                RandomSource random = fork.at(adjGridX, adjGridY, adjGridZ);
                                adjAquifer = BlockPos.asLong(
                                        adjGridX * GRID_WIDTH + random.nextInt(XZ_RANGE) + (GRID_WIDTH - XZ_RANGE) / 2,
                                        adjGridY * GRID_HEIGHT + random.nextInt(Y_RANGE) + (GRID_HEIGHT - Y_RANGE) / 2,
                                        adjGridZ * GRID_WIDTH + random.nextInt(XZ_RANGE) + (GRID_WIDTH - XZ_RANGE) / 2);
                                this.aquiferLocations[adjIndex] = adjAquifer;
                            }

                            int dx = BlockPos.getX(adjAquifer) - x;
                            int dy = BlockPos.getY(adjAquifer) - y;
                            int dz = BlockPos.getZ(adjAquifer) - z;
                            int distance = dx * dx + dy * dy + dz * dz;

                            // Update the closest three aquifers
                            if (distance <= distance1) {
                                aquifer3 = aquifer2;
                                aquifer2 = aquifer1;
                                aquifer1 = adjAquifer;

                                distance3 = distance2;
                                distance2 = distance1;
                                distance1 = distance;
                            } else if (distance <= distance2) {
                                aquifer3 = aquifer2;
                                aquifer2 = adjAquifer;

                                distance3 = distance2;
                                distance2 = distance;
                            } else if (distance <= distance3) {
                                aquifer3 = adjAquifer;
                                distance3 = distance;
                            }
                        }
                    }
                }

                // The status of the closest three aquifers (1, 2, 3 in order)
                FluidStatus entry1 = getOrCreateAquifer(aquifer1);
                FluidStatus entry2 = getOrCreateAquifer(aquifer2);
                FluidStatus entry3 = getOrCreateAquifer(aquifer3);


                // Similarity between each pair of aquifers
                double similarity12 = similarity(distance1, distance2);
                double similarity13 = similarity(distance1, distance3);
                double similarity23 = similarity(distance2, distance3);

                BlockState aq1Block = entry1.at(y);
                BlockState fluidBlock = WildernessConstants.DEBUG_GEN ? debug(aq1Block) : aq1Block;

                if (similarity12 <= 0.0) {
                    if (similarity12 >= FLOWING_UPDATE_SIMILARITY) {
                        aquiferNoiseContribution = !entry1.equals(entry2) ? 1 : 0;
                        this.shouldScheduleFluidUpdate = !entry1.equals(entry2);
                    } else {
                        aquiferNoiseContribution = 0;
                        this.shouldScheduleFluidUpdate = false;
                    }
                    state = fluidBlock;
                }else if (aq1Block.is(Blocks.WATER) && global.at(y).is(Blocks.LAVA)) {
                    // Border lava and water with solid blocks.
                    aquiferNoiseContribution = 1;
                    this.shouldScheduleFluidUpdate = true;
                    state = fluidBlock;
                }else {


                    final MutableDouble barrierNoise = new MutableDouble(Double.NaN);

                    // Pressure between each pair of aquifers
                    double pressure12 = calculatePressure(x, y, z, barrierNoise, entry1, entry2);
                    double pressure13 = calculatePressure(x, y, z, barrierNoise, entry1, entry3);
                    double pressure23 = calculatePressure(x, y, z, barrierNoise, entry2, entry3);

                    double d2 = similarity12 * pressure12;
                    if (d2 > 0.0){
                        aquiferNoiseContribution = 0;
                        this.shouldScheduleFluidUpdate = false;
                        state = fluidBlock;

                    }else{
                        if (similarity13 > 0.0){
                            double d3 = similarity12 * similarity13 * pressure13;
                            if (d3 > 0.0){
                                aquiferNoiseContribution = 0;
                                this.shouldScheduleFluidUpdate = false;
                                state = defaultState;
                            }else{
                                aquiferNoiseContribution = 1;
                                this.shouldScheduleFluidUpdate = true;
                                state = fluidBlock;
                            }
                        }
                        else if (similarity23 > 0.0){
                            double d3 = similarity12 * similarity23 * pressure23;
                            if (d3 > 0.0) {
                                aquiferNoiseContribution = 0;
                                this.shouldScheduleFluidUpdate = false;
                                state = defaultState;
                            }else{
                                aquiferNoiseContribution = 1;
                                this.shouldScheduleFluidUpdate = true;
                                state = fluidBlock;
                            }
                        }else {


                            boolean flag2 = !entry1.equals(entry2);
                            boolean flag1 = similarity13 >= FLOWING_UPDATE_SIMILARITY && !entry1.equals(entry3);
                            boolean flag = similarity23 >= FLOWING_UPDATE_SIMILARITY && !entry2.equals(entry3);

                            if (!flag2 && !flag && !flag1) {
                                aquiferNoiseContribution = 0;
                                this.shouldScheduleFluidUpdate = false;
                            } else {

                                aquiferNoiseContribution = 1;
                                this.shouldScheduleFluidUpdate = true;
                            }
                        }
                    }

                }

                isSurfaceLevelAquifer = entry1.fluidLevel() == WildChunkGenerator.SEA_LEVEL_Y;
            }
            if (aquiferNoiseContribution <= 0) {
                if (isSurfaceLevelAquifer) {
//                    state = baseBlockSource.modifyFluid(state, x, z);
                }
                return state;
            }
        }
        return null;
    }

    private BlockState debug(BlockState aq1Block) {
        if (aq1Block.isAir()){
            return aq1Block;
        }
        if (aq1Block.getFluidState().is(Tags.Fluids.WATER)) return Blocks.BLUE_STAINED_GLASS.defaultBlockState();
        if (aq1Block.getFluidState().is(Tags.Fluids.LAVA)) return Blocks.RED_STAINED_GLASS.defaultBlockState();
        return aq1Block;
    }


    public boolean shouldScheduleFluidUpdate()
    {
        return shouldScheduleFluidUpdate;
    }

    private double calculatePressure(
            int x, int y, int z, MutableDouble barrierNoise, Aquifer.FluidStatus firstFluid, Aquifer.FluidStatus secondFluid
    ) {
        BlockState blockstate = firstFluid.at(y);
        BlockState blockstate1 = secondFluid.at(y);
        if ((!blockstate.is(Blocks.LAVA) || !blockstate1.is(Blocks.WATER)) && (!blockstate.is(Blocks.WATER) || !blockstate1.is(Blocks.LAVA))) {
            int fluidDifference = Math.abs(firstFluid.fluidLevel() - secondFluid.fluidLevel());
            if (fluidDifference == 0) {
                return 0.0;
            } else {
                double d0 = 0.5 * (firstFluid.fluidLevel() + secondFluid.fluidLevel());
                double d1 = y + 0.5 - d0;
                double fluidDifMid = fluidDifference / 2.0;
                double d3 = 0.0;
                double d4 = 2.5;
                double d5 = 1.5;
                double d6 = 3.0;
                double d7 = 10.0;
                double d8 = 3.0;
                double d9 = fluidDifMid - Math.abs(d1);
                double d10;
                if (d1 > d3) {
                    double d11 = d3 + d9;
                    if (d11 > d3) {
                        d10 = d11 / d5;
                    } else {
                        d10 = d11 / d4;
                    }
                } else {
                    double d15 = d8 + d9;
                    if (d15 > d3) {
                        d10 = d15 / d6;
                    } else {
                        d10 = d15 / d7;
                    }
                }

                double d16 = 2.0;
                double d12;
                if (!(d10 < -d16) && !(d10 > d16)) {
                    double d13 = barrierNoise.getValue();
                    if (Double.isNaN(d13)) {
                        double d14 = this.barrierNoise.getValue(x,y,z);
                        barrierNoise.setValue(d14);
                        d12 = d14;
                    } else {
                        d12 = d13;
                    }
                } else {
                    d12 = 0.0;
                }

                return d16 * (d12 + d10);
            }
        } else {
            return 2.0;
        }
    }

    private FluidStatus getOrCreateAquifer(long location)
    {
        // Queries the cache first, and generates if the cache isn't found
        final int x = BlockPos.getX(location);
        final int y = BlockPos.getY(location);
        final int z = BlockPos.getZ(location);
        final int gridIndex = getIndex(gridX(x), gridY(y), gridZ(z));

        FluidStatus status = aquiferCache[gridIndex];
        if (status == null)
        {
            status = createAquifer(x, y, z);
            this.aquiferCache[gridIndex] = status;
        }
        return status;
    }

    private FluidStatus createAquifer(int x, int y, int z)
    {

        FluidStatus globalAquifer = this.globalBasePicker.computeFluid(x, y, z);
        int maxSurfaceLevel = Integer.MAX_VALUE;
        int maxY = y + 12;
        int minY = y - 12;
        boolean flag = false;
        
        for (int[] aint : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
            int sectionX = x + SectionPos.sectionToBlockCoord(aint[0]);
            int sectionZ = z + SectionPos.sectionToBlockCoord(aint[1]);
            int surfaceY = this.chunk.getSurfaceY(sectionX, sectionZ);
            int surfaceAdjusted = this.adjustSurfaceLevel(surfaceY);
            boolean flag1 = aint[0] == 0 && aint[1] == 0;
            if (flag1 && minY > surfaceAdjusted) {
                return globalAquifer;
            }

            boolean flag2 = maxY > surfaceAdjusted;
            if (flag2 || flag1) {
                FluidStatus globalStatus = this.globalBasePicker.computeFluid(sectionX, surfaceAdjusted, sectionZ);
                if (!globalStatus.at(surfaceAdjusted).isAir()) {
                    if (flag1) {
                        flag = true;
                    }

                    if (flag2) {
                        return globalStatus;
                    }
                }
            }

            maxSurfaceLevel = Math.min(maxSurfaceLevel, surfaceY);
        }
        
        int surfaceLevel = this.computeSurfaceLevel(x, y, z, globalAquifer, maxSurfaceLevel, flag);
        return new FluidStatus(surfaceLevel, this.computeFluidType(x, y, z, globalAquifer, surfaceLevel));
    }


    private int adjustSurfaceLevel(int level) {
        return level + 8;
    }


    protected int getIndex(int x, int y, int z) {
        final int dx = x - minGridX;
        final int dy = y - minGridY;
        final int dz = z - minGridZ;
        return (dy * gridSizeZ + dz) * gridSizeX + dx;
    }
    private int computeSurfaceLevel(int x, int y, int z, FluidStatus globalFluidStatus, int maxSurfaceLevel, boolean fluidPresent) {
        double d0;
        double d1;
//        if (OverworldBiomeBuilder.isDeepDarkRegion(this.erosion, this.depth, densityfunction$singlepointcontext)) {
//            d0 = -1.0;
//            d1 = -1.0;
//        } else {
            int heightAbove = maxSurfaceLevel + 8 - y;
            double d2 = fluidPresent ? Mth.clampedMap(heightAbove, 0.0, 64.0, 1.0, 0.0) : 0.0;
            double floodedness = Mth.clamp(this.fluidLevelFloodednessNoise.getValue(x, y, z), -1.0, 1.0);
            double d4 = Mth.map(d2, 1.0, 0.0, -0.3, 0.8);
            double d5 = Mth.map(d2, 1.0, 0.0, -0.8, 0.4);
            d0 = floodedness - d5;
            d1 = floodedness - d4;
//        }

        int surfaceLevel;
        if (d1 > 0.0) {
            surfaceLevel = globalFluidStatus.fluidLevel();
        } else if (d0 > 0.0) {
            surfaceLevel = this.computeRandomizedFluidSurfaceLevel(x, y, z, maxSurfaceLevel);
        } else {
            surfaceLevel = DimensionType.WAY_BELOW_MIN_Y;
        }

        return surfaceLevel;
    }

    private int computeRandomizedFluidSurfaceLevel(int x, int y, int z, int maxSurfaceLevel) {
        int xzFloor = 16;
        int yFloor = 40;
        int xFloored = Math.floorDiv(x, xzFloor);
        int yFloored = Math.floorDiv(y, yFloor);
        int zFloored = Math.floorDiv(z, xzFloor);
        int j1 = yFloored * 40 + 20;
        int k1 = 10;
        double spread = this.fluidLevelSpreadNoise.getValue(xFloored, yFloored, zFloored) * k1;
        int l1 = Mth.quantize(spread, 3);
        int i2 = j1 + l1;
        return Math.min(maxSurfaceLevel, i2);
    }

    private BlockState computeFluidType(int x, int y, int z, FluidStatus fluidStatus, int surfaceLevel) {
        BlockState blockstate = fluidStatus.fluidType();
        if (surfaceLevel <= 16 && surfaceLevel != DimensionType.WAY_BELOW_MIN_Y && fluidStatus.fluidType() != Blocks.LAVA.defaultBlockState()) {
            int xzFloor = 64;
            int yFloor = 40;
            int fX = Math.floorDiv(x, xzFloor);
            int fY = Math.floorDiv(y, yFloor);
            int fZ = Math.floorDiv(z, xzFloor);
            double lava = this.lavaNoise.getValue(fX, fY, fZ);
            if (Math.abs(lava) > 0.3) {
                blockstate = Blocks.LAVA.defaultBlockState();
            }
        }

        return blockstate;
    }
}

