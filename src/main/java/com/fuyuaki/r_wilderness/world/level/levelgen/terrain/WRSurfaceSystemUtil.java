package com.fuyuaki.r_wilderness.world.level.levelgen.terrain;

import com.fuyuaki.r_wilderness.world.level.levelgen.RandomStateExtension;
import com.fuyuaki.r_wilderness.world.level.levelgen.chunk.WRNoiseChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class WRSurfaceSystemUtil {

    public static void buildSurface(
            RandomState randomState,
            BiomeManager biomeManager,
            WorldGenerationContext generationContext,
            ChunkAccess protoChunk,
            WRNoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource,
            @Nullable Set<Holder<Biome>> possibleBiomes
    ) {


        SurfaceSystem surfaceSystem = randomState.surfaceSystem();
        SurfaceSystemExtension extendedSurfaceSystem = ((RandomStateExtension) (Object) randomState).getSurfaceSystemExtension();
        final BlockPos.MutableBlockPos columnPos = new BlockPos.MutableBlockPos();
        final ChunkPos chunkPos = protoChunk.getPos();
        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        BlockColumn column = new BlockColumn() {
            @Override
            public BlockState getBlock(int blockY) {
                return protoChunk.getBlockState(columnPos.setY(blockY));
            }

            @Override
            public void setBlock(int blockY, BlockState state) {
                LevelHeightAccessor heightAccessor = protoChunk.getHeightAccessorForGeneration();
                if (heightAccessor.isInsideBuildHeight(blockY)) {
                    protoChunk.setBlockState(columnPos.setY(blockY), state);
                    if (!state.getFluidState().isEmpty()) {
                        protoChunk.markPosForPostProcessing(columnPos);
                    }
                }
            }

            @Override
            public String toString() {
                return "ChunkBlockColumn " + chunkPos;
            }
        };
        SurfaceRules.Context context = new SurfaceRules.Context(
                surfaceSystem, randomState, protoChunk, null, biomeManager::getBiome, generationContext, possibleBiomes
        );
        ((SurfaceRulesContextExtension) (Object) context).setNoiseChunk(noiseChunk);
        SurfaceRules.SurfaceRule rule = ruleSource.apply(context);
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = minBlockX + x;
                int blockZ = minBlockZ + z;
                int startingHeight = protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
                columnPos.setX(blockX).setZ(blockZ);
                Holder<Biome> surfaceBiome = biomeManager.getBiome(blockPos.set(blockX, startingHeight, blockZ));
                if (surfaceBiome.is(Biomes.ERODED_BADLANDS)) {
                    surfaceSystem.erodedBadlandsExtension(column, blockX, blockZ, startingHeight, protoChunk);
                }

                int height = protoChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
                context.updateXZ(blockX, blockZ);
                int stoneAboveDepth = 0;
                int waterHeight = Integer.MIN_VALUE;
                int nextCeilingStoneY = Integer.MAX_VALUE;
                int endY = protoChunk.getMinY();

                for (int y = height; y >= endY; y--) {
                    BlockState old = column.getBlock(y);
                    if (old.isAir()) {
                        stoneAboveDepth = 0;
                        waterHeight = Integer.MIN_VALUE;
                    } else if (!old.getFluidState().isEmpty()) {
                        if (waterHeight == Integer.MIN_VALUE) {
                            waterHeight = y + 1;
                        }
                    } else {
                        if (nextCeilingStoneY >= y) {
                            nextCeilingStoneY = DimensionType.WAY_BELOW_MIN_Y;

                            for (int lookaheadY = y - 1; lookaheadY >= endY - 1; lookaheadY--) {
                                BlockState nextState = column.getBlock(lookaheadY);
                                if (!surfaceSystem.isStone(nextState)) {
                                    nextCeilingStoneY = lookaheadY + 1;
                                    break;
                                }
                            }
                        }

                        stoneAboveDepth++;
                        int stoneBelowDepth = y - nextCeilingStoneY + 1;
                        context.updateY(stoneAboveDepth, stoneBelowDepth, waterHeight, y);
                        if (old == surfaceSystem.defaultBlock) {
                            BlockState state = rule.tryApply(blockX, y, blockZ);
                            if (state != null) {
                                column.setBlock(y, state);
                            }
                        }
                    }
                }

                if (surfaceBiome.is(Biomes.FROZEN_OCEAN) || surfaceBiome.is(Biomes.DEEP_FROZEN_OCEAN)) {
                    surfaceSystem.frozenOceanExtension(context.getMinSurfaceLevel(), surfaceBiome.value(), column, blockPos, blockX, blockZ, startingHeight);
                }
            }
        }
    }

}
