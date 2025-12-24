package com.fuyuaki.r_wilderness.client.model.block;

import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;

public class TreeModelState implements ModelState {

    public static final MapCodec<TreeModelState> MAP_CODEC = MapCodec.unit(new TreeModelState());


    @Override
    public Transformation transformation() {
        return Transformation.identity();
    }

    @Override
    public Matrix4fc faceTransformation(Direction facing) {
        return NO_TRANSFORM;
    }

    @Override
    public Matrix4fc inverseFaceTransformation(Direction facing) {
        return NO_TRANSFORM;
    }
}
