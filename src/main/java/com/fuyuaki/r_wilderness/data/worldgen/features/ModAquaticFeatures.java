package com.fuyuaki.r_wilderness.data.worldgen.features;

import com.fuyuaki.r_wilderness.init.RFeatures;
import com.fuyuaki.r_wilderness.world.level.levelgen.feature.configurations.WaterDeltaFeatureConfiguration;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModAquaticFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_DELTA = ModFeatureUtils.createKey("water_delta");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_WATER_DELTA = ModFeatureUtils.createKey("large_water_delta");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(
                context,
                WATER_DELTA,
                RFeatures.WATER_DELTA.get(),
                new WaterDeltaFeatureConfiguration(UniformInt.of(2, 5))
        );

        FeatureUtils.register(
                context,
                LARGE_WATER_DELTA,
                RFeatures.WATER_DELTA.get(),
                new WaterDeltaFeatureConfiguration(UniformInt.of(4, 7))
        );
    }
}
