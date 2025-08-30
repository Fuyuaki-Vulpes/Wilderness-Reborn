package com.fuyuaki.wilderness_reborn.data.pack.worldgen;

import com.fuyuaki.wilderness_reborn.data.worldgen.ModWorldGenConstants;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
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
                        ModWorldGenConstants.WORLD_BOTTOM,
                        ModWorldGenConstants.WORLD_HEIGHT,
                        ModWorldGenConstants.WORLD_HEIGHT,
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0.0F,
                        new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
                )
        );

    }
}