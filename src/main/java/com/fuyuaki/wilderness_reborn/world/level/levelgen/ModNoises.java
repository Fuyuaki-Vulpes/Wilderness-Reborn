package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {


    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_A = createKey("terrain/base_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_B = createKey("terrain/base_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_PLATEAU_A = createKey("terrain/plateau_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_PLATEAU_B = createKey("terrain/plateau_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_A = createKey("terrain/tectonic_base_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_B = createKey("terrain/tectonic_base_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_SMOOTH_A = createKey("terrain/tectonic_base_smooth_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_SMOOTH_B = createKey("terrain/tectonic_base_smooth_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_BLENDER = createKey("terrain/blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_PLATEAU_BLENDER = createKey("terrain/plateau_blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_BLENDER_SMOOTH = createKey("terrain/blender_smooth");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_BLENDER = createKey("terrain/tectonic_blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_TERRAIN_BLENDER_SMOOTH = createKey("terrain/tectonic_blender_smooth");
    public static final ResourceKey<NormalNoise.NoiseParameters> TERRAIN_SMOOTHNESS = createKey("terrain/smoothness");

    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_EROSION = createKey("land/erosion");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_CONTINENTS = createKey("land/continents");


    public static final ResourceKey<NormalNoise.NoiseParameters> WATER_LARGE_BODIES = createKey("water/large_bodies");

    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_A = createKey("land/noise_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_B = createKey("land/noise_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_C = createKey("land/noise_c");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_BLENDER = createKey("land/noise_blender");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_BLENDER_2 = createKey("land/noise_blender_2");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_NOISE_STRENGHT = createKey("land/noise_strenght");


    public static final ResourceKey<NormalNoise.NoiseParameters> GEO_TECTONICS = createKey("geo/tectonics");

    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_RANDOMNESS = createKey("tectonic_plates/randomness");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_DIRECTION = createKey("tectonic_plates/direction");
    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_FACTOR_ACTIVITY = createKey("tectonic_plates/activity");



    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES = createKey("cave/noodle");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_DENSITY = createKey("cave/noodle_density");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_FILTER = createKey("cave/noodle_filter");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_PILLAR = createKey("cave/pillar");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_PILLAR_DENSITY = createKey("cave/pillar_density");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_PILLAR_RARITY = createKey("cave/pillar_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_EXOGENES = createKey("cave/exogene");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_ENDOGENES = createKey("cave/endogene");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_FILTER_A = createKey("cave/filter_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_FILTER_B = createKey("cave/filter_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_FILTER = createKey("cave/filter");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_DENSITY = createKey("cave/density");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS_FREQUENCY = createKey("cave/crack_frequency");








    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_ACTIVITY_OLD = old("detail/tectonic_activity");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_VALLEY_DEPTH = old("details/plateau_valley_depth");
    public static final ResourceKey<NormalNoise.NoiseParameters> JAGGEDNESS = old("details/jaggedness");
    public static final ResourceKey<NormalNoise.NoiseParameters> BIOME_VARIATION = old("detail/biome_variation");

    //CLIMATE AND ROUTER
    public static final ResourceKey<NormalNoise.NoiseParameters> TEMPERATURE = old("climate/temperature");
    public static final ResourceKey<NormalNoise.NoiseParameters> VEGETATION = old("climate/vegetation");

    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_LAVA = old("aquifers/lava_noise");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_SPREAD = old("aquifers/spread");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLOOD = old("aquifers/flood");



    public static final ResourceKey<NormalNoise.NoiseParameters> LANDMASS = old("elevation/landmass");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATES = old("elevation/plates");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_A = old("elevation/noise_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_B = old("elevation/noise_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_C = old("elevation/noise_c");
    public static final ResourceKey<NormalNoise.NoiseParameters> ELEVATION = old("elevation/elevation");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIVER_DEPTH = old("elevation/river_depth");
    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_VALLEYS = old("elevation/plateau_valleys");
    public static final ResourceKey<NormalNoise.NoiseParameters> HILLS_AND_MOUNTAINS = old("elevation/mountains");
    public static final ResourceKey<NormalNoise.NoiseParameters> MOUNTAIN_ELEVATION_OFFSET = old("elevation/mountain_elevation_offset");

    public static final ResourceKey<NormalNoise.NoiseParameters> PLATEAU_MASK = old("mask/plateaus");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIVER_MASK = old("mask/river_mask");

    public static final ResourceKey<NormalNoise.NoiseParameters> CRATERS = old("geomorphology/craters");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIDGES = old("geomorphology/ridges");
    public static final ResourceKey<NormalNoise.NoiseParameters> WEATHERING = old("geomorphology/weathering");

    //CAVES

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_ENTRANCES = old("caves/entrances");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_NOODLE = old("caves/noodle");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_NOODLE_RIDGE_1 = old("caves/noodle_ridge_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_NOODLE_RIDGE_2 = old("caves/noodle_ridge_2");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_NOODLE_THICKNESS = old("caves/noodle_thickness");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_1 = old("caves/spaghetti_3d_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_2 = old("caves/spaghetti_3d_2");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_THICKNESS = old("caves/spaghetti_3d_thickness");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_ROUGHNESS = old("caves/spaghetti_3d_roughness");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_MODULATOR = old("caves/spaghetti_3d_modulator");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_SPAGHETTI_3D_RARITY = old("caves/spaghetti_3d_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_LAVA_TUBES = old("caves/lava_tubes");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_LAVA_TUBES_RARITY = old("caves/lava_tubes_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_PITS = old("caves/pits");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_PIT_RARITY = old("caves/pits_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_CAVERNS = old("caves/caverns");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_CAVERN_PILLARS = old("caves/cavern_pillars");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_CAVERN_PILLARS_RARITY = old("caves/cavern_pillars_rarity");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_CAVERN_PILLARS_THICKNESS = old("caves/cavern_pillars_thickness");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_GROTTO = old("caves/grotto");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_GROTTO_RARITY = old("caves/grotto_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_FRACTURE_1 = old("caves/fracture_1");
    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_FRACTURE_2 = old("caves/fracture_2");

    public static final ResourceKey<NormalNoise.NoiseParameters> OLD_CAVE_RARITY = old("caves/rarity");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, WildernessRebornMod.mod(key));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> old(String location) {
        return createKey("old/" + location);
    }

}
