package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.util.RebAABB;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaterBody {

    public static final Codec<WaterBody> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    RiverNode.GridPoint.CODEC.fieldOf("position").forGetter(WaterBody::getPosition),
                    Vec2.CODEC.fieldOf("offset").forGetter(WaterBody::getOffset),
                    Codec.FLOAT.fieldOf("control_area").forGetter(WaterBody::getControlArea),
                    Codec.FLOAT.fieldOf("depth").forGetter(WaterBody::getDepth),
                    Codec.FLOAT.fieldOf("y_elevation").forGetter(WaterBody::getElevation),
                    RegionPos.CODEC.fieldOf("region").forGetter(WaterBody::getRegionPos)
            ).apply(i,WaterBody::new));
    public static final int MAX_DEPTH = 16;

    private static final long PRIME_1 = 0x9e3779b97f4a7c15L;
    private static final long PRIME_2 = 0xbf58476d1ce4e5b9L;
    private static final long PRIME_3 = 0x94d049bb133111ebL;
    private static final long SALT = 0xc4ceb9fe1a85ec53L;
    public static final int MAX_DIAMETER = 64;

    private final RiverNode.GridPoint position;
    private float controlArea = 0.0f;
    private Vec2 offset;
    private float elevation = WildernessConstants.WORLD_BOTTOM;
    private float depth = 0.0f;
    private boolean enabled = false;
    private final RegionPos regionPos;
    private List<RiverPoint> points = new ArrayList<>();
    private RebAABB collision;


    public WaterBody(RiverNode.GridPoint position, Vec2 offset, float controlArea, float depth, float elevation, RegionPos pos) {
        this.position = position;
        this.offset = offset;
        this.elevation = elevation;
        this.depth = depth;
        this.controlArea = controlArea;
        this.regionPos = pos;
    }

    public WaterBody(RegionPos pos, int cellX, int cellZ, RegionNoisesRecord record, float centerX, float centerZ, long hash, WorldRegion region) {
        this.position = new RiverNode.GridPoint(cellX, cellZ);
        this.offset = new Vec2(centerX, centerZ);
        this.regionPos = pos;

        float worldX = getWorldX();
        float worldZ = getWorldZ();

        this.controlArea = Math.clamp(
                (1 + getSaltedFloat(hash,3))
                        * ((region.layers().getHumidity().getGlobal(Mth.floor(worldX), Mth.floor(worldZ)) + 1) / 2)
                        * 8 + 8f,
                8, 16);


        this.elevation = region.layers().getHeightmapBasic().getGlobal(Mth.floor(worldX), Mth.floor(worldZ));
    }

    public float getDistance(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        return (float) Math.sqrt(dx * dx + dz * dz);
    }
    public float getDistanceSquared(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        return dx * dx + dz * dz;
    }


    public RebAABB getCollision() {
        return collision;
    }

    public boolean touches(float x, float z){
        return this.collision != null && this.collision.contains(x,z);
    }

    public float getWorldX() {
        return worldPosition(this.getCellX() + this.getOffset().x) + RegionPos.toBlockCoord(this.getRegionPos().x());
    }

    public float getWorldZ() {
        return worldPosition(this.getCellZ() + this.getOffset().y) + RegionPos.toBlockCoord(this.getRegionPos().z());
    }

    private float getCellX() {
        return this.getPosition().x();
    }

    private float getCellZ() {
        return this.getPosition().z();
    }

    public RiverNode.GridPoint getPosition() {
        return position;
    }

    public float getControlArea() {
        return controlArea;
    }

    public void setControlArea(float controlArea) {
        this.controlArea = controlArea;
    }

    public Vec2 getOffset() {
        return offset;
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RegionPos getRegionPos() {
        return regionPos;
    }

    public List<RiverPoint> getPoints() {
        return points;
    }

    public static float worldPosition(float pos) {
        return pos * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
    }
    public RiverNode.WorldPosition getWorldPosition() {
        return new RiverNode.WorldPosition(this.getWorldX(),this.getWorldZ());
    }

    public static long coordinateHash(int x, int z, long seed) {
        long hashedSeed = seed ^ SALT;
        long hash = PRIME_1 + hashedSeed;

        hash ^= ((long) x * PRIME_2);
        hash = Long.rotateLeft(hash, 27) * PRIME_3;

        hash ^= ((long) z * PRIME_3);
        hash = Long.rotateLeft(hash, 31) * PRIME_2;

        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;

        return hash;
    }

    public static float getSaltedFloat(long baseHash, int propertyIndex) {
        long uniqueHash = baseHash + propertyIndex;

        uniqueHash ^= uniqueHash >>> 33;
        uniqueHash *= 0xff51afd7ed558ccdL;
        uniqueHash ^= uniqueHash >>> 33;
        uniqueHash *= 0xc4ceb9fe1a85ec53L;
        uniqueHash ^= uniqueHash >>> 33;

        return (float) (uniqueHash & 0xFFFFFF) / 16777215.0f;
    }

    public static WaterBody read(CompoundTag nbt) throws IOException {
        return nbt.read("river",CODEC).orElseThrow();
    }

    public void write(CompoundTag nbt) throws Exception {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE,this);
        if (result.isError() || result.getOrThrow() == null)  {
            throw new Exception("Hey Yagasa check this out please");
        }
        nbt.put("body",result.getOrThrow());
        if (nbt == null || nbt.isEmpty()) {
            throw new Exception("NBT cannot be null!");
        }
    }

    public enum Type implements StringRepresentable {
        LAKE("lake"),
        FLOODPLAIN("floods"),
        DELTA("delta"),
        OCEAN("ocean"),
        ;
        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
