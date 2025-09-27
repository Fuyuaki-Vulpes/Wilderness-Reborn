package com.fuyuaki.r_wilderness.util.world;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkBaseBlockSource {

    private static int index(int x, int z)
    {
        return (x & 15) | ((z & 15) << 4);
    }

    private final BlockState[] cachedFluidStates;

    private final BlockState freshWater = Blocks.WATER.defaultBlockState(), saltWater = Blocks.WATER.defaultBlockState();

    public ChunkBaseBlockSource()
    {
        this.cachedFluidStates = new BlockState[16 * 16];
    }


    public BlockState modifyFluid(BlockState fluidOrAir, int x, int z)
    {
        if (fluidOrAir == freshWater)
        {
            final int index = index(x, z);
            BlockState state = cachedFluidStates[index];
            if (state == null)
            {
//                state = biomeSampler.get(x, z).isSalty() ? saltWater : freshWater;
                state = freshWater;
                cachedFluidStates[index] = state;
            }
            return state;
        }
        return fluidOrAir;
    }
}
