package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayerTypes;
import com.fuyuaki.r_wilderness.world.level.material.RiverFluid;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.Map;

public class GenerationUtils {

    public static BlockState pickWaterBlockStateFor(RegionMemory memory, WorldRegion region, int x, int z, float waterLevel, boolean riverWater, Vec2 waterFlow) {
        if (!riverWater || waterLevel <= WildernessConstants.SEA_LEVEL + 0.5f && region.layers().getContinentalness().getUpsampled(x,z,memory) <= 0.0f) return Blocks.WATER.defaultBlockState();
        BlockState waterState = RBlocks.RIVER_WATER.get().defaultBlockState();
        waterState.getFluidState().setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
        waterState.getFluidState().setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));

        waterState.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
        waterState.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        return waterState;
    }

    public static float windsweptFactor(RegionMemory memory, WorldRegion region, int x, int z){
        int count = 0;

        float atmosPressureAt = region.layers().getAtmospherePressure().getUpsampled(x,z,memory);

        float atmosX = 0.0f, atmosZ = 0.0f;
        float heightX = 0.0f, heightZ = 0.0f;

        float elevation = region.layers().getElevation().getUpsampled(x,z,memory);
        int step = RegionLayerTypes.ATMOSPHERE_PRESSURE.scale();

        for (int xO = -1; xO <= 1; xO++) {
            for (int zO = -1; zO <= 1; zO++) {
                if (zO == 0 && xO == 0) continue;
                int xPost = x + (xO * step);
                int zPost = z + (zO * step);

                float heightAt = region.layers().getElevation().getUpsampled(xPost,zPost,memory);;


                float distSq = (float) (xO * xO + zO * zO);
                float aP = region.layers().getAtmospherePressure().getUpsampled(xPost,zPost,memory);
                float difference = atmosPressureAt - aP;
                float atmosFactor = difference / distSq;
                atmosX += atmosFactor * xO;
                atmosZ += atmosFactor * zO;

                float heightDifference = (heightAt - elevation) / distSq;

                heightX += heightDifference * xO;
                heightZ += heightDifference * zO;
                count++;
            }
        }

        float invCount = 1.0f / count;
        atmosX *= invCount;
        atmosZ *= invCount;
        heightX *= invCount;
        heightZ *= invCount;

        return (atmosX * heightX) + (atmosZ * heightZ);
    }


    public static Vec2 windVector(RegionMemory memory, WorldRegion region, int trueX, int trueZ){
        float atmosPressureAt =  region.layers().getAtmospherePressure().getUpsampled(trueX, trueZ,memory);
        int step = 8;
        int count = 0;
        float atmosX = 0.0f, atmosZ = 0.0f;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                int xPos = trueX + (x * step);
                int zPos = trueZ + (z * step);
                float aP =  region.layers().getAtmospherePressure().getUpsampled(xPos,zPos,memory);

                float difference = atmosPressureAt - aP;
                float distSq = (float) (x * x + z * z);
                count++;
                float factor = difference / distSq;
                atmosX += factor * x;
                atmosZ += factor * z;

            }
        }

        float invCount = 1.0f / count;
        atmosX *= invCount;
        atmosZ *= invCount;

        return new Vec2(atmosX,atmosZ);
    }


    public static Vec2 windVector(RegionFloatMap atmosphericPressure, int trueX, int trueZ, Map<RegionPos, WorldRegion> neighbors) {
        float atmosPressureAt = atmosphericPressure.upsampleFromNeighbors(trueX, trueZ, neighbors);
        int step = 8;
        int count = 0;
        float atmosX = 0.0f, atmosZ = 0.0f;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                int xPos = trueX + (x * step);
                int zPos = trueZ + (z * step);
                float aP = atmosphericPressure.upsampleFromNeighbors(xPos, zPos, neighbors);

                float difference = atmosPressureAt - aP;
                float distSq = (float) (x * x + z * z);
                count++;
                float factor = difference / distSq;
                atmosX += factor * x;
                atmosZ += factor * z;

            }
        }

        float invCount = 1.0f / count;
        atmosX *= invCount;
        atmosZ *= invCount;

        return new Vec2(atmosX,atmosZ);
    }


    public static class TectonicArrayIndex{
        public static final int WEIGHT = 0;
        public static final int EDGES = 1;
        public static final int BASE_ELEVATION_OFFSET = 2;
        public static final int VOLCANICNESS = 3;
        public static final int TECTONIC_STRESS = 4;
        public static final int BUOYANCY = 5;
        public static final int DISTANCE_PARENT = 6;
        public static final int AGE = 7;
        public static final int SWELLING = 8;


        public static final int LAYER_COUNT = 9;
    }
}
