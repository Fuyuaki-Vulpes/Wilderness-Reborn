package com.fuyuaki.r_wilderness.client.model.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuadHelper {

    private static final Map<QuadCacheKey, BakedQuad> OVERLAY_CACHE = new ConcurrentHashMap<>();

    private static QuadCacheKey buildCacheKey(BakedQuad quad, TextureAtlasSprite sprite)
    {
        return new QuadCacheKey(quad.direction(), quad.position0(), quad.position1(), quad.position2(), quad.position3(), quad.bakedNormals(), sprite);
    }
    public static void onResourceReload(@SuppressWarnings("unused") ResourceManager resourceManager)
    {
        OVERLAY_CACHE.clear();
    }
}
