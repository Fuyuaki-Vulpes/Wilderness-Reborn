package com.fuyuaki.wilderness_reborn.world.level.levelgen.feature;

import com.fuyuaki.wilderness_reborn.world.level.levelgen.feature.configurations.WaterDeltaFeatureConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.Tags;

import java.util.Set;

public class WaterDeltaFeature  extends Feature<WaterDeltaFeatureConfiguration> {

    private static final Direction[] DIRECTIONS = Direction.values();

    public WaterDeltaFeature(Codec<WaterDeltaFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WaterDeltaFeatureConfiguration> context) {
        boolean flag = false;
        RandomSource randomsource = context.random();
        WorldGenLevel worldgenlevel = context.level();
        WaterDeltaFeatureConfiguration configuration = context.config();
        BlockPos origin = context.origin().below();
        int sizeX = configuration.size().sample(randomsource);
        int sizeZ = configuration.size().sample(randomsource);
        int sizeMax = Math.max(sizeX, sizeZ);

        for (BlockPos pos : BlockPos.withinManhattan(origin, sizeX, 0, sizeZ)) {
            if (pos.distManhattan(origin) > sizeMax) {
                break;
            }
            if (!worldgenlevel.getBlockState(pos).isAir()){
                if (validLocation(worldgenlevel,pos)
                ){
                    this.setBlock(worldgenlevel, pos, Blocks.WATER.defaultBlockState());
                    flag = true;
                }

            }
        }

        return flag;
    }


    private static boolean validLocation(WorldGenLevel level, BlockPos pos){
        return !isExposed(level,pos, new BlockPos.MutableBlockPos()) && level.getBlockState(pos.above()).isAir();
    }


    private static boolean isExposed(WorldGenLevel level, BlockPos pos, BlockPos.MutableBlockPos mutablePos) {
        return isExposedDirection(level, pos, mutablePos, Direction.NORTH)
                || isExposedDirection(level, pos, mutablePos, Direction.EAST)
                || isExposedDirection(level, pos, mutablePos, Direction.SOUTH)
                || isExposedDirection(level, pos, mutablePos, Direction.WEST)
                || isExposedDirection(level, pos, mutablePos, Direction.DOWN);
    }

    private static boolean isExposedDirection(WorldGenLevel level, BlockPos pos, BlockPos.MutableBlockPos mutablePos, Direction direction) {
        mutablePos.setWithOffset(pos, direction);
        return !level.getBlockState(mutablePos).isFaceSturdy(level, mutablePos, direction.getOpposite()) && !level.getFluidState(mutablePos).is(Tags.Fluids.WATER);
    }

}
