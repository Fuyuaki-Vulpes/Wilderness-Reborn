package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

import static com.fuyuaki.r_wilderness.api.WildernessConstants.SEA_LEVEL;
import static com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator.RIVER_LIMIT_SQR;

public record WaterFlowData(float waterLevel, Vec2 flow) {


    public static final WaterFlowData NONE = new WaterFlowData(0.0f, Vec2.ZERO);

    public static WaterFlowData quicklyMakeFlow(int xPos, int zPos, ServerLevel level) {
        RegionMemory memory = RegionMemory.getInstance(level);
        if (memory == null) return WaterFlowData.NONE;
        WorldRegion worldRegion = memory.getRegionAt(xPos,zPos);
        List<RiverPoint> flowData = new ArrayList<>();
        RegionPos pos = worldRegion.getPos();

        int riverSearchRange = RegionRivers.RIVER_SEARCH_RANGE;
        for (int xO = -riverSearchRange; xO <= riverSearchRange; xO++) {
            for (int zO = -riverSearchRange; zO <= riverSearchRange; zO++) {
                int xOff = RegionRivers.RIVER_SIZE * xO;
                int zOff = RegionRivers.RIVER_SIZE * zO;
                RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(xPos + xOff), RegionPos.fromBlockCoord(zPos + zOff));
                WorldRegion r;
                if (pos0.x() == pos.x() && pos0.z() == pos.z()) {
                    r = worldRegion;
                } else {
                    r = memory.getRegionAt(pos0);
                }
                RiverNode n = r.getNbtData().rivers().getNodeAtBlock(xPos + xOff, zPos + zOff);
                if (n == null || !n.touches(xPos,zPos)) continue;

                for (RiverPoint p0 : n.getPoints()) {
                    float d0 = p0.getDistance(xPos, zPos);
                    float radLimit = p0.radius() * 1.5f;
                    if (d0 > radLimit) continue;

                    flowData.add(p0);
                }
            }
        }


        WaterFlowData data = WaterFlowData.fastMix(xPos,zPos,flowData);
        return data;
    }

    public static WaterFlowData fastMix(int x, int z,List<RiverPoint> flowDatas) {
        if (flowDatas.isEmpty()) return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);

        float firstInfluence = 0.0f;
        Vec2 firstVelocity = Vec2.ZERO;

        float secondInfluence = 0.0f;
        Vec2 secondVelocity = Vec2.ZERO;

        int pointCount = flowDatas.size();
        for (int i = 0; i < pointCount; i++) {
            RiverPoint flowData = flowDatas.get(i);
            if (flowData == null) {
                continue;
            }

            float distSqr = flowData.distanceSqr(x, z);
            if (distSqr > RIVER_LIMIT_SQR) continue;

            float distance = (float) Math.sqrt(distSqr);

            float influence;
            float distDelta;
            float radius = flowData.radius() * 1.5f;

            if (distance <= radius) {
                distDelta = distance / radius;
                float w = 1 - Math.clamp(distDelta, 0.0f, 1.0f);
                influence = w;
            } else {
                continue;
            }

            if (influence < 0.01) continue;


            influence = Mth.clamp(influence * influence,0.0f,1.0f);

            if (influence > firstInfluence) {
                firstInfluence = influence;
                firstVelocity = flowData.flow();

            }else if(influence > secondInfluence) {
                secondInfluence = influence;
                secondVelocity = flowData.flow();

            }
        }

        if (firstInfluence <= 0.0001f) {
            return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);
        }

        float difference = firstInfluence - secondInfluence;

        float flowX = Mth.lerp(Mth.clamp((1 - difference) * 0.5f,0.0f,0.5f),firstVelocity.x,secondVelocity.x);
        float flowZ = Mth.lerp(Mth.clamp((1 - difference) * 0.5f,0.0f,0.5f),firstVelocity.y,secondVelocity.y);

        return new WaterFlowData(0.0f,new Vec2(flowX,flowZ).scale(Math.min(firstInfluence,1.0f)));
    }

    public static WaterFlowData mixAll(int x, int z,List<RiverPoint> points, float elevationAt) {
        if (points.isEmpty()) return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);

        float firstInfluence = 0.0f;
        float firstWaterHeight = Float.MAX_VALUE;
        float finalWaterHeight = Float.MAX_VALUE;
        Vec2 firstVelocity = Vec2.ZERO;
        boolean insideCliff = true;
        float heightAccountingForCliff = 0.0f;
        float totalWeight = 0.0f;
        float secondInfluence = 0.0f;
        Vec2 secondVelocity = Vec2.ZERO;

        int pointCount = points.size();
        for (int i = 0; i < pointCount; i++) {
            RiverPoint point = points.get(i);
            if (point == null) {
                continue;
            }

            float distSqr = point.distanceSqr(x, z);
            if (distSqr > RIVER_LIMIT_SQR) continue;

            float distance = (float) Math.sqrt(distSqr);

            float radius = point.radius();

            float waterHeight = point.waterHeight();

            if (distance > radius) continue;

            float distDelta = distance / radius;
            float influence = 1 - Math.clamp(distDelta, 0.0f, 1.0f);


            if (influence < 0.01) continue;

            influence = Mth.clamp(influence * influence,0.0f,2.0f);
            if (point.isCliff()){
                heightAccountingForCliff += waterHeight * influence;
                totalWeight += influence;
            }
            if (influence >= 0.5f) {
                finalWaterHeight = Math.min(waterHeight,finalWaterHeight);
            }


                if (influence > firstInfluence) {
                    insideCliff = point.isCliff();
                    firstInfluence = influence;
                    firstVelocity = point.flow();
                    firstWaterHeight = waterHeight;

                } else {
                    secondInfluence = influence;
                    secondVelocity = point.flow();
                }


        }

        if (firstInfluence <= 0.0001f) {
            return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);
        }

        float difference = firstInfluence - secondInfluence;

        float difLerp = Mth.clamp((1 - difference) * 0.5f, 0.0f, 0.5f);
        float flowX = Mth.lerp(difLerp,firstVelocity.x,secondVelocity.x);
        float flowZ = Mth.lerp(difLerp,firstVelocity.y,secondVelocity.y);
        float finalWater = Math.max(SEA_LEVEL, insideCliff ? (heightAccountingForCliff / totalWeight) : finalWaterHeight);

        return new WaterFlowData(finalWater,new  Vec2(flowX,flowZ).scale(Math.min(firstInfluence,1.0f)));
    }
}
