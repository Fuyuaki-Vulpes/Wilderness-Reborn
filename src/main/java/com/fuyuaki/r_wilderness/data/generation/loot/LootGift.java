package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class LootGift implements LootTableSubProvider {
    private final LootTableSubProvider.Context output;
    private final HolderGetter<ChickenVariant> chickenVariants;

    public LootGift(LootTableSubProvider.Context output) {
        this.output = output;
        this.chickenVariants = output.lookup(Registries.CHICKEN_VARIANT);
    }

    @Override
    public void run() {

    }
}
