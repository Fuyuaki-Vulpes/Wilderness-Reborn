package com.fuyuaki.r_wilderness.world.level.levelgen.util;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.levelgen.RandomState;

public interface LevelChunkSectionModifier {
    void setBiomes(PalettedContainerRO<Holder<Biome>> biomes);

    void fillBiomesReborn(BiomeResolver biomeResolver, RandomState state, int x, int y, int z);

}
