package com.fuyuaki.r_wilderness.world.level.biome;

import net.minecraft.world.level.biome.Biome;

public record BiomeByNoiseRange(
        BiomeNoiseRange temperature,
        BiomeNoiseRange wetness
) {
}
