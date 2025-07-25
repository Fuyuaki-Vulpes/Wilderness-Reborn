package com.fuyuaki.wilderness_reborn.data.pack.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class PackOreVeinifier {
    private static final float VEININESS_THRESHOLD = 0.4F;
    private static final int EDGE_ROUNDOFF_BEGIN = 20;
    private static final double MAX_EDGE_ROUNDOFF = 0.2;
    private static final float VEIN_SOLIDNESS = 0.7F;
    private static final float MIN_RICHNESS = 0.1F;
    private static final float MAX_RICHNESS = 0.3F;
    private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
    private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
    private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;

    private PackOreVeinifier() {
    }

    protected static NoiseChunk.BlockStateFiller create(
            DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, PositionalRandomFactory random
    ) {
        BlockState blockstate = null;
        return p_209666_ -> {
            double d0 = veinToggle.compute(p_209666_);
            int i = p_209666_.blockY();
            PackOreVeinifier.VeinType oreveinifier$veintype = d0 > 0.0 ? PackOreVeinifier.VeinType.COPPER : PackOreVeinifier.VeinType.IRON;
            double d1 = Math.abs(d0);
            int j = oreveinifier$veintype.maxY - i;
            int k = i - oreveinifier$veintype.minY;
            if (k >= 0 && j >= 0) {
                int l = Math.min(j, k);
                double d2 = Mth.clampedMap((double)l, 0.0, 20.0, -0.2, 0.0);
                if (d1 + d2 < 0.4F) {
                    return blockstate;
                } else {
                    RandomSource randomsource = random.at(p_209666_.blockX(), i, p_209666_.blockZ());
                    if (randomsource.nextFloat() > 0.7F) {
                        return blockstate;
                    } else if (veinRidged.compute(p_209666_) >= 0.0) {
                        return blockstate;
                    } else {
                        double d3 = Mth.clampedMap(d1, 0.4F, 0.6F, 0.1F, 0.3F);
                        if (randomsource.nextFloat() < d3 && veinGap.compute(p_209666_) > -0.3F) {
                            return randomsource.nextFloat() < 0.02F ? oreveinifier$veintype.rawOreBlock : oreveinifier$veintype.ore;
                        } else {
                            return oreveinifier$veintype.filler;
                        }
                    }
                }
            } else {
                return blockstate;
            }
        };
    }

    protected static enum VeinType {
        COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
        IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

        final BlockState ore;
        final BlockState rawOreBlock;
        final BlockState filler;
        protected final int minY;
        protected final int maxY;

        private VeinType(BlockState ore, BlockState rawOreBlock, BlockState filler, int minY, int maxY) {
            this.ore = ore;
            this.rawOreBlock = rawOreBlock;
            this.filler = filler;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}
