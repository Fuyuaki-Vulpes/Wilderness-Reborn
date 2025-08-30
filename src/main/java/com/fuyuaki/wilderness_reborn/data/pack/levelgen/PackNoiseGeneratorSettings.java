package com.fuyuaki.wilderness_reborn.data.pack.levelgen;


import com.fuyuaki.wilderness_reborn.data.worldgen.ModSurfaceRuleData;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;

import java.util.List;

public record PackNoiseGeneratorSettings (
        NoiseSettings noiseSettings,
        BlockState defaultBlock,
        BlockState defaultFluid,
        NoiseRouter noiseRouter,
        SurfaceRules.RuleSource surfaceRule,
        List<Climate.ParameterPoint> spawnTarget,
        int seaLevel,
        @Deprecated boolean disableMobGeneration,
        boolean aquifersEnabled,
        boolean oreVeinsEnabled,
        boolean useLegacyRandomSource
) {
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("overworld")
    );
    public static final ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("large_biomes")
    );


    public WorldgenRandom.Algorithm getRandomSource() {
        return this.useLegacyRandomSource ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO;
    }

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {
        context.register(OVERWORLD, overworld(context, false, false));
        context.register(LARGE_BIOMES, overworld(context, false, true));

    }

    public static NoiseGeneratorSettings overworld(BootstrapContext<?> context, boolean large, boolean amplified) {
        return new NoiseGeneratorSettings(
                PackNoiseSettings.OVERWORLD_NOISE_SETTINGS,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                PackNoiseRouterData.overworld(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE), amplified, large),
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
