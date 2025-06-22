package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {


    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_A = createKey("terrain/base_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_B = createKey("terrain/base_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_A = createKey("terrain/tectonic_base_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_B = createKey("terrain/tectonic_base_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_SMOOTH_A = createKey("terrain/tectonic_base_smooth_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_SMOOTH_B = createKey("terrain/tectonic_base_smooth_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_BLENDER = createKey("terrain/blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_BLENDER_SMOOTH = createKey("terrain/blender_smooth");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_BLENDER = createKey("terrain/tectonic_blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_BLENDER_SMOOTH = createKey("terrain/tectonic_blender_smooth");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_SMOOTHNESS = createKey("terrain/smoothness");

    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_EROSION = createKey("land/erosion");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_CONTINENTS = createKey("land/continents");


    public static final ResourceKey<NormalNoise.NoiseParameters> GEO_TECTONICS = createKey("geo/tectonics");

    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_RANDOMNESS = createKey("tectonic_plates/randomness");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_DIRECTION = createKey("tectonic_plates/direction");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_FACTOR_ACTIVITY = createKey("tectonic_plates/activity");


    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_ACTIVITY = createKey("detail/tectonic_activity");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_VALLEY_DEPTH = createKey("details/plateau_valley_depth");
    public static final ResourceKey<NormalNoise.NoiseParameters> JAGGEDNESS = createKey("details/jaggedness");
    public static final ResourceKey<NormalNoise.NoiseParameters> BIOME_VARIATION = createKey("detail/biome_variation");

    //CLIMATE AND ROUTER
    public static final ResourceKey<NormalNoise.NoiseParameters> TEMPERATURE = createKey("climate/temperature");
    public static final ResourceKey<NormalNoise.NoiseParameters> VEGETATION = createKey("climate/vegetation");

    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_LAVA = createKey("aquifers/lava_noise");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_SPREAD = createKey("aquifers/spread");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLOOD = createKey("aquifers/flood");



    public static final ResourceKey<NormalNoise.NoiseParameters> LANDMASS = createKey("elevation/landmass");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATES = createKey("elevation/plates");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_A = createKey("elevation/noise_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_B = createKey("elevation/noise_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_C = createKey("elevation/noise_c");
    public static final ResourceKey<NormalNoise.NoiseParameters> ELEVATION = createKey("elevation/elevation");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIVER_DEPTH = createKey("elevation/river_depth");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_VALLEYS = createKey("elevation/plateau_valleys");
    public static final ResourceKey<NormalNoise.NoiseParameters> HILLS_AND_MOUNTAINS = createKey("elevation/mountains");
    public static final ResourceKey<NormalNoise.NoiseParameters> MOUNTAIN_ELEVATION_OFFSET = createKey("elevation/mountain_elevation_offset");

    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_MASK = createKey("mask/plateaus");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIVER_MASK = createKey("mask/river_mask");

    public static final ResourceKey<NormalNoise.NoiseParameters> CRATERS = createKey("geomorphology/craters");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIDGES = createKey("geomorphology/ridges");
    public static final ResourceKey<NormalNoise.NoiseParameters> WEATHERING = createKey("geomorphology/weathering");

    //CAVES

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_ENTRANCES = createKey("caves/entrances");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_NOODLE = createKey("caves/noodle");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_NOODLE_RIDGE_1 = createKey("caves/noodle_ridge_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_NOODLE_RIDGE_2 = createKey("caves/noodle_ridge_2");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_NOODLE_THICKNESS = createKey("caves/noodle_thickness");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_1 = createKey("caves/spaghetti_3d_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_2 = createKey("caves/spaghetti_3d_2");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_THICKNESS = createKey("caves/spaghetti_3d_thickness");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_ROUGHNESS = createKey("caves/spaghetti_3d_roughness");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_MODULATOR = createKey("caves/spaghetti_3d_modulator");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_SPAGHETTI_3D_RARITY = createKey("caves/spaghetti_3d_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_LAVA_TUBES = createKey("caves/lava_tubes");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_LAVA_TUBES_RARITY = createKey("caves/lava_tubes_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_PITS = createKey("caves/pits");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_PIT_RARITY = createKey("caves/pits_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_CAVERNS = createKey("caves/caverns");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_CAVERN_PILLARS = createKey("caves/cavern_pillars");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_CAVERN_PILLARS_RARITY = createKey("caves/cavern_pillars_rarity");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_CAVERN_PILLARS_THICKNESS = createKey("caves/cavern_pillars_thickness");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_GROTTO = createKey("caves/grotto");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_GROTTO_RARITY = createKey("caves/grotto_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_FRACTURE_1 = createKey("caves/fracture_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_FRACTURE_2 = createKey("caves/fracture_2");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_RARITY = createKey("caves/rarity");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, WildernessRebornMod.mod(key));
    }



}
