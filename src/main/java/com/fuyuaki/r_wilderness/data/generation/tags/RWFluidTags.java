package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.world.level.material.RFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RWFluidTags extends FluidTagsProvider{
    public RWFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(FluidTags.WATER)
                .add(RFluids.RIVER.key())
                .add(RFluids.FLOWING_RIVER.key());

        this.tag(FluidTags.SUPPORTS_LILY_PAD).add(RFluids.RIVER.key());
        this.tag(FluidTags.SUPPORTS_FROGSPAWN).add(RFluids.RIVER.key());
        this.tag(FluidTags.BUBBLE_COLUMN_CAN_OCCUPY).add(RFluids.RIVER.key());

    }
}
