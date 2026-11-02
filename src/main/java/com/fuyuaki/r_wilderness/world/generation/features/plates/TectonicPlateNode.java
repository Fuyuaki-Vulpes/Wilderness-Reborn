package com.fuyuaki.r_wilderness.world.generation.features.plates;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec2;

public class TectonicPlateNode {
    public static final Codec<TectonicPlateNode> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    Codec.INT.fieldOf("id").forGetter(TectonicPlateNode::getId),
                    Type.CODEC.fieldOf("type").forGetter(TectonicPlateNode::getType),
                    Vec2.CODEC.fieldOf("velocity").forGetter(TectonicPlateNode::getVelocity),
                    Codec.FLOAT.fieldOf("distance_parent").forGetter(TectonicPlateNode::getDistToParent),
                    TerrainMaker.CODEC.fieldOf("terrain_maker").forGetter(TectonicPlateNode::getTerrainMaker),
                    DeformationStorage.CODEC.fieldOf("deformation").forGetter(TectonicPlateNode::getDeformation),
                    LocationStorage.CODEC.fieldOf("location").forGetter(TectonicPlateNode::getLocation)
            ).apply(i, TectonicPlateNode::new)
    );


    private static final float MAX_ANGLE = (float) (Math.PI / 4.0);

    private static final long PRIME_1 = 0x9e3779b97f4a7c15L;
    private static final long PRIME_2 = 0xbf58476d1ce4e5b9L;
    private static final long PRIME_3 = 0x94d049bb133111ebL;
    private static final long SALT = 0x7b5aa4e711ad4287L;


    private final int id;
    private final Type  type;

    private final LocationStorage location;
    private final DeformationStorage deformation;
    private final TerrainMaker terrainMaker;
    //Shared Properties
    private final float velocityX, velocityZ;

    private final float distToParent;




    public static Codec<TectonicPlateNode> codec() {
        return CODEC;
    }


    public TectonicPlateNode(RegionPos regionPos, int cellX, int cellZ, RegionNoisesRecord record, float centerX, float centerZ, long hash, MajorTectonicPlate parent){
        int plateId = Long.hashCode(hash);
        float velocityXParent = parent.velocityX;
        float velocityZParent = parent.velocityZ;
        int offsetDistance = 4096;
        int i = 0;
        float worldX = worldPosition(cellX + centerX) + RegionPos.toBlockCoord(regionPos.x()) - (velocityXParent * offsetDistance);
        float worldZ = worldPosition(cellZ + centerZ) + RegionPos.toBlockCoord(regionPos.z()) - (velocityZParent * offsetDistance);
        float distTo = parent.getWeightedDistance(worldX,worldZ);
        this.distToParent = distTo;
        float weightLoss = 0.25f;
        float distRaw = parent.getRawDistance(worldX,worldZ);
        int maxDistanceForBias = 8192;
        float dB = Math.clamp(distTo / maxDistanceForBias,0.0f,1.0f);
        float distBias = (float) Mth.smoothstep(dB);

        float rollBias = Math.clamp(distRaw / maxDistanceForBias,0,1);
        float rollBiasMulti = rollBias * rollBias;
        float roll = getSaltedFloat(hash,i++) * rollBiasMulti;
        float microChance = 0.15f;
        float weight;
        float parentX;
        float parentZ;
        if (roll < (1 - microChance)) {
            plateId = parent.id;
            this.type = Type.MINOR;
            weight = Math.clamp(parent.weight - (distBias * weightLoss),0,1);
            this.velocityX = parent.velocityX;
            this.velocityZ = parent.velocityZ;
            parentX = parent.getWorldX();
            parentZ = parent.getWorldZ();
        }
        else {
            this.type = Type.MICRO;
            weight = Math.clamp(
                    (getSaltedFloat(hash,i++) - 0.5f) * 0.5f + parent.weight
                    ,0.0f,1.0f);

            double velAngle = getSaltedFloat(hash, i++) * (2.0 * Math.PI);
            float speedRaw = getSaltedFloat(hash, i++) * 0.95f + 0.05f;
            float speedSquared = speedRaw * speedRaw;
            this.velocityX = (float) Math.cos(velAngle) * speedSquared;
            this.velocityZ = (float) Math.sin(velAngle) * speedSquared;
            parentX = worldX;
            parentZ = worldZ;
        }

        this.id = plateId;
        float angle = (getSaltedFloat(hash, i++) * 2.0f - 1.0f) * MAX_ANGLE;
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);
        float stretchX;
        float stretchZ;
        float crustalBuoyancy;
        float age;
        if (this.type == Type.MICRO) {
            stretchX = getSaltedFloat(hash, i++) + 0.5f;
            stretchZ = getSaltedFloat(hash, i++) + 0.5f;
            crustalBuoyancy = 0.0f;
            age = getSaltedFloat(hash, i++);
        } else {
            stretchX = getSaltedFloat(hash, i++) * 0.3f + 0.85f;
            stretchZ = getSaltedFloat(hash, i++) * 0.3f + 0.85f;
            age = Math.clamp((getSaltedFloat(hash, i++)) * 0.25f + parent.age,0,1);


            float noise = (float) record.buoyancyBias().getNoise().noise(worldX, worldZ);
            crustalBuoyancy = noise * getSaltedFloat(hash, i++);
        }
        float swelling = (1 - age) * (getSaltedFloat(hash, i) - 0.5f) * 2.0f;
        this.location = new LocationStorage(cellX,cellZ, new Vec2(parentX,parentZ),regionPos, new Vec2(centerX,centerZ));
        this.deformation = new DeformationStorage(new Vec2(stretchX,stretchZ),cosTheta,sinTheta);
        this.terrainMaker = new TerrainMaker(weight,crustalBuoyancy,age,swelling);

    }

    public TectonicPlateNode(int id, Type type, Vec2 velocity,float distToParent, TerrainMaker maker, DeformationStorage deformation, LocationStorage location) {
        this.id = id;
        this.type = type;
        this.velocityX = velocity.x;
        this.velocityZ = velocity.y;
        this.distToParent = distToParent;
        this.terrainMaker = maker;
        this.deformation = deformation;
        this.location = location;
    }

    public float getWeightedDistance(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        float rx = (dx * this.getCosTheta() - dz * this.getSinTheta()) / this.getStretch().x;
        float rz = (dx * this.getSinTheta() + dz * this.getCosTheta()) / this.getStretch().y;

        return (float) Math.sqrt(rx * rx + rz * rz);
    }
    public float getWeightedDistanceSquared(float targetX, float targetZ) {
        float dx = targetX - this.getWorldX();
        float dz = targetZ - this.getWorldZ();

        float rx = (dx * this.getCosTheta() - dz * this.getSinTheta()) / this.getStretch().x;
        float rz = (dx * this.getSinTheta() + dz * this.getCosTheta()) / this.getStretch().y;

        return rx * rx + rz * rz;
    }

    public float getWorldX() {
        return worldPosition(this.getCellX() + this.getCenterX()) + RegionPos.toBlockCoord(this.getRegionPos().x());
    }
    public float getWorldZ() {
        return worldPosition(this.getCellZ() + this.getCenterZ()) + RegionPos.toBlockCoord(this.getRegionPos().z());
    }

    public static float worldPosition(float pos) {
        return pos * RegionPos.REGION_SIZE / RegionPlates.MINOR_PLATE_GRID_SIZE;
    }

    private Type getType() {
        return this.type;
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

    public int getId() {
        return this.id;
    }
    public Vec2 getCenter() {
        return this.location.center();
    }

    public float getCenterX() {
        return this.location.center().x;
    }

    public float getCenterZ() {
        return this.location.center().y;
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


    public float getWeight() {
        return this.terrainMaker.weight;

    }
    public Vec2 getStretch(){
        return this.deformation.stretch;
    }

    public float getCosTheta() {
        return this.deformation.cosTheta;
    }

    public float getSinTheta() {
        return this.deformation.sinTheta;
    }

    public RegionPos getRegionPos() {
        return this.location.regionPos;
    }

    public int getCellX() {
        return this.location.cellX;
    }

    public int getCellZ() {
        return this.location.cellZ;
    }

    public float getParentX() {
        return this.location.parentLocation.x;
    }

    public float getParentZ() {
        return this.location.parentLocation.y;
    }

    public float getBuoyancy() {
        return this.terrainMaker.buoyancy;
    }

    public float getDistToParent() {
        return distToParent;
    }

    public float getAge() {
        return this.terrainMaker.age;
    }

    public float getSwelling() {
        return this.terrainMaker.swelling;
    }

    public TerrainMaker getTerrainMaker() {
        return this.terrainMaker;
    }

    public DeformationStorage getDeformation() {
        return this.deformation;
    }

    public LocationStorage getLocation() {
        return this.location;
    }

    public static TectonicPlateNode read(CompoundTag nbt) {
        return nbt.read("node",TectonicPlateNode.codec()).orElseThrow();
    }

    public void write(CompoundTag nbt) {
        CODEC.encodeStart(NbtOps.INSTANCE,this)
                .ifSuccess(tag -> nbt.put("node",tag));
    }

    public enum Type implements StringRepresentable {
        MINOR ("minor"),
        MICRO ("micro"),
        ;
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    private record LocationStorage(int cellX, int cellZ,Vec2 parentLocation,RegionPos regionPos,Vec2 center){
        public static final Codec<LocationStorage> CODEC = RecordCodecBuilder.create(

                i -> i.group(
                        Codec.INT.fieldOf("cell_x").forGetter(LocationStorage::cellX),
                        Codec.INT.fieldOf("cell_z").forGetter(LocationStorage::cellZ),
                        Vec2.CODEC.fieldOf("parent_location").forGetter(LocationStorage::parentLocation),
                        RegionPos.CODEC.fieldOf("region").forGetter(LocationStorage::regionPos),
                        Vec2.CODEC.fieldOf("center").forGetter(LocationStorage::center)
                ).apply(i, LocationStorage::new)
        );

        public static Codec<LocationStorage> codec() {
            return CODEC;
        }

    }

    private record DeformationStorage(Vec2 stretch, float cosTheta, float sinTheta){

        public static final Codec<DeformationStorage> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                        Vec2.CODEC.fieldOf("stretch").forGetter(DeformationStorage::stretch),
                        Codec.FLOAT.fieldOf("cos_theta").forGetter(DeformationStorage::cosTheta),
                        Codec.FLOAT.fieldOf("sin_theta").forGetter(DeformationStorage::sinTheta)
                ).apply(i, DeformationStorage::new)
        );

        public static Codec<DeformationStorage> codec() {
            return CODEC;
        }

    }

    private record TerrainMaker(float weight, float buoyancy, float age, float swelling){
        public static final Codec<TerrainMaker> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                        Codec.FLOAT.fieldOf("weight").forGetter(TerrainMaker::weight),
                        Codec.FLOAT.fieldOf("buoyancy").forGetter(TerrainMaker::buoyancy),
                        Codec.FLOAT.fieldOf("age").forGetter(TerrainMaker::age),
                        Codec.FLOAT.fieldOf("swelling").forGetter(TerrainMaker::swelling)
                ).apply(i, TerrainMaker::new)
        );

        public static Codec<TerrainMaker> codec() {
            return CODEC;
        }

    }

}
