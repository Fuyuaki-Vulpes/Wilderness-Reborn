package com.fuyuaki.r_wilderness.data.worldgen.placement;

import com.fuyuaki.r_wilderness.data.worldgen.features.ModAquaticFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.*;

public class ModAquaticPlacements {
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_BEACH = ModPlacementUtils.createKey("water_deltas_beach");
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_SWAMP = ModPlacementUtils.createKey("water_deltas_swamp");
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_STONY_SHORE = ModPlacementUtils.createKey("water_deltas_stony_shore");


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        PlacementUtils.register(context,
                WATER_DELTAS_BEACH,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA_SAND),
                CountPlacement.of(BiasedToBottomInt.of(0,3)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                WATER_DELTAS_SWAMP,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA_GRASS),
                CountPlacement.of(UniformInt.of(1,9)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                WATER_DELTAS_STONY_SHORE,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA_STONE),
                CountPlacement.of(BiasedToBottomInt.of(0,2)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

    }
}
