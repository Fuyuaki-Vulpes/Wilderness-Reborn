package com.fuyuaki.wilderness_reborn.data.worldgen;

import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoises;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        register(context, ModNoises.TERRAIN_A,-9,1,1,1);
        register(context, ModNoises.TERRAIN_B,-9,1,1,1);
        register(context, ModNoises.TERRAIN_PLATEAU_A,-9,5,5);
        register(context, ModNoises.TERRAIN_PLATEAU_B,-9,5,5);
        register(context, ModNoises.TECTONIC_TERRAIN_A,-9,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_B,-9,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_SMOOTH_A,-9,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_SMOOTH_B,-9,1,1);
        register(context, ModNoises.TERRAIN_BLENDER,-6,5,5,10);
        register(context, ModNoises.TERRAIN_PLATEAU_BLENDER,-7,5,10);
        register(context, ModNoises.TERRAIN_BLENDER_SMOOTH,-7,5);
        register(context, ModNoises.TECTONIC_TERRAIN_BLENDER,-7,5,10,10);
        register(context, ModNoises.TECTONIC_TERRAIN_BLENDER_SMOOTH,-8,2,5);
        register(context, ModNoises.TERRAIN_SMOOTHNESS,-6,1,2,1);

        register(context,ModNoises.LAND_NOISE_A,-3,1,1,1);
        register(context,ModNoises.LAND_NOISE_B,-3,1,1,1);
        register(context,ModNoises.LAND_NOISE_C,-3,1,1,1);
        register(context,ModNoises.LAND_NOISE_BLENDER,-5,2);
        register(context,ModNoises.LAND_NOISE_BLENDER_2,-5,2);
        register(context,ModNoises.LAND_NOISE_STRENGHT,-7,2,1,2);

        register(context,ModNoises.LAND_PLATEAU_A,-9,1);
        register(context,ModNoises.LAND_PLATEAU_B,-9,1);
        register(context,ModNoises.LAND_PLATEAU_BLENDER,-6,5);
        register(context,ModNoises.LAND_VALLEYS,-7,1,1,1);

        register(context, ModNoises.LAND_CONTINENTS,-10,2,2,1,1);
        register(context, ModNoises.LAND_EROSION, -9,1,1,2,2);

        register(context, ModNoises.GEO_TECTONICS, -11,3,2,2,1);

        register(context, ModNoises.TECTONIC_RANDOMNESS, -9,1,2);
        register(context, ModNoises.TECTONIC_DIRECTION, -10,1,2);
        register(context, ModNoises.TECTONIC_FACTOR_ACTIVITY, -10,1,2,2);



        register(context, ModNoises.CAVES_NOODLES, -8,5);
        register(context, ModNoises.CAVES_NOODLES_DENSITY, -9,1);
        register(context, ModNoises.CAVES_NOODLES_FILTER, -8,1.35);


        register(context, ModNoises.CAVES_PILLAR, -7,1);
        register(context, ModNoises.CAVES_PILLAR_DENSITY, -8,1);
        register(context, ModNoises.CAVES_PILLAR_RARITY, -8,1);

        register(context, ModNoises.CAVES_SECONDARY_PILLAR, -7,1);
        register(context, ModNoises.CAVES_SECONDARY_PILLAR_DENSITY, -8,1);
        register(context, ModNoises.CAVES_SECONDARY_PILLAR_RARITY, -8,1);

        register(context, ModNoises.CAVES_EXOGENES, -7,0.5,1,2);
        register(context, ModNoises.CAVES_ENDOGENES, -9,0.5,1,2,1);

        register(context, ModNoises.CAVES_DENSITY, -9,1.5);

        register(context, ModNoises.CAVES_CRACKS, -6,1,1);
        register(context, ModNoises.CAVES_CRACKS_FREQUENCY, -8,1);

        register(context, ModNoises.BIOME_VARIATION, -9,1,3,1);

        register(context, ModNoises.TEMPERATURE, -10,1.2,0,1);
        register(context, ModNoises.VEGETATION, -9,1, 0.7);

        register(context, ModNoises.AQUIFER_LAVA, -1,1);
        register(context, ModNoises.AQUIFER_FLOOD, -7,1);
        register(context, ModNoises.AQUIFER_SPREAD, -5,1);
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
