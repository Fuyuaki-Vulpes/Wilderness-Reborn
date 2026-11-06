package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.material.OverworldMaterialRules;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.Optional;

public record RebornWorldSettings(
        NoiseSettings noiseSettings,
        NoiseRouter noiseRouter,
        List<RebornSpawnTarget> spawnTarget,
        BlockState defaultBlock,
        BlockState defaultFluid,
        Holder<MaterialRule> materialRule,
        Optional<Aquifer.Config> aquifers,
        int seaLevel,
        int lavaLevel) {


    public static final Codec<RebornWorldSettings> DIRECT_CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            NoiseSettings.CODEC.fieldOf("noise").forGetter(RebornWorldSettings::noiseSettings),
                            NoiseRouter.CODEC.fieldOf("noise_router").forGetter(RebornWorldSettings::noiseRouter),
                            RebornSpawnTarget.CODEC.listOf().fieldOf("spawn_target").forGetter(RebornWorldSettings::spawnTarget),
                            BlockState.CODEC.fieldOf("default_block").forGetter(RebornWorldSettings::defaultBlock),
                            BlockState.CODEC.fieldOf("default_fluid").forGetter(RebornWorldSettings::defaultFluid),
                            MaterialRule.HOLDER_CODEC.fieldOf("material_rule").forGetter(RebornWorldSettings::materialRule),
                            Aquifer.Config.CODEC.optionalFieldOf("aquifers").forGetter(RebornWorldSettings::aquifers),
                            Codec.INT.fieldOf("sea_level").forGetter(RebornWorldSettings::seaLevel),
                            Codec.INT.fieldOf("lava_level").forGetter(RebornWorldSettings::lavaLevel)
                    )
                    .apply(i, RebornWorldSettings::new)
    );


    public static final Codec<Holder<RebornWorldSettings>> HOLDER_CODEC = RegistryCodecs.holder(RRegistries.WRGEN_SETTINGS, DIRECT_CODEC);

    public static final ResourceKey<RebornWorldSettings> OVERWORLD = ResourceKey.create(
            RRegistries.WRGEN_SETTINGS, RWildernessMod.identifier("overworld")
    );

    public static void bootstrap(BootstrapContext<RebornWorldSettings> context) {
        context.register(OVERWORLD, overworld(context));
    }


    public static RebornWorldSettings overworld(BootstrapContext<?> context) {
        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise> noises = context.lookup(Registries.NOISE);
        OverworldFunctionSet<ResourceKey<DensityFunction>> functionNames;

        functionNames = NoiseRouterData.OVERWORLD_FUNCTIONS;

        NoiseRouter router = RebornRouterData.overworld(functions, functionNames);
        return new RebornWorldSettings(
                NoiseSettings.create(WildernessConstants.WORLD_BOTTOM, WildernessConstants.WORLD_HEIGHT),
                router,
                RebornGenerationUtils.spawnTargets(WildernessConstants.SEA_LEVEL),
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD),
                Optional.of(RebornRouterData.overworldAquifers(functions, noises, functionNames)),
                WildernessConstants.SEA_LEVEL,
                WildernessConstants.CAVE_LAVA
        );
    }


}
