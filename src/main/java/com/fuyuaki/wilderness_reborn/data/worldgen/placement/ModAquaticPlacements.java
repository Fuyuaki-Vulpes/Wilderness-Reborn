package com.fuyuaki.wilderness_reborn.data.worldgen.placement;

import com.fuyuaki.wilderness_reborn.data.worldgen.features.ModAquaticFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.*;

public class ModAquaticPlacements {
    public static final ResourceKey<PlacedFeature> WATER_DELTAS = ModPlacementUtils.createKey("water_deltas");
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_COMMON = ModPlacementUtils.createKey("water_deltas_common");
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_VERY_COMMON = ModPlacementUtils.createKey("water_deltas_very_common");
    public static final ResourceKey<PlacedFeature> WATER_DELTAS_LARGE = ModPlacementUtils.createKey("water_deltas_large");


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        PlacementUtils.register(context,
                WATER_DELTAS,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                WATER_DELTAS_COMMON,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA),
                CountPlacement.of(UniformInt.of(1,5)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                WATER_DELTAS_VERY_COMMON,
                ModPlacementUtils.holder(context,ModAquaticFeatures.WATER_DELTA),
                CountPlacement.of(UniformInt.of(1,9)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

        PlacementUtils.register(context,
                WATER_DELTAS_LARGE,
                ModPlacementUtils.holder(context,ModAquaticFeatures.LARGE_WATER_DELTA),
                CountPlacement.of(UniformInt.of(0,2)),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        );

    }
}
