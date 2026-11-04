package com.fuyuaki.r_wilderness.data.worldgen.features;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;

public class ModFrozenFeatures {
    public static final ResourceKey<Feature> SHARP_ICE_SPIKE = ModFeatureUtils.createKey("sharp_ice_spike");

    public static void bootstrap(BootstrapContext<Feature> context) {
        context.register(SHARP_ICE_SPIKE, new SpikeFeature(Blocks.PACKED_ICE.defaultBlockState(),
                BlockPredicate.matchesBlocks(Blocks.SNOW_BLOCK),
                BlockPredicate.matchesTag(BlockTags.ICE_SPIKE_REPLACEABLE)));

    }
}