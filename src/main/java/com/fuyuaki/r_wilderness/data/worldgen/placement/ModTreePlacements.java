package com.fuyuaki.r_wilderness.data.worldgen.placement;

import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModTreeFeatures;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModTreePlacements {
    public static final ResourceKey<PlacedFeature> ALPINE_TREE = ModPlacementUtils.createKey("alpine_tree");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        PlacementUtils.register(
                context,
                ALPINE_TREE,
                ModPlacementUtils.holder(context, ModTreeFeatures.ALPINE_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.5F, 1),
                        RBlocks.ALPINE_SAPLING.get())
        );
    }

}
