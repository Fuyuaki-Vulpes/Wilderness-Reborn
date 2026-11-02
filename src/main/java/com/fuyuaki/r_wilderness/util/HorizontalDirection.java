package com.fuyuaki.r_wilderness.util;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Comparator;

public enum HorizontalDirection implements StringRepresentable {
    NORTH(0, 8, "north", 0.0f, Direction.NORTH, Direction.EAST),
    NORTH_NORTH_EAST(1, 9, "north_north_east", 22.5f, Direction.NORTH, Direction.EAST),
    NORTH_EAST(2, 10, "north_east", 45.0f, Direction.NORTH, Direction.EAST),
    EAST_NORTH_EAST(3, 11, "east_north_east", 67.5f, Direction.EAST, Direction.NORTH),
    EAST(4, 12, "east", 90.0f, Direction.EAST, Direction.SOUTH),
    EAST_SOUTH_EAST(5, 13, "east_south_east", 112.5f, Direction.EAST, Direction.SOUTH),
    SOUTH_EAST(6, 14, "south_east", 135.0f, Direction.SOUTH, Direction.EAST),
    SOUTH_SOUTH_EAST(7, 15, "south_south_east", 157.5f, Direction.SOUTH, Direction.EAST),
    SOUTH(8, 0, "south", 180.0f, Direction.SOUTH, Direction.WEST),
    SOUTH_SOUTH_WEST(9, 1, "south_south_west", 202.5f, Direction.SOUTH, Direction.WEST),
    SOUTH_WEST(10, 2, "south_west", 225.0f, Direction.SOUTH, Direction.WEST),
    WEST_SOUTH_WEST(11, 3, "west_south_west", 247.5f, Direction.WEST, Direction.SOUTH),
    WEST(12, 4, "west", 270.0f, Direction.WEST, Direction.NORTH),
    WEST_NORTH_WEST(13, 5, "west_north_west", 292.5f, Direction.WEST, Direction.NORTH),
    NORTH_WEST(14, 6, "north_west", 315.0f, Direction.NORTH, Direction.WEST),
    NORTH_NORTH_WEST(15, 7, "north_north_west", 337.5f, Direction.NORTH, Direction.WEST);

    private static final HorizontalDirection[] BY_INDEX = Arrays.stream(values())
            .sorted(Comparator.comparingInt(HorizontalDirection::getIndex))
            .toArray(HorizontalDirection[]::new);

    private final int index;
    private final int oppositeIndex;
    private final String name;
    private final float degrees;
    private final Direction closestDirection;
    private final Direction secondClosestDirection;
    private final Vec3 normal;

    HorizontalDirection(int index, int oppositeIndex, String name, float degrees, Direction closest, Direction secondClosest) {
        this.index = index;
        this.oppositeIndex = oppositeIndex;
        this.name = name;
        this.degrees = degrees;
        this.closestDirection = closest;
        this.secondClosestDirection = secondClosest;

        double rad = Math.toRadians(degrees);
        double unitX = Math.sin(rad);
        double unitZ = -Math.cos(rad);
        this.normal = new Vec3(unitX, 0.0, unitZ);
    }

    public int getIndex() {
        return index;
    }

    public HorizontalDirection getOpposite() {
        return BY_INDEX[oppositeIndex];
    }

    public float getDegrees() {
        return degrees;
    }

    public Direction getClosestDirection() {
        return closestDirection;
    }

    public Direction getSecondClosestDirection() {
        return secondClosestDirection;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public double getUnitX() {
        return normal.x;
    }

    public double getUnitZ() {
        return normal.z;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static HorizontalDirection fromIndex(int index) {
        return BY_INDEX[Math.floorMod(index, BY_INDEX.length)];
    }

    public static HorizontalDirection fromAngle(float degrees) {
        int idx = Math.round(Mth.wrapDegrees(degrees) / 22.5f);
        return fromIndex(idx);
    }

    public static HorizontalDirection fromVector(double dx, double dz) {
        float degrees = (float) Math.toDegrees(Math.atan2(dx, -dz));
        return fromAngle(degrees);
    }

    public static HorizontalDirection fromVector(Vec2 vec) {
        float degrees = (float) Math.toDegrees(Math.atan2(vec.x, -vec.y));
        return fromAngle(degrees);
    }
}

