package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.Optional;
import java.util.OptionalLong;

public class WildWorldSettings {
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = NoiseSettings.create(WildernessConstants.WORLD_BOTTOM, WildernessConstants.WORLD_HEIGHT, 1, 2);



    public static class DimensionTypes {
        public static final ResourceKey<DimensionType> OVERWORLD = ResourceKey.create(Registries.DIMENSION_TYPE, RWildernessMod.modLocation("overworld"));


        public static void bootstrap(BootstrapContext<DimensionType> context) {
            context.register(OVERWORLD,
                    new DimensionType(
                            OptionalLong.empty(),
                            true,
                            false,
                            false,
                            true,
                            1.0,
                            true,
                            false,
                            WildernessConstants.WORLD_BOTTOM,
                            WildernessConstants.WORLD_HEIGHT,
                            WildernessConstants.WORLD_HEIGHT,
                            BlockTags.INFINIBURN_OVERWORLD,
                            BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                            0.0F,
                            Optional.of(WildernessConstants.CLOUD_HEIGHT),
                            new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
                    )
            );

        }

    }

}
