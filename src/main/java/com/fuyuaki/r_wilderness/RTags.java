package com.fuyuaki.r_wilderness;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class RTags {
    public static class Blocks {

        private static TagKey<Block> common(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> create(String name) {
            return BlockTags.create(RWildernessMod.modLocation(name));
        }
    }
}