package com.fuyuaki.r_wilderness.data.generation.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenEntityTags extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public GenEntityTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
        super(pOutput, Registries.ENTITY_TYPE, pProvider, entityType -> entityType.builtInRegistryHolder().key(),MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}
