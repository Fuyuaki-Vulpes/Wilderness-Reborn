package com.fuyuaki.r_wilderness.client.model.block.state;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.client.model.block.TreePartModel;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record TreePartBlockStateModel(BlockModelPart model) implements DynamicBlockStateModel {

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
        ModelData data = level.getModelData(pos);

        parts.add(this.model);
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        return this;
    }

    @Override
    public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return this.model.particleIcon();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.model.particleIcon();
    }

    public record Unbaked(TreePartModel.Unbaked model) implements CustomUnbakedBlockStateModel {
        public static final MapCodec<Unbaked> CODEC = TreePartModel.Unbaked.CODEC.xmap(
                Unbaked::new, Unbaked::model
        );
        public static final Identifier ID = RWildernessMod.modLocation("tree_part_unbaked_loader");

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return CODEC;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new TreePartBlockStateModel(this.model.bake(baker));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            this.model.resolveDependencies(resolver);
        }
    }
}
