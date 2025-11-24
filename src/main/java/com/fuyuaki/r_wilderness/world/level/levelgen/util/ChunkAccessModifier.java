package com.fuyuaki.r_wilderness.world.level.levelgen.util;

import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomState;

public interface ChunkAccessModifier {
    void fillBiomesReborn(RebornBiomeSource biomeSource, RandomState state);
}
