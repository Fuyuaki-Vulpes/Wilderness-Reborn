package com.fuyuaki.wilderness_reborn.data.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
    }
    private static void registerBiomeNoises(
            BootstrapContext<NormalNoise.NoiseParameters> context,
            int firstOctave,
            ResourceKey<NormalNoise.NoiseParameters> temperature,
            ResourceKey<NormalNoise.NoiseParameters> vegetation,
            ResourceKey<NormalNoise.NoiseParameters> continentalness,
            ResourceKey<NormalNoise.NoiseParameters> erosion
    ) {
//        register(context, temperature, -10 + firstOctave, 1.5, 0.0, 1.0, 0.0, 0.0, 0.0);
//        register(context, vegetation, -8 + firstOctave, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0);
//        register(context, continentalness, -9 + firstOctave, 1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
//        register(context, erosion, -9 + firstOctave, 1.0, 1.0, 0.0, 1.0, 1.0);
    }

    private static void register(
            BootstrapContext<NormalNoise.NoiseParameters> context,
            ResourceKey<NormalNoise.NoiseParameters> key,
            int firstOctave,
            double amplitude,
            double... otherAmplitudes
    ) {
        context.register(key, new NormalNoise.NoiseParameters(firstOctave, amplitude, otherAmplitudes));
    }
}
