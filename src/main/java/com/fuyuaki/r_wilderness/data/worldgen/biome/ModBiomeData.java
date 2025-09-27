package com.fuyuaki.r_wilderness.data.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModBiomeData {
    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> holdergetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> holdergetter1 = context.lookup(Registries.CONFIGURED_CARVER);

    }
}
