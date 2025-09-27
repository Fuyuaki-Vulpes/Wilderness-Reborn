package com.fuyuaki.r_wilderness.data.pack.levelgen;

import com.fuyuaki.r_wilderness.world.level.levelgen.ModNoiseRouterData;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;

public class PackNoiseRouterData {
    public static final ResourceKey<DensityFunction> Y = createKey("y");
    public static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    public static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");

    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace(location));
    }
    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctionRegistry, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctionRegistry.getOrThrow(key));
    }


    public static NoiseRouter overworld(
            HolderGetter<DensityFunction> densityFunctionRegistry
    ) {
        return new NoiseRouter(
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_BARRIER),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_FLUID_LEVEL_FLOODEDNESS),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_FLUID_LEVEL_SPREAD),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_LAVA_NOISE),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_TEMPERATURE),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_VEGETATION),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_CONTINENTALNESS),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_EROSION),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_DEPTH),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_RIDGES),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_TOPOGRAPHY_FINAL_DENSITY),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_VEIN_TOGGLE),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_VEIN_RIDGED),
                getFunction(densityFunctionRegistry,ModNoiseRouterData.R_VEIN_GAP)
        );
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction whenInRange, int minY, int maxY, int whenOutOfRange) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, minY, maxY + 1, whenInRange, DensityFunctions.constant(whenOutOfRange)));
    }

}

