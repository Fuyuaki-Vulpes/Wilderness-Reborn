package com.fuyuaki.r_wilderness.world.level.biome;

import com.fuyuaki.r_wilderness.mixin.accessor.BiomeAccessAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class BiomeDitherer extends BiomeManager {
    private final PositionalRandomFactory positionalRandomFactory;
    private static final int DITHERING_RANGE = 12;
    private static final int DITHERING_RANGE_HALVED = DITHERING_RANGE / 2;


    public BiomeDitherer(BiomeManager.NoiseBiomeSource storage, long seed) {
        super(storage, seed);
        this.positionalRandomFactory = (new XoroshiroRandomSource(seed)).forkPositional();
    }

    public BiomeManager withDifferentSource(BiomeManager.NoiseBiomeSource storage) {
        long seed = ((BiomeAccessAccessor)this).getSeed();
        return new BiomeDitherer(storage, seed);
    }

    public Holder<Biome> getBiome(BlockPos pos) {
        RandomSource random = this.positionalRandomFactory.at(pos);
        return super.getBiome(
                pos.offset(
                        random.nextInt(DITHERING_RANGE) - DITHERING_RANGE_HALVED,
                        random.nextInt(DITHERING_RANGE) - DITHERING_RANGE_HALVED,
                        random.nextInt(DITHERING_RANGE) - DITHERING_RANGE_HALVED
                )
        );
    }
}
