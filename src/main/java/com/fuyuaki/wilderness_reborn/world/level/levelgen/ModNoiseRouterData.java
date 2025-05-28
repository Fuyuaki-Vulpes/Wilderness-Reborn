package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseRouterData {

    public static final ResourceKey<DensityFunction> TECTONIC_ACTIVITY = createKey("tectonic_activity");
    public static final ResourceKey<DensityFunction> HILL_CURVATURE = createKey("hill_curvature");
    public static final ResourceKey<DensityFunction> ELEVATION = createKey("elevation");
    public static final ResourceKey<DensityFunction> SPIKES = createKey("spikes");
    public static final ResourceKey<DensityFunction> OVERWORLD_NOISE = createKey("overworld_noise");
    public static final ResourceKey<DensityFunction> OVERWORLD_NOISE_2 = createKey("overworld_noise_2");
    public static final ResourceKey<DensityFunction> NOISE_FILTER = createKey("noise_filter");


    public static Holder<? extends DensityFunction> bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        DensityFunction shiftX = getFunction(densityLookup,PackNoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(densityLookup,PackNoiseRouterData.SHIFT_Z);

        context.register(HILL_CURVATURE,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.HILL_CURVATURE
                        )
                )
        );
        context.register(ELEVATION,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.ELEVATION
                        )
                )
        );
        context.register(SPIKES,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.SPIKES
                        )
                )
        );
        context.register(OVERWORLD_NOISE,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.OVERWORLD_NOISE
                        )
                )
        );
        context.register(OVERWORLD_NOISE_2,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.OVERWORLD_NOISE_2
                        )
                )
        );
        context.register(NOISE_FILTER,
                DensityFunctions.shiftedNoise2d(
                        shiftX,
                        shiftZ,
                        0.25,
                        noiseLookup.getOrThrow(
                                ModNoises.NOISE_FILTER
                        )
                )
        );
        return context.register(TECTONIC_ACTIVITY,DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.TECTONIC_ACTIVITY)));

    }
    private static DensityFunction registerAndWrap(
            BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction value
    ) {
        return new DensityFunctions.HolderHolder(context.register(key, value));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctionRegistry, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctionRegistry.getOrThrow(key));
    }
    private static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction densityfunction = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction), DensityFunctions.constant(0.64)).squeeze();
    }
    protected static NoiseRouter none() {
        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );
    }

    private static DensityFunction splineWithBlending(DensityFunction minFunction, DensityFunction maxFunction) {
        DensityFunction densityfunction = DensityFunctions.lerp(DensityFunctions.blendAlpha(), maxFunction, minFunction);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityfunction));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction minFunction, DensityFunction maxFunction) {
        DensityFunction densityfunction = DensityFunctions.mul(maxFunction, minFunction);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), densityfunction.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction whenInRange, int minY, int maxY, int whenOutOfRange) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, minY, maxY + 1, whenInRange, DensityFunctions.constant(whenOutOfRange)));
    }

    private static DensityFunction slide(
            DensityFunction input, int minY, int height, int topStartOffset, int topEndOffset, double topDelta, int bottomStartOffset, int bottomEndOffset, double bottomDelta
    ) {
        DensityFunction densityfunction1 = DensityFunctions.yClampedGradient(minY + height - topStartOffset, minY + height - topEndOffset, 1.0, 0.0);
        DensityFunction $$9 = DensityFunctions.lerp(densityfunction1, topDelta, input);
        DensityFunction densityfunction2 = DensityFunctions.yClampedGradient(minY + bottomStartOffset, minY + bottomEndOffset, 0.0, 1.0);
        return DensityFunctions.lerp(densityfunction2, bottomDelta, $$9);
    }


    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, WildernessRebornMod.mod(location));
    }

    public static DensityFunction shiftedNoise3d(
            DensityFunction shiftX, DensityFunction shiftZ, double xzScale,double yScale, Holder<NormalNoise.NoiseParameters> noiseData
    ) {
        return new DensityFunctions.ShiftedNoise(shiftX, DensityFunctions.zero(), shiftZ, xzScale, yScale, new DensityFunction.NoiseHolder(noiseData));
    }

}
