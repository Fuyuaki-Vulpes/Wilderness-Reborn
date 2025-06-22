package com.fuyuaki.wilderness_reborn.data.worldgen;

import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoises;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseData {
    public static final NormalNoise.NoiseParameters DEFAULT_SHIFT = new NormalNoise.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0);

    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> context) {
        register(context, ModNoises.TERRAIN_A,-9,1,1,1,1);
        register(context, ModNoises.TERRAIN_B,-9,1,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_A,-9,1,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_B,-9,1,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_SMOOTH_A,-10,1,1,1);
        register(context, ModNoises.TECTONIC_TERRAIN_SMOOTH_B,-10,1,1,1);
        register(context, ModNoises.TERRAIN_BLENDER,-7,5,5,5,5,10,10);
        register(context, ModNoises.TERRAIN_BLENDER_SMOOTH,-7,5,5,5);
        register(context, ModNoises.TECTONIC_TERRAIN_BLENDER,-7,5,5,5,10,10,10);
        register(context, ModNoises.TECTONIC_TERRAIN_BLENDER_SMOOTH,-8,2,5,5,5,7.5,10);
        register(context, ModNoises.TERRAIN_SMOOTHNESS,-6,1,2,1,1);

        register(context,ModNoises.LAND_NOISE_A,-3,1,1,1,1);
        register(context,ModNoises.LAND_NOISE_B,-3,1,1,1,1);
        register(context,ModNoises.LAND_NOISE_C,-3,1,1,1,1);
        register(context,ModNoises.LAND_NOISE_BLENDER,-5,2,1,1,1);
        register(context,ModNoises.LAND_NOISE_BLENDER_2,-5,2,1,1,1);
        register(context,ModNoises.LAND_NOISE_STRENGHT,-7,2,1,2,1);

        register(context, ModNoises.LAND_CONTINENTS,-10,2,2,1,1);
        register(context, ModNoises.LAND_EROSION, -9,1,1,2,2);

        register(context, ModNoises.GEO_TECTONICS, -11,3,2,2,1,1);
        register(context, ModNoises.TECTONIC_RANDOMNESS, -9,1,2);
        register(context, ModNoises.TECTONIC_DIRECTION, -10,1,2);
        register(context, ModNoises.TECTONIC_FACTOR_ACTIVITY, -10,1,2,2,1);



        register(context, ModNoises.ELEVATION, -12,1,1,1,1,1);
        register(context, ModNoises.PLATEAU_VALLEYS, -8,1,2,1,1,2);
        register(context, ModNoises.RIVER_DEPTH, -10,1,1,2,2,2);

        register(context, ModNoises.TECTONIC_ACTIVITY, -10,1,0,1,1);
        register(context, ModNoises.BIOME_VARIATION, -9,1,3,1,3,5);
        register(context, ModNoises.LANDMASS, -11,1.3,0.3,1,0.7,1);
        register(context, ModNoises.PLATES, -12,1,2,1,0,0,1);

        register(context, ModNoises.TEMPERATURE, -10,1.2,0,1,1);
        register(context, ModNoises.VEGETATION, -9,1, 0.7);

        register(context, ModNoises.AQUIFER_LAVA, -4,1, 1,1);
        register(context, ModNoises.AQUIFER_FLOOD, -6,1, 1,1,0);
        register(context, ModNoises.AQUIFER_SPREAD, -4,1, 1);

        register(context, ModNoises.PLATEAU_VALLEY_DEPTH, -10,1,1,1);
        register(context, ModNoises.JAGGEDNESS, -5,0,1,2,3,2);
        register(context, ModNoises.HILLS_AND_MOUNTAINS, -9,1,2,0,4,4,5);
        register(context, ModNoises.MOUNTAIN_ELEVATION_OFFSET, -7,0.6,0.5,1,1,1,1);

        register(context, ModNoises.PLATEAU_MASK, -13,2,1,2,2,2,3);
        register(context, ModNoises.RIVER_MASK, -9,2,3,2,2);

        register(context, ModNoises.NOISE_A, -6,1,1,0,1,2);
        register(context, ModNoises.NOISE_B, -6,1,1,1,0,2);
        register(context, ModNoises.NOISE_C, -6,1,1,1,0,1);

        register(context, ModNoises.CRATERS, -12,2,1,1,2,2,4);
        register(context, ModNoises.RIDGES, -9,1,1);
        register(context, ModNoises.WEATHERING, -10,1,2,2,1);


        register(context, ModNoises.CAVE_ENTRANCES, -6,0.4,0.5,0,1);

        register(context, ModNoises.CAVE_NOODLE, -8,1);
        register(context, ModNoises.CAVE_NOODLE_RIDGE_1, -7,1,1,1);
        register(context, ModNoises.CAVE_NOODLE_RIDGE_2, -7,1,1,1);
        register(context, ModNoises.CAVE_NOODLE_THICKNESS, -8,1);

        register(context, ModNoises.CAVE_SPAGHETTI_3D_1, -7,1,1,1,1);
        register(context, ModNoises.CAVE_SPAGHETTI_3D_2, -7,1,1,1,1);
        register(context, ModNoises.CAVE_SPAGHETTI_3D_ROUGHNESS, -5,1,1,1);
        register(context, ModNoises.CAVE_SPAGHETTI_3D_MODULATOR, -8,1,1,1,0,0,0);
        register(context, ModNoises.CAVE_SPAGHETTI_3D_RARITY, -7,1,1);
        register(context, ModNoises.CAVE_SPAGHETTI_3D_THICKNESS, -7,1,1);

        register(context, ModNoises.CAVE_LAVA_TUBES, -9,2,5,2,1,1,1);
        register(context, ModNoises.CAVE_LAVA_TUBES_RARITY, -8,1,1);

        register(context, ModNoises.CAVE_PITS, -4,1,0);
        register(context, ModNoises.CAVE_PIT_RARITY, -9,1,1,1);

        register(context, ModNoises.CAVE_CAVERNS, -10,1,2,2,2,3,1);
        register(context, ModNoises.CAVE_CAVERN_PILLARS, -7,1,1);
        register(context, ModNoises.CAVE_CAVERN_PILLARS_RARITY, -8,1,1);
        register(context, ModNoises.CAVE_CAVERN_PILLARS_THICKNESS, -8,1,1);

        register(context, ModNoises.CAVE_GROTTO, -7,0.5,0,4,1);
        register(context, ModNoises.CAVE_GROTTO_RARITY, -8,1);

        register(context, ModNoises.CAVE_FRACTURE_1, -8,1,1,1);
        register(context, ModNoises.CAVE_FRACTURE_2, -9,1,2);

        register(context, ModNoises.CAVE_RARITY, -8,1,1);
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
