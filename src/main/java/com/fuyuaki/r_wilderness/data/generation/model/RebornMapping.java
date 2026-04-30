package com.fuyuaki.r_wilderness.data.generation.model;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RebornMapping {

    public static TextureMapping singleSlot(TextureSlot slot, Material texture) {
        return new TextureMapping().put(slot, texture);
    }
    public static TextureMapping crossEmissive(Block block) {
        return new TextureMapping().put(TextureSlot.CROSS, getBlockTexture(block)).put(TextureSlot.CROSS_EMISSIVE, getBlockTexture(block, "_emissive"));
    }

    public static TextureMapping cross(Material texture) {
        return singleSlot(TextureSlot.CROSS, texture);
    }

    public static Material getBlockTexture(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        return new Material(id.withPath(path -> "block/" + path));
    }

    public static Material getBlockTexture(Block block, String suffix) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        return new Material(id.withPath(path -> "block/" + path + suffix));
    }

    public static Identifier getItemTexture(Item item) {
        Identifier Identifier = BuiltInRegistries.ITEM.getKey(item);
        return Identifier.withPrefix("item/");
    }

    public static Identifier getItemTexture(Item item, String suffix) {
        Identifier Identifier = BuiltInRegistries.ITEM.getKey(item);
        return Identifier.withPath(string -> "item/" + string + suffix);
    }

    public static TextureMapping crossBottom(Block block) {
        return new TextureMapping().put(TextureSlot.CROSS, getBlockTexture(block,"_bottom")).put(RebornSlots.CROSS_TOP, getBlockTexture(block, "_top"));

    }
}
