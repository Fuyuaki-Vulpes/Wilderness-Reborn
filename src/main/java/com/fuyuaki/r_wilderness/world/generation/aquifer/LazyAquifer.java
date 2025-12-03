package com.fuyuaki.r_wilderness.world.generation.aquifer;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.Nullable;

public class LazyAquifer implements Aquifer {

    private final TerrainParameters parameters;

    public LazyAquifer(TerrainParameters parameters) {
        this.parameters = parameters;
    }

    @Nullable
    @Override
    public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
        TerrainParameters.Sampled sampled = this.parameters.samplerAtCached(context.blockX(), context.blockZ());
        double yLevel = this.parameters.yLevelAtWithCache(context.blockX(),context.blockZ());
        if (sampled.continentalness() < 0.0 && context.blockY() < WildernessConstants.SEA_LEVEL && context.blockY() >= yLevel - 8) {
            return Blocks.WATER.defaultBlockState();

        }
        return null;
    }

    @Override
    public boolean shouldScheduleFluidUpdate() {
        return true;
    }
}
