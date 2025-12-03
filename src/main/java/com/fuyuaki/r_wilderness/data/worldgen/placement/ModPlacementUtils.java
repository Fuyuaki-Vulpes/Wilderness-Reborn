package com.fuyuaki.r_wilderness.data.worldgen.placement;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModVegetationFeatures;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.data.worldgen.placement.PlacementUtils.register;

public class ModPlacementUtils {


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        ModFrozenPlacements.bootstrap(context);
        ModMiscPlacements.bootstrap(context);
        ModAquaticPlacements.bootstrap(context);
        ModTreePlacements.bootstrap(context);
        ModVegetationPlacements.bootstrap(context);


    }

        public static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, RWildernessMod.modLocation(name));
    }



    public static Holder<ConfiguredFeature<?,?>> holder(BootstrapContext<PlacedFeature> context, ResourceKey<ConfiguredFeature<?,?>> feature){
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        return holdergetter.getOrThrow(feature);
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    public static List<PlacementModifier> listAdd(List<PlacementModifier> list, PlacementModifier... modifiers){
        List<PlacementModifier> newList = new ArrayList<>(List.copyOf(list));
        newList.addAll(List.of(modifiers));
        return newList;
    }

}
