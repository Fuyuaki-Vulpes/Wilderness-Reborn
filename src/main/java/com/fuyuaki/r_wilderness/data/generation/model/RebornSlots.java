package com.fuyuaki.r_wilderness.data.generation.model;

import net.minecraft.client.data.models.model.TextureSlot;

public class RebornSlots {

    public static final TextureSlot CROSS_TOP = create("cross_top");
    public static final TextureSlot DIRT = create("dirt");
    public static final TextureSlot OVERLAY = create("overlay");
    public static final TextureSlot GRASS = create("grass");
    public static final TextureSlot TOP_GRASS = create("top_grass");
    public static final TextureSlot LOG = create("log");
    public static final TextureSlot STRIPPED_LOG = create("stripped_log");

    public static TextureSlot create(String id) {
        return TextureSlot.create(id);
    }

    public static TextureSlot create(String id, TextureSlot parent) {
        return TextureSlot.create(id,parent);
    }

}
