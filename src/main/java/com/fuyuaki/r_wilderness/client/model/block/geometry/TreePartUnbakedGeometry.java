package com.fuyuaki.r_wilderness.client.model.block.geometry;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.data.generation.model.RebornSlots;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.UniformValue;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.QuadCollectionBuilderExtension;
import net.neoforged.neoforge.client.model.ExtendedUnbakedGeometry;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceBuilder;

public class TreePartUnbakedGeometry implements ExtendedUnbakedGeometry {
    @Override
    public QuadCollection bake(TextureSlots slots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap properties) {
        QuadCollection.Builder builder = new QuadCollection.Builder();

        float thickness = (float) properties.getOrDefault(RWildernessMod.contextKey("tree_thickness"), 1);
        for (Direction direction : Direction.values()){
            ContextKey<Integer> key = RWildernessMod.contextKey("tree_" + direction.getName());
            if (properties.has(key)){
                float dirThickness = (float) Math.max(properties.getOrDefault(key, 1), 8);
                boolean carved = properties.getOrDefault(RWildernessMod.contextKey("tree_carved"),false);

                Vec3i offset = direction.getUnitVec3i();
                if (thickness > 8){
                    offset = offset.multiply(2);
                }
                float x1, y1, z1;
                float x2, y2, z2;
                if (offset.getX() == 0){
                    x1 = -dirThickness;
                    x2 = dirThickness;
                }else{
                    x1 = 0;
                    x2 = offset.getX();
                }if (offset.getY() == 0){
                    y1 = -dirThickness;
                    y2 = dirThickness;
                }else{
                    y1 = 0;
                    y2 = offset.getY();

                }if (offset.getZ() == 0){
                    z1 = -dirThickness;
                    z2 = dirThickness;
                }else{
                    z1 = 0;
                    z2 = offset.getZ();

                }


                Vec3 from = new Vec3(
                        Math.min(x1,x2),
                        Math.min(y1,y2),
                        Math.min(z1,z2)
                );

                Vec3 to = new Vec3(
                        Math.max(x1,x2),
                        Math.max(y1,y2),
                        Math.max(z1,z2)
                );

            }

        }

        return builder.build();
    }
}
