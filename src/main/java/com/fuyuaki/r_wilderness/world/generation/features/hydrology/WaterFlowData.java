package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.List;

import static com.fuyuaki.r_wilderness.api.WildernessConstants.SEA_LEVEL;
import static com.fuyuaki.r_wilderness.world.generation.features.RegionFeatureGenerator.RIVER_LIMIT_SQR;

public record WaterFlowData(float waterLevel, Vec2 flow) {

    public static WaterFlowData mixAll(int x, int z,List<RiverPoint> flowDatas) {
        if (flowDatas.isEmpty()) return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);

        float firstInfluence = 0.0f;
        float firstHeight = 0.0f;
        Vec2 firstVelocity = Vec2.ZERO;

        float secondInfluence = 0.0f;
        float secondHeight = 0.0f;
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

            float targetWaterHeight;
            float influence;
            float distDelta;
            float radius = flowData.radius();

            float waterHeight = flowData.waterHeight();
            float diffToSeaLevel = Math.min(Math.max(waterHeight - 63, -1.5f) * 0.25f, 1.0f);

            if (distance <= radius) {
                distDelta = distance / radius;
                float w = 1 - Math.clamp(distDelta, 0.0f, 1.0f);
                influence = 1 + w;
            } else {

                distDelta = (distance - radius) / radius;
                influence = 1 - Math.clamp(distDelta, 0.0f, 1.0f);

                if (influence > firstInfluence) {
                    firstInfluence = influence;
                    firstHeight = SEA_LEVEL;
                    firstVelocity = flowData.flow();

                }else if(influence > secondInfluence) {
                    secondInfluence = influence;
                    secondHeight = SEA_LEVEL;
                    secondVelocity = flowData.flow();

                }

                continue;
            }

            if (influence < 0.01) continue;

            targetWaterHeight = waterHeight + (3 * diffToSeaLevel);

            influence = Mth.clamp(influence * influence,0.0f,2.0f);

            if (influence > firstInfluence) {
                firstInfluence = influence;
                firstHeight = targetWaterHeight;
                firstVelocity = flowData.flow();

            }else if(influence > secondInfluence) {
                secondInfluence = influence;
                secondHeight = targetWaterHeight;
                secondVelocity = flowData.flow();

            }
        }

        if (firstInfluence <= 0.0001f) {
            return new WaterFlowData(SEA_LEVEL, Vec2.ZERO);
        }

        float difference = firstInfluence - secondInfluence;

        float flowX = Mth.lerp(Mth.clamp((1 - difference) * 0.5f,0.0f,0.5f),firstVelocity.x,secondVelocity.x);
        float flowZ = Mth.lerp(Mth.clamp((1 - difference) * 0.5f,0.0f,0.5f),firstVelocity.y,secondVelocity.y);
        float finalWater = Math.max(Mth.lerp(Math.min(firstInfluence,1.0f), SEA_LEVEL, firstHeight), SEA_LEVEL);

        return new WaterFlowData(finalWater,new  Vec2(flowX,flowZ).scale(Math.min(firstInfluence,1.0f)));
    }
}
