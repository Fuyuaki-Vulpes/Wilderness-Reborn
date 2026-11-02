package com.fuyuaki.r_wilderness.data.generation.advancement;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GenAdvancements extends AdvancementProvider {

    public GenAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(getAdvancementProviders());
    }

    private static List<AdvancementSubProvider.Factory> getAdvancementProviders() {

    }

}
