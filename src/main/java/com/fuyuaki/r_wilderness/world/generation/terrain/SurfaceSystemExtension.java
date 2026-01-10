package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.api.common.ModTags;
import com.fuyuaki.r_wilderness.world.level.levelgen.ModNoises;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Arrays;

public class SurfaceSystemExtension {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
    private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
    private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
    private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
    private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
    private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
    private static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
    public final BlockState defaultBlock;
    private final int seaLevel;
    private final BlockState[] clayBands;
    private final NormalNoise sandDunesNoise;
    private final NormalNoise sandDunesNoiseLarge;
    private final PositionalRandomFactory noiseRandom;
    private final NormalNoise clayBandsOffsetNoise;
    private final NormalNoise badlandsPillarNoise;
    private final NormalNoise badlandsPillarRoofNoise;
    private final NormalNoise badlandsSurfaceNoise;
    private final NormalNoise icebergPillarNoise;
    private final NormalNoise icebergPillarRoofNoise;
    private final NormalNoise icebergSurfaceNoise;
    private final NormalNoise surfaceNoise;
    private final NormalNoise surfaceSecondaryNoise;

    public SurfaceSystemExtension(RandomState randomState, BlockState defaultBlock, int seaLevel, PositionalRandomFactory noiseRandom) {
        this.defaultBlock = defaultBlock;
        this.seaLevel = seaLevel;
        this.noiseRandom = noiseRandom;
        this.sandDunesNoise = randomState.getOrCreateNoise(ModNoises.SAND_DUNES);
        this.sandDunesNoiseLarge = randomState.getOrCreateNoise(ModNoises.SAND_DUNES_LARGE);
        this.clayBands = generateBands(noiseRandom.fromHashOf(Identifier.withDefaultNamespace("clay_bands")));
        this.clayBandsOffsetNoise = randomState.getOrCreateNoise(Noises.CLAY_BANDS_OFFSET);
        this.surfaceNoise = randomState.getOrCreateNoise(Noises.SURFACE);
        this.surfaceSecondaryNoise = randomState.getOrCreateNoise(Noises.SURFACE_SECONDARY);
        this.badlandsPillarNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR);
        this.badlandsPillarRoofNoise = randomState.getOrCreateNoise(Noises.BADLANDS_PILLAR_ROOF);
        this.badlandsSurfaceNoise = randomState.getOrCreateNoise(Noises.BADLANDS_SURFACE);
        this.icebergPillarNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR);
        this.icebergPillarRoofNoise = randomState.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
        this.icebergSurfaceNoise = randomState.getOrCreateNoise(Noises.ICEBERG_SURFACE);
    }

    private static BlockState[] generateBands(RandomSource random) {
        BlockState[] ablockstate = new BlockState[192];
        Arrays.fill(ablockstate, TERRACOTTA);

        for (int k = 0; k < ablockstate.length; k++) {
            k += random.nextInt(5) + 1;
            if (k < ablockstate.length) {
                ablockstate[k] = ORANGE_TERRACOTTA;
            }
        }

        makeBands(random, ablockstate, 1, YELLOW_TERRACOTTA);
        makeBands(random, ablockstate, 2, BROWN_TERRACOTTA);
        makeBands(random, ablockstate, 1, RED_TERRACOTTA);
        int l = random.nextIntBetweenInclusive(9, 15);
        int i = 0;

        for (int j = 0; i < l && j < ablockstate.length; j += random.nextInt(16) + 4) {
            ablockstate[j] = WHITE_TERRACOTTA;
            if (j - 1 > 0 && random.nextBoolean()) {
                ablockstate[j - 1] = LIGHT_GRAY_TERRACOTTA;
            }

            if (j + 1 < ablockstate.length && random.nextBoolean()) {
                ablockstate[j + 1] = LIGHT_GRAY_TERRACOTTA;
            }

            i++;
        }

        return ablockstate;
    }

    private static void makeBands(RandomSource random, BlockState[] output, int minSize, BlockState state) {
        int i = random.nextIntBetweenInclusive(6, 15);

        for (int j = 0; j < i; j++) {
            int k = minSize + random.nextInt(3);
            int l = random.nextInt(output.length);

            for (int i1 = 0; l + i1 < output.length && i1 < k; i1++) {
                output[l + i1] = state;
            }
        }
    }

    protected BlockState getBand(int x, int y, int z) {
        int i = (int)Math.round(this.clayBandsOffsetNoise.getValue(x, 0.0, z) * 4.0);
        return this.clayBands[(y + i + this.clayBands.length) % this.clayBands.length];
    }

    public void sandDunes(BiomeManager manager, BlockColumn blockColumn, int x, int z, int height, LevelHeightAccessor level) {
        double count = 0;
        double matches = 0;
        for (int xOff = -4; xOff < 4; xOff ++){
            for (int zOff = -4; zOff < 4; zOff ++){
                int x1 = x + (4*xOff);
                int z1 = z + (4*zOff);
                count++;
                if (manager.getNoiseBiomeAtPosition(new BlockPos(x1,height,z1)).is(ModTags.Biomes.HAS_SAND_DUNES)){
                    matches++;
                }
            }
        }
        double influence = Math.pow(matches / count,2);
        int duneSize = 6;
        double dunes = Math.max(1 - Math.abs(this.sandDunesNoise.getValue(x,0,z)),0) * influence * duneSize;
        for (int duneHeight = 0; duneHeight < Math.floor(dunes); duneHeight++){
            BlockState blockstate = blockColumn.getBlock(height+duneHeight);

            if (blockstate.canBeReplaced()){
                blockColumn.setBlock(height + duneHeight,Blocks.SAND.defaultBlockState());
            }
        }
    }
    public void sandDunesLarge(BiomeManager manager, BlockColumn blockColumn, int x, int z, int height, LevelHeightAccessor level) {
        double count = 0;
        double matches = 0;
        for (int xOff = -4; xOff < 4; xOff ++){
            for (int zOff = -4; zOff < 4; zOff ++){
                int x1 = x + (4*xOff);
                int z1 = z + (4*zOff);
                count++;
                if (manager.getNoiseBiomeAtPosition(new BlockPos(x1,height,z1)).is(ModTags.Biomes.HAS_BIG_SAND_DUNES)){
                    matches++;
                }
            }
        }
        double influence = Math.pow(matches / count,2);
        int duneSize = 16;
        double dunes = Math.max(1 - Math.abs(this.sandDunesNoiseLarge.getValue(x,0,z)),0) * influence * duneSize;
        for (int duneHeight = 0; duneHeight < Math.floor(dunes); duneHeight++){
            BlockState blockstate = blockColumn.getBlock(height+duneHeight);

            if (blockstate.canBeReplaced()){
                blockColumn.setBlock(height + duneHeight,Blocks.SAND.defaultBlockState());
            }
        }
    }
    public void erodedBadlandsExtension(BlockColumn blockColumn, int x, int z, int height, LevelHeightAccessor level) {
        double d0 = 0.2;
        double d1 = Math.min(
                Math.abs(this.badlandsSurfaceNoise.getValue(x, 0.0, z) * 8.25),
                this.badlandsPillarNoise.getValue(x * 0.2, 0.0, z * 0.2) * 15.0
        );
        if (!(d1 <= 0.0)) {
            double d2 = 0.75;
            double d3 = 1.5;
            double d4 = Math.abs(this.badlandsPillarRoofNoise.getValue(x * 0.75, 0.0, z * 0.75) * 1.5);
            double d5 = 64.0 + Math.min(d1 * d1 * 2.5, Math.ceil(d4 * 50.0) + 24.0);
            int i = Mth.floor(d5);
            if (height <= i) {
                for (int j = i; j >= level.getMinY(); j--) {
                    BlockState blockstate = blockColumn.getBlock(j);
                    if (blockstate.is(this.defaultBlock.getBlock())) {
                        break;
                    }

                    if (blockstate.is(Blocks.WATER)) {
                        return;
                    }
                }

                for (int k = i; k >= level.getMinY() && blockColumn.getBlock(k).isAir(); k--) {
                    blockColumn.setBlock(k, getBand(x,i,z));
                }
            }
        }
    }

    public void frozenOceanExtension(
            int minSurfaceLevel, Biome biome, BlockColumn blockColumn, BlockPos.MutableBlockPos topWaterPos, int x, int z, int height
    ) {
        double d0 = 1.28;
        double d1 = Math.min(
                Math.abs(this.icebergSurfaceNoise.getValue(x, 0.0, z) * 8.25),
                this.icebergPillarNoise.getValue(x * 1.28, 0.0, z * 1.28) * 15.0
        );
        if (!(d1 <= 1.8)) {
            double d3 = 1.17;
            double d4 = 1.5;
            double d5 = Math.abs(this.icebergPillarRoofNoise.getValue(x * 1.17, 0.0, z * 1.17) * 1.5);
            double d6 = Math.min(d1 * d1 * 1.2, Math.ceil(d5 * 40.0) + 14.0);
            if (biome.shouldMeltFrozenOceanIcebergSlightly(topWaterPos.set(x, this.seaLevel, z), this.seaLevel)) {
                d6 -= 2.0;
            }

            double d2;
            if (d6 > 2.0) {
                d2 = this.seaLevel - d6 - 7.0;
                d6 += this.seaLevel;
            } else {
                d6 = 0.0;
                d2 = 0.0;
            }

            double d7 = d6;
            RandomSource randomsource = this.noiseRandom.at(x, 0, z);
            int i = 2 + randomsource.nextInt(4);
            int j = this.seaLevel + 18 + randomsource.nextInt(10);
            int k = 0;

            for (int l = Math.max(height, (int)d6 + 1); l >= minSurfaceLevel; l--) {
                if (blockColumn.getBlock(l).isAir() && l < (int)d7 && randomsource.nextDouble() > 0.01
                        || blockColumn.getBlock(l).is(Blocks.WATER) && l > (int)d2 && l < this.seaLevel && d2 != 0.0 && randomsource.nextDouble() > 0.15) {
                    if (k <= i && l > j) {
                        blockColumn.setBlock(l, SNOW_BLOCK);
                        k++;
                    } else {
                        blockColumn.setBlock(l, PACKED_ICE);
                    }
                }
            }
        }
    }

}
