package com.fuyuaki.r_wilderness.mixin.common;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SnowAndFreezeFeature.class)
public abstract class SnowAndFreezeFeatureMixin extends Feature<NoneFeatureConfiguration> {

    public SnowAndFreezeFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160368_) {
        WorldGenLevel worldgenlevel = p_160368_.level();
        BlockPos blockpos = p_160368_.origin();
        BlockPos.MutableBlockPos motionBlockingMutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos motionBlockingBelow = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos worldSurfaceMutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos worldSurfaceBelow = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int k = blockpos.getX() + i;
                int l = blockpos.getZ() + j;
                int heightMotionBlocking = worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
                int heightWorldSurface = worldgenlevel.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k, l);
                motionBlockingMutable.set(k, heightMotionBlocking, l);
                worldSurfaceMutable.set(k, heightWorldSurface, l);
                motionBlockingBelow.set(motionBlockingMutable).move(Direction.DOWN, 1);
                worldSurfaceBelow.set(worldSurfaceMutable).move(Direction.DOWN, 1);
                Biome biome = worldgenlevel.getBiome(motionBlockingMutable).value();
                if (biome.shouldFreeze(worldgenlevel, motionBlockingBelow, false)) {
                    worldgenlevel.setBlock(motionBlockingBelow, Blocks.ICE.defaultBlockState(), 2);
                }if (biome.shouldFreeze(worldgenlevel, worldSurfaceBelow, false)) {
                    worldgenlevel.setBlock(worldSurfaceBelow, Blocks.ICE.defaultBlockState(), 2);
                }

                if (biome.shouldSnow(worldgenlevel, motionBlockingMutable)) {
                    worldgenlevel.setBlock(motionBlockingMutable, Blocks.SNOW.defaultBlockState(), 2);
                    BlockState blockstate = worldgenlevel.getBlockState(motionBlockingBelow);
                    if (blockstate.hasProperty(SnowyDirtBlock.SNOWY)) {
                        worldgenlevel.setBlock(motionBlockingBelow, blockstate.setValue(SnowyDirtBlock.SNOWY, true), 2);
                    }
                }
                if (biome.shouldSnow(worldgenlevel, worldSurfaceMutable)) {
                    worldgenlevel.setBlock(worldSurfaceMutable, Blocks.SNOW.defaultBlockState(), 2);
                    BlockState blockstate = worldgenlevel.getBlockState(worldSurfaceBelow);
                    if (blockstate.hasProperty(SnowyDirtBlock.SNOWY)) {
                        worldgenlevel.setBlock(worldSurfaceBelow, blockstate.setValue(SnowyDirtBlock.SNOWY, true), 2);
                    }
                }
            }
        }

        return true;
    }
}
