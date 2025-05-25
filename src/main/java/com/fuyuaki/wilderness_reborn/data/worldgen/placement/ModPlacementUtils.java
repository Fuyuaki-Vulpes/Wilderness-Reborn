package com.fuyuaki.wilderness_reborn.data.worldgen.placement;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacementUtils {

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {

    }

        public static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, WildernessRebornMod.mod(name));
    }



}
