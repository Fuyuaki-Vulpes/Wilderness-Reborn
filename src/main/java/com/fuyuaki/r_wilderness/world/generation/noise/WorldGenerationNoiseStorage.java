package com.fuyuaki.r_wilderness.world.generation.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;

import java.util.function.Function;

public record WorldGenerationNoiseStorage(
        DensityFunction barrierNoise,
        DensityFunction fluidLevelFloodednessNoise,
        DensityFunction fluidLevelSpreadNoise,
        DensityFunction lavaNoise,
        DensityFunction temperature,
        DensityFunction vegetationDensity,
        DensityFunction erosion,
        DensityFunction age,
        DensityFunction humidity,
        DensityFunction mountainMap,
        DensityFunction hillsMap,
        DensityFunction flatMap,
        DensityFunction continentalness,
        DensityFunction caves

) {

    public static final Codec<WorldGenerationNoiseStorage> CODEC = RecordCodecBuilder.create(
            p_224411_ -> p_224411_.group(
                            field("barrier", WorldGenerationNoiseStorage::barrierNoise),
                            field("fluid_level_floodedness", WorldGenerationNoiseStorage::fluidLevelFloodednessNoise),
                            field("fluid_level_spread", WorldGenerationNoiseStorage::fluidLevelSpreadNoise),
                            field("lava", WorldGenerationNoiseStorage::lavaNoise),
                            field("temperature", WorldGenerationNoiseStorage::temperature),
                            field("vegetation_density", WorldGenerationNoiseStorage::vegetationDensity),
                            field("erosion", WorldGenerationNoiseStorage::erosion),
                            field("age", WorldGenerationNoiseStorage::age),
                            field("humidity", WorldGenerationNoiseStorage::humidity),
                            field("mountain_map", WorldGenerationNoiseStorage::mountainMap),
                            field("hills_map", WorldGenerationNoiseStorage::hillsMap),
                            field("flat_map", WorldGenerationNoiseStorage::flatMap),
                            field("continentalness", WorldGenerationNoiseStorage::continentalness),
                            field("caves", WorldGenerationNoiseStorage::caves)
                    )
                    .apply(p_224411_, WorldGenerationNoiseStorage::new)
    );
    private static RecordCodecBuilder<WorldGenerationNoiseStorage, DensityFunction> field(String name, Function<WorldGenerationNoiseStorage, DensityFunction> getter) {
        return DensityFunction.HOLDER_HELPER_CODEC.fieldOf(name).forGetter(getter);
    }

    public WorldGenerationNoiseStorage mapAll(DensityFunction.Visitor visitor) {
        return new WorldGenerationNoiseStorage(
                this.barrierNoise.mapAll(visitor),
                this.fluidLevelFloodednessNoise.mapAll(visitor),
                this.fluidLevelSpreadNoise.mapAll(visitor),
                this.lavaNoise.mapAll(visitor),
                this.temperature.mapAll(visitor),
                this.vegetationDensity.mapAll(visitor),
                this.erosion.mapAll(visitor),
                this.age.mapAll(visitor),
                this.humidity.mapAll(visitor),
                this.mountainMap.mapAll(visitor),
                this.hillsMap.mapAll(visitor),
                this.flatMap.mapAll(visitor),
                this.continentalness.mapAll(visitor),
                this.caves.mapAll(visitor)
        );
    }
}
