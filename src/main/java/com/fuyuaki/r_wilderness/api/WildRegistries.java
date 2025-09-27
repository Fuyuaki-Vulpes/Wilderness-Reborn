package com.fuyuaki.r_wilderness.api;

import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class WildRegistries {

    public static final ResourceKey<Registry<WildGeneratorSettings>> WRGEN_SETTINGS = createRegistryKey("worldgen/generator_settings");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(RWildernessMod.modLocation(name));
    }

}
