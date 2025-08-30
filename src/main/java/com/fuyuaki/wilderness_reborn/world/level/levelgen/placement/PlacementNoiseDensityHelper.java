package com.fuyuaki.wilderness_reborn.world.level.levelgen.placement;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import java.util.Map;

public class PlacementNoiseDensityHelper implements DensityFunction.Visitor {
    private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();
    private final boolean useLegacySource;
    private final long seed;
    final RandomState randomState;
    final PositionalRandomFactory random;

    private RandomSource newLegacyInstance(long noiseSeed) {
        return new LegacyRandomSource(this.seed + noiseSeed);
    }

    PlacementNoiseDensityHelper(long seed, boolean useLegacySource, RandomState randomState, PositionalRandomFactory random) {
        this.seed = seed;
        this.useLegacySource = useLegacySource;
        this.randomState = randomState;
        this.random = random;
    }

    public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
        Holder<NormalNoise.NoiseParameters> noiseData = noiseHolder.noiseData();
        NormalNoise noise;
        if (this.useLegacySource) {
            if (noiseData.is(Noises.TEMPERATURE)) {
                noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(0L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }

            if (noiseData.is(Noises.VEGETATION)) {
                noise = NormalNoise.createLegacyNetherBiome(this.newLegacyInstance(1L), new NormalNoise.NoiseParameters(-7, 1.0, new double[]{1.0}));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }

            if (noiseData.is(Noises.SHIFT)) {
                noise = NormalNoise.create(this.random.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0, new double[0]));
                return new DensityFunction.NoiseHolder(noiseData, noise);
            }
        }

        noise = this.randomState.getOrCreateNoise((ResourceKey)noiseData.unwrapKey().orElseThrow());
        return new DensityFunction.NoiseHolder(noiseData, noise);
    }

    private DensityFunction wrapNew(DensityFunction densityFunction) {
        if (densityFunction instanceof BlendedNoise $$1) {
            RandomSource $$2x = this.useLegacySource ? this.newLegacyInstance(0L) : this.random.fromHashOf(ResourceLocation.withDefaultNamespace("terrain"));
            return $$1.withNewRandom($$2x);
        } else {
            return (DensityFunction)(densityFunction instanceof DensityFunctions.EndIslandDensityFunction ? new DensityFunctions.EndIslandDensityFunction(this.seed) : densityFunction);
        }
    }

    public DensityFunction apply(DensityFunction densityFunction) {
        return (DensityFunction)this.wrapped.computeIfAbsent(densityFunction, this::wrapNew);
    }
}
