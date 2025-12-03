package com.fuyuaki.r_wilderness.world.generation.hydrology;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum NeighborDirection implements StringRepresentable {
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    EAST(Direction.EAST),
    WEST(Direction.WEST)
    ;
    public final Direction direction;

    NeighborDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String getSerializedName() {
        return this.direction.getSerializedName();
    }
}
