package com.fuyuaki.r_wilderness.world.generation.features;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.GenerationUtils;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionRivers;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RiverNode;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RiverPoint;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.WaterFlowData;
import com.fuyuaki.r_wilderness.world.generation.features.plates.TectonicPlateNode;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RegionFeatureGenerator {
    public static final float RIVER_LIMIT = RiverNode.MAX_DIAMETER * 2.0f;
    public static final float RIVER_LIMIT_SQR = RIVER_LIMIT * RIVER_LIMIT;
    public static final float RIVER_INV_MAX_DEPTH = 1.0f / RiverNode.MAX_DEPTH;
    public static final float RIVER_MINIMUM_HEIGHT_PROCESSOR = WildernessConstants.SEA_LEVEL - RiverNode.MAX_DEPTH;


    public static float[] calculateRasterizedPlatesForPosition(int xPos, int zPos, Map<RegionPos, WorldRegion> neighbors, TectonicPlateNode[] nearestNodes, float[] distances, int arrayHold) {
        TectonicPlateNode[] nodes2 = new TectonicPlateNode[9];
        float[] dist2 = new float[9];
        Arrays.fill(nearestNodes, null);
        Arrays.fill(distances, Float.MAX_VALUE);
        Arrays.fill(nodes2, null);
        Arrays.fill(dist2, Float.MAX_VALUE);
        int scale = 512;
        int scanZone = 4;
        int x;
        int z;
        RegionPos pos;
        TectonicPlateNode nearbyNode;
        float distance;
        boolean shouldInsert;
        for (int xO = -scanZone; xO <= scanZone; xO++) {
            for (int zO = -scanZone; zO <= scanZone; zO++) {
                x = xPos + (xO * scale);
                z = zPos + (zO * scale);

                pos = new RegionPos(RegionPos.fromBlockCoord(x),
                        RegionPos.fromBlockCoord(z));

                nearbyNode = neighbors.get(pos).getNbtData().plates().closestNode(x, z);

                if (nearbyNode == null) continue;
                distance = nearbyNode.getWeightedDistance(xPos, zPos);

                shouldInsert = true;
                for (int k = 0; k < arrayHold; k++) {
                    if (nearestNodes[k] != null && nearestNodes[k].getId() == nearbyNode.getId()) {
                        if (distance >= distances[k]) {
                            shouldInsert = false;
                            break;
                        } else {
                            for (int j = k; j < arrayHold - 1; j++) {
                                nearestNodes[j] = nearestNodes[j + 1];
                                distances[j] = distances[j + 1];
                            }
                            nearestNodes[arrayHold - 1] = null;
                            distances[arrayHold - 1] = Float.MAX_VALUE;
                            k--;
                        }
                    }
                }

                if (!shouldInsert) continue;


                for (int i = 0; i < arrayHold; i++) {
                    if (distance < distances[i]) {
                        for (int j = arrayHold - 1; j > i; j--) {
                            distances[j] = distances[j - 1];
                            nearestNodes[j] = nearestNodes[j - 1];
                        }
                        distances[i] = distance;
                        nearestNodes[i] = nearbyNode;
                        break;
                    }
                }


            }
        }
        for (int xO = -1; xO <= 1; xO++) {
            for (int zO = -1; zO <= 1; zO++) {
                x = xPos + (xO * scale);
                z = zPos + (zO * scale);

                pos = new RegionPos(RegionPos.fromBlockCoord(x),
                        RegionPos.fromBlockCoord(z));

                nearbyNode = neighbors.get(pos).getNbtData().plates().closestNode(x, z);

                if (nearbyNode == null) continue;
                distance = nearbyNode.getWeightedDistance(xPos, zPos);

                shouldInsert = true;
                for (TectonicPlateNode tectonicPlateNode : nodes2) {
                    if (tectonicPlateNode != null && tectonicPlateNode == nearbyNode) {
                        shouldInsert = false;
                        break;

                    }
                }
                if (!shouldInsert) continue;

                for (int i = 0; i < nodes2.length; i++) {
                    if (distance < dist2[i]) {
                        for (int j = nodes2.length - 1; j > i; j--) {
                            dist2[j] = dist2[j - 1];
                            nodes2[j] = nodes2[j - 1];
                        }
                        dist2[i] = distance;
                        nodes2[i] = nearbyNode;
                        break;
                    }
                }
            }
        }
        float[] finalMap = new float[GenerationUtils.TectonicArrayIndex.LAYER_COUNT];
        Arrays.fill(finalMap, 0.0f);
        if (nearestNodes[0] == null) return finalMap;


        int weightI = GenerationUtils.TectonicArrayIndex.WEIGHT;
        int edgeI = GenerationUtils.TectonicArrayIndex.EDGES;
        int heightI = GenerationUtils.TectonicArrayIndex.BASE_ELEVATION_OFFSET;
        int volcanicI = GenerationUtils.TectonicArrayIndex.VOLCANICNESS;
        int stressI = GenerationUtils.TectonicArrayIndex.TECTONIC_STRESS;
        int buoyancyI = GenerationUtils.TectonicArrayIndex.BUOYANCY;
        int distI = GenerationUtils.TectonicArrayIndex.DISTANCE_PARENT;
        int ageI = GenerationUtils.TectonicArrayIndex.AGE;
        int swellI = GenerationUtils.TectonicArrayIndex.SWELLING;

        finalMap[weightI] = nearestNodes[0].getWeight();
        finalMap[edgeI] = 0;
        finalMap[heightI] = 0;
        finalMap[volcanicI] = 0;
        finalMap[stressI] = 0;

        float[] localData = makePlateData(dist2, nodes2);
        finalMap[buoyancyI] = localData[0];
        finalMap[distI] = localData[1];
        float localAge = localData[2];
        finalMap[ageI] = localAge;
        finalMap[swellI] = localData[3];

        if (nearestNodes[1] == null) return finalMap;

        int validNodeCount = 0;
        while (validNodeCount < nearestNodes.length && nearestNodes[validNodeCount] != null) {
            validNodeCount++;
        }
        if (validNodeCount < 2) return new float[]{nearestNodes[0].getWeight(), 0, 0, 0, 0};

        float closestDist = distances[0];
        TectonicPlateNode closestNode = nearestNodes[0];

        float[] rasterFinal = calculatePlateRasterization(validNodeCount, nearestNodes, distances, closestNode, closestDist);
        finalMap[weightI] = rasterFinal[weightI];
        finalMap[edgeI] = rasterFinal[edgeI];

        pos = new RegionPos(RegionPos.fromBlockCoord(xPos),
                RegionPos.fromBlockCoord(zPos));

        WorldRegion regionAt = neighbors.get(pos);
        float volcanicness;
        try {
            float min = 0.35f;
            volcanicness = ((float) regionAt.layers().record().volcanicness().getNoise().noise(xPos,zPos) * min) / (1 - min);
        }catch (Exception e) {
            volcanicness = 0.0f;
        }

        finalMap[heightI] = rasterFinal[heightI] * (0.5f + ((float) neighbors.get(pos).layers().record().tectonicIntensity().getNoise().noise(xPos,zPos) * 0.5f));
        finalMap[volcanicI] = Mth.clamp(rasterFinal[volcanicI] + (volcanicness * localAge * localAge),0.0f,1.0f);
        finalMap[stressI] = rasterFinal[stressI];
        finalMap[buoyancyI] = localData[0];
        return finalMap;
    }

    private static float[] makePlateData(float[] distances, TectonicPlateNode[] nodes) {

        float closestDist = distances[0];
        TectonicPlateNode closest = nodes[0];

        float effectRange = 256.0f;
        int validNodeCount = 0;

        float buoyancies = closest.getBuoyancy();
        float age = closest.getAge();
        float swell = closest.getSwelling();

        float total = 1.0f;
        float maxParentDist = 8192.0f;
        float distToParent = closest.getDistToParent() / maxParentDist;

        while (validNodeCount < nodes.length && nodes[validNodeCount] != null) {
            validNodeCount++;
        }
        float otherDist;
        float distanceDifference;
        float d2;
        float pairBias;
        float parentDist;
        for (int i = 0; i < validNodeCount; i++) {
            float range = effectRange;

            TectonicPlateNode otherNode = nodes[i];
            otherDist = distances[i];
            if (closest.getId() != otherNode.getId()) range = effectRange / 2;

            distanceDifference = otherDist - closestDist;
            d2 = Math.clamp(1 - (distanceDifference / range), 0, 1);
            pairBias = (d2 * d2 * (3.0f - 2.0f * d2));
            parentDist = Math.clamp(otherNode.getDistToParent() / maxParentDist, 0, 1);
            distToParent += parentDist * pairBias;
            buoyancies += otherNode.getBuoyancy() * pairBias;
            age += otherNode.getAge() * pairBias;
            swell += otherNode.getSwelling() * pairBias;
            total += pairBias;
        }

        return new float[]{
                buoyancies / total,
                distToParent / total,
                age / total,
                swell / total,
        };
    }

    private static float[] calculatePlateRasterization(int validNodeCount, TectonicPlateNode[] nearestNodes, float[] distances, TectonicPlateNode closestNode, float closestDist) {
        float boundaryWidth = 1024.0f;
        int weightI = GenerationUtils.TectonicArrayIndex.WEIGHT;
        int edgeI = GenerationUtils.TectonicArrayIndex.EDGES;
        int heightI = GenerationUtils.TectonicArrayIndex.BASE_ELEVATION_OFFSET;
        int volcanicI = GenerationUtils.TectonicArrayIndex.VOLCANICNESS;
        int stressI = GenerationUtils.TectonicArrayIndex.TECTONIC_STRESS;

        float highestDeform = 0.0f;
        float subHighDeform = 0.0f;
        float lowestDeform = 0.0f;
        float subLowDeform = 0.0f;

        float totalVolcanicness = 0.0f;
        float totalStress = 0.0f;
        float weights = closestNode.getWeight();

        float dist12 = distances[1] - closestDist;
        float multiplier = Math.clamp(dist12 / (boundaryWidth / 2), 0.0f, 1.0f);

        final float blendFactor = 0.25f;
        float total = 1.0f;
        TectonicPlateNode otherNode;
        float otherDist;
        float distanceDifference;
        float d;
        float pairWeight;
        float pairFarWeight;
        float d2;
        float pairWeight2;
        float[] pairResults;
        float effectiveDeform;
        for (int i = 0; i < validNodeCount; i++) {
            otherNode = nearestNodes[i];
            otherDist = distances[i];
            if (closestNode.getId() == otherNode.getId()) continue;

            distanceDifference = otherDist - closestDist;
            d = Math.clamp(1 - (distanceDifference / boundaryWidth), 0, 1);
            pairWeight = (d * d * (3.0f - 2.0f * d));
            pairFarWeight = d ;
            d2 = Math.clamp(1 - (distanceDifference / (boundaryWidth / 2)), 0, 1);
            pairWeight2 = (d2 * d2 * (3.0f - 2.0f * d2));
            pairResults = getRasterizedDeformation(closestNode, otherNode, otherDist, closestDist, boundaryWidth);

            effectiveDeform = pairResults[0] * pairWeight;
            if (effectiveDeform > 0.0f) {
                if (effectiveDeform > highestDeform) {
                    subHighDeform += highestDeform * blendFactor;
                    highestDeform = effectiveDeform;

                } else {
                    subHighDeform += effectiveDeform;

                }
            } else if (effectiveDeform <= 0.0f) {
                if (effectiveDeform < lowestDeform) {
                    subLowDeform += lowestDeform * blendFactor;
                    lowestDeform = effectiveDeform;

                } else {
                    subLowDeform += effectiveDeform;

                }
            }
            totalVolcanicness += pairResults[1] * pairWeight;
            totalStress += pairResults[2] * pairFarWeight * 2.0f;

            weights += otherNode.getWeight() * pairWeight2;
            total += pairWeight2;
        }

        float[] finalRaster = new float[5];
        finalRaster[weightI] = weights / total;
        float edge1 = 1 - multiplier;
        finalRaster[edgeI] = edge1;
        finalRaster[heightI] = highestDeform + subHighDeform + lowestDeform + subLowDeform;
        finalRaster[volcanicI] = Math.clamp(totalVolcanicness, 0.0f, 1.0f);
        finalRaster[stressI] = Math.clamp(totalStress, 0.0f, 1.0f);


        return finalRaster;
    }

    public static float[] getRasterizedDeformation(TectonicPlateNode closestNode, TectonicPlateNode otherNode, float otherDist, float closestDist, float boundaryWidth) {
        boolean lockedHeightOffset = false;
        float finalHeightOff = 0.0f;
        float distWithSideBias = otherDist - closestDist;
        float distToEdge = Math.abs(distWithSideBias);
        float[] otherResults = new float[]{0.0f, 0.0f};
        if (distToEdge >= boundaryWidth) {
            lockedHeightOffset = true;
        }

        float bF = 1.0f - (distToEdge / boundaryWidth);
        float boundaryFadeSmooth = bF * bF * (3.0f - 2.0f * bF);
        float weight1 = closestNode.getWeight();
        float weight2 = otherNode.getWeight();

        if (!lockedHeightOffset) {
            float relativeSpeedX = closestNode.getVelocityX() - otherNode.getVelocityX();
            float relativeSpeedZ = closestNode.getVelocityZ() - otherNode.getVelocityZ();

            float parentXB = otherNode.getParentX();
            float parentXA = closestNode.getParentX();
            float distToBX = parentXB - parentXA;
            float parentZB = otherNode.getParentZ();
            float parentZA = closestNode.getParentZ();
            float distToBZ = parentZB - parentZA;

            float length = (float) Math.sqrt(distToBX * distToBX + distToBZ * distToBZ);
            if (length < 1e-5f || Float.isInfinite(length) || Float.isNaN(length)) {
                lockedHeightOffset = true;
            }
            if (!lockedHeightOffset) {


                distToBX /= length;
                distToBZ /= length;

                float directSpeed = relativeSpeedX * distToBX + relativeSpeedZ * distToBZ;
                float transformSpeedX = relativeSpeedX - directSpeed * distToBX;
                float transformSpeedZ = relativeSpeedZ - directSpeed * distToBZ;
                float transformSpeed = (float) Math.sqrt(transformSpeedX * transformSpeedX + transformSpeedZ * transformSpeedZ);

                float weightDifference = weight1 - weight2;
                float collisionSpeed = Math.max(0.0f, -directSpeed);
                float separationSpeed = Math.max(0.0f, directSpeed);

                float convergenceShift = makeConvergence(distToEdge, collisionSpeed, weightDifference, boundaryWidth, otherResults);
                float divergenceShift = makeDivergence(distToEdge, separationSpeed, boundaryWidth, otherResults);


                float shearDeformation = makeShear(transformSpeed, distToEdge, boundaryWidth, otherResults);

                float heightOffset = convergenceShift + divergenceShift + shearDeformation;

                finalHeightOff = heightOffset * boundaryFadeSmooth;
            }
        }

        return new float[]{finalHeightOff, otherResults[0], otherResults[1]};
    }

    private static float makeShear(float transformSpeed, float distToEdge, float boundaryWidth, float[] results) {
        float shearFrequency = 0.15f;
        float speedIntensity = Math.abs(transformSpeed);
        float shearDeformation = speedIntensity * WildernessConstants.SHEAR_AMPLITUDE * (float) Math.sin(distToEdge * shearFrequency);

        float faultWidth = boundaryWidth * 0.15f;
        float faultRatio = distToEdge / faultWidth;
        float faultProximity = (float) Math.exp(-faultRatio * faultRatio);
        results[1] += faultProximity * speedIntensity;

        return shearDeformation;
    }

    private static float makeConvergence(float distanceToEdge, float convergenceSpeed, float weightDifference, float boundaryWidth, float[] results) {
        float wShift = Math.signum(weightDifference);


        float shiftIntensity = 3.0f;
        float subdueBias = Math.clamp(weightDifference * shiftIntensity, -1.0f, 1.0f);
        float collisionBias = 1.0f - Math.abs(subdueBias);


        float mountainRadius = boundaryWidth * 0.25f;
        float mDistanceRatio = distanceToEdge / mountainRadius;

        float collisionGaussian = (float) Math.exp(-mDistanceRatio * mDistanceRatio);
        float collisionShift = WildernessConstants.MAX_MOUNTAIN_HEIGHT * collisionBias * collisionGaussian;
        float subdueShift;
        float volcanicArcFactor = 0.0f;

        //TRENCH
        if (wShift < 0.0f) {
            float trenchRatio = distanceToEdge / (boundaryWidth * 0.3f);
            if (trenchRatio > 1.0f) {
                subdueShift = 0.0f;
            } else {
                subdueShift = -WildernessConstants.MAX_TRENCH_DEPTH * (1.0f - trenchRatio * trenchRatio);
            }
        } else {
            //OVERRIDE
            float arcPeakDist = boundaryWidth * 0.25f;
            if (distanceToEdge < arcPeakDist) {
                float t = distanceToEdge / arcPeakDist;
                float smoothT = (t * t * (3.0f - 2.0f * t));
                subdueShift = -WildernessConstants.MAX_TRENCH_DEPTH + ((WildernessConstants.MAX_TRENCH_DEPTH + WildernessConstants.MAX_ARC_HEIGHT) * smoothT);
                volcanicArcFactor = smoothT;
            } else {
                float backArcDist = (distanceToEdge - arcPeakDist) / (boundaryWidth * 0.6f);
                float backArcFalloff = (float) Math.exp(-backArcDist * backArcDist * backArcDist);
                subdueShift = WildernessConstants.MAX_ARC_HEIGHT * backArcFalloff;
                volcanicArcFactor = backArcFalloff;
            }
        }

        float subdueShiftPost = subdueShift * Math.abs(subdueBias);

        results[0] += volcanicArcFactor * Math.abs(subdueBias) * convergenceSpeed;
        float compressionalStress = Math.max(collisionGaussian, 1.0f - Math.min(1.0f, distanceToEdge / (boundaryWidth * 0.3f)));
        results[1] += compressionalStress * convergenceSpeed;
        return (subdueShiftPost + collisionShift) * (convergenceSpeed * 0.5f + 0.5f);
    }

    private static float makeDivergence(float distanceToEdge, float divergenceSpeed, float boundaryWidth, float[] results) {
        float riftWidth = boundaryWidth * 0.4f;
        float distanceRatio = distanceToEdge / riftWidth;
        float riftValleyUpShift = WildernessConstants.RIFT_VALLEY_UPSHIFT;
        float riftValleyDepth = WildernessConstants.RIFT_VALLEY_DEPTH + riftValleyUpShift;

        float riftGaussian = (float) Math.exp(-distanceRatio * distanceRatio);
        float riftValleyDownShift = -riftValleyDepth * riftGaussian;
        results[0] += divergenceSpeed * riftGaussian;

        return divergenceSpeed * (riftValleyDownShift + riftValleyUpShift);
    }


    public static RegionFloatMap[] createRasterizedPlateLayers(RegionPos pos, Map<RegionPos, WorldRegion> neighbors, WorldRegion worldRegion) {
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap[] rasterizedPlates = new RegionFloatMap[GenerationUtils.TectonicArrayIndex.LAYER_COUNT];
        int resolution = RegionFloatMap.Resolution.R_512;
        for (int i = 0; i < rasterizedPlates.length; i++) {
            rasterizedPlates[i] = worldRegion.layers().getOfIndex(i);
        }

        float[][] maps = new float[GenerationUtils.TectonicArrayIndex.LAYER_COUNT][resolution * resolution];
        int threadCount = 8;
        CompletableFuture<Void>[] futures = new CompletableFuture[threadCount];
        int samples = resolution / threadCount;
        int scale = RegionPos.REGION_SIZE / resolution;
        int arrayHold = 12;

        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                TectonicPlateNode[] nearestNodes = new TectonicPlateNode[arrayHold];
                float[] distances = new float[arrayHold];
                int trueX;
                int trueZ;
                float[] rasterLocal;
                float r;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;
                        rasterLocal = calculateRasterizedPlatesForPosition(trueX, trueZ, neighbors, nearestNodes, distances, arrayHold);
                        for (int i = 0; i < GenerationUtils.TectonicArrayIndex.LAYER_COUNT; i++) {
                            r = rasterLocal[i];
                            if (rasterizedPlates[i].getNoiseMap() != null) {
                                r *= (float) (rasterizedPlates[i].getNoiseMap().noise(trueX, trueZ) + 1) * 0.5f;
                            }
                            maps[i][xAdapted + zPos] = r;
                        }
                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();
        for (int i = 0; i < GenerationUtils.TectonicArrayIndex.LAYER_COUNT; i++) {
            rasterizedPlates[i].updateMap(maps[i]);
            rasterizedPlates[i].setStatus(RegionFloatMap.Status.COMPLETE);
        }
        return rasterizedPlates;
    }


    public static RegionFloatMap plateauMap(RegionPos pos, ServerLevel level, WorldRegion worldRegion) {
        Noise2D plateauFilter = worldRegion.layers().record().plateauFilter().getNoise();
        RegionFloatMap stress = worldRegion.layers().getTectonicStress();
        RegionFloatMap swell = worldRegion.layers().getSwelling();
        RegionFloatMap buoyancy = worldRegion.layers().getBuoyancy();
        RegionFloatMap age = worldRegion.layers().getAge();
        RegionFloatMap distParent = worldRegion.layers().getDistToParent();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap rockiness = worldRegion.layers().getRockiness();
        RegionFloatMap erosion = worldRegion.layers().getErodability();
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap map = worldRegion.layers().getPlateaus();
        int resolution = map.getResolution();


        float[] fMap = new float[resolution * resolution];
        int threadCount = 8;
        CompletableFuture<Void>[] futures = new CompletableFuture[threadCount];
        int samples = resolution / threadCount;
        int scale = RegionPos.REGION_SIZE / resolution;

        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float distEdge;
                float swellFilter;
                float buoyFilter;
                float volcanicFilter;
                float stressFilter;
                float buoyOrSwell;
                float distFilter;
                float distInv;
                float ageFilter;
                float filter1;
                float filter2;
                float filter3;
                float filter4;
                float p;
                float plat;
                float e;
                float effectiveErosion;
                float r;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;

                        distEdge = 1 - distParent.getGlobal(trueX, trueZ);

                        swellFilter = filterMap(swell, trueX, trueZ, 0.15f, 0.50f);
                        buoyFilter = filterMap(buoyancy, trueX, trueZ, 0.15f, 0.50f);
                        volcanicFilter = filterMap(volcanic, trueX, trueZ, 0.65f, 0.85f);
                        stressFilter = filterMap(stress, trueX, trueZ, 0.15f, 0.45f);
                        buoyOrSwell = Math.max(buoyFilter, swellFilter);
                        r = filterMap(rockiness, trueX, trueZ, 0.15f, 0.75f);
                        e = filterMap(erosion, trueX, trueZ, 0.2f, 0.75f);
                        effectiveErosion = e * (1 - (r * 0.5f));
                        distFilter = Math.clamp((0.6f - distEdge) * 5, 0, 1);
                        distInv = Math.clamp((distEdge - 0.45f) * 5, 0, 1);

                        ageFilter = filterMap(age, trueX, trueZ, 0.35f, 0.65f);

                        filter1 = distInv * ageFilter * buoyOrSwell;
                        filter2 = distFilter * volcanicFilter * buoyOrSwell;
                        filter3 = distFilter * stressFilter * buoyOrSwell;
                        filter4 = buoyOrSwell * effectiveErosion;

                        p = Math.clamp(((float) ((plateauFilter.noise(trueX, trueZ) + 1) / 2) - 0.35f) * 10, 0, 1);
                        plat = p * Math.max(Math.max(filter1, filter2), Math.max(filter3, filter4));
                        fMap[xAdapted + zPos] = Math.clamp(plat, 0.0f, 1.0f);

                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();

        map.updateMap(fMap);
        map.setStatus(RegionFloatMap.Status.COMPLETE);
        return map;
    }

    private static float filterMap(RegionFloatMap map, int x, int z, float minimum, float maximum) {
        float multiplier = 1 / (maximum - minimum);
        return Math.clamp((map.getGlobal(x, z) - minimum) * multiplier, 0, 1.25f);
    }

    private static float filter(float v, float minimum, float maximum) {
        float multiplier = 1 / (maximum - minimum);
        return Math.clamp((v - minimum) * multiplier, 0, 1.0f);
    }

    private static float peakAtMap(RegionFloatMap map, int x, int z, float range, float peak) {
        float f = map.getGlobal(x, z);
        float difference = Math.abs(f - peak) / range;
        return Math.clamp(1 - difference, 0, 1);
    }

    private static float peakAt(float f, float range, float peak) {
        float difference = Math.abs(f - peak) / range;
        return Math.clamp(1 - difference, 0, 1);
    }

    public static RegionFloatMap createHills(RegionPos pos, ServerLevel level, WorldRegion worldRegion) {

        RegionFloatMap swell = worldRegion.layers().getSwelling();
        RegionFloatMap buoyancy = worldRegion.layers().getBuoyancy();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap rockiness = worldRegion.layers().getRockiness();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap plateaus = worldRegion.layers().getPlateaus();
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        int resolution = worldRegion.layers().getHilliness().getResolution();
        RegionFloatMap hills = worldRegion.layers().getHilliness();

        float[] hMap = new float[resolution * resolution];
        int threadCount = 8;
        CompletableFuture<Void>[] futures = new CompletableFuture[threadCount];
        int samples = resolution / threadCount;
        int scale = RegionPos.REGION_SIZE / resolution;
        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float plateauF;
                float swellFilter;
                float buoyFilter;
                float buoyOrSwell;
                float erod;
                float erodEffective;
                float rock;
                float volc;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;

                        plateauF = plateaus.getGlobal(trueX, trueZ);

                        swellFilter = filterMap(swell, trueX, trueZ, 0.25f, 0.50f);
                        buoyFilter = filterMap(buoyancy, trueX, trueZ, 0.25f, 0.85f);
                        buoyOrSwell = Math.max(buoyFilter, swellFilter);
                        volc = filterMap(volcanic, trueX, trueZ, 0.25f, 0.65f);
                        erod = 1 - filterMap(erodability, trueX, trueZ, -0.5f, 0.90f);
                        rock = filterMap(rockiness, trueX, trueZ, -0.25f, 0.85f);
                        erodEffective = erod * (0.5f + (rock * 0.5f));

                        hMap[xAdapted + zPos] = Math.clamp(((1 - (plateauF * 0.5f)) *
                                Math.max(
                                        buoyOrSwell * volc * (0.5f + (erod * 0.5f)),
                                        erodEffective * filter((float) ((worldRegion.layers().record().mountains().getNoise().noise(trueX, trueZ) + 1) / 2), 0.0f, 0.5f)
                                )
                        ), 0, 1);

                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();
        hills.updateMap(hMap);
        hills.setStatus(RegionFloatMap.Status.COMPLETE);
        return hills;
    }


    public static void createHeightmapsAndPrepareForRiver(RegionPos pos, ServerLevel level, WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {

        RegionFloatMap plateElevationOffset = worldRegion.layers().getPlateElevationOffsets();
        RegionFloatMap buoyancy = worldRegion.layers().getBuoyancy();
        RegionFloatMap age = worldRegion.layers().getAge();
        RegionFloatMap swell = worldRegion.layers().getSwelling();
        Noise2D plateauRiverCarve = worldRegion.layers().record().plateauRiverCarve().getNoise();
        Noise2D plateauTableCarve = worldRegion.layers().record().plateauTableCarve().getNoise();
        RegionFloatMap vegetation = worldRegion.layers().getVegetation();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap humidity = worldRegion.layers().getHumidity();
        RegionFloatMap stress = worldRegion.layers().getTectonicStress();
        RegionFloatMap weight = worldRegion.layers().getPlateWeights();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap hilliness = worldRegion.layers().getHilliness();
        RegionFloatMap plateauOffset = worldRegion.layers().getPlateaus();

        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap heightmap = worldRegion.layers().getHeightmapBasic();
        RegionFloatMap continentalness = worldRegion.layers().getContinentalness();
        int resolution = heightmap.getResolution();
        int resolutionCont = continentalness.getResolution();


        float[] heightFmap = new float[resolution * resolution];
        float[] continentalFMap = new float[resolutionCont * resolutionCont];
        int threadCount = 8;
        CompletableFuture<Void>[] futures = new CompletableFuture[threadCount];
        int samples = resolution / threadCount;
        int scale = RegionPos.REGION_SIZE / resolution;
        int samplesC = resolutionCont / threadCount;
        int scaleC = RegionPos.REGION_SIZE / resolutionCont;
        float maxContinentForBuyancyInfluence = 0.35f;
        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samplesC;
            int endX = startX + samplesC;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float buoyancyLocal;
                float weightLocal;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolutionCont;
                    for (int zPos = 0; zPos < resolutionCont; zPos++) {
                        trueX = xPos * scaleC + minX;
                        trueZ = zPos * scaleC + minZ;
                        weightLocal = Math.clamp(((weight.upsampleFromNeighbors(trueX, trueZ, neighbors) - 0.65f) / 0.35f), -1.0f, 1.0f);
                        float buyancyInf = Math.clamp(weightLocal,0,maxContinentForBuyancyInfluence) / maxContinentForBuyancyInfluence;
                        buoyancyLocal = buoyancy.upsampleFromNeighbors(trueX, trueZ, neighbors) * 0.45f * (buyancyInf * 0.5f + 0.5f);
                        continentalFMap[xAdapted + zPos] = Math.clamp(buoyancyLocal + weightLocal, -1.5f, 1.5f);
                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();


        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float pEOf; //Plate Elevation Offset
                float buoyancyLocal;
                float weightLocal;
                float plateAge;
                float swelling;
                float plateHeightVariation;
                float erosion;
                float veg;
                float hum;
                float pBaseE;
                float pTable;
                float pRiver;
                float plateauElevation;
                float stressL;
                float platStepped;
                float hills;
                float hillElev;
                float v;
                float artifacts;
                float localElevation;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;
                        pEOf = plateElevationOffset.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        buoyancyLocal = buoyancy.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        weightLocal = Math.clamp(((weight.upsampleFromNeighbors(trueX, trueZ, neighbors) - 0.7f) / 0.3f), -1.0f, 1.0f);
                        plateAge = age.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        swelling = Math.clamp((1+ swell.upsampleFromNeighbors(trueX, trueZ, neighbors)) / 2,0,1);

                        float continents = Math.clamp((buoyancyLocal * 0.45f) + weightLocal, -1.5f, 1.5f);
                        plateHeightVariation = (pEOf * ((1 - plateAge) * 0.25f + 0.75f))
                                + (continents * WildernessConstants.CONTINENTAL_ELEVATION_MIN_MAX)
                                + (swelling * WildernessConstants.SWELL_HILL_SCALE);

                        erosion = erodability.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        veg = vegetation.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        hum = humidity.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        stressL = stress.upsampleFromNeighbors(trueX, trueZ, neighbors);


                        float plat = plateauOffset.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        pBaseE = plat * WildernessConstants.BASE_PLATEAU_UPLIFT;
                        hills = hilliness.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        pRiver = 1.0f - ((float) Mth.smoothstep(filter((float) plateauRiverCarve.noise(trueX, trueZ), -0.85f, 0.85f)) * filter(erosion, -0.35f, 0.75f) * filter(veg, -0.15f, 0.90f));
                        pTable = 1.0f - ((float) Mth.smoothstep(filter((float) plateauTableCarve.noise(trueX, trueZ), -0.65f, 0.65f)) * filter(erosion, -0.35f, 0.75f) * filter(hum, 0.0f, 0.85f) * (1 - filter(stressL, 0.1f, 0.85f)));
                        plateauElevation = pBaseE * pTable * pRiver;
                        float scaleToStep = Math.clamp(1 - ((1 + erosion) * 0.5f), 0, 1) * WildernessConstants.PLATEAU_STEPS_MAX;
                        platStepped = Math.max(stepped(plateauElevation, scaleToStep), 0);
                        float hillO = filter(hills, 0.05f, 0.95f) * ((1 - (filter(erosion, -0.5f, 0.85f))) * 0.5f + 0.5f) * WildernessConstants.MOUNT_HILL_ELEVATION;
                        hillElev = ((1 - plat) * hillO) + (plat * stepped(hillO, scaleToStep));


                        v = volcanic.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        artifacts = (v * v * (1 - filter(plateAge, 0.1f, 0.65f)) * WildernessConstants.VOLCANIC_ARTIFACT_SCALE);

                        localElevation = plateHeightVariation + platStepped + artifacts + hillElev;


                        heightFmap[xAdapted + zPos] = 64 + localElevation;

                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();

        heightmap.updateMap(heightFmap);
        heightmap.setStatus(RegionFloatMap.Status.COMPLETE);

        continentalness.updateMap(continentalFMap);
        continentalness.setStatus(RegionFloatMap.Status.COMPLETE);

        worldRegion.layers().setHeightmapBasic(heightmap);
        worldRegion.layers().setContinentalness(continentalness);
    }

    public static void createRiverShapeAndHeightmaps(RegionPos pos, ServerLevel level, WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {


        RegionFloatMap heightmapBasic = worldRegion.layers().getHeightmapBasic();
        RegionFloatMap buoyancy = worldRegion.layers().getBuoyancy();
        RegionFloatMap age = worldRegion.layers().getAge();
        RegionFloatMap swell = worldRegion.layers().getSwelling();
        Noise2D plateauRiverCarve = worldRegion.layers().record().plateauRiverCarve().getNoise();
        Noise2D plateauTableCarve = worldRegion.layers().record().plateauTableCarve().getNoise();
        RegionFloatMap vegetation = worldRegion.layers().getVegetation();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap humidity = worldRegion.layers().getHumidity();
        RegionFloatMap stress = worldRegion.layers().getTectonicStress();
        RegionFloatMap weight = worldRegion.layers().getPlateWeights();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap hilliness = worldRegion.layers().getHilliness();
        RegionFloatMap plateauOffset = worldRegion.layers().getPlateaus();
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap elevations = worldRegion.layers().getElevation();
        RegionFloatMap rivers = worldRegion.layers().getRivers();
        int riverRes = rivers.getResolution();


        float[] riversMap = new float[riverRes * riverRes];
        int threadCount = 8;
        CompletableFuture<Void>[] futuresR = new CompletableFuture[threadCount];
        int maxRiverRange = RegionRivers.RIVER_SEARCH_RANGE;

        int samplesR = riverRes / threadCount;
        int scaleR = RegionPos.REGION_SIZE / riverRes;
        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samplesR;
            int endX = startX + samplesR;
            futuresR[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float finalInfluence;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * riverRes;

                    for (int zPos = 0; zPos < riverRes; zPos++) {
                        trueX = xPos * scaleR + minX;
                        trueZ = zPos * scaleR + minZ;
                        finalInfluence = 0.0f;
                        searchLoop:
                        for (int xO = -maxRiverRange; xO <= maxRiverRange; xO++) {
                            for (int zO = -maxRiverRange; zO <= maxRiverRange; zO++) {
                                int xOff = RegionRivers.RIVER_SIZE * xO;
                                int zOff = RegionRivers.RIVER_SIZE * zO;
                                RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(trueX + xOff), RegionPos.fromBlockCoord(trueZ + zOff));
                                boolean sameRegion = pos0.x() == pos.x() && pos0.z() == pos.z();
                                WorldRegion r = sameRegion ? worldRegion : neighbors.get(pos0);
                                if (r == null) continue;
                                RiverNode n;

                                n = r.getNbtData().rivers().getNodeAtBlock(trueX + xOff, trueZ + zOff);

                                if (n == null || !n.touches(trueX,trueZ)) continue;

                                for (RiverPoint points : n.getPoints()) {

                                    float d0 = points.getDistance(trueX, trueZ);
                                    float radLimit = (points.radius());
                                    if (d0 >= radLimit * 2) continue;
                                    if (d0 < radLimit) {
                                        finalInfluence = 1.0f;
                                        break searchLoop;
                                    } else {
                                        float d00 = d0 - radLimit;
                                        float d0delta = Math.clamp(d00 / radLimit * 2.5f, 0.0f, 1.0f);
                                        float inf = 1 - d0delta;
                                        finalInfluence = Math.max(finalInfluence, inf);
                                    }
                                }
                            }
                        }


                        riversMap[xAdapted+zPos] = finalInfluence;
                    }
                }
            });
        }
        CompletableFuture.allOf(futuresR).join();

        rivers.updateMap(riversMap);
        rivers.setStatus(RegionFloatMap.Status.COMPLETE);

        worldRegion.layers().setRivers(rivers);

        int resolution = elevations.getResolution();
        float[] heightmapMap = new float[resolution * resolution];

        int samples = resolution / threadCount;
        int scale = RegionPos.REGION_SIZE / resolution;

        CompletableFuture<Void>[] futures = new CompletableFuture[threadCount];

        for (int thread = 0; thread < threadCount; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                /*
                float pEOf; //Plate Elevation Offset
                float buoyancyLocal;
                float weightLocal;
                float plateAge;
                float swelling;
                float plateHeightVariation;
                float erosion;
                float veg;
                float hum;
                float pBaseE;
                float pTable;
                float pRiver;
                float plateauElevation;
                float stressL;
                float platStepped;
                float hills;
                float hillElev;
                float v;
                float artifacts;
                float localElevation;*/
                List<RiverPoint> points = new ArrayList<>();
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;

                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;

                        points.clear();
                        for (int xO = -maxRiverRange; xO <= maxRiverRange; xO++) {
                            for (int zO = -maxRiverRange; zO <= maxRiverRange; zO++) {
                                int xOff = RegionRivers.RIVER_SIZE * xO;
                                int zOff = RegionRivers.RIVER_SIZE * zO;
                                RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(trueX + xOff), RegionPos.fromBlockCoord(trueZ + zOff));
                                boolean sameRegion = pos0.x() == pos.x() && pos0.z() == pos.z();
                                WorldRegion r = sameRegion ? worldRegion : neighbors.get(pos0);
                                if (r == null) continue;
                                RiverNode n;

                                n = r.getNbtData().rivers().getNodeAtBlock(trueX + xOff, trueZ + zOff);

                                if (n == null || !n.touches(trueX,trueZ)) continue;

                                points.addAll(n.getPoints());
                            }
                        }


                        float elevation = heightmapBasic.upsampleFromNeighbors(trueX, trueZ,neighbors);
                        float post = points.isEmpty() ?  elevation : calculateHeightFromRivers(trueX, trueZ, points, elevation);
                        if (points.isEmpty() && trueX >= 120 && trueX < 170 &&  trueZ >= 2480 && trueZ < 2700) {
                            LogUtils.getLogger().warn("For Some Reason, no Points were found!");
                        }
                        heightmapMap[xAdapted + zPos] = post;

                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();


        elevations.updateMap(heightmapMap);
        elevations.setStatus(RegionFloatMap.Status.COMPLETE);
        worldRegion.layers().setElevation(elevations);

    }

    private static float calculateHeightFromRivers(int xPos, int zPos, List<RiverPoint> points, float originalHeight) {
        float firstInfluence = 0.0f;
        float firstHeight = 0.0f;

        float secondInfluence = 0.0f;
        float secondHeight = 0.0f;

        float targetDepth = 0.0f;

        int pointCount = points.size();
        for (int i = 0; i < pointCount; i++) {
            RiverPoint point = points.get(i);
            float distSqr = point.distanceSqr(xPos, zPos);

            if (distSqr > RIVER_LIMIT_SQR) continue;

            float distance = (float) Math.sqrt(distSqr);
            if (Float.isNaN(distance) || distance <= 0){
                distance = 0.0f;
            }

            float targetHeight;
            float weight;
            float distDelta;

            float e = point.elevation();
            boolean flag = originalHeight > 68 && e < 63;
            float elevation = flag ? Math.min(originalHeight, 64 - point.depth()) : e;
            if (elevation == originalHeight) continue;

            float diffToSeaLevel = Math.min(Math.max(elevation - 63, -1.5f) * 0.25f, 1.0f);
            float radius = point.radius();
            if (distance <= radius) {
                distDelta = distance / radius;
                float w = 1 - Math.clamp(distDelta, 0.0f, 1.0f);
                weight = 1 + w;
                float pointDepth = w * point.depth();
                targetDepth = Math.max(targetDepth, pointDepth);
            } else {
                float range = Math.clamp(radius * 1.5f, 8.0f, 64.0f);
                distDelta = (distance - radius) / range;

                if (distDelta > 1.0f) continue;

                if (originalHeight < elevation) {

                    weight = 1 - distDelta;

                }else{

                float eDifference = (Math.clamp(originalHeight - elevation,32,64) - 32) / 32;
                float d0 = (float) Mth.smoothstep(distDelta);
                float d1 = (1 - distDelta) * (1 - distDelta);
                weight = Mth.lerp(eDifference, 1-(d0 * d0), d1 * d1 * d1);

                }
            }
            if (weight < 0.01) continue;

            targetHeight = elevation + (3 * diffToSeaLevel);

            weight = Mth.clamp(weight * weight,0.0f,2.0f);

            if (weight > firstInfluence) {
                firstInfluence = weight;
                firstHeight = targetHeight;

            }else if(weight > secondInfluence) {
                secondInfluence = weight;
                secondHeight = targetHeight;

            }
        }

        if (firstInfluence <= 0.0001f) {
            return originalHeight - targetDepth;
        }

        float difference = firstInfluence - secondInfluence;
        float blendedRiverHeight = Mth.lerp(Mth.clamp((1 - difference) * 0.5f,0.0f,0.5f),firstHeight,secondHeight);

        if (blendedRiverHeight <= RIVER_MINIMUM_HEIGHT_PROCESSOR && targetDepth >= 0.1f) {
            float hDif = (RIVER_MINIMUM_HEIGHT_PROCESSOR - blendedRiverHeight) * RIVER_INV_MAX_DEPTH;
            targetDepth *= (1 - Mth.clamp(hDif, 0.0f, 1.0f));
        }

        return Mth.lerp(Math.min(firstInfluence,1.0f), originalHeight, blendedRiverHeight) - targetDepth;
    }

    public static float stepped(float originalTerrain, float scaleToStep) {
        float times = originalTerrain / Math.max(scaleToStep, 0.1f);
        float base = (float) Math.floor(times);
        float smooth = times - base;

        float smoothed = smooth * smooth * (3.0f - 2.0f * smooth);

        return (base + smoothed) * scaleToStep;
    }

    public static WaterFlowData makeChunkFlowRiverData(int xPos, int zPos, RegionPos pos, WorldRegion worldRegion, RegionMemory memory) {
        worldRegion.query();
        List<RiverPoint> flowData = new ArrayList<>();

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


        WaterFlowData data = WaterFlowData.mixAll(xPos,zPos,flowData);
        worldRegion.release();
        return data;
    }

    private float[] trimEdges(float[] paddedMap,int paddingSize,int resolution, int paddedResolution) {
        float[] map = new float[resolution * resolution];

        for (int x = 0; x < resolution; x++) {
            int startPos = ((x + paddingSize) * paddedResolution) + paddingSize;
            int endPos = x * resolution;
            System.arraycopy(paddedMap, startPos, map, endPos, resolution);
        }

        return map;
    }


}
