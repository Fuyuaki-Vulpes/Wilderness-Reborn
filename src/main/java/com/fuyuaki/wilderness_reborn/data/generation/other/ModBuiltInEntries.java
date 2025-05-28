package com.fuyuaki.wilderness_reborn.data.generation.other;

import com.fuyuaki.wilderness_reborn.data.worldgen.biome.ModBiomeData;
import com.fuyuaki.wilderness_reborn.data.worldgen.biome.ModBiomeModifiers;
import com.fuyuaki.wilderness_reborn.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.wilderness_reborn.data.worldgen.placement.ModPlacementUtils;
import com.fuyuaki.wilderness_reborn.data.worldgen.ModNoiseData;
import com.fuyuaki.wilderness_reborn.world.item.enchantment.ModEnchantments;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoiseRouterData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModBuiltInEntries extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModFeatureUtils::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacementUtils::bootstrap)
            .add(Registries.NOISE, ModNoiseData::bootstrap)
            .add(Registries.DENSITY_FUNCTION, ModNoiseRouterData::bootstrap)
            .add(Registries.BIOME, ModBiomeData::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)

            ;


    public ModBuiltInEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
}
