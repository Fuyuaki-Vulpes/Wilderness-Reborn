package com.fuyuaki.r_wilderness.world.level.levelgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class RebornRouterData {

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> functions, OverworldFunctionSet<ResourceKey<DensityFunction>> functionNames) {
        OverworldFunctionSet<DensityFunction> functionSet = functionNames.map(key -> NoiseRouterData.getFunction(functions, key));
        return new NoiseRouter(
                functionSet.temperature(),
                functionSet.vegetation(),
                functionSet.continents(),
                functionSet.erosion(),
                functionSet.depth(),
                NoiseRouterData.getFunction(functions, NoiseRouterData.RIDGES),
                functionSet.chunkSurfaceLevel(),
                functionSet.finalDensity()
        );
    }

    protected static Aquifer.Config overworldAquifers(
            HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise> noises, OverworldFunctionSet<ResourceKey<DensityFunction>> names
    ) {
        DensityFunction barrierNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction fluidLevelFloodednessNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction fluidLevelSpreadNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction lavaNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction exclusion = OverworldBiomeBuilder.deepDarkRegion(NoiseRouterData.getFunction(functions, names.erosion()), NoiseRouterData.getFunction(functions, names.depth()));
        return new Aquifer.Config(
                barrierNoise, fluidLevelFloodednessNoise, fluidLevelSpreadNoise, lavaNoise, exclusion, NoiseRouterData.getFunction(functions, names.preliminarySurfaceLevel())
        );
    }


}
