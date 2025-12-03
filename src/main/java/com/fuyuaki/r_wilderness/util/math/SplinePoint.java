package com.fuyuaki.r_wilderness.util.math;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class SplinePoint {
    public double x, y, z;

    public Vec3 vec3()
    {
        return new Vec3(x,y,z);
    }

    public SplinePoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SplinePoint(Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    // Helper method to add two SplinePoints
    public SplinePoint add(SplinePoint other) {
        return new SplinePoint(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // Helper method to subtract two SplinePoints
    public SplinePoint subtract(SplinePoint other) {
        return new SplinePoint(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    // Helper method to multiply a SplinePoint by a scalar
    public SplinePoint multiply(float scalar) {
        return new SplinePoint(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // Helper method to divide a SplinePoint by a scalar
    public SplinePoint divide(float scalar) {
        return new SplinePoint(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplinePoint that = (SplinePoint) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "SplinePoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
