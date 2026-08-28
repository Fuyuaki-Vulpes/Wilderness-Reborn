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
        PalettedContainer<Holder<Biome>> newBiomes = this.biomes.recreate();

        for (int quartMinX = 0; quartMinX < 4; quartMinX++) {
            for (int quartMinY = 0; quartMinY < 4; quartMinY++) {
                for (int quartMinZ = 0; quartMinZ < 4; quartMinZ++) {
                    newBiomes.getAndSetUnchecked(
                            quartMinX, quartMinY, quartMinZ,
                            biomeResolver.getNoiseBiome(
                                    x + quartMinX,
                                    y + quartMinY,
                                    z + quartMinZ,
                                    state.sampler()));
                }
            }
        }

        this.biomes = newBiomes;
    }

}
