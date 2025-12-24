package com.fuyuaki.r_wilderness.client.model.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record TreeBlockModelPart(QuadCollection quads, TextureAtlasSprite particleIcon) implements BlockModelPart {
    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        return this.quads.getQuads(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return particleIcon;
    }

    public record Unbaked(Identifier identifier, TreeModelState state) implements BlockModelPart.Unbaked{
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                p_465619_ -> p_465619_.group(
                                Identifier.CODEC.fieldOf("model").forGetter(Unbaked::identifier),
                                TreeModelState.MAP_CODEC.forGetter(Unbaked::state)
                        )
                        .apply(p_465619_, Unbaked::new)
        );
        public static final Codec<Unbaked> CODEC = MAP_CODEC.codec();

        @Override
        public TreeBlockModelPart bake(ModelBaker modelBaker) {
            ResolvedModel resolvedModel = modelBaker.getModel(this.identifier);

            TextureSlots slots = resolvedModel.getTopTextureSlots();
            TextureAtlasSprite particle = resolvedModel.resolveParticleSprite(slots, modelBaker);
            QuadCollection quads = resolvedModel.bakeTopGeometry(slots, modelBaker, this.state);
            return new TreeBlockModelPart(quads, particle);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(identifier);
        }
    }
}
