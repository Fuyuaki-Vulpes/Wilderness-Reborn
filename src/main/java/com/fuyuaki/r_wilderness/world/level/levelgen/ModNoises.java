package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {


    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES = createKey("cave/noodle");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_DENSITY = createKey("cave/noodle_density");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_NOODLES_FILTER = createKey("cave/noodle_filter");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_EXOGENES = createKey("cave/exogene");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_ENDOGENES = createKey("cave/endogene");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_DENSITY = createKey("cave/density");

    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<NormalNoise.NoiseParameters> CAVES_CRACKS_FREQUENCY = createKey("cave/crack_frequency");


    public static final ResourceKey<NormalNoise.NoiseParameters> AQUIFERS = createKey("aquifers");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, RWildernessMod.modLocation(key));
    }

    private static ResourceKey<NormalNoise.NoiseParameters> old(String location) {
        return createKey("old/" + location);
    }

}
