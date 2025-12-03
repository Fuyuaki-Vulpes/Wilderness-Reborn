package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
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

public class WRSurfaceSystemUtil {

    public static void buildSurface(

            RandomState randomState,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            WorldGenerationContext context,
            final ChunkAccess chunk,
            WRNoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource
    ) {
        SurfaceSystem surfaceSystem = randomState.surfaceSystem();
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        final ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        BlockColumn blockcolumn = new BlockColumn() {
            @Override
            public BlockState getBlock(int p_190006_) {
                return chunk.getBlockState(blockpos$mutableblockpos.setY(p_190006_));
            }

            @Override
            public void setBlock(int p_190008_, BlockState p_190009_) {
                LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
                if (levelheightaccessor.isInsideBuildHeight(p_190008_)) {
                    chunk.setBlockState(blockpos$mutableblockpos.setY(p_190008_), p_190009_);
                    if (!p_190009_.getFluidState().isEmpty()) {
                        chunk.markPosForPostprocessing(blockpos$mutableblockpos);
                    }
                }
            }

            @Override
            public String toString() {
                return "ChunkBlockColumn " + chunkpos;
            }
        };
        SurfaceRules.Context surfacerules$context = new SurfaceRules.Context(surfaceSystem, randomState, chunk, null, biomeManager::getBiome, biomes, context);
        ((SurfaceRulesContextExtension) (Object) surfacerules$context).setNoiseChunk(noiseChunk);
        SurfaceRules.SurfaceRule surfacerules$surfacerule = ruleSource.apply(surfacerules$context);
        BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

        for (int k = 0; k < 16; k++) {
            for (int l = 0; l < 16; l++) {
                int i1 = i + k;
                int j1 = j + l;
                int k1 = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
                blockpos$mutableblockpos.setX(i1).setZ(j1);
                Holder<Biome> holder = biomeManager.getBiome(blockpos$mutableblockpos1.set(i1, k1, j1));
                if (holder.is(Biomes.ERODED_BADLANDS)) {
                    surfaceSystem.erodedBadlandsExtension(blockcolumn, i1, j1, k1, chunk);
                }

                int l1 = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l) + 1;
                surfacerules$context.updateXZ(i1, j1);
                int i2 = 0;
                int j2 = Integer.MIN_VALUE;
                int k2 = Integer.MAX_VALUE;
                int l2 = chunk.getMinY();

                for (int i3 = l1; i3 >= l2; i3--) {
                    BlockState blockstate = blockcolumn.getBlock(i3);
                    if (blockstate.isAir()) {
                        i2 = 0;
                        j2 = Integer.MIN_VALUE;
                    } else if (!blockstate.getFluidState().isEmpty()) {
                        if (j2 == Integer.MIN_VALUE) {
                            j2 = i3 + 1;
                        }
                    } else {
                        if (k2 >= i3) {
                            k2 = DimensionType.WAY_BELOW_MIN_Y;

                            for (int j3 = i3 - 1; j3 >= l2 - 1; j3--) {
                                BlockState blockstate1 = blockcolumn.getBlock(j3);
                                if (!surfaceSystem.isStone(blockstate1)) {
                                    k2 = j3 + 1;
                                    break;
                                }
                            }
                        }

                        i2++;
                        int k3 = i3 - k2 + 1;
                        surfacerules$context.updateY(i2, k3, j2, i1, i3, j1);
                        if (blockstate == surfaceSystem.defaultBlock) {
                            BlockState blockstate2 = surfacerules$surfacerule.tryApply(i1, i3, j1);
                            if (blockstate2 != null) {
                                blockcolumn.setBlock(i3, blockstate2);
                            }
                        }
                    }
                }

                if (holder.is(Biomes.FROZEN_OCEAN) || holder.is(Biomes.DEEP_FROZEN_OCEAN)) {
                    surfaceSystem.frozenOceanExtension(surfacerules$context.getMinSurfaceLevel(), holder.value(), blockcolumn, blockpos$mutableblockpos1, i1, j1, k1);
                }
            }
        }
    }

}
