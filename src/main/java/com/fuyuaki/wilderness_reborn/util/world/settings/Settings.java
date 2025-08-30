package com.fuyuaki.wilderness_reborn.util.world.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Settings(
        boolean flatBedrock,
        int spawnDistance,
        int spawnCenterX,
        int spawnCenterZ,
        int temperatureScale,
        float temperatureConstant,
        int rainfallScale,
        float rainfallConstant,
        float continentalness,
        float grassDensity,
        boolean finiteContinents
)
{
    public static final MapCodec<Settings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("flat_bedrock").forGetter(c -> c.flatBedrock),
            Codec.INT.fieldOf("spawn_distance").forGetter(c -> c.spawnDistance),
            Codec.INT.fieldOf("spawn_center_x").forGetter(c -> c.spawnCenterX),
            Codec.INT.fieldOf("spawn_center_z").forGetter(c -> c.spawnCenterZ),
            Codec.INT.fieldOf("temperature_scale").forGetter(c -> c.temperatureScale),
            Codec.FLOAT.optionalFieldOf("temperature_constant", 0f).forGetter(c -> c.temperatureConstant),
            Codec.INT.fieldOf("rainfall_scale").forGetter(c -> c.rainfallScale),
            Codec.FLOAT.optionalFieldOf("rainfall_constant", 0f).forGetter(c -> c.rainfallConstant),
            Codec.FLOAT.fieldOf("continentalness").forGetter(c -> c.continentalness),
            Codec.FLOAT.fieldOf("grass_density").forGetter(c -> c.grassDensity),
            Codec.BOOL.fieldOf("finite_continents").forGetter(c -> c.finiteContinents)
    ).apply(instance, Settings::new));
}
