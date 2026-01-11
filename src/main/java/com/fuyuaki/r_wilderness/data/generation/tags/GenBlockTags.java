package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenBlockTags extends IntrinsicHolderTagsProvider<Block> {

    public GenBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.BLOCK, lookupProvider, block -> block.builtInRegistryHolder().key(),MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(RTags.Blocks.ROOT_BLOCK_GROWS_INTO).addTag(BlockTags.DIRT);

        this.tag(Tags.Blocks.VILLAGER_FARMLANDS).add(
                RBlocks.CHALKY_FARMLAND.get(),
                RBlocks.CLAY_FARMLAND.get(),
                RBlocks.PEAT_FARMLAND.get(),
                RBlocks.SANDY_FARMLAND.get(),
                RBlocks.SILT_FARMLAND.get()
        );
        this.tag(BlockTags.BASE_STONE_OVERWORLD).add(
                RBlocks.SCHINITE.get(),
                RBlocks.MAGNEISS.get(),
                RBlocks.MALATITE.get()
        );
        this.tag(Tags.Blocks.STONES).add(
                RBlocks.SCHINITE.get(),
                RBlocks.MAGNEISS.get(),
                RBlocks.MALATITE.get()
        );

        this.tag(Tags.Blocks.COBBLESTONES).add(
               RBlocks.COBBLED_SCHINITE.get(),
               RBlocks.COBBLED_MAGNEISS.get(),
               RBlocks.COBBLED_MALATITE.get()
        );

        this.tag(BlockTags.DIRT).add(
                RBlocks.CHALKY_SOIL.get(),
                RBlocks.CLAY_SOIL.get(),
                RBlocks.PEAT.get(),
                RBlocks.SILT.get(),
                RBlocks.SANDY_SOIL.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                RBlocks.CHALKY_SOIL.get(),
                RBlocks.CLAY_SOIL.get(),
                RBlocks.PEAT.get(),
                RBlocks.SILT.get(),
                RBlocks.SANDY_SOIL.get(),
                RBlocks.CHALKY_FARMLAND.get(),
                RBlocks.CLAY_FARMLAND.get(),
                RBlocks.PEAT_FARMLAND.get(),
                RBlocks.SANDY_FARMLAND.get(),
                RBlocks.SILT_FARMLAND.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                RBlocks.SCHINITE.get(),
                RBlocks.MAGNEISS.get(),
                RBlocks.MALATITE.get(),
                RBlocks.COBBLED_SCHINITE.get(),
                RBlocks.COBBLED_MAGNEISS.get(),
                RBlocks.COBBLED_MALATITE.get(),
                RBlocks.CHALK.get(),
                RBlocks.LIMESTONE.get(),
                RBlocks.MUD_STONE.get()
        );
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(
                RBlocks.SCHINITE.get(),
                RBlocks.COBBLED_SCHINITE.get()
        );
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                RBlocks.MAGNEISS.get(),
                RBlocks.COBBLED_MAGNEISS.get()
        );
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                RBlocks.MALATITE.get(),
                RBlocks.COBBLED_MALATITE.get()
        );
        this.tag(RTags.Blocks.SCHINITE_ORE_REPLACEABLES).add(
                RBlocks.SCHINITE.get()
        );
        this.tag(RTags.Blocks.MAGNEISS_ORE_REPLACEABLES).add(
                RBlocks.MAGNEISS.get()
        );
        this.tag(RTags.Blocks.MALATITE_ORE_REPLACEABLES).add(
                RBlocks.MALATITE.get()
        );

        this.tag(RTags.Blocks.SLIGHTLY_INCREASES_TEMPERATURE).add(
                Blocks.TORCH,
                Blocks.LANTERN
        );
        this.tag(RTags.Blocks.MODERATELY_INCREASES_TEMPERATURE).add(
                Blocks.CAMPFIRE,
                Blocks.FIRE
        );
        this.tag(RTags.Blocks.GREATLY_INCREASES_TEMPERATURE).add(
                Blocks.LAVA,
                Blocks.LAVA_CAULDRON,
                Blocks.MAGMA_BLOCK
        );

        this.tag(RTags.Blocks.INCREASES_TEMPERATURE)
                .addTag(RTags.Blocks.SLIGHTLY_INCREASES_TEMPERATURE)
                .addTag(RTags.Blocks.MODERATELY_INCREASES_TEMPERATURE)
                .addTag(RTags.Blocks.GREATLY_INCREASES_TEMPERATURE);


        this.tag(RTags.Blocks.SLIGHTLY_DECREASES_TEMPERATURE).add(
                Blocks.SOUL_FIRE,
                Blocks.SOUL_LANTERN
        );
        this.tag(RTags.Blocks.MODERATELY_DECREASES_TEMPERATURE).add(
                Blocks.SOUL_CAMPFIRE,
                Blocks.SOUL_FIRE
        );
        this.tag(RTags.Blocks.GREATLY_DECREASES_TEMPERATURE).add(
                Blocks.SNOW
        );
        this.tag(RTags.Blocks.DECREASES_TEMPERATURE)
                .addTag(RTags.Blocks.SLIGHTLY_DECREASES_TEMPERATURE)
                .addTag(RTags.Blocks.MODERATELY_DECREASES_TEMPERATURE)
                .addTag(RTags.Blocks.GREATLY_DECREASES_TEMPERATURE);



    }
}
