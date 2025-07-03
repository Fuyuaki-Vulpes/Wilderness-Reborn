package com.fuyuaki.wilderness_reborn.data.pack.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class PackCarvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> CAVE = createKey("cave");
    public static final ResourceKey<ConfiguredWorldCarver<?>> CAVE_EXTRA_UNDERGROUND = createKey("cave_extra_underground");
    public static final ResourceKey<ConfiguredWorldCarver<?>> CANYON = createKey("canyon");

    private static ResourceKey<ConfiguredWorldCarver<?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, ResourceLocation.withDefaultNamespace(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> holdergetter = context.lookup(Registries.BLOCK);
        context.register(
                CAVE,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.2F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(320)),
                                        UniformFloat.of(0.1F, 0.9F),
                                        VerticalAnchor.aboveBottom(8),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.3F, 1.1F),
                                        UniformFloat.of(0.2F, 1.0F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )
                        )
        );
        context.register(
                CAVE_EXTRA_UNDERGROUND,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.15F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)),
                                        UniformFloat.of(0.15F, 1.2F),
                                        VerticalAnchor.aboveBottom(8),
                                        CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.5F, 1.1F),
                                        UniformFloat.of(0.35F, 1.0F),
                                        UniformFloat.of(-1.0F, -0.8F)
                                )
                        )
        );
        context.register(
                CANYON,
                WorldCarver.CANYON
                        .configured(
                                new CanyonCarverConfiguration(
                                        0.005F,
                                        UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(82)),
                                        ConstantFloat.of(3.0F),
                                        VerticalAnchor.aboveBottom(8),
                                        CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(-0.125F, 0.125F),
                                        new CanyonCarverConfiguration.CanyonShapeConfiguration(
                                                UniformFloat.of(0.75F, 1.9F), TrapezoidFloat.of(0.0F, 5.2F, 2.3F), 3, UniformFloat.of(0.75F, 1.2F), 1.0F, 0.0F
                                        )
                                )
                        )
        );
    }
}
