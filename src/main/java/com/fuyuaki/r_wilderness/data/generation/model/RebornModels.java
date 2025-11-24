package com.fuyuaki.r_wilderness.data.generation.model;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;


public class RebornModels {
    public static final ModelTemplate BIG_HANDHELD_LOW_HILT = createItem("handheld_big_low_hilt", TextureSlot.LAYER0);
    public static final ModelTemplate BIG_HANDHELD_MEDIUM_HILT = createItem("handheld_big_medium_hilt", TextureSlot.LAYER0);
    public static final ModelTemplate BIG_HANDHELD_MIDDLE_HILT = createItem("handheld_big_middle_hilt", TextureSlot.LAYER0);
    public static final ModelTemplate SPRINKLER_INVENTORY = createItem("template_sprinkler", TextureSlot.PARTICLE);
    public static final ModelTemplate CROSSBOW = createItem("template_crossbow", TextureSlot.LAYER0);
    public static final ModelTemplate BOW = createItem("template_bow", TextureSlot.LAYER0);
    public static final ModelTemplate BOW_BIG = createItem("template_bow_big", TextureSlot.LAYER0);
    public static final ModelTemplate TRIDENT = createItem("template_trident", TextureSlot.PARTICLE);
    public static final ModelTemplate TRIDENT_THROWING = createItem("template_trident_throwing", TextureSlot.PARTICLE);

    public static final ModelTemplate CLAW = createItem("handheld_claw", TextureSlot.LAYER0);

    public static final ModelTemplate GRASS_FOLIAGE = create("foliage",TextureSlot.CROSS);
    public static final ModelTemplate EMISSIVE_FOLIAGE = create("foliage_emissive",TextureSlot.CROSS,TextureSlot.CROSS_EMISSIVE);
    public static final ModelTemplate FERN_FOLIAGE = create("fern",TextureSlot.CROSS);
    public static final ModelTemplate FERN_FOLIAGE_BOTTOM = create("fern_bottom_large",TextureSlot.CROSS,RebornSlots.CROSS_TOP);
    public static final ModelTemplate TEMPLATE_GRASS_BLOCK = create("template_grass_block",RebornSlots.GRASS,RebornSlots.DIRT,RebornSlots.OVERLAY);
    public static final ModelTemplate TEMPLATE_GRASS_BLOCK_NO_TINT = create("template_grass_block_no_tint",RebornSlots.GRASS,RebornSlots.DIRT,RebornSlots.OVERLAY);


    public static ModelTemplate create(TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), requiredSlots);
    }

    public static ModelTemplate create(String name, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateBlockModelLocation(name)), Optional.empty(), requiredSlots);
    }

    public static ModelTemplate createItem(String name, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateItemModelLocation(name)), Optional.empty(), requiredSlots);
    }

    public static ModelTemplate createItem(String name, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateItemModelLocation(name)), Optional.of(suffix), requiredSlots);
    }

    public static ModelTemplate create(String name, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateBlockModelLocation(name)), Optional.of(suffix), requiredSlots);
    }

    public static ResourceLocation decorateBlockModelLocation(String name) {
        // Neo: Use ResourceLocation.parse to support modded paths
        return RWildernessMod.modLocation(name).withPrefix("block/");
    }
    public static ResourceLocation decorateItemModelLocation(String name) {
        return RWildernessMod.modLocation(name).withPrefix("item/");
    }


}
