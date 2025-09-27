package com.fuyuaki.r_wilderness.world.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;

public record MutableDensityFunctionContext(BlockPos.MutableBlockPos cursor) implements DensityFunction.FunctionContext
{
    @Override
    public int blockX()
    {
        return cursor.getX();
    }

    @Override
    public int blockY()
    {
        return cursor.getY();
    }

    @Override
    public int blockZ()
    {
        return cursor.getZ();
    }
}