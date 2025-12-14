package com.fuyuaki.r_wilderness.client.model.block.state;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;

public class TreePartState implements ModelState {

    @Override
    public Transformation transformation() {
        return Transformation.identity();
    }

    @Override
    public Matrix4fc faceTransformation(Direction direction) {
        return NO_TRANSFORM;
    }

    @Override
    public Matrix4fc inverseFaceTransformation(Direction direction) {
        return NO_TRANSFORM;
    }

}
