package com.fuyuaki.r_wilderness.data;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class ModCarvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> LAVA_TUBES = createKey("lava_tubes");
    public static final ResourceKey<ConfiguredWorldCarver<?>> LARGE_CAVES = createKey("large_caves");

    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, RWildernessMod.modLocation(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> holdergetter = context.lookup(Registries.BLOCK);
        context.register(
                LARGE_CAVES,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.05F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(64)),
                                        UniformFloat.of(0.2F, 1.9F),
                                        VerticalAnchor.aboveBottom(12),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.8F, 2.5F),
                                        UniformFloat.of(0.6F, 2.0F),
                                        UniformFloat.of(-0.8F, -0.2F)
                                )
                        )
        );

        context.register(
                LAVA_TUBES,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.15F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(180)),
                                        UniformFloat.of(0.25F, 0.9F),
                                        VerticalAnchor.aboveBottom(8),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.9F, 1.6F),
                                        UniformFloat.of(0.7F, 1.5F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )
                        )
        );
    }


}
