package com.fuyuaki.r_wilderness.data.generation.other;

import com.fuyuaki.r_wilderness.data.ModCarvers;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeData;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeModifiers;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModPlacementUtils;
import com.fuyuaki.r_wilderness.data.worldgen.ModNoiseData;
import com.fuyuaki.r_wilderness.world.item.enchantment.ModEnchantments;
import com.fuyuaki.r_wilderness.world.level.levelgen.ModNoiseRouterData;
import com.fuyuaki.r_wilderness.world.level.levelgen.ModWorldPresets;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildWorldSettings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModBuiltInEntries extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModFeatureUtils::bootstrap)
            .add(Registries.CONFIGURED_CARVER, ModCarvers::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacementUtils::bootstrap)
            .add(Registries.NOISE, ModNoiseData::bootstrap)
            .add(Registries.DENSITY_FUNCTION, ModNoiseRouterData::bootstrap)
            .add(Registries.BIOME, ModBiomeData::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)
            .add(Registries.WORLD_PRESET, ModWorldPresets::bootstrap)
            .add(Registries.DIMENSION_TYPE, WildWorldSettings.DimensionTypes::bootstrap)
            .add(Registries.NOISE_SETTINGS, WildWorldSettings.NoiseGenerator::bootstrap)

            ;


    public ModBuiltInEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
}
