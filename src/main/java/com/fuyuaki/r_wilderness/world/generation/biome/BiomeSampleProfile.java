package com.fuyuaki.r_wilderness.world.generation.biome;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;

import java.util.List;

public record BiomeSampleProfile(
        float temperature,
        float erodability,
        float vegetation,
        float humidity,
        float rockiness,
        float continentalness,
        float volcanicness,
        float magicalness,
        List<BiomePlacementTags> tags
) {

    public static BiomeSampleProfile make(RegionMemory memory, int x, int y, int z) {
        WorldRegion region = memory.getRegionAt(x,z);
        RegionLayers layers = region.layers();
        float erodability = layers.getErodability().getUpsampled(x,z,memory);
        float vegetation = layers.getVegetation().getUpsampled(x,z,memory);
        float humidity = layers.getHumidity().getUpsampled(x,z,memory);
        float rockiness = layers.getRockiness().getUpsampled(x,z,memory);
        float continentalness = layers.getContinentalness().getUpsampled(x,z,memory);
        float volcanicness = layers.getVolcanicness().getUpsampled(x,z,memory);
        float magicalness = layers.getMagicalness().getUpsampled(x,z,memory);

        float baseTemperature = layers.getTemperature().getUpsampled(x,z,memory);
        float baseDeviationMax = 0.5f;

        float exposureMultiplier = (rockiness * 0.30f)
                + (continentalness * 0.20f)
                + (erodability * 0.10f);


        float thermalBuffer = (humidity * 0.25f)
                + (volcanicness * 0.40f)
                + (vegetation * 0.15f);

        float magicFactor = Math.max(magicalness * 0.35f,0.0f);

        float yDeviationMax = Math.clamp(
                baseDeviationMax - thermalBuffer + exposureMultiplier + magicFactor - Math.max(0.0f,baseTemperature * 0.25f),
                0.2f,
                0.8f);
        float yTempDeviation = Math.min(y - WildernessConstants.SEA_LEVEL,0.0f) / (WildernessConstants.BUILD_HEIGHT - WildernessConstants.SEA_LEVEL)
                * yDeviationMax;
        float temperature = Math.clamp(baseTemperature - yTempDeviation,-1.0f,1.0f);

        List<BiomePlacementTags> tags = BiomePlacementTags.scan(region,memory,x,y,z);

        return new BiomeSampleProfile(
                temperature,erodability,vegetation,humidity,rockiness,continentalness,volcanicness,magicalness,tags);
    }
}
