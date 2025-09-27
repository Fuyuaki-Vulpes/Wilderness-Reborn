package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GlobalLootModifiers extends GlobalLootModifierProvider {
    public GlobalLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    @Override
    protected void start() {

    }
}
