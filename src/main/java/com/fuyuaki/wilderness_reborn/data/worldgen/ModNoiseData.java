package com.fuyuaki.wilderness_reborn.data.worldgen;

import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoises;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        register(context, ModNoises.TECTONIC_ACTIVITY, -10,1,0,1,1,1);
        register(context, ModNoises.HILL_CURVATURE, -12,0,2,1,2,2,2);
        register(context, ModNoises.ELEVATION, -11,1,1,1);
        register(context, ModNoises.OVERWORLD_NOISE, -6,1,2,3,1,2,1);
        register(context, ModNoises.OVERWORLD_NOISE_2, -7,1F,4F,1,1,2,1);
        register(context, ModNoises.SPIKES, -4,0.2,1,0,2,1);
        register(context, ModNoises.NOISE_FILTER, -7,0,3,2,2,2);
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
