package com.fuyuaki.r_wilderness.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class RebAABB {
    private static final float EPSILON = 1.0E-7f;
    public static final RebAABB INFINITE = new RebAABB(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    public final float minX;
    public final float minZ;
    public final float maxX;
    public final float maxZ;

    public RebAABB(float minX, float minZ, float maxX, float maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public RebAABB(BlockPos pos) {
        this(pos.getX(), pos.getZ(), pos.getX() + 1, pos.getZ() + 1);
    }

    public RebAABB(Vec2 begin, Vec2 end) {
        this(begin.x, begin.y,  end.x, end.y);
    }

    public static RebAABB of(BoundingBox box) {
        return new RebAABB(box.minX(), box.minZ(), box.maxX() + 1, box.maxZ() + 1);
    }

    public static RebAABB unitCubeFromLowerCorner(Vec2 pos) {
        return new RebAABB(pos.x, pos.y,pos.x + 1.0f, pos.y + 1.0f);
    }

    public static RebAABB encapsulatingFullBlocks(BlockPos pos0, BlockPos pos1) {
        return new RebAABB(
                Math.min(pos0.getX(), pos1.getX()),
                Math.min(pos0.getZ(), pos1.getZ()),
                Math.max(pos0.getX(), pos1.getX()) + 1,
                Math.max(pos0.getZ(), pos1.getZ()) + 1
        );
    }

    public RebAABB setMinX(float minX) {
        return new RebAABB(minX, this.minZ, this.maxX, this.maxZ);
    }


    public RebAABB setMinZ(float minZ) {
        return new RebAABB(this.minX, minZ, this.maxX,this.maxZ);
    }

    public RebAABB setMaxX(float maxX) {
        return new RebAABB(this.minX, this.minZ, maxX, this.maxZ);
    }

    public RebAABB setMaxZ(float maxZ) {
        return new RebAABB(this.minX, this.minZ, this.maxX, maxZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof RebAABB RebAABB) {
            if (Float.compare(RebAABB.minX, this.minX) != 0) {
                return false;
            } else if (Float.compare(RebAABB.minZ, this.minZ) != 0) {
                return false;
            } else {
                return Float.compare(RebAABB.maxX, this.maxX) != 0 ? false : Float.compare(RebAABB.maxZ, this.maxZ) == 0;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(this.minX);
        result = prime * result + Float.floatToIntBits(this.minZ);
        result = prime * result + Float.floatToIntBits(this.maxX);
        return prime * result + Float.floatToIntBits(this.maxZ);

    }

    public RebAABB contract(float xa, float za) {
        float minX = this.minX;
        float minZ = this.minZ;
        float maxX = this.maxX;
        float maxZ = this.maxZ;
        if (xa < 0.0) {
            minX -= xa;
        } else if (xa > 0.0) {
            maxX -= xa;
        }


        if (za < 0.0) {
            minZ -= za;
        } else if (za > 0.0) {
            maxZ -= za;
        }

        return new RebAABB(minX, minZ, maxX, maxZ);
    }

    public RebAABB expandTowards(Vec2 delta) {
        return this.expandTowards(delta.x, delta.y);
    }

    public RebAABB expandTowards(float xa, float za) {
        float minX = this.minX;
        float minZ = this.minZ;
        float maxX = this.maxX;
        float maxZ = this.maxZ;
        if (xa < 0.0) {
            minX += xa;
        } else if (xa > 0.0) {
            maxX += xa;
        }

        if (za < 0.0) {
            minZ += za;
        } else if (za > 0.0) {
            maxZ += za;
        }

        return new RebAABB(minX, minZ, maxX,  maxZ);
    }

    public RebAABB inflate(float xAdd, float zAdd) {
        float minX = this.minX - xAdd;
        float minZ = this.minZ - zAdd;
        float maxX = this.maxX + xAdd;
        float maxZ = this.maxZ + zAdd;
        return new RebAABB(minX, minZ, maxX, maxZ);
    }

    public RebAABB inflate(float amountToAddInAllDirections) {
        return this.inflate(amountToAddInAllDirections, amountToAddInAllDirections);
    }

    public RebAABB intersect(RebAABB other) {
        float minX = Math.max(this.minX, other.minX);
        float minZ = Math.max(this.minZ, other.minZ);
        float maxX = Math.min(this.maxX, other.maxX);
        float maxZ = Math.min(this.maxZ, other.maxZ);
        return new RebAABB(minX, minZ, maxX, maxZ);
    }

    public RebAABB minmax(RebAABB other) {
        float minX = Math.min(this.minX, other.minX);
        float minZ = Math.min(this.minZ, other.minZ);
        float maxX = Math.max(this.maxX, other.maxX);
        float maxZ = Math.max(this.maxZ, other.maxZ);
        return new RebAABB(minX, minZ, maxX, maxZ);
    }

    public RebAABB move(float xa, float za) {
        return new RebAABB(this.minX + xa, this.minZ + za, this.maxX + xa, this.maxZ + za);
    }

    public RebAABB move(BlockPos pos) {
        return new RebAABB(
                this.minX + pos.getX(), this.minZ + pos.getZ(), this.maxX + pos.getX(), this.maxZ + pos.getZ()
        );
    }

    public RebAABB move(Vec2 pos) {
        return this.move(pos.x, pos.y);
    }

    public RebAABB move(Vector2f pos) {
        return this.move(pos.x, pos.y);
    }

    public boolean intersects(RebAABB RebAABB) {
        return this.intersects(RebAABB.minX, RebAABB.minZ, RebAABB.maxX, RebAABB.maxZ);
    }

    public boolean intersects(float minX, float minZ, float maxX, float maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minZ < maxZ && this.maxZ > minZ;
    }

    public boolean intersects(Vec2 min, Vec2 max) {
        return this.intersects(
                Math.min(min.x, max.x), Math.min(min.y, max.y), Math.max(min.x, max.x),Math.max(min.y, max.y)
        );
    }

    public boolean intersects(BlockPos pos) {
        return this.intersects(pos.getX(), pos.getZ(), pos.getX() + 1, pos.getZ() + 1);
    }

    public boolean contains(Vec2 vec) {
        return this.contains(vec.x, vec.y);
    }

    public boolean contains(float x,float z) {
        return x >= this.minX && x < this.maxX && z >= this.minZ && z < this.maxZ;
    }

    public float getSize() {
        float xs = this.getXsize();
        float zs = this.getZsize();
        return (xs + zs) / 2.0f;
    }

    public float getXsize() {
        return this.maxX - this.minX;
    }


    public float getZsize() {
        return this.maxZ - this.minZ;
    }

    public RebAABB deflate(float xSubstract, float zSubtract) {
        return this.inflate(-xSubstract, -zSubtract);
    }

    public RebAABB deflate(float amount) {
        return this.inflate(-amount);
    }


    public float distanceToSqr(Vec2 point) {
        float dx = Math.max(Math.max(this.minX - point.x, point.x - this.maxX), 0.0f);
        float dz = Math.max(Math.max(this.minZ - point.y, point.y - this.maxZ), 0.0f);
        return (float) Mth.lengthSquared(dx, dz);
    }

    public float distanceToSqr(RebAABB boundingBox) {
        float dx = Math.max(Math.max(this.minX - boundingBox.maxX, boundingBox.minX - this.maxX), 0.0f);
        float dz = Math.max(Math.max(this.minZ - boundingBox.maxZ, boundingBox.minZ - this.maxZ), 0.0f);
        return (float) Mth.lengthSquared(dx, dz);
    }

    @Override
    public String toString() {
        return "AABB[" + this.minX + ", " +  this.minZ + "] -> [" + this.maxX + ", " + this.maxZ + "]";
    }

    public boolean hasNaN() {
        return Float.isNaN(this.minX)
                || Float.isNaN(this.minZ)
                || Float.isNaN(this.maxX)
                || Float.isNaN(this.maxZ);
    }

    public Vec2 getCenter() {
        return new Vec2(Mth.lerp(0.5f, this.minX, this.maxX), Mth.lerp(0.5f, this.minZ, this.maxZ));
    }

    public Vec2 getBottomCenter() {
        return new Vec2(Mth.lerp(0.5f, this.minX, this.maxX), Mth.lerp(0.5f, this.minZ, this.maxZ));
    }

    public Vec2 getMinPosition() {
        return new Vec2(this.minX, this.minZ);
    }

    public Vec2 getMaxPosition() {
        return new Vec2(this.maxX, this.maxZ);
    }

    public static RebAABB ofSize(Vec2 center, float sizeX, float sizeZ) {
        return new RebAABB(
                center.x - sizeX / 2.0f, center.y - sizeZ / 2.0f, center.x + sizeX / 2.0f, center.y + sizeZ / 2.0f
        );
    }

    /**
     * {@return true if this RebAABB is infinite in all directions}
     */
    public boolean isInfinite() {
        return this == INFINITE || (Float.isInfinite(this.minX) && Float.isInfinite(this.minZ)
                && Float.isInfinite(this.maxX) && Float.isInfinite(this.maxZ));
    }

    public static class Builder {
        private float minX = Float.POSITIVE_INFINITY;
        private float minY = Float.POSITIVE_INFINITY;
        private float maxX = Float.NEGATIVE_INFINITY;
        private float maxY = Float.NEGATIVE_INFINITY;
        private boolean defined;

        public void include(Vector2fc v) {
            this.minX = Math.min(this.minX, v.x());
            this.minY = Math.min(this.minY, v.y());
            this.maxX = Math.max(this.maxX, v.x());
            this.maxY = Math.max(this.maxY, v.y());
            this.defined = true;
        }

        public boolean isDefined() {
            return this.defined;
        }

        public RebAABB build() {
            if (!this.defined) {
                throw new IllegalStateException("Cannot build an undefined RebAABB. Include at least one point.");
            } else {
                return new RebAABB(this.minX, this.minY, this.maxX, this.maxY);
            }
        }
    }


}
