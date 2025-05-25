package com.fuyuaki.wilderness_reborn.data.pack.worldgen;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

public class PackDimensionTypes {
    public static void bootstrap(BootstrapContext<DimensionType> context) {
        context.register(
                BuiltinDimensionTypes.OVERWORLD,
                new DimensionType(
                        OptionalLong.empty(),
                        true,
                        false,
                        false,
                        true,
                        1.0,
                        true,
                        false,
                        -256,
                        1024,
                        1024,
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0.0F,
                        new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
                )
        );

    }
}