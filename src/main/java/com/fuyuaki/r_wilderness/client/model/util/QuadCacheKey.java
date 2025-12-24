package com.fuyuaki.r_wilderness.client.model.util;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record QuadCacheKey (
        Direction face,
        Vector3fc pos0,
        Vector3fc pos1,
        Vector3fc pos2,
        Vector3fc pos3,
        BakedNormals normals,
        TextureAtlasSprite sprite
)
{
    public void pos(int vert, Vector3f out)
    {
        out.set(switch (vert)
        {
            case 0 -> pos0;
            case 1 -> pos1;
            case 2 -> pos2;
            case 3 -> pos3;
            default -> throw new IndexOutOfBoundsException(vert);
        });
    }

    public void normal(int vert, Vector3f out)
    {
        BakedNormals.unpack(normals.normal(vert), out);
    }
}