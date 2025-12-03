package com.fuyuaki.r_wilderness.data.generation.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenItemTags extends IntrinsicHolderTagsProvider<Item> {

    private final CompletableFuture<TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();


    public GenItemTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(pOutput, Registries.ITEM, pLookupProvider, item -> item.builtInRegistryHolder().key(),MODID);
        this.blockTags = blockTags;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {


    }
    protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        this.tagsToCopy.put(blockTag, itemTag);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return super.createContentsProvider().thenCombine(this.blockTags, (p_274766_, p_274767_) -> {
            this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
                TagBuilder tagbuilder = this.getOrCreateRawBuilder((TagKey<Item>)p_274764_);
                Optional<TagBuilder> optional = p_274767_.apply(p_274763_);
                optional.orElseThrow(() -> new IllegalStateException("Missing block tag " + p_274764_.location())).build().forEach(tagbuilder::add);
            });
            return (HolderLookup.Provider)p_274766_;
        });
    }


}
