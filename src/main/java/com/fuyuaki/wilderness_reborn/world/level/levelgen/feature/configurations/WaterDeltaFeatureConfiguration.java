package com.fuyuaki.wilderness_reborn.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record WaterDeltaFeatureConfiguration(IntProvider size) implements FeatureConfiguration {
    public static final Codec<WaterDeltaFeatureConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            IntProvider.codec(0, 16).fieldOf("size").forGetter(configuration -> configuration.size)
                    )
                    .apply(instance, WaterDeltaFeatureConfiguration::new)
    );
}
