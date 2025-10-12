package com.fuyuaki.r_wilderness.world.level.levelgen.material;

import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

import javax.annotation.Nullable;

public record MaterialRuleFiller(WRNoiseChunk.BlockStateFiller[] materialRuleList) implements WRNoiseChunk.BlockStateFiller {
    @Nullable
    @Override
    public BlockState calculate(DensityFunction.FunctionContext context) {
        for (WRNoiseChunk.BlockStateFiller noisechunk$blockstatefiller : this.materialRuleList) {
            BlockState blockstate = noisechunk$blockstatefiller.calculate(context);
            if (blockstate != null) {
                return blockstate;
            }

        }

        return null;
    }
}