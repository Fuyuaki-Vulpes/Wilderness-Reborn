package com.fuyuaki.r_wilderness.world.level.levelgen;

import net.minecraft.world.level.levelgen.DensityFunction;

public record WildernessNoiseRouter(
        DensityFunction barrierNoise,
        DensityFunction fluidLevelFloodednessNoise,
        DensityFunction fluidLevelSpreadNoise,
        DensityFunction lavaNoise,
        DensityFunction temperature,
        DensityFunction vegetation,
        DensityFunction continents,
        DensityFunction erosion,
        DensityFunction depth,
        DensityFunction ridges,
        DensityFunction initialDensityWithoutJaggedness,
        DensityFunction finalDensity,
        DensityFunction veinToggle,
        DensityFunction veinRidged,
        DensityFunction veinGap
) {
}
