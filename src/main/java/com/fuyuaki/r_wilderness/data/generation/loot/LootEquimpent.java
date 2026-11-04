package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class LootEquimpent implements LootTableSubProvider {

    private final LootTableSubProvider.Context output;
    private final HolderGetter<TrimPattern> trimPatterns;
    private final HolderGetter<TrimMaterial> trimMaterials;
    private final HolderGetter<Enchantment> enchantments;

    public LootEquimpent(LootTableSubProvider.Context output) {
        this.output = output;
        this.trimPatterns = output.lookup(Registries.TRIM_PATTERN);
        this.trimMaterials = output.lookup(Registries.TRIM_MATERIAL);
        this.enchantments = output.lookup(Registries.ENCHANTMENT);
    }

    @Override
    public void run() {
        ArmorTrim flowTrim = new ArmorTrim(this.trimMaterials.getOrThrow(TrimMaterials.COPPER), this.trimPatterns.getOrThrow(TrimPatterns.FLOW));
        ArmorTrim boltTrim = new ArmorTrim(this.trimMaterials.getOrThrow(TrimMaterials.COPPER), this.trimPatterns.getOrThrow(TrimPatterns.BOLT));

    }
}
