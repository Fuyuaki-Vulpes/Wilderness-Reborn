package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
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

    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_PLATEAU_A = createKey("land/plateau_a");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_PLATEAU_B = createKey("land/plateau_b");
    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_PLATEAU_BLENDER = createKey("land/plateau_blender");

    public static final ResourceKey<NormalNoise.NoiseParameters> LAND_VALLEYS = createKey("land/valleys");

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

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_SECONDARY_PILLAR = createKey("cave/secondary_pillar");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_SECONDARY_PILLAR_DENSITY = createKey("cave/secondary_pillar_density");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_SECONDARY_PILLAR_RARITY = createKey("cave/secondary_pillar_rarity");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_EXOGENES = createKey("cave/exogene");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_ENDOGENES = createKey("cave/endogene");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_DENSITY = createKey("cave/density");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS_FREQUENCY = createKey("cave/crack_frequency");


    public static final ResourceKey<NormalNoise.NoiseParameters> BIOME_VARIATION = createKey("detail/biome_variation");

    //CLIMATE AND ROUTER
    public static final ResourceKey<NormalNoise.NoiseParameters> TEMPERATURE = createKey("climate/temperature");
    public static final ResourceKey<NormalNoise.NoiseParameters> VEGETATION = createKey("climate/vegetation");

    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_LAVA = createKey("aquifers/lava_noise");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_SPREAD = createKey("aquifers/spread");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLOOD = createKey("aquifers/flood");





    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, RWildernessMod.modLocation(key));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> old(String location) {
        return createKey("old/" + location);
    }

}
