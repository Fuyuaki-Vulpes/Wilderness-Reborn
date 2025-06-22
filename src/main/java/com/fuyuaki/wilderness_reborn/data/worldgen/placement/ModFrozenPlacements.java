package com.fuyuaki.wilderness_reborn.data.worldgen.placement;

import com.fuyuaki.wilderness_reborn.data.worldgen.features.ModFrozenFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModFrozenPlacements {
    public static final ResourceKey<PlacedFeature> SHARP_ICE_SPIKES_COMMON = ModPlacementUtils.createKey("sharp_ice_spikes_common");
    public static final ResourceKey<PlacedFeature> SHARP_ICE_SPIKES = ModPlacementUtils.createKey("sharp_ice_spikes");


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        PlacementUtils.register(context,
                SHARP_ICE_SPIKES_COMMON,
                ModPlacementUtils.holder(context,ModFrozenFeatures.SHARP_ICE_SPIKE),
                CountPlacement.of(5),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                SHARP_ICE_SPIKES,
                ModPlacementUtils.holder(context,ModFrozenFeatures.SHARP_ICE_SPIKE),
                CountPlacement.of(2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome()
        );

    }

}
