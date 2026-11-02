package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {


    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES = createKey("cave/noodle");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_DENSITY = createKey("cave/noodle_density");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_FILTER = createKey("cave/noodle_filter");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_RIDGES = createKey("cave/ridge");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_RIDGE_FILTER = createKey("cave/ridge_filter");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVE_BUBBLES = createKey("cave/bubble");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_DENSITY = createKey("cave/density");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS_FREQUENCY = createKey("cave/crack_frequency");



    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_BARRIER = createKey("aquifer_barrier");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = createKey("aquifer_fluid_level_floodedness");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_LAVA = createKey("aquifer_lava");
    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = createKey("aquifer_fluid_level_spread");


    public static final ResourceKey<NormalNoise.NoiseParameters> SAND_DUNES = createKey("sand_dunes");
    public static final ResourceKey<NormalNoise.NoiseParameters> SAND_DUNES_LARGE = createKey("sand_dunes_large");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, RWildernessMod.modLocation(key));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> old(String location) {
        return createKey("old/" + location);
    }

}
