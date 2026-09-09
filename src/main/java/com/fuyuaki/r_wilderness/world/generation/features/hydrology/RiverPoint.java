package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import static com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator.RIVER_LIMIT_SQR;

public record RiverPoint(float delta,float worldX, float worldZ, float elevation, float waterHeight, float diameter, float depth, Vec2 flow, RiverNode.Cliff cliffData) {

    public static final Codec<RiverPoint> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    Codec.FLOAT.fieldOf("delta").forGetter(RiverPoint::delta),
                    Codec.FLOAT.fieldOf("x").forGetter(RiverPoint::worldX),
                    Codec.FLOAT.fieldOf("z").forGetter(RiverPoint::worldZ),
                    Codec.FLOAT.fieldOf("elevation").forGetter(RiverPoint::elevation),
                    Codec.FLOAT.fieldOf("water_height").forGetter(RiverPoint::waterHeight),
                    Codec.FLOAT.fieldOf("diameter").forGetter(RiverPoint::diameter),
                    Codec.FLOAT.fieldOf("depth").forGetter(RiverPoint::depth),
                    Vec2.CODEC.fieldOf("flow").forGetter(RiverPoint::flow),
                    RiverNode.Cliff.CODEC.fieldOf("cliff").forGetter(RiverPoint::cliffData)
            ).apply(i, RiverPoint::new)
    );


    public static RiverPoint interpolated(RiverPoint first, RiverPoint second, float delta, Noise2D noise, float trueDelta, RiverPoint river0, RiverPoint river4, float lastElevation) {
        if (delta <= 0.0f) return first;
        if(delta >= 1.0f) return second;
        delta = Mth.clamp(delta, 0.0f, 1.0f);
        Vec2 position0 = new Vec2(first.worldX(), first.worldZ());
        Vec2 position1 = new Vec2(second.worldX(), second.worldZ());

        float distance = (float) Math.sqrt(position0.distanceToSqr(position1));
        Vec2 fallbackDir = position1.add(position0.negated());
        if (fallbackDir.lengthSquared() > 0.0001f) {
            fallbackDir = fallbackDir.normalized();
        } else {
            fallbackDir = Vec2.ZERO;
        }

        Vec2 flow0 = first.flow().lengthSquared() > 0.0001f ? first.flow().normalized() : fallbackDir;
        Vec2 flow1 = second.flow().lengthSquared() > 0.0001f ? second.flow().normalized() : fallbackDir;


        float tangentScale = distance * 0.75f;
        Vec2 tangent0 = flow0.normalized().scale(tangentScale);
        Vec2 tangent1 = flow1.normalized().scale(tangentScale);


        float deltaSq = delta * delta;
        float deltaCube = deltaSq * delta;

        float factorPos0 = 2 * deltaCube - 3 * deltaSq + 1;
        float factorTan0 = deltaCube - 2 * deltaSq + delta;
        float factorPos1 = -2 * deltaCube + 3 * deltaSq;
        float factorTan1 = deltaCube - deltaSq;

        float x = factorPos0 * position0.x + factorTan0 * tangent0.x + factorPos1 * position1.x + factorTan1 * tangent1.x;
        float z = factorPos0 * position0.y + factorTan0 * tangent0.y + factorPos1 * position1.y + factorTan1 * tangent1.y;

        float dh00 = 6 * deltaSq - 6 * delta;
        float dh10 = 3 * deltaSq - 4 * delta + 1;
        float dh01 = -6 * deltaSq + 6 * delta;
        float dh11 = 3 * deltaSq - 2 * delta;

        float dx = dh00 * position0.x + dh10 * tangent0.x + dh01 * position1.x + dh11 * tangent1.x;
        float dz = dh00 * position0.y + dh10 * tangent0.y + dh01 * position1.y + dh11 * tangent1.y;

        Vec2 tangent = new Vec2(dx, dz).normalized();
        float diameter = Mth.lerp(delta, first.diameter(), second.diameter());

        float smoothDelta = trueDelta * trueDelta * (3.0f - 2.0f * trueDelta);


        float elevation;
        float depth;
        float waterAt;
        RiverNode.Cliff cliffData = river0.cliffData();
        float finalE = river4.elevation();
        depth = Mth.lerp(trueDelta, river0.depth(), river4.depth());
        elevation = Mth.lerp(smoothDelta, river0.elevation(), finalE);
        if (cliffData == null || !cliffData.valid()) {
            float eDifference = lastElevation - elevation;
            float depthLower = eDifference * 3.0f;
            waterAt = elevation - 2 - depthLower;
        } else {
            float start = cliffData.start();
            float end = cliffData.end();
            float postDelta;
            float cliffStartDepth = Math.max(Mth.lerp(start, river0.depth, river4.depth()) * 0.5f,2.0f);
            float cliffEndDepth = Mth.lerp(end, river0.depth(), river4.depth()) * 1.5f;
            if (trueDelta < start) {
                postDelta = Mth.clamp(trueDelta / start,0,1);

                elevation = Mth.lerp(postDelta, river0.elevation(), cliffData.startElevation());
                depth = Mth.lerp(postDelta, river0.depth(), cliffStartDepth);
                float eDifference = lastElevation - elevation;
                float depthLower = Math.max(eDifference * 0.5f,eDifference * 7.0f * (postDelta * postDelta * postDelta * postDelta * 1.25f));
                waterAt = elevation - 1 - depthLower;

            } else if (trueDelta > start && trueDelta < end) {
                float d = Math.clamp(1 - ((trueDelta - start) / (start - end)),0,1);
                postDelta =  Mth.clamp(1 - (d*d*d),0,1);

                depth = Mth.lerp(postDelta, cliffStartDepth,cliffEndDepth);
                elevation = Mth.lerp(postDelta, cliffData.startElevation(), cliffData.endElevation());
                float eDifference = lastElevation - elevation;
                float depthLower = eDifference * 5.0f;
                waterAt = Math.max(elevation - 3 - depthLower,cliffData.endElevation() - 1);


            } else if (trueDelta > end) {
                postDelta =  Mth.clamp((trueDelta - end) / (1 - end),0,1);

                depth = Mth.lerp(postDelta, cliffEndDepth, river4.depth());
                elevation = Mth.lerp(postDelta, cliffData.endElevation(), finalE);
                float eDifference = lastElevation - elevation;
                float depthLower = eDifference * 0.5f;
                waterAt = elevation - 1 - depthLower;
            }else{
                float eDifference = lastElevation - elevation;
                float depthLower = eDifference * 0.5f;
                waterAt = elevation - 1 - depthLower;
            }

        }
        float minimumWaterHeight = elevation - depth + 2;

        waterAt = Math.max(waterAt,minimumWaterHeight);

        float maxBendStrength = diameter/3;
        float singleNoiseVal = (float) noise.noise(x, z);
        Vec2 normal = new Vec2(-tangent.y, tangent.x);
        float envelope = 4.0f * delta * (1.0f - delta);
        float offset = singleNoiseVal * maxBendStrength * envelope;
        x += normal.x * offset;
        z += normal.y * offset;

        float envelopeDerivative = 4.0f * (1.0f - 2.0f * delta);
        float tangentSlope = Mth.clamp((singleNoiseVal * maxBendStrength * envelopeDerivative) / Math.max(distance, 8.0f),-0.4f, 0.4f);
        tangent = tangent.add(normal.scale(tangentSlope)).normalized();

        Vec2 flow = tangent.scale(Mth.lerp(delta, first.flow().length(), second.flow().length()));
        return new RiverPoint(delta,x, z, Math.min(lastElevation, elevation), waterAt, diameter, depth, flow, cliffData);
    }

    public static RiverPoint bendPoint(RiverPoint first, RiverPoint second, float delta, Noise2D noise, Noise2D noise2, float maxDiversion) {
        if (delta <= 0.0f) return first;
        if(delta >= 1.0f) return second;
        delta = Mth.clamp(delta, 0.0f, 1.0f);
        Vec2 position0 = new Vec2(first.worldX(), first.worldZ());
        Vec2 position1 = new Vec2(second.worldX(), second.worldZ());

        float distance = (float) Math.sqrt(position0.distanceToSqr(position1));
        Vec2 fallbackDir = position1.add(position0.negated());
        if (fallbackDir.lengthSquared() > 0.0001f) {
            fallbackDir = fallbackDir.normalized();
        } else {
            fallbackDir = Vec2.ZERO;
        }

        Vec2 flow0 = first.flow().lengthSquared() > 0.0001f ? first.flow().normalized() : fallbackDir;
        Vec2 flow1 = second.flow().lengthSquared() > 0.0001f ? second.flow().normalized() : fallbackDir;


        Vec2 tangent0 = flow0.normalized().scale(distance * 0.45f);
        Vec2 tangent1 = flow1.normalized().scale(distance * 0.45f);


        float deltaSq = delta * delta;
        float deltaCube = deltaSq * delta;

        float factorPos0 = 2 * deltaCube - 3 * deltaSq + 1;
        float factorTan0 = deltaCube - 2 * deltaSq + delta;
        float factorPos1 = -2 * deltaCube + 3 * deltaSq;
        float factorTan1 = deltaCube - deltaSq;

        float x = factorPos0 * position0.x + factorTan0 * tangent0.x + factorPos1 * position1.x + factorTan1 * tangent1.x;
        float z = factorPos0 * position0.y + factorTan0 * tangent0.y + factorPos1 * position1.y + factorTan1 * tangent1.y;

        float dh00 = 6 * deltaSq - 6 * delta;
        float dh10 = 3 * deltaSq - 4 * delta + 1;
        float dh01 = -6 * deltaSq + 6 * delta;
        float dh11 = 3 * deltaSq - 2 * delta;

        float dx = dh00 * position0.x + dh10 * tangent0.x + dh01 * position1.x + dh11 * tangent1.x;
        float dz = dh00 * position0.y + dh10 * tangent0.y + dh01 * position1.y + dh11 * tangent1.y;

        Vec2 tangent = new Vec2(dx, dz).normalized();

        float diameter = Mth.lerp(delta * delta * delta, first.diameter(), second.diameter());


        float singleNoiseVal = (float) noise.noise(x, z);
        Vec2 normal = new Vec2(-tangent.y, tangent.x);
        float envelope = 4.0f * delta * (1.0f - delta);
        float offset = singleNoiseVal * maxDiversion * envelope;
        x += normal.x * offset;
        z += normal.y * offset;

        float envelopeDerivative = 4.0f * (1.0f - 2.0f * delta);
        float tangentSlope = Mth.clamp((singleNoiseVal * maxDiversion * envelopeDerivative) / Math.max(distance, 16.0f),-0.4f, 0.4f);
        tangent = tangent.add(normal.scale(tangentSlope)).normalized();

        float flowAngle = (float) (noise2.noise(x,z) * (Math.PI));

        Vec2 flow = tangent.scale(Mth.lerp(delta, first.flow().length(), second.flow().length())).rotate(flowAngle);


        return new RiverPoint(delta,x, z, 0.0f, 0.0f, diameter, 0.0f, flow, RiverNode.Cliff.EMPTY);
    }

    public RiverPoint withNewElevation(float elevationHere) {
        float waterAt = elevationHere - 2;
        if (this.cliffData != null && this.cliffData.start() >= 0.0f) {
            float start = cliffData.start();
            float end = cliffData.end();
            if (delta > start && delta < end) {
                waterAt = Math.max(elevationHere - 4,cliffData.endElevation() - 1);
            }
        }
        return new RiverPoint(delta,this.worldX,this.worldZ,elevationHere, waterAt,this.diameter,this.depth,this.flow,this.cliffData);
    }

    public float distanceSqr(float targetX, float targetZ) {
        float dx = targetX - this.worldX();
        float dz = targetZ - this.worldZ();
        return dx * dx + dz * dz;
    }

    public float getDistance(float targetX, float targetZ) {
        float dx = targetX - this.worldX();
        float dz = targetZ - this.worldZ();

        return (float) Math.sqrt(dx * dx + dz * dz);
    }

    public float radius(){
        return this.diameter() / 2;
    }

    public boolean isCliff() {
        return this.delta >= cliffData.start() && this.delta <= cliffData.end();
    }

    public float adaptElevationToRiver(float originalHeight, float xPos, float zPos){
        float distSqr = this.distanceSqr(xPos, zPos);

        if (distSqr > RIVER_LIMIT_SQR) return originalHeight;

        float distance = (float) Math.sqrt(distSqr);

        if (Float.isNaN(distance) || distance <= 0) {
            distance = 0.0f;
        }
        float targetHeight;
        float distDelta;

        float e = this.elevation();
        boolean flag = originalHeight > 68 && e < 63;
        float elevation = flag ? Math.min(originalHeight, 64 - this.depth()) : e;
        if (elevation == originalHeight) return originalHeight;


        float radius = this.radius();
        if (distance <= radius) {
            distDelta = distance / radius;
            float w = 1 - Math.clamp(distDelta, 0.0f, 1.0f);
            float pointDepth = w * this.depth();
            targetHeight = elevation - pointDepth;
        } else {

            float range = Math.clamp(radius * 1.5f, 8.0f, 64.0f);
            distDelta = (distance - radius) / range;

            if (distDelta > 1.0f) return originalHeight;

            targetHeight = Mth.lerp((float) Mth.smoothstep(distDelta),originalHeight,elevation);
        }
        return targetHeight;
    }

}
