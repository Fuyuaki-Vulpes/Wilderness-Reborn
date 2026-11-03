package com.fuyuaki.r_wilderness.data.worldgen.features;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModFeatureUtils {

    public static void bootstrap(BootstrapContext<Feature> context) {
        ModFrozenFeatures.bootstrap(context);
        ModMiscFeatures.bootstrap(context);
        ModAquaticFeatures.bootstrap(context);
        ModTreeFeatures.bootstrap(context);
        ModVegetationFeatures.bootstrap(context);

    }


    public static ResourceKey<Feature> createKey(String name) {
        return ResourceKey.create(Registries.FEATURE, RWildernessMod.identifier(name));
    }


}