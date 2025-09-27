package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.data.pack.levelgen.PackNoiseRouterData;
import com.fuyuaki.r_wilderness.data.worldgen.ModSurfaceRuleData;
import com.fuyuaki.r_wilderness.world.generation.ModWorldGenConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.Optional;
import java.util.OptionalLong;

public class WildWorldSettings {
    protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = NoiseSettings.create(ModWorldGenConstants.WORLD_BOTTOM, ModWorldGenConstants.WORLD_HEIGHT, 1, 2);


    public static class NoiseGenerator{

        public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(
                Registries.NOISE_SETTINGS, RWildernessMod.modLocation("overworld")
        );

        public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {
            context.register(OVERWORLD, overworld(context));

        }

        public static NoiseGeneratorSettings overworld(BootstrapContext<?> context) {
            return new NoiseGeneratorSettings(
                    WildWorldSettings.OVERWORLD_NOISE_SETTINGS,
                    Blocks.STONE.defaultBlockState(),
                    Blocks.WATER.defaultBlockState(),
                    PackNoiseRouterData.overworld(context.lookup(Registries.DENSITY_FUNCTION)),
                    ModSurfaceRuleData.overworld(),
                    new OverworldBiomeBuilder().spawnTarget(),
                    68,
                    false,
                    true,
                    true,
                    false
            );
        }
    }

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
                            ModWorldGenConstants.WORLD_BOTTOM,
                            ModWorldGenConstants.WORLD_HEIGHT,
                            ModWorldGenConstants.WORLD_HEIGHT,
                            BlockTags.INFINIBURN_OVERWORLD,
                            BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                            0.0F,
                            Optional.of(ModWorldGenConstants.CLOUD_HEIGHT),
                            new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
                    )
            );

        }

    }

}
