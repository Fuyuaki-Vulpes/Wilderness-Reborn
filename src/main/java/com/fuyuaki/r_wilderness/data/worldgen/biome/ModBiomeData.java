package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModBiomeData {



    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carver = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(RBiomes.LUSH_RIVER, ROverworldBiomes.lushRiver(features,carver));
        context.register(RBiomes.SPARSE_CHERRY_GROVE, ROverworldBiomes.sparseCherryGrove(features,carver));
        context.register(RBiomes.SPARSE_TAIGA, ROverworldBiomes.sparseTaiga(features,carver));
        context.register(RBiomes.OASIS, ROverworldBiomes.oasis(features,carver));
        context.register(RBiomes.TUNDRA, ROverworldBiomes.tundra(features,carver));
        context.register(RBiomes.GRAVELLY_RIVER, ROverworldBiomes.gravelRiver(features,carver));
        context.register(RBiomes.LUSH_MEADOW, ROverworldBiomes.lushMeadow(features,carver));
        context.register(RBiomes.CRYSTALLINE_GROTTO, ROverworldBiomes.crystallineGrotto(features,carver));
        context.register(RBiomes.UNDERWATER_FOREST, ROverworldBiomes.underwaterForest(features,carver));
        context.register(RBiomes.MAPLE_FOREST, ROverworldBiomes.mapleForest(features,carver));
        context.register(RBiomes.ALPINE_TUNDRA, ROverworldBiomes.alpineTundra(features,carver));
        context.register(RBiomes.BOG, ROverworldBiomes.bog(features,carver));
        context.register(RBiomes.POLAR_DESERT, ROverworldBiomes.polarDesert(features,carver));
        context.register(RBiomes.CERRADO, ROverworldBiomes.cerrado(features,carver));
        context.register(RBiomes.CAATINGA, ROverworldBiomes.caatinga(features,carver));
        context.register(RBiomes.DENSE_SAVANNA, ROverworldBiomes.dense_savanna(features,carver));
        context.register(RBiomes.BARREN_CAVES, ROverworldBiomes.barrenCaves(features,carver));
    }






    public static final ResourceKey<MapCodec<? extends BiomeSource>> REBORN_SOURCE = register("reborn_source");

    public static void bootstrapSources(Registry<MapCodec<? extends BiomeSource>> registry) {
        Registry.register(registry, REBORN_SOURCE, FixedBiomeSource.CODEC);
    }

    private static ResourceKey<MapCodec<? extends BiomeSource>> register(String name) {
        return ResourceKey.create(Registries.BIOME_SOURCE, RWildernessMod.modLocation(name));
    }
}
