package com.fuyuaki.r_wilderness.api;

import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomePlacement;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildGeneratorSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class WildRegistries {


    public static final ResourceKey<Registry<WildGeneratorSettings>> WRGEN_SETTINGS_KEY = createRegistryKey("worldgen/generator_settings");
    public static final ResourceKey<Registry<RebornBiomePlacement>> REBORN_BIOME_PLACEMENT_KEY = createRegistryKey("worldgen/biome_placement");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.withDefaultNamespace(name));
    }


}
