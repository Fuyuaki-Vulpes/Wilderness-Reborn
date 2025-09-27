package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public record LootEquimpent(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        HolderLookup.RegistryLookup<TrimPattern> registrylookup = this.registries.lookupOrThrow(Registries.TRIM_PATTERN);
        HolderLookup.RegistryLookup<TrimMaterial> registrylookup1 = this.registries.lookupOrThrow(Registries.TRIM_MATERIAL);
        HolderLookup.RegistryLookup<Enchantment> registrylookup2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        ArmorTrim armortrim = new ArmorTrim(registrylookup1.getOrThrow(TrimMaterials.COPPER), registrylookup.getOrThrow(TrimPatterns.FLOW));
        ArmorTrim armortrim1 = new ArmorTrim(registrylookup1.getOrThrow(TrimMaterials.COPPER), registrylookup.getOrThrow(TrimPatterns.BOLT));

    }
}
