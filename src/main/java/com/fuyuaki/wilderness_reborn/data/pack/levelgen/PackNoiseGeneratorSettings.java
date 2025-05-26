package com.fuyuaki.wilderness_reborn.data.pack.levelgen;

import com.fuyuaki.wilderness_reborn.data.pack.worldgen.PackSurfaceRuleData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.RegistryFileCodec;
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
    public static final Codec<NoiseGeneratorSettings> DIRECT_CODEC = RecordCodecBuilder.create(
            p_64475_ -> p_64475_.group(
                            NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings),
                            BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::defaultBlock),
                            BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::defaultFluid),
                            NoiseRouter.CODEC.fieldOf("noise_router").forGetter(NoiseGeneratorSettings::noiseRouter),
                            SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(NoiseGeneratorSettings::surfaceRule),
                            Climate.ParameterPoint.CODEC.listOf().fieldOf("spawn_target").forGetter(NoiseGeneratorSettings::spawnTarget),
                            Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel),
                            Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration),
                            Codec.BOOL.fieldOf("aquifers_enabled").forGetter(NoiseGeneratorSettings::isAquifersEnabled),
                            Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(NoiseGeneratorSettings::oreVeinsEnabled),
                            Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource)
                    )
                    .apply(p_64475_, NoiseGeneratorSettings::new)
    );
    public static final Codec<Holder<NoiseGeneratorSettings>> CODEC = RegistryFileCodec.create(Registries.NOISE_SETTINGS, DIRECT_CODEC);
    public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("overworld")
    );
    public static final ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("large_biomes")
    );
    public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("amplified")
    );
    public static final ResourceKey<NoiseGeneratorSettings> NETHER = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("nether")
    );
    public static final ResourceKey<NoiseGeneratorSettings> END = ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("end"));
    public static final ResourceKey<NoiseGeneratorSettings> CAVES = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("caves")
    );
    public static final ResourceKey<NoiseGeneratorSettings> FLOATING_ISLANDS = ResourceKey.create(
            Registries.NOISE_SETTINGS, ResourceLocation.withDefaultNamespace("floating_islands")
    );

    public boolean isAquifersEnabled() {
        return this.aquifersEnabled;
    }

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
                PackSurfaceRuleData.overworld(),
                new OverworldBiomeBuilder().spawnTarget(),
                63,
                false,
                true,
                true,
                false
        );
    }



}
