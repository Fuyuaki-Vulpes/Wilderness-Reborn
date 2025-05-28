package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {

    public static final ResourceKey<NormalNoise.NoiseParameters> TECTONIC_ACTIVITY = createKey("tectonic_activity");
    public static final ResourceKey<NormalNoise.NoiseParameters> HILL_CURVATURE = createKey("hill_curvature");
    public static final ResourceKey<NormalNoise.NoiseParameters> ELEVATION = createKey("elevation");
    public static final ResourceKey<NormalNoise.NoiseParameters> OVERWORLD_NOISE = createKey("overworld_noise");
    public static final ResourceKey<NormalNoise.NoiseParameters> OVERWORLD_NOISE_2 = createKey("overworld_noise_2");
    public static final ResourceKey<NormalNoise.NoiseParameters> NOISE_FILTER = createKey("noise_filter");
    public static final ResourceKey<NormalNoise.NoiseParameters> SPIKES = createKey("spikes");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, WildernessRebornMod.mod(key));
    }



}
