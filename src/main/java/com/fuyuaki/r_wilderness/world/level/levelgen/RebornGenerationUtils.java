package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementTags;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomeTargetProfile;

import java.util.List;

public class RebornGenerationUtils {

    public static List<RebornSpawnTarget> spawnTargets(int seaLevel){
        return List.of(
                new RebornSpawnTarget(
                        BiomeTargetProfile.builder()
                                .temperate()
                                .anyErosion()
                                .sparse()
                                .mildHumidity()
                                .inland()
                                .lowVolcanicActivity()
                                .withMagicalness(-1.0f,0.25f)
                                .disallowTags(
                                        BiomePlacementTags.CAVE,
                                        BiomePlacementTags.RIVER,
                                        BiomePlacementTags.TRENCH,
                                        BiomePlacementTags.PEAK)
                                .build(),
                        seaLevel,
                        WildernessConstants.MAX_SPAWN_ELEVATION
                ),

                new RebornSpawnTarget(
                        BiomeTargetProfile.builder()
                                .temperate()
                                .anyErosion()
                                .lush()
                                .mildHumidity()
                                .island()
                                .withVolcanicness(-1.0f,-0.25f,1.0f)
                                .withMagicalness(-1.0f,0.25f)
                                .disallowTags(
                                        BiomePlacementTags.CAVE,
                                        BiomePlacementTags.RIVER,
                                        BiomePlacementTags.TRENCH,
                                        BiomePlacementTags.PEAK)
                                .build(),
                        72,
                        128
                )

        );
    }
}
