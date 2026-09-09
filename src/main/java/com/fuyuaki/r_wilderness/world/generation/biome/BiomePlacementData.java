package com.fuyuaki.r_wilderness.world.generation.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

public record BiomePlacementData(
        Identifier name,
        Holder<Biome> biome,
        BiomeTargetProfile target
) {


    public static final Codec<BiomePlacementData> CODEC = ExtraCodecs.catchDecoderException(
            RecordCodecBuilder.create(
                    p_415523_ -> p_415523_.group(
                                    Identifier.CODEC.fieldOf("name").forGetter(BiomePlacementData::name),
                                    Biome.CODEC.fieldOf("biome").forGetter(BiomePlacementData::biome),
                                    BiomeTargetProfile.CODEC.fieldOf("target").forGetter(BiomePlacementData::target)
                            )
                            .apply(p_415523_, BiomePlacementData::new)
            )
    );
}
