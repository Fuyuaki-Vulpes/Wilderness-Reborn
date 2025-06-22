package com.fuyuaki.wilderness_reborn.data.worldgen.features;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModFeatureUtils {
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        ModFrozenFeatures.bootstrap(context);
        ModMiscFeatures.bootstrap(context);
        ModAquaticFeatures.bootstrap(context);
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, WildernessRebornMod.mod(name));
    }
    public static void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Feature<NoneFeatureConfiguration> feature
    ) {
        register(context, key, feature, FeatureConfiguration.NONE);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config
    ) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }

}