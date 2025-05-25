package com.fuyuaki.wilderness_reborn.data.generation.other;

import com.fuyuaki.wilderness_reborn.data.generation.loot.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GenLoot {
    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> pRegistries) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(LootBlocks::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(LootGift::new, LootContextParamSets.GIFT),
                new LootTableProvider.SubProviderEntry(LootArcheology::new, LootContextParamSets.ARCHAEOLOGY),
                new LootTableProvider.SubProviderEntry(LootChest::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(LootEntity::new, LootContextParamSets.ENTITY),
                new LootTableProvider.SubProviderEntry(LootEquimpent::new, LootContextParamSets.EQUIPMENT)
        )
        ,pRegistries);
    }


    //refer to VanillaLootTableProvider (net.minecraft.data.loot.packs.VanillaLootTableProvider) for help on loot
}
