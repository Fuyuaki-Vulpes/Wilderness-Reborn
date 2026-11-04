package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class LootArcheology implements LootTableSubProvider {

    private final LootTableSubProvider.Context output;
    public LootArcheology(LootTableSubProvider.Context output) {
        this.output = output;
    }

    @Override
    public void run() {

    }
}
