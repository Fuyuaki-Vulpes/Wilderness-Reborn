package com.fuyuaki.wilderness_reborn.data.worldgen.placement;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

public class ModPlacementUtils {

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        ModFrozenPlacements.bootstrap(context);
        ModMiscPlacements.bootstrap(context);
        ModAquaticPlacements.bootstrap(context);
    }

        public static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, WildernessRebornMod.mod(name));
    }



    public static Holder<ConfiguredFeature<?,?>> holder(BootstrapContext<PlacedFeature> context, ResourceKey<ConfiguredFeature<?,?>> feature){
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        return holdergetter.getOrThrow(feature);
    }

    public static List<PlacementModifier> listAdd(List<PlacementModifier> list, PlacementModifier... modifiers){
        List<PlacementModifier> newList = new ArrayList<>(List.copyOf(list));
        newList.addAll(List.of(modifiers));
        return newList;
    }

}
