package com.fuyuaki.r_wilderness.data.worldgen.features;

import com.fuyuaki.r_wilderness.init.RFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModFrozenFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SHARP_ICE_SPIKE = ModFeatureUtils.createKey("sharp_ice_spike");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(context, SHARP_ICE_SPIKE, RFeatures.SHARP_ICE_SPIKES.get());

    }
}