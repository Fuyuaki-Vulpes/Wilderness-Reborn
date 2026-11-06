package com.fuyuaki.r_wilderness.api;

import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class RRegistries {


    public static final ResourceKey<Registry<RebornWorldSettings>> WRGEN_SETTINGS = createRegistryKey("worldgen/reborn_settings");
    public static final ResourceKey<Registry<BiomePlacementData>> REBORN_BIOME_PLACEMENT_KEY = createRegistryKey("worldgen/reborn_biome_placement");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Identifier.withDefaultNamespace(name));
    }


}
