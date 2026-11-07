package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RWBiomeData {



    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<WorldCarver> carver = context.lookup(Registries.CARVER);

        context.register(RWBiomes.LUSH_RIVER, RWOverworldBiomes.lushRiver(features,carver));
        context.register(RWBiomes.SPARSE_CHERRY_GROVE, RWOverworldBiomes.sparseCherryGrove(features,carver));
        context.register(RWBiomes.SPARSE_TAIGA, RWOverworldBiomes.sparseTaiga(features,carver));
        context.register(RWBiomes.OASIS, RWOverworldBiomes.oasis(features,carver));
        context.register(RWBiomes.TUNDRA, RWOverworldBiomes.tundra(features,carver));
        context.register(RWBiomes.GRAVELLY_RIVER, RWOverworldBiomes.gravelRiver(features,carver));
        context.register(RWBiomes.LUSH_MEADOW, RWOverworldBiomes.lushMeadow(features,carver));
        context.register(RWBiomes.CRYSTALLINE_GROTTO, RWOverworldBiomes.crystallineGrotto(features,carver));
        context.register(RWBiomes.UNDERWATER_FOREST, RWOverworldBiomes.underwaterForest(features,carver));
        context.register(RWBiomes.MAPLE_FOREST, RWOverworldBiomes.mapleForest(features,carver));
        context.register(RWBiomes.ALPINE_TUNDRA, RWOverworldBiomes.alpineTundra(features,carver));
        context.register(RWBiomes.BOG, RWOverworldBiomes.bog(features,carver));
        context.register(RWBiomes.POLAR_DESERT, RWOverworldBiomes.polarDesert(features,carver));
        context.register(RWBiomes.CERRADO, RWOverworldBiomes.cerrado(features,carver));
        context.register(RWBiomes.CAATINGA, RWOverworldBiomes.caatinga(features,carver));
        context.register(RWBiomes.DENSE_SAVANNA, RWOverworldBiomes.dense_savanna(features,carver));
        context.register(RWBiomes.BARREN_CAVES, RWOverworldBiomes.barrenCaves(features,carver));
    }






    public static final ResourceKey<MapCodec<? extends BiomeSource>> REBORN_SOURCE = register("reborn_source");

    public static void bootstrapSources(Registry<MapCodec<? extends BiomeSource>> registry) {
        Registry.register(registry, REBORN_SOURCE, FixedBiomeSource.CODEC);
    }

    private static ResourceKey<MapCodec<? extends BiomeSource>> register(String name) {
        return ResourceKey.create(Registries.BIOME_SOURCE, RWildernessMod.identifier(name));
    }
}
