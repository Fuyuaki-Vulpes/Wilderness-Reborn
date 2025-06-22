package com.fuyuaki.wilderness_reborn.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SharpIceSpikesFeature extends Feature<NoneFeatureConfiguration> {
    public SharpIceSpikesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        BlockPos blockpos = featurePlaceContext.origin();
        RandomSource randomsource = featurePlaceContext.random();
        WorldGenLevel worldgenlevel = featurePlaceContext.level();

        while (worldgenlevel.isEmptyBlock(blockpos) && blockpos.getY() > worldgenlevel.getMinY() + 2) {
            blockpos = blockpos.below();
        }

            blockpos = blockpos.above(randomsource.nextInt(3));

            int randomY = randomsource.nextInt(3) + 7;

            int j = randomY / 4 + randomsource.nextInt(2);

            if (j > 1 && randomsource.nextInt(60) == 0) {

                blockpos = blockpos.above(10 + randomsource.nextInt(10));
            }

            for (int yOff = 0; yOff < randomY; yOff++) {

                float f = (1.0F - (float)yOff / randomY) * j;

                int yCeil = Mth.ceil(f);

                for (int xOff = -yCeil; xOff <= yCeil; xOff++) {

                    float xAbs = Mth.abs(xOff) - 0.25F;

                    for (int zOff = -yCeil; zOff <= yCeil; zOff++) {

                        float zAbs = Mth.abs(zOff) - 0.25F;

                        if ((xOff == 0 && zOff == 0 || !(xAbs * xAbs + zAbs * zAbs > f * f))
                                && (xOff != -yCeil && xOff != yCeil && zOff != -yCeil && zOff != yCeil || !(randomsource.nextFloat() > 0.75F))) {

                            BlockState blockstate = worldgenlevel.getBlockState(blockpos.offset(xOff, yOff, zOff));

                            if (blockstate.isAir() || isDirt(blockstate) || blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.ICE)) {

                                this.setBlock(worldgenlevel, blockpos.offset(xOff, yOff, zOff), Blocks.PACKED_ICE.defaultBlockState());
                            }

                            if (yOff != 0 && yCeil > 1) {

                                blockstate = worldgenlevel.getBlockState(blockpos.offset(xOff, -yOff, zOff));

                                if (blockstate.isAir() || isDirt(blockstate)|| isStone(blockstate) || blockstate.is(Blocks.SNOW_BLOCK) || blockstate.is(Blocks.ICE)) {

                                    this.setBlock(worldgenlevel, blockpos.offset(xOff, -yOff, zOff), Blocks.PACKED_ICE.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }

            int offset = j - 2;
            if (offset < 0) {
                offset = 0;
            } else if (offset > 2) {
                offset = 2;
            }

            for (int x = -offset; x <= offset; x++) {
                for (int z = -offset; z <= offset; z++) {
                    BlockPos blockpos1 = blockpos.offset(x, -1, z);
                    int j2 = 10;
                    if (Math.abs(x) == 1 && Math.abs(z) == 1) {
                        j2 = randomsource.nextInt(5);
                    }

                    while (blockpos1.getY() > 50) {
                        BlockState blockstate1 = worldgenlevel.getBlockState(blockpos1);
                        if (!blockstate1.isAir()
                                && !isDirt(blockstate1)
                                && !isStone(blockstate1)
                                && !blockstate1.is(Blocks.SNOW_BLOCK)
                                && !blockstate1.is(Blocks.ICE)
                                && !blockstate1.is(Blocks.PACKED_ICE)) {
                            break;
                        }

                        this.setBlock(worldgenlevel, blockpos1, Blocks.PACKED_ICE.defaultBlockState());
                        blockpos1 = blockpos1.below();
                        if (--j2 <= 0) {
                            blockpos1 = blockpos1.below(randomsource.nextInt(5) + 1);
                            j2 = randomsource.nextInt(5);
                        }
                    }
                }
            }

            return true;

    }
}
