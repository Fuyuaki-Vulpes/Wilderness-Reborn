package com.fuyuaki.r_wilderness.data.worldgen.placement;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

public class ModPlacementUtils {


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        ModFrozenPlacements.bootstrap(context);
        ModMiscPlacements.bootstrap(context);
        ModAquaticPlacements.bootstrap(context);
        ModTreePlacements.bootstrap(context);
        ModVegetationPlacements.bootstrap(context);


    }

        public static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, RWildernessMod.identifier(name));
    }



    public static Holder<Feature> holder(BootstrapContext<PlacedFeature> context, ResourceKey<Feature> feature){
        HolderGetter<Feature> holdergetter = context.lookup(Registries.FEATURE);
        return holdergetter.getOrThrow(feature);
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<Feature> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    public static List<PlacementModifier> listAdd(List<PlacementModifier> list, PlacementModifier... modifiers){
        List<PlacementModifier> newList = new ArrayList<>(List.copyOf(list));
        newList.addAll(List.of(modifiers));
        return newList;
    }

}
