package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.levelgen.util.LevelChunkSectionModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements LevelChunkSectionModifier {

    @Shadow private PalettedContainerRO<Holder<Biome>> biomes;

    @Override
    public void setBiomes(PalettedContainerRO<Holder<Biome>> biomes) {
        this.biomes = biomes;
    }

    @Override
    public void fillBiomesReborn(BiomeResolver biomeResolver, RandomState state, int x, int y, int z) {
        PalettedContainer<Holder<Biome>> palettedcontainer = this.biomes.recreate();
        int i = 4;

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                for (int l = 0; l < 4; l++) {
                    palettedcontainer.getAndSetUnchecked(
                            j, k, l,
                            biomeResolver.getNoiseBiome(
                                    x + QuartPos.toBlock(j),
                                    y + QuartPos.toBlock(k),
                                    z + QuartPos.toBlock(l),
                                    state.sampler()));
                }
            }
        }

        this.biomes = palettedcontainer;
    }

}
