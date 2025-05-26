package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoises {

    public static final ResourceKey<NormalNoise.NoiseParameters> RIVER_PLACEMENT = createKey("river_placement");
    public static final ResourceKey<NormalNoise.NoiseParameters> RIVERS = createKey("rivers");


    private static ResourceKey<NormalNoise.NoiseParameters> createKey(String key) {
        return ResourceKey.create(Registries.NOISE, WildernessRebornMod.mod(key));
    }



}
