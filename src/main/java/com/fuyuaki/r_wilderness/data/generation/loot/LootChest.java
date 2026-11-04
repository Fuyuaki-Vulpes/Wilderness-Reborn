package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class LootChest implements LootTableSubProvider {
    private final LootTableSubProvider.Context output;
    private final HolderGetter<Biome> biomes;
    private final HolderGetter<Enchantment> enchantments;
    private final HolderGetter<Instrument> instruments;
    private final HolderGetter<Structure> structures;

    public LootChest(LootTableSubProvider.Context output) {
        this.output = output;
        this.biomes = output.lookup(Registries.BIOME);
        this.enchantments = output.lookup(Registries.ENCHANTMENT);
        this.instruments = output.lookup(Registries.INSTRUMENT);
        this.structures = output.lookup(Registries.STRUCTURE);
    }
    @Override
    public void run() {

    }
}
