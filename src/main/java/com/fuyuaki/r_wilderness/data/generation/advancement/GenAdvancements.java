package com.fuyuaki.r_wilderness.data.generation.advancement;

import com.fuyuaki.r_wilderness.data.generation.advancement.packs.ModAdventureAdvancements;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GenAdvancements extends AdvancementProvider {

    public GenAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries,
                List.of(
                        new ModAdventureAdvancements()
                )
        );
    }


}
