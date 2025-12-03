package com.fuyuaki.r_wilderness.data;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class ModCarvers {
    public static final ResourceKey<ConfiguredWorldCarver<?>> LAVA_TUBES = createKey("lava_tubes");
    public static final ResourceKey<ConfiguredWorldCarver<?>> LARGE_CAVES = createKey("large_caves");
    public static final ResourceKey<ConfiguredWorldCarver<?>> SINKHOLE = createKey("sinkhole");

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
                                        0.01F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(64)),
                                        UniformFloat.of(0.2F, 1.9F),
                                        VerticalAnchor.aboveBottom(32),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.8F, 2.5F),
                                        UniformFloat.of(0.6F, 2.0F),
                                        UniformFloat.of(-0.8F, -0.2F)
                                )
                        )
        );

        context.register(
                SINKHOLE,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.01F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(64), VerticalAnchor.belowTop(64)),
                                        UniformFloat.of(0.01F, 3.5F),
                                        VerticalAnchor.aboveBottom(128),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.0F, 0.2F),
                                        UniformFloat.of(1.6F, 8.0F),
                                        UniformFloat.of(-0.01F, 0.1F)
                                )
                        )
        );

        context.register(
                LAVA_TUBES,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.03F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(180)),
                                        UniformFloat.of(0.25F, 0.9F),
                                        VerticalAnchor.aboveBottom(128),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.9F, 1.6F),
                                        UniformFloat.of(0.7F, 1.5F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )
                        )
        );
        context.register(
                Carvers.CAVE,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.05F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                                        UniformFloat.of(0.1F, 0.9F),
                                        VerticalAnchor.aboveBottom(32),
                                        CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.7F, 1.4F),
                                        UniformFloat.of(0.8F, 1.3F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )
                        )
        );
        context.register(
                Carvers.CAVE_EXTRA_UNDERGROUND,
                WorldCarver.CAVE
                        .configured(
                                new CaveCarverConfiguration(
                                        0.02F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)),
                                        UniformFloat.of(0.1F, 0.9F),
                                        VerticalAnchor.aboveBottom(32),
                                        CarverDebugSettings.of(false, Blocks.OAK_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(0.7F, 1.4F),
                                        UniformFloat.of(0.8F, 1.3F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )
                        )
        );
        context.register(
                Carvers.CANYON,
                WorldCarver.CANYON
                        .configured(
                                new CanyonCarverConfiguration(
                                        0.001F,
                                        UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)),
                                        ConstantFloat.of(3.0F),
                                        VerticalAnchor.aboveBottom(64),
                                        CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()),
                                        holdergetter.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                        UniformFloat.of(-0.125F, 0.125F),
                                        new CanyonCarverConfiguration.CanyonShapeConfiguration(
                                                UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.15F
                                        )
                                )
                        )
        );


    }


}
