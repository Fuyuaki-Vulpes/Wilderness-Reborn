package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.world.generation.biome.BiomeTargetProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RebornSpawnTarget(
        BiomeTargetProfile profile,
        int minY,
        int maxY
) {

    public static final Codec<RebornSpawnTarget> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            BiomeTargetProfile.CODEC.fieldOf("profile").forGetter(RebornSpawnTarget::profile),
                            Codec.INT.fieldOf("min_y").forGetter(RebornSpawnTarget::minY),
                            Codec.INT.fieldOf("max_y").forGetter(RebornSpawnTarget::maxY)
                    )
                    .apply(i, i.stable(RebornSpawnTarget::new)));


}
