package com.fuyuaki.r_wilderness.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record WildGeneratorSettings(
        NoiseSettings noiseSettings,
        BlockState defaultBlock,
        BlockState defaultFluid,
        SurfaceRules.RuleSource surfaceRule,
        int seaLevel,
        boolean stoneLayerGradients
) {
    public static final Codec<WildGeneratorSettings> CODEC = RecordCodecBuilder.create(
            p_64475_ -> p_64475_.group(
                    NoiseSettings.CODEC.fieldOf("noise").forGetter(WildGeneratorSettings::noiseSettings),
                            BlockState.CODEC.fieldOf("default_block").forGetter(WildGeneratorSettings::defaultBlock),
                            BlockState.CODEC.fieldOf("default_fluid").forGetter(WildGeneratorSettings::defaultFluid),
                            SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(WildGeneratorSettings::surfaceRule),
                            Codec.INT.fieldOf("sea_level").forGetter(WildGeneratorSettings::seaLevel),
                            Codec.BOOL.fieldOf("legacy_random_source").forGetter(WildGeneratorSettings::stoneLayerGradients)
                    )
                    .apply(p_64475_, WildGeneratorSettings::new)
    );


}
