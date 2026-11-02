package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;

import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeBlockTagsProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenBlockTags extends BlockTagsProvider {

    public GenBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output,  lookupProvider,MODID);
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
        this.tag(Tags.BlockItems.STONES).add(
                RBlocks.WEATHERED_BEDROCK.get(),
                RBlocks.SCHINITE.get(),
                RBlocks.MAGNEISS.get(),
                RBlocks.MALATITE.get()
        );

        this.tag(Tags.BlockItems.COBBLESTONES).add(
               RBlocks.COBBLED_SCHINITE.get(),
               RBlocks.COBBLED_MAGNEISS.get(),
               RBlocks.COBBLED_MALATITE.get()
        );
        this.tag(BlockTags.WALLS).add(
                RBlocks.SCHINITE_WALL.get(),
                RBlocks.MAGNEISS_WALL.get(),
                RBlocks.MALATITE_WALL.get(),
                RBlocks.COBBLED_SCHINITE_WALL.get(),
                RBlocks.COBBLED_MAGNEISS_WALL.get(),
                RBlocks.COBBLED_MALATITE_WALL.get(),
                RBlocks.WEATHERED_BEDROCK_WALL.get()
        );
        this.tag(BlockTags.SLABS).add(
                RBlocks.SCHINITE_SLAB.get(),
                RBlocks.MAGNEISS_SLAB.get(),
                RBlocks.MALATITE_SLAB.get(),
                RBlocks.COBBLED_SCHINITE_SLAB.get(),
                RBlocks.COBBLED_MAGNEISS_SLAB.get(),
                RBlocks.COBBLED_MALATITE_SLAB.get(),
                RBlocks.WEATHERED_BEDROCK_SLAB.get(),
                RBlocks.ALPINE_SLAB.get()
        );
        this.tag(BlockTags.STAIRS).add(
               RBlocks.SCHINITE_STAIRS.get(),
               RBlocks.MAGNEISS_STAIRS.get(),
               RBlocks.MALATITE_STAIRS.get(),
               RBlocks.COBBLED_SCHINITE_STAIRS.get(),
               RBlocks.COBBLED_MAGNEISS_STAIRS.get(),
               RBlocks.COBBLED_MALATITE_STAIRS.get(),
               RBlocks.WEATHERED_BEDROCK_STAIRS.get(),
               RBlocks.ALPINE_STAIRS.get()
        );
        this.tag(BlockTags.WOODEN_FENCES).add(
               RBlocks.ALPINE_FENCE.get()
        );
        this.tag(BlockTags.FENCE_GATES).add(
               RBlocks.ALPINE_FENCE_GATE.get()
        );
        this.tag(BlockTags.SIGNS).add(
               RBlocks.ALPINE_SIGN.get()
        );
        this.tag(BlockTags.WALL_SIGNS).add(
               RBlocks.ALPINE_WALL_SIGN.get()
        );
        this.tag(BlockTags.CEILING_HANGING_SIGNS).add(
               RBlocks.ALPINE_HANGING_SIGN.get()
        );
        this.tag(BlockTags.WALL_HANGING_SIGNS).add(
               RBlocks.ALPINE_HANGING_WALL_SIGN.get()
        );

        this.tag(BlockTags.TRAPDOORS).add(
               RBlocks.ALPINE_TRAPDOOR.get()
        );
        this.tag(BlockTags.DOORS).add(
               RBlocks.ALPINE_DOOR.get()
        );
        this.tag(BlockTags.BUTTONS).add(
               RBlocks.ALPINE_BUTTON.get()
        );
        this.tag(BlockTags.PRESSURE_PLATES).add(
               RBlocks.ALPINE_PRESSURE_PLATE.get()
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
                RBlocks.WEATHERED_BEDROCK.get(),
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
                RBlocks.WEATHERED_BEDROCK.get(),
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
    private void addColored(TagKey<Block> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            Identifier key = Identifier.fromNamespaceAndPath("minecraft", pattern.replace("{color}", color.getName()));
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            Block block = BuiltInRegistries.BLOCK.getValue(key);
            if (block == null || block == Blocks.AIR)
                throw new IllegalStateException("Unknown vanilla block: " + key);
            tag(tag).add(block);
        }
    }

    protected record Appender(TagAppender<Block> app) implements TagAppender<Block> {
        @Override
        public Appender add(ResourceKey<Block> element) {
            app.add(element);
            return this;
        }

        @Override
        public Appender addOptional(ResourceKey<Block> element) {
            app.addOptional(element);
            return this;
        }

        @Override
        public Appender addTag(TagKey<Block> tag) {
            app.addTag(tag);
            return this;
        }

        @Override
        public Appender addOptionalTag(TagKey<Block> tag) {
            app.addOptionalTag(tag);
            return this;
        }

        @Override
        public Appender add(TagEntry entry) {
            app.add(entry);
            return this;
        }

        @Override
        public Appender replace(boolean value) {
            app.replace(value);
            return this;
        }

        @Override
        public Appender remove(ResourceKey<Block> element) {
            app.remove(element);
            return this;
        }

        @Override
        public Appender remove(TagKey<Block> tag) {
            app.remove(tag);
            return this;
        }

        public Appender add(Block... blocks) {
            for (Block block : blocks) {
                add(BuiltInRegistries.BLOCK.wrapAsHolder(block).key());
            }
            return this;
        }

        public Appender addAll(Iterable<Block> blocks) {
            for (Block block : blocks) {
                add(block);
            }
            return this;
        }
    }

    @Override
    protected Appender tag(TagKey<Block> tag) {
        return new Appender(super.tag(tag));
    }

    private Appender tag(BlockItemTagId tag) {
        return tag(tag.block());
    }
    private void addColoredTags(Consumer<TagKey<Block>> consumer, TagKey<Block> group) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor color : DyeColor.values()) {
            TagKey<Block> tag = getForgeTag(prefix + color.getName());
            consumer.accept(tag);
        }
    }

    @SuppressWarnings("unchecked")
    private TagKey<Block> getForgeTag(String name) {
        try {
            name = name.toUpperCase(Locale.ENGLISH);
            return (TagKey<Block>) Tags.Blocks.class.getDeclaredField(name).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
        }
    }
}
