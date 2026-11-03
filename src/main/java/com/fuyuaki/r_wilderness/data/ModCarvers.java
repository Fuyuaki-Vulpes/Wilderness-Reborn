package com.fuyuaki.r_wilderness.data;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class ModCarvers {
    public static final ResourceKey<WorldCarver> LAVA_TUBES = createKey("lava_tubes");
    public static final ResourceKey<WorldCarver> LARGE_CAVES = createKey("large_caves");
    public static final ResourceKey<WorldCarver> SINKHOLE = createKey("sinkhole");

    private static ResourceKey<WorldCarver> createKey(String name) {
        return ResourceKey.create(Registries.CARVER, RWildernessMod.identifier(name));
    }

    public static void bootstrap(BootstrapContext<WorldCarver> context) {
        context.register(
                LARGE_CAVES,
                                new CaveWorldCarver(
                                        0.01F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(64)),
                                        VeryBiasedToBottomInt.of(0, 14),
                                        TrapezoidFloat.of(0.2F, 1.9F,1.5f),
                                        true,
                                        UniformFloat.of(0.3F, 1.6F),
                                        UniformFloat.of(0.8F, 2.5F),
                                        UniformFloat.of(0.6F, 2.0F),
                                        ConstantFloat.of(1.0F),
                                        UniformFloat.of(-0.8F, -0.2F)
                                )

        );

        context.register(
                SINKHOLE,
                                new CaveWorldCarver(
                                        0.01F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(64), VerticalAnchor.belowTop(64)),
                                        UniformInt.of(0, 14),
                                        TrapezoidFloat.of(0.01F, 3.5F,2.0f),
                                        true,
                                        UniformFloat.of(0.8F, 1.6F),
                                        UniformFloat.of(0.0F, 0.2F),
                                        UniformFloat.of(1.6F, 8.0F),
                                        ConstantFloat.of(1.0F),
                                        UniformFloat.of(-0.01F, 0.1F)
                                )

        );

        context.register(
                LAVA_TUBES,
                                new CaveWorldCarver(
                                        0.03F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(180)),
                                        VeryBiasedToBottomInt.of(0, 14),
                                        TrapezoidFloat.of(0.25F, 0.9F,0.6f),
                                        true,
                                        UniformFloat.of(0.5F, 0.9F),
                                        UniformFloat.of(0.9F, 1.6F),
                                        UniformFloat.of(0.7F, 1.5F),
                                        ConstantFloat.of(1.0F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )

        );
        context.register(
                Carvers.CAVE,
                                new CaveWorldCarver(
                                        0.15F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                                        VeryBiasedToBottomInt.of(0, 14),
                                        TrapezoidFloat.of(0.0F, 3.0F, 1.0F),
                                        true,
                                        UniformFloat.of(0.1F, 0.9F),
                                        UniformFloat.of(0.7F, 1.4F),
                                        UniformFloat.of(0.8F, 1.3F),
                                        ConstantFloat.of(1.0F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )

        );
        context.register(
                Carvers.CAVE_EXTRA_UNDERGROUND,
                                new CaveWorldCarver(
                                        0.07F,
                                        UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(47)),
                                        VeryBiasedToBottomInt.of(0, 14),
                                        TrapezoidFloat.of(0.0F, 3.0F, 1.0F),
                                        true,
                                        UniformFloat.of(0.1F, 0.9F),
                                        UniformFloat.of(0.7F, 1.4F),
                                        UniformFloat.of(0.8F, 1.3F),
                                        ConstantFloat.of(1.0F),
                                        UniformFloat.of(-1.0F, -0.4F)
                                )

        );
        context.register(
                Carvers.CANYON,
                                new CanyonWorldCarver(
                                        0.01F,
                                        UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)),
                                        UniformFloat.of(-0.125F, 0.125F),
                                        new CanyonWorldCarver.Shape(
                                                UniformFloat.of(0.75F, 1.0F), TrapezoidFloat.of(0.0F, 6.0F, 2.0F), 3, UniformFloat.of(0.75F, 1.0F), 1.0F, 0.0F, ConstantFloat.of(3.0F)
                                        )
                                )

        );


    }


}
