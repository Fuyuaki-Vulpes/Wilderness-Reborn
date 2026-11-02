package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.world.level.material.RFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidIds;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenFluidTags extends FluidTagsProvider{
    public GenFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(FluidTags.WATER)
                .add(RFluids.RIVER.getKey())
                .add(RFluids.FLOWING_RIVER.getKey());

        this.tag(FluidTags.SUPPORTS_LILY_PAD).add(RFluids.RIVER.getKey());
        this.tag(FluidTags.SUPPORTS_FROGSPAWN).add(RFluids.RIVER.getKey());
        this.tag(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY).add(RFluids.RIVER.getKey());

    }
}
