package com.fuyuaki.r_wilderness.client.model.block;

import com.fuyuaki.r_wilderness.client.model.block.state.TreePartState;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record TreePartModel(QuadCollection quads, TextureAtlasSprite particleIcon) implements BlockModelPart {

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        return this.quads.getQuads(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }
    public record Unbaked(Identifier modelLocation, TreePartState modelState) implements BlockModelPart.Unbaked {

        // Used for the unbaked block state model
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Identifier.CODEC.fieldOf("model").forGetter(Unbaked::modelLocation)
                ).apply(instance, model -> new Unbaked(model,new TreePartState()))
        );


        @Override
        public BlockModelPart bake(ModelBaker baker) {

            // Get the model to bake
            ResolvedModel resolvedModel = baker.getModel(this.modelLocation);

            // Get the necessary settings for the model part
            TextureSlots slots = resolvedModel.getTopTextureSlots();
            TextureAtlasSprite particle = resolvedModel.resolveParticleSprite(slots, baker);
            QuadCollection quads = resolvedModel.bakeTopGeometry(slots, baker, this.modelState);

            // Return the baked part
            return new TreePartModel(quads,  particle);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(this.modelLocation);
        }
    }
}
