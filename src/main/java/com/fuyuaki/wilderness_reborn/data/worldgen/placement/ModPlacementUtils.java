package com.fuyuaki.wilderness_reborn.data.worldgen.placement;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
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

import java.util.List;

public class ModPlacementUtils {
    public static final ResourceKey<PlacedFeature> ALPINE_TREE = createKey("alpine_tree");


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {

        register(
                context,
                ALPINE_TREE,
                holder(context, ModFeatureUtils.ALPINE_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.5F, 1),
                        ModBlocks.ALPINE_SAPLING.get())
        );
    }

        public static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, WildernessRebornMod.mod(name));
    }
    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static Holder<ConfiguredFeature<?,?>> holder(BootstrapContext<PlacedFeature> context, ResourceKey<ConfiguredFeature<?,?>> feature){
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        return holdergetter.getOrThrow(feature);
    }

}
