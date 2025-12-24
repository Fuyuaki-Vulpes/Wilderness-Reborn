package com.fuyuaki.r_wilderness.data.worldgen;

import com.fuyuaki.r_wilderness.world.level.levelgen.ModNoises;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {

        register(context, ModNoises.CAVES_NOODLES, -8,5);
        register(context, ModNoises.CAVES_NOODLES_DENSITY, -9,1);
        register(context, ModNoises.CAVES_NOODLES_FILTER, -8,1.35);

        register(context, ModNoises.CAVES_EXOGENES, -7,0.5,1,2);
        register(context, ModNoises.CAVES_ENDOGENES, -9,0.5,1,2,1);

        register(context, ModNoises.CAVES_DENSITY, -8,1.5);

        register(context, ModNoises.CAVES_CRACKS, -6,1,1);
        register(context, ModNoises.CAVES_CRACKS_FREQUENCY, -8,1);
        register(context, ModNoises.AQUIFERS, -8,1.5);
        register(context, ModNoises.SAND_DUNES, -5,2);

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
