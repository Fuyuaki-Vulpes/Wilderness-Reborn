package com.fuyuaki.wilderness_reborn.data.worldgen.features;

import com.fuyuaki.wilderness_reborn.init.ModFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModFrozenFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SHARP_ICE_SPIKE = ModFeatureUtils.createKey("sharp_ice_spike");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, SHARP_ICE_SPIKE, ModFeatures.SHARP_ICE_SPIKES.get());

    }
}