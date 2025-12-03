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


        context.register(RBiomes.BARREN_CAVES,ROverworldBiomes.barrenCaves(features,carver));
    }






    public static final ResourceKey<MapCodec<? extends BiomeSource>> REBORN_SOURCE = register("reborn_source");

    public static void bootstrapSources(Registry<MapCodec<? extends BiomeSource>> registry) {
        Registry.register(registry, REBORN_SOURCE, FixedBiomeSource.CODEC);
    }

    private static ResourceKey<MapCodec<? extends BiomeSource>> register(String name) {
        return ResourceKey.create(Registries.BIOME_SOURCE, RWildernessMod.modLocation(name));
    }
}
