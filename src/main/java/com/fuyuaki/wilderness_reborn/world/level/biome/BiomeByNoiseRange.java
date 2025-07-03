package com.fuyuaki.wilderness_reborn.world.level.biome;

import net.minecraft.world.level.biome.Biome;

public record BiomeByNoiseRange(
        BiomeNoiseRange temperature,
        BiomeNoiseRange wetness
) {
}
