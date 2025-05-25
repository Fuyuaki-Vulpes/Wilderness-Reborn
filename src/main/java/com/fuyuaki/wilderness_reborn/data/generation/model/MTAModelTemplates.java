package com.fuyuaki.wilderness_reborn.data.generation.model;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;


public class MTAModelTemplates {
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



    public static ModelTemplate create(TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), requiredSlots);
    }


    public static ModelTemplate createItem(String name, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateItemModelLocation(name)), Optional.empty(), requiredSlots);
    }

    public static ModelTemplate createItem(String name, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateItemModelLocation(name)), Optional.of(suffix), requiredSlots);
    }



    public static ResourceLocation decorateItemModelLocation(String name) {
        return WildernessRebornMod.mod(name).withPrefix("item/");
    }
}
