package com.fuyuaki.r_wilderness;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class RTags {
    public static class Blocks {
        public static final TagKey<Block> SCHINITE_ORE_REPLACEABLES = create("schinite_ore_replaceables");
        public static final TagKey<Block> MAGNEISS_ORE_REPLACEABLES = create("magneiss_ore_replaceables");
        public static final TagKey<Block> MALATITE_ORE_REPLACEABLES = create("malatite_ore_replaceables");


        private static TagKey<Block> common(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> create(String name) {
            return BlockTags.create(RWildernessMod.modLocation(name));
        }
    }
}