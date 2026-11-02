package com.fuyuaki.r_wilderness.data.generation.other;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.data.ModCarvers;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeData;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeModifiers;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomePlacements;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModPlacementUtils;
import com.fuyuaki.r_wilderness.init.RDamageTypes;
import com.fuyuaki.r_wilderness.world.item.enchantment.ModEnchantments;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldPresets;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldConfiguration;
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
            .add(Registries.BIOME, ModBiomeData::bootstrap)
            .add(RRegistries.REBORN_BIOME_PLACEMENT_KEY, ModBiomePlacements::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)
            .add(Registries.WORLD_PRESET, RebornWorldPresets::bootstrap)
            .add(Registries.DIMENSION_TYPE, RebornWorldConfiguration.DimensionTypes::bootstrap)
            .add(Registries.DAMAGE_TYPE, RDamageTypes::bootstrap)
            .add(RRegistries.WRGEN_SETTINGS, com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldSettings::bootstrap)
            ;


    public ModBuiltInEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
}
