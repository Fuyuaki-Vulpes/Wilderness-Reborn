package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.util.RebAABB;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RiverNode {
    public static final Codec<RiverNode> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    GridPoint.CODEC.fieldOf("position").forGetter(RiverNode::getPosition),
                    Vec2.CODEC.fieldOf("offset").forGetter(RiverNode::getOffset),
                    Vec2.CODEC.fieldOf("flow").forGetter(RiverNode::getFlow),
                    WorldPosition.CODEC.fieldOf("flow_to").forGetter(RiverNode::getFlowTo),
                    WorldPosition.CODEC.fieldOf("flow_from").forGetter(RiverNode::getFlowFrom),
                    Codec.FLOAT.fieldOf("slope_to").forGetter(RiverNode::getSlopeTo),
                    Codec.FLOAT.fieldOf("slope_from").forGetter(RiverNode::getSlopeFrom),
                    Codec.FLOAT.fieldOf("diameter").forGetter(RiverNode::getDiameter),
                    Codec.FLOAT.fieldOf("y_elevation").forGetter(RiverNode::getElevation),
                    Codec.INT.fieldOf("to_drainage").forGetter(RiverNode::getNodesToDrainage),
                    Codec.FLOAT.fieldOf("from_diameter").forGetter(RiverNode::getFlowFromDiameter),
                    RegionPos.CODEC.fieldOf("region").forGetter(RiverNode::getRegionPos),
                    Cliff.CODEC.optionalFieldOf("cliff").forGetter(RiverNode::getCliffOptional)
            ).apply(i,RiverNode::new));
    public static final int MAX_DEPTH = 16;

    private static final long PRIME_1 = 0x9e3779b97f4a7c15L;
    private static final long PRIME_2 = 0xbf58476d1ce4e5b9L;
    private static final long PRIME_3 = 0x94d049bb133111ebL;
    private static final long SALT = 0xc4ceb9fe1a85ec53L;
    public static final int MAX_DIAMETER = 64;

    private final GridPoint position;
    private Vec2 flow;
    private float slopeFrom = 0.0f;
    private float slopeTo = 0.0f;
    private WorldPosition flowTo = WorldPosition.OUT_OF_BOUNDS;
    private WorldPosition flowFrom = WorldPosition.OUT_OF_BOUNDS;
    private float diameter = -999.0f;
    private Vec2 offset;
    private float elevation = WildernessConstants.WORLD_BOTTOM;
    private int nodesToDrainage;
    private final RegionPos regionPos;
    private float flowFromDiameter = -999.0f;
    private Cliff cliff = Cliff.EMPTY;
    private List<RiverPoint> points = new ArrayList<>();
    private RebAABB collision;

    public RiverNode(GridPoint position, Vec2 offset, Vec2 flow, WorldPosition flowTo, WorldPosition flowFrom,float slopeTo,float slopeFrom, float diameter, float elevation, int nodesToDrainage,float flowFromDiameter, RegionPos pos, Optional<Cliff> oCliff) {
        this.position = position;
        this.offset = offset;
        this.flow = flow;
        this.flowTo = flowTo;
        this.flowFrom = flowFrom;
        this.slopeTo = slopeTo;
        this.slopeFrom = slopeFrom;
        this.diameter = diameter;
        this.elevation = elevation;
        this.nodesToDrainage = nodesToDrainage;
        this.regionPos = pos;
        this.flowFromDiameter = flowFromDiameter;
        this.cliff = oCliff.orElse(Cliff.EMPTY);
    }

    public RiverNode(RegionPos pos, int cellX, int cellZ, RegionNoisesRecord record, float centerX, float centerZ, long hash, WorldRegion region) {
        this.position = new GridPoint(cellX, cellZ);
        this.offset = new Vec2(centerX, centerZ);
        this.regionPos = pos;
        this.nodesToDrainage = 0;

        float worldX = getWorldX();
        float worldZ = getWorldZ();

        this.diameter = Math.clamp(
                (1 + getSaltedFloat(hash,3))
                        * ((region.layers().getHumidity().getGlobal(Mth.floor(worldX), Mth.floor(worldZ)) + 1) / 2)
                        * 8 + 8f,
                8, 16);

        float flowL = getSaltedFloat(hash,4)  * 0.75f + 0.25f;
        float flowAngle = (float) (getSaltedFloat(hash,5) * (2.0 * Math.PI));

        this.flow = new Vec2((float) Math.cos(flowAngle) * flowL,(float) Math.sin(flowAngle) * flowL);


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

    public RegionPos getRegionPos() {
        return this.regionPos;
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
        return this.getPosition().x;
    }

    private float getCellZ() {
        return this.getPosition().z;
    }

    public static float worldPosition(float pos) {
        return pos * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
    }

    public GridPoint getPosition() {
        return position;
    }

    public Vec2 getFlow() {
        return flow;
    }

    public void setFlow(Vec2 flow) {
        this.flow = flow;
    }

    public WorldPosition getFlowTo() {
        return flowTo;
    }

    public void setFlowTo(WorldPosition flowTo) {
        this.flowTo = flowTo;
    }

    public WorldPosition getFlowFrom() {
        return flowFrom;
    }

    public void setFlowFrom(WorldPosition flowFrom) {
        this.flowFrom = flowFrom;
    }

    public float getDiameter() {
        return diameter;
    }

    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    public float getRadius(){
        return this.diameter / 2;
    }

    public void setOffset(Vec2 offset) {
        this.offset = offset;
    }

    public Vec2 getOffset() {
        return offset;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getSlopeFrom() {
        return slopeFrom;
    }

    public void setSlopeFrom(float slopeFrom) {
        this.slopeFrom = slopeFrom;
    }

    public float getSlopeTo() {
        return slopeTo;
    }

    public void setSlopeTo(float slopeTo) {
        this.slopeTo = slopeTo;
    }

    public int getNodesToDrainage() {
        return nodesToDrainage;
    }

    public void setNodesToDrainage(int nodesToDrainage) {
        this.nodesToDrainage = nodesToDrainage;
    }

    public float getFlowFromDiameter() {
        return flowFromDiameter;
    }

    public Cliff getCliff() {
        return cliff;
    }
    public Optional<Cliff> getCliffOptional() {
        if (cliff == Cliff.EMPTY) { return Optional.empty();}
        return Optional.of(cliff);
    }

    public void setCliff(Cliff cliff) {
        this.cliff = cliff;
    }

    public void setFlowFromDiameter(float flowFromDiameter) {
        this.flowFromDiameter = flowFromDiameter;
    }

    public WorldPosition getWorldPosition() {
        return new WorldPosition(this.getWorldX(),this.getWorldZ());
    }

    public float getDepth() {
        if (this.diameter <= 0.0f) return 2.0f;

        float baseDepth = (float) Math.pow(this.diameter, 0.75f);

        float speed = this.flow.length();
        float slopeBonus = 1.0f + (float) Math.sqrt(Math.max(0.0f, this.slopeTo)) * 1.5f;

        float calculatedDepth = baseDepth * slopeBonus * (0.8f + (speed * 0.2f));

        return Mth.clamp(calculatedDepth, Math.max(4.0f,this.getRadius()/2), MAX_DEPTH);
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

    public static RiverNode read(CompoundTag nbt) throws IOException {
        return nbt.read("river",CODEC).orElseThrow();
    }

    public void write(CompoundTag nbt) throws Exception {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE,this);
        if (result.isError() || result.getOrThrow() == null)  {
            throw new Exception("Hey Yagasa check this out please");
        }
        nbt.put("river",result.getOrThrow());
        if (nbt == null || nbt.isEmpty()) {
            throw new Exception("NBT cannot be null!");
        }
    }

    public RiverPoint asPoint(){
        return new RiverPoint(0.0f,
                this.getWorldX(),
                this.getWorldZ(),
                this.elevation,
                this.elevation - 2,
                this.diameter,
                this.getDepth(),
                this.flow,
                this.cliff);
    }

    public void setAndCalculateDownhillConnection(RiverNode bestMatch, float slope,long hash) {
        this.slopeTo = slope;
        this.nodesToDrainage = bestMatch.getNodesToDrainage() + 1;
        this.flowTo = bestMatch.getWorldPosition();
        float targetX = bestMatch.getWorldX() - this.getWorldX();
        float targetZ = bestMatch.getWorldZ() - this.getWorldZ();
        float distLen = (float) Math.sqrt(targetX * targetX + targetZ * targetZ);

        Vec2 downhillDir = distLen > 0.001f
                ? new Vec2(targetX / distLen, targetZ / distLen)
                : Vec2.ZERO;

        float baseSpeed = 0.5f;
        float slopeSpeedMultiplier = 1.0f + (float) Math.sqrt(Math.max(0.0f, slope)) * 2.0f;
        float targetSpeed = baseSpeed * slopeSpeedMultiplier;
        float blendedX = Mth.lerp(0.70f, this.flow.x, downhillDir.x * targetSpeed);
        float blendedZ = Mth.lerp(0.70f, this.flow.y, downhillDir.y * targetSpeed);
        float flowAngle = (float) ((getSaltedFloat(hash,6) - 0.5f) * (Math.PI) * 2);
        this.flow =this.flow.scale(0.1f).add(new Vec2(blendedX, blendedZ).scale(0.5f)).rotate(flowAngle).normalized();
    }

    public void setAsEndOfRiverAndCalculate(Vec2 terrainTangent) {
        this.nodesToDrainage = 0;
        if (this.flowFrom != WorldPosition.OUT_OF_BOUNDS) {
            this.diameter = RiverNode.MAX_DIAMETER;
            this.flow = this.flow.scale(0.4f).add(terrainTangent).normalized();
        } else {
            this.flow = terrainTangent;
        }
    }

    public void updateFlow(RiverNode incomingFeeder,Vec2 terrainTangent) {
        float incomingDiameter = incomingFeeder.getDiameter() + 2.0f;
        boolean isMainTrunk = (this.flowFrom == WorldPosition.OUT_OF_BOUNDS || incomingDiameter > this.flowFromDiameter);

        if (isMainTrunk) {
            this.flowFrom = incomingFeeder.getWorldPosition();
            this.flowFromDiameter = incomingDiameter;

            float heightD = incomingFeeder.getElevation() - this.getElevation();
            float dist = incomingFeeder.getDistance(this.getWorldX(), this.getWorldZ());

            this.slopeFrom = heightD / Math.max(dist, 0.001f);

            this.diameter = Math.max(this.diameter, incomingDiameter);
        }else{
            float selfSq = this.diameter * this.diameter;
            float tributarySq = incomingDiameter * incomingDiameter * 0.25f;

            this.diameter = (float) Math.sqrt(selfSq + tributarySq);
        }
        this.diameter = Math.min(this.diameter, MAX_DIAMETER);

        float momentumWeight = isMainTrunk ? 1.0f : 0.35f;
        this.flow = this.flow.add(incomingFeeder.getFlow().scale(momentumWeight)).add(terrainTangent.scale(0.1f));
    }

    public void generatePoints(RegionPos pos,WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors, long seed){

        this.points = new ArrayList<>();
        RiverPoint river0 = this.asPoint();
        if (this.getFlowTo() == WorldPosition.OUT_OF_BOUNDS) return;

        int x1 = Mth.floor(this.getFlowTo().x());
        int z1 = Mth.floor(this.getFlowTo().z());
        RegionPos pos1 = new RegionPos(RegionPos.fromBlockCoord(x1), RegionPos.fromBlockCoord(z1));
        WorldRegion reg = pos1.equals(pos) ? worldRegion : neighbors.get(pos1);
        if (reg == null) return;

        RiverNode nodeAtBlock = reg.getNbtData().rivers().getNodeAtBlock(x1, z1);
        if (nodeAtBlock == null) return;
        RiverPoint river4 = nodeAtBlock.asPoint();
        float interpDist = Mth.floor((this.getDistance(river4.worldX(), river4.worldZ()) + 1) / 4) + 1;
        float lastElevation = this.getElevation();

        float maxDiversion = Math.min(this.getDistance(river4.worldX(), river4.worldZ()) / 2,128.0f);
        RiverPoint river1 = RiverPoint.bendPoint(river0, river4, 0.25f,worldRegion.layers().record().riverSway().getNoise(),worldRegion.layers().record().riverRandom().getNoise(), maxDiversion);
        RiverPoint river2 = RiverPoint.bendPoint(river0, river4, 0.50f,worldRegion.layers().record().riverSway().getNoise(),worldRegion.layers().record().riverRandom().getNoise(), maxDiversion);
        RiverPoint river3 = RiverPoint.bendPoint(river0, river4, 0.75f,worldRegion.layers().record().riverSway().getNoise(),worldRegion.layers().record().riverRandom().getNoise(), maxDiversion);

        RebAABB.Builder colBox = new RebAABB.Builder();
        for (int d = 0; d < interpDist; d ++) {
            float delta = d / interpDist;
            RiverPoint p0 = RegionRivers.interpolated4PointRiver(river0,river1,river2,river3,river4,delta,worldRegion.layers().record().riverSway().getNoise(), lastElevation);
            float interpX = p0.worldX();
            float interpZ = p0.worldZ();
            float range = p0.diameter() * 2;
            Vector2f min = new Vector2f(interpX - range, interpZ - range);
            Vector2f max = new Vector2f(interpX + range, interpZ + range);
            colBox.include(min);
            colBox.include(max);
            RegionLayers layers = neighbors.get(new RegionPos(RegionPos.fromBlockCoord(Mth.floor(interpX)), RegionPos.fromBlockCoord(Mth.floor(interpZ)))).layers();

            float elevationHere = layers.getHeightmapBasic().upsampleFromNeighbors(Mth.floor(interpX),Mth.floor(interpZ),neighbors);
            if (elevationHere > lastElevation){
                elevationHere = lastElevation;
            } else if (elevationHere < river4.elevation()) {
                elevationHere = river4.elevation();
            }
            lastElevation = elevationHere;
            p0 = p0.withNewElevation(elevationHere);
            points.add(p0);
        }
        this.collision = colBox.build();
    }

    public List<RiverPoint> getPoints() {
        return points;
    }

    public void setPoints(List<RiverPoint> points) {
        this.points = points;
    }

    public record GridPoint(int x, int z){
        public static final Codec<GridPoint> CODEC = RecordCodecBuilder.create(

                i -> i.group(
                        Codec.INT.fieldOf("x").forGetter(GridPoint::x),
                        Codec.INT.fieldOf("z").forGetter(GridPoint::z)
                ).apply(i, GridPoint::new)
        );

        public static Codec<GridPoint> codec() {
            return CODEC;
        }
    }

    public record WorldPosition(double x, double z){
        public static final WorldPosition OUT_OF_BOUNDS = new WorldPosition(Double.MAX_VALUE, Double.MAX_VALUE);
        public static final Codec<WorldPosition> CODEC = RecordCodecBuilder.create(

                i -> i.group(
                        Codec.DOUBLE.fieldOf("x").forGetter(WorldPosition::x),
                        Codec.DOUBLE.fieldOf("z").forGetter(WorldPosition::z)
                ).apply(i, WorldPosition::new)
        );

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WorldPosition(double x1, double z1))) {return false;}
            return x1 == this.x && z1 == this.z;
        }

        public static Codec<WorldPosition> codec() {
            return CODEC;
        }
    }

    public record Cliff(float start, float end, float startElevation, float endElevation){
        public static final Cliff EMPTY = new Cliff(-1.0f,1.0f,1.0f,1.0f);
        public static final Codec<Cliff> CODEC = RecordCodecBuilder.create(

                i -> i.group(
                        Codec.FLOAT.fieldOf("start").forGetter(Cliff::start),
                        Codec.FLOAT.fieldOf("end").forGetter(Cliff::end),
                        Codec.FLOAT.fieldOf("start_elevation").forGetter(Cliff::startElevation),
                        Codec.FLOAT.fieldOf("end_elevation").forGetter(Cliff::endElevation)
                ).apply(i, Cliff::new)
        );

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Cliff(float start1, float end1, float startElevation1, float endElevation1))) {return false;}
            return start1 == this.start && end1 == this.end && startElevation1 == this.startElevation && endElevation1 == this.endElevation;
        }

        public static Codec<Cliff> codec() {
            return CODEC;
        }
    }
}
