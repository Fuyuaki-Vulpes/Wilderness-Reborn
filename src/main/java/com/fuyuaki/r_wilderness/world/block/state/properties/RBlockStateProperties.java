package com.fuyuaki.r_wilderness.world.block.state.properties;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RBlockStateProperties {
    public static final EnumProperty<Direction> BASE = EnumProperty.create(
            "base", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
    );

    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 1, 8);
}
