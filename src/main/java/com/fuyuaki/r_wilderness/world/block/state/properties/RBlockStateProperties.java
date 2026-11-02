package com.fuyuaki.r_wilderness.world.block.state.properties;

import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RBlockStateProperties {

    public static final EnumProperty<HorizontalDirection> HORIZONTAL_DIRECTION_16 = EnumProperty.create("direction", HorizontalDirection.class, HorizontalDirection.values());
    public static final IntegerProperty FLOW_STRENGTH = IntegerProperty.create("strength", 1, 1000);

    public static final IntegerProperty THICKNESS = IntegerProperty.create("thickness", 1, 24);
    public static final EnumProperty<Direction> SUPPORT = EnumProperty.create(
            "support", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
    );
    public static final BooleanProperty CARVED = BooleanProperty.create("carved");

}
