package com.fuyuaki.r_wilderness.data.generation.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenEntityTags extends TagsProvider<EntityType<?>> {
    public GenEntityTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider) {
        super(pOutput, Registries.ENTITY_TYPE, pProvider,MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}
