package com.fuyuaki.r_wilderness.data.worldgen.features;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModAquaticFeatures {
    public static final ResourceKey<Feature> WATER_DELTA_GRASS = ModFeatureUtils.createKey("water_delta_grass");
    public static final ResourceKey<Feature> WATER_DELTA_SAND = ModFeatureUtils.createKey("water_delta_sand");
    public static final ResourceKey<Feature> WATER_DELTA_STONE = ModFeatureUtils.createKey("water_delta_stone");

    public static void bootstrap(BootstrapContext<Feature> context) {
        context.register(
                WATER_DELTA_GRASS,
                new DeltaFeature(Blocks.WATER.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));

        context.register(
                WATER_DELTA_SAND,
                new DeltaFeature(Blocks.WATER.defaultBlockState(), Blocks.SAND.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));

        context.register(
                WATER_DELTA_STONE,
                new DeltaFeature(Blocks.WATER.defaultBlockState(), Blocks.STONE.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));

    }
}
