package com.fuyuaki.r_wilderness.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SnowAndFreezeFeature.class)
public abstract class SnowAndFreezeFeatureMixin implements Feature {


    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        BlockPos.MutableBlockPos topPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos worldSurfaceMutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos worldSurfaceBelow = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int k = origin.getX() + i;
                int l = origin.getZ() + j;
                int heightMotionBlocking = level.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
                int heightWorldSurface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l);
                topPos.set(k, heightMotionBlocking, l);
                worldSurfaceMutable.set(k, heightWorldSurface, l);
                belowPos.set(topPos).move(Direction.DOWN, 1);
                worldSurfaceBelow.set(worldSurfaceMutable).move(Direction.DOWN, 1);
                Biome biome = level.getBiome(topPos).value();
                if (biome.shouldFreeze(level, belowPos, false)) {
                    level.setBlock(belowPos, Blocks.ICE.defaultBlockState(), 2);
                }if (biome.shouldFreeze(level, worldSurfaceBelow, false)) {
                    level.setBlock(worldSurfaceBelow, Blocks.ICE.defaultBlockState(), 2);
                }

                if (biome.shouldSnow(level, topPos)) {
                    level.setBlock(topPos, Blocks.SNOW.defaultBlockState(), 2);
                    BlockState belowState = level.getBlockState(belowPos);
                    if (belowState.hasProperty(SnowyBlock.SNOWY)) {
                        level.setBlock(belowPos, belowState.setValue(SnowyBlock.SNOWY, true), 2);
                    }
                }

                if (biome.shouldSnow(level, worldSurfaceMutable)) {
                    level.setBlock(worldSurfaceMutable, Blocks.SNOW.defaultBlockState(), 2);
                    BlockState blockstate = level.getBlockState(worldSurfaceBelow);
                    if (blockstate.hasProperty(SnowyBlock.SNOWY)) {
                        level.setBlock(worldSurfaceBelow, blockstate.setValue(SnowyBlock.SNOWY, true), 2);
                    }
                }
            }
        }

        return true;
    }
}
