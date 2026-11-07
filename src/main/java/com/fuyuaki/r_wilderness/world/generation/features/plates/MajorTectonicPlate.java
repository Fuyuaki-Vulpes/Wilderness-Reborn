package com.fuyuaki.r_wilderness.world.generation.features.plates;

import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class MajorTectonicPlate {

    public static final Codec<MajorTectonicPlate> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    Codec.INT.fieldOf("id").forGetter(MajorTectonicPlate::getId),
                    Vec2.CODEC.fieldOf("center").forGetter(MajorTectonicPlate::getCenter),
                    Vec2.CODEC.fieldOf("velocity").forGetter(MajorTectonicPlate::getVelocity),
                    Codec.FLOAT.fieldOf("influence").forGetter(MajorTectonicPlate::getInfluence),
                    Codec.FLOAT.fieldOf("weight").forGetter(MajorTectonicPlate::getWeight),
                    Codec.FLOAT.fieldOf("age").forGetter(MajorTectonicPlate::getAge),
                    Vec2.CODEC.fieldOf("stretch").forGetter(MajorTectonicPlate::getStretch),
                    Codec.FLOAT.fieldOf("cos_theta").forGetter(MajorTectonicPlate::getCosTheta),
                    Codec.FLOAT.fieldOf("sin_theta").forGetter(MajorTectonicPlate::getSinTheta),
                    Codec.INT.fieldOf("cell_x").forGetter(MajorTectonicPlate::getCellX),
                    Codec.INT.fieldOf("cell_z").forGetter(MajorTectonicPlate::getCellZ),
                    RegionPos.CODEC.fieldOf("region_pos").forGetter(MajorTectonicPlate::getRegionPos)
            ).apply(i, MajorTectonicPlate::new)
    );
    private static final float MAX_ANGLE = (float) (Math.PI / 4.0);

    private static final long PRIME_1 = 0x9e3779b97f4a7c15L;
    private static final long PRIME_2 = 0xbf58476d1ce4e5b9L;
    private static final long PRIME_3 = 0x94d049bb133111ebL;
    private static final long SALT = 0x9f01e1ee4e2adac8L;
    private static final float LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT = 0.65f;

    public final int id;

    public final float centerX, centerZ;
    public final float velocityX, velocityZ;
    public final float influence;
    public final float weight;

    public final float age;

    public final float stretchX;
    public final float stretchZ;
    public final float cosTheta;
    public final float sinTheta;
    private final int cellX, cellZ;
    private final RegionPos regionPos;

    private static Codec<MajorTectonicPlate> codec() {
        return CODEC;
    }

    public MajorTectonicPlate(int id, float centerX, float centerZ, float velocityX, float velocityZ, float influence, float weight, float age, float stretchX, float stretchZ, float cosTheta, float sinTheta, int cellX, int cellZ, RegionPos regionPos) {
        this.id = id;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.velocityX = velocityX;
        this.velocityZ = velocityZ;
        this.influence = influence;
        this.weight = weight;
        this.age = age;
        this.stretchX = stretchX;
        this.stretchZ = stretchZ;
        this.cosTheta = cosTheta;
        this.sinTheta = sinTheta;
        this.cellX = cellX;
        this.cellZ = cellZ;
        this.regionPos = regionPos;
    }

    public MajorTectonicPlate(int id, Vec2 center, Vec2 velocity, float influence, float weight, float age, Vec2 stretch, float cosTheta, float sinTheta, int cellX, int cellZ, RegionPos regionPos) {
        this.id = id;
        this.centerX = center.x;
        this.centerZ = center.y;
        this.velocityX = velocity.x;
        this.velocityZ = velocity.y;
        this.influence = influence;
        this.weight = weight;
        this.age = age;
        this.stretchX = stretch.x;
        this.stretchZ = stretch.y;
        this.cosTheta = cosTheta;
        this.sinTheta = sinTheta;
        this.cellX = cellX;
        this.cellZ = cellZ;
        this.regionPos = regionPos;
    }

    public MajorTectonicPlate(RegionPos regionPos, int cellX, int cellZ, long seed) {
        int truePosX = Mth.floor(RegionPos.toBlockCoord(regionPos.x()) + worldPosition(cellX));
        int truePosZ = Mth.floor(RegionPos.toBlockCoord(regionPos.z()) + worldPosition(cellZ));
        long hash = coordinateHash(truePosX, truePosZ, seed);
        this.id = Long.hashCode(hash);
        this.regionPos = regionPos;
        this.cellX = cellX;
        this.cellZ = cellZ;
        this.centerX = getSaltedFloat(hash, 0);
        this.centerZ = getSaltedFloat(hash, 1);

        double velAngle = getSaltedFloat(hash, 2) * (2.0 * Math.PI);
        float speedRaw = getSaltedFloat(hash, 3) * 0.95f + 0.05f;
        float speedSquared = speedRaw * speedRaw;
        this.velocityX = (float) Math.cos(velAngle) * speedSquared;
        this.velocityZ = (float) Math.sin(velAngle) * speedSquared;

        float w = getSaltedFloat(hash, 4);

        this.weight = adaptWeight(w);
        this.influence = getSaltedFloat(hash, 5);

        float angle = (getSaltedFloat(hash, 6) * 2.0f - 1.0f) * MAX_ANGLE;
        this.cosTheta = (float) Math.cos(angle);
        this.sinTheta = (float) Math.sin(angle);
        this.stretchX = getSaltedFloat(hash, 7) * 0.3f + 0.85f;
        this.stretchZ = getSaltedFloat(hash, 8) * 0.3f + 0.85f;
        this.age = getSaltedFloat(hash, 9);

    }

    private static float adaptWeight(float w) {
        float fW;
        if (w < LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT) {
            float adaptW = w / LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT;
            fW = (float) (LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT * Math.pow(adaptW, 2));
        } else {
            float adaptW = (w - LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT) / (1.0f - LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT);
            fW = (float) (LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT + (1.0f - LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT) * (1.0 - Math.pow(1.0 - adaptW, 2)));
        }
        if (fW > LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT - 0.1f && fW <= LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT) {
            fW = LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT - 0.1F;
        } else if (fW < LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT + 0.1f && fW > LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT) {
            fW =  LAND_OCEANIC_PLATE_BOUNDARY_WEIGHT + 0.1F;
        }
        return fW;
    }

    public float getWeightedDistance(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        float rx = (dx * this.cosTheta - dz * this.sinTheta) / this.stretchX;
        float rz = (dx * this.sinTheta + dz * this.cosTheta) / this.stretchZ;

        return (float) Math.sqrt(rx * rx + rz * rz);
    }
    public float getRawDistance(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();


        return (float) Math.sqrt(dx * dx + dz * dz);
    }

    public float getWeightedDistanceSquared(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        float rx = (dx * this.cosTheta - dz * this.sinTheta) / this.stretchX;
        float rz = (dx * this.sinTheta + dz * this.cosTheta) / this.stretchZ;

        return Math.abs(rx * rx + rz * rz);
    }
    public float getWorldX() {
        return worldPosition(this.cellX + this.centerX) + RegionPos.toBlockCoord(this.regionPos.x());
    }

    public float getWorldZ() {
        return worldPosition(this.cellZ + this.centerZ) + RegionPos.toBlockCoord(this.regionPos.z());
    }

    public static float worldPosition(float pos) {
        return pos * RegionPos.REGION_SIZE / RegionPlates.MAJOR_PLATE_GRID_SIZE;
    }

    private long coordinateHash(int x, int z, long seed) {
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
    public int getId() {
        return this.id;
    }

    public Vec2 getCenter() {
        return new Vec2(this.centerX, this.centerZ);
    }

    public float getCenterX() {
        return this.centerX;
    }

    public float getCenterZ() {
        return this.centerZ;
    }

    public Vec2 getVelocity(){
        return new Vec2(this.velocityX, this.velocityZ);
    }

    public float getVelocityX() {
        return this.velocityX;
    }

    public float getVelocityZ() {
        return this.velocityZ;
    }

    public float getInfluence() {
        return this.influence;
    }
    public float getWeight() {
        return this.weight;
    }

    public Vec2 getStretch(){
        return new  Vec2(this.stretchX, this.stretchZ);
    }

    public float getCosTheta() {
        return cosTheta;
    }

    public float getSinTheta() {
        return sinTheta;
    }

    public RegionPos getRegionPos() {
        return regionPos;
    }

    public int getCellX() {
        return cellX;
    }

    public int getCellZ() {
        return cellZ;
    }

    public float getAge() {
        return age;
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

    public static MajorTectonicPlate read(CompoundTag nbt) {
        return nbt.read("plate",MajorTectonicPlate.codec()).orElseThrow();
    }

    public void write(CompoundTag nbt) {
        CODEC.encodeStart(NbtOps.INSTANCE,this)
                .ifSuccess(tag -> nbt.put("plate",tag));
    }
}
