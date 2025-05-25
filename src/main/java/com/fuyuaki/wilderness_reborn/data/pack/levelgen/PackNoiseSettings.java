package com.fuyuaki.wilderness_reborn.data.pack.levelgen;

import net.minecraft.world.level.levelgen.NoiseSettings;

public class PackNoiseSettings {
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = NoiseSettings.create(-64, 384, 1, 2);
    protected static final NoiseSettings NETHER_NOISE_SETTINGS = NoiseSettings.create(0, 128, 1, 2);
    protected static final NoiseSettings END_NOISE_SETTINGS = NoiseSettings.create(0, 128, 2, 1);
    protected static final NoiseSettings CAVES_NOISE_SETTINGS = NoiseSettings.create(-64, 192, 1, 2);
    protected static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = NoiseSettings.create(0, 256, 2, 1);

}
