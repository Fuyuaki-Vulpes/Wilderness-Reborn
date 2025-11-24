package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.common.ModTags;
import com.fuyuaki.r_wilderness.data.ModCarvers;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModAquaticPlacements;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModFrozenPlacements;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Arrays;
import java.util.Set;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_LARGE_CAVES = registerKey("add_large_caves");
    public static final ResourceKey<BiomeModifier> ADD_SINKHOLES = registerKey("add_sinkholes");
    public static final ResourceKey<BiomeModifier> REMOVE_BLOBS = registerKey("remove_blobs");
    public static final ResourceKey<BiomeModifier> SPIKES_FROZEN_PEAKS = registerKey("spikes_frozen_peaks");
    public static final ResourceKey<BiomeModifier> SPIKES_BEACH = registerKey("spikes_beach");
    public static final ResourceKey<BiomeModifier> WATER_DELTAS = registerKey("water_deltas");
    public static final ResourceKey<BiomeModifier> WATER_DELTAS_COMMON = registerKey("water_deltas_common");
    public static final ResourceKey<BiomeModifier> WATER_DELTAS_VERY_COMMON = registerKey("water_deltas_very_common");
    public static final ResourceKey<BiomeModifier> WATER_DELTAS_LARGE = registerKey("water_deltas_large");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(REMOVE_BLOBS,
                new BiomeModifiers.RemoveFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                        getHolder(placedFeatures,OrePlacements.ORE_GRAVEL, MiscOverworldPlacements.DISK_CLAY,MiscOverworldPlacements.DISK_GRAVEL,MiscOverworldPlacements.DISK_SAND),
                        Set.of(GenerationStep.Decoration.values())
                )
        );

        context.register(SPIKES_FROZEN_PEAKS,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                getBiome(biomes,Biomes.FROZEN_PEAKS),
                        getHolder(placedFeatures, ModFrozenPlacements.SHARP_ICE_SPIKES_COMMON),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )
        );
        context.register(SPIKES_BEACH,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                getBiome(biomes,Biomes.SNOWY_BEACH),
                        getHolder(placedFeatures, ModFrozenPlacements.SHARP_ICE_SPIKES),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )

        );
        context.register(WATER_DELTAS,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.Biomes.HAS_WATER_DELTAS_REGULAR),
                        getHolder(placedFeatures, ModAquaticPlacements.WATER_DELTAS),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )
        );

        context.register(WATER_DELTAS_COMMON,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.Biomes.HAS_WATER_DELTAS_COMMON),
                        getHolder(placedFeatures, ModAquaticPlacements.WATER_DELTAS_COMMON),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )
        );

        context.register(WATER_DELTAS_VERY_COMMON,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.Biomes.HAS_WATER_DELTAS_VERY_COMMON),
                        getHolder(placedFeatures, ModAquaticPlacements.WATER_DELTAS_VERY_COMMON),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )
        );

        context.register(WATER_DELTAS_LARGE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_WATER_DELTAS_LARGE),
                        getHolder(placedFeatures, ModAquaticPlacements.WATER_DELTAS_LARGE),
                        GenerationStep.Decoration.SURFACE_STRUCTURES
                )
        );
        context.register(ADD_LARGE_CAVES,
                new BiomeModifiers.AddCarversBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                        HolderSet.direct(carvers.getOrThrow(ModCarvers.LARGE_CAVES))

                )
        );
        context.register(ADD_SINKHOLES,
                new BiomeModifiers.AddCarversBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_OVERWORLD),
                        HolderSet.direct(carvers.getOrThrow(ModCarvers.SINKHOLE))

                )
        );

    }



    private static HolderSet<Biome> getBiome(HolderGetter<Biome> biomeGetter, ResourceKey<Biome> biome) {
        return HolderSet.direct(biomeGetter.getOrThrow(biome));
    }

    @SafeVarargs
    private static HolderSet<Biome> getBiome(HolderGetter<Biome> placedFeatures, ResourceKey<Biome>... biomes) {
        return HolderSet.direct(Arrays.stream(biomes).map(placedFeatures::getOrThrow).toList());
    }



    private static HolderSet<PlacedFeature> getHolder(HolderGetter<PlacedFeature> placedFeatures, ResourceKey<PlacedFeature> feature) {
        return HolderSet.direct(placedFeatures.getOrThrow(feature));
    }

    @SafeVarargs
    private static HolderSet<PlacedFeature> getHolder(HolderGetter<PlacedFeature> placedFeatures, ResourceKey<PlacedFeature>... features) {
        return HolderSet.direct(Arrays.stream(features).map(placedFeatures::getOrThrow).toList());
    }


    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RWildernessMod.modLocation(name));
    }
}
