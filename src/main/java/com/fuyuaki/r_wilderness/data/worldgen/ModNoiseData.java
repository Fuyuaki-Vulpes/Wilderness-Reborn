package com.fuyuaki.r_wilderness.data.worldgen;

import com.fuyuaki.r_wilderness.world.level.levelgen.ModNoises;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        register(context, ModNoises.AQUIFER_BARRIER, -3, 1.0);
        register(context, ModNoises.AQUIFER_FLUID_LEVEL_FLOODEDNESS, -7, 1.0);
        register(context, ModNoises.AQUIFER_LAVA, -1, 1.0);
        register(context, ModNoises.AQUIFER_FLUID_LEVEL_SPREAD, -5, 1.0);

        register(context, ModNoises.CAVES_NOODLES, -7,5);
        register(context, ModNoises.CAVES_NOODLES_DENSITY, -8,1);
        register(context, ModNoises.CAVES_NOODLES_FILTER, -7,1);

        register(context, ModNoises.CAVE_RIDGES, -7,1,2);
        register(context, ModNoises.CAVE_RIDGE_FILTER, -8,1);
        register(context, ModNoises.CAVE_BUBBLES, -8,1,1,2,1);

        register(context, ModNoises.CAVES_DENSITY, -7,1.5);

        register(context, ModNoises.CAVES_CRACKS, -6,1,1);
        register(context, ModNoises.CAVES_CRACKS_FREQUENCY, -7,1);

        register(context, ModNoises.SAND_DUNES, -5,2);
        register(context, ModNoises.SAND_DUNES_LARGE, -6,2);

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
