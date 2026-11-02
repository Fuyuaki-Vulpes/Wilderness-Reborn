package com.fuyuaki.r_wilderness.world.generation.features;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.GenerationUtils;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RegionRivers;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RiverNode;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.RiverPoint;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.WaterFlowData;
import com.fuyuaki.r_wilderness.world.generation.features.plates.RegionPlates;
import com.fuyuaki.r_wilderness.world.generation.features.plates.TectonicPlateNode;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.ibm.icu.impl.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RegionFeatureGenerator {
    public static final int THREAD_COUNT = 8;
    public static final float RIVER_LIMIT = RiverNode.MAX_DIAMETER * 2.0f;
    public static final float RIVER_LIMIT_SQR = RIVER_LIMIT * RIVER_LIMIT;
    public static final float RIVER_INV_MAX_DEPTH = 1.0f / RiverNode.MAX_DEPTH;
    public static final float RIVER_MINIMUM_HEIGHT_PROCESSOR = WildernessConstants.SEA_LEVEL - RiverNode.MAX_DEPTH;
    private static final int MAX_RIVER_POINTS_HEIGHT_SCAN = 8;

    public static float[] calculateRasterizedPlatesForPosition(int xPos, int zPos, Map<RegionPos, WorldRegion> neighbors, TectonicPlateNode[] nearestNodes, float[] distances, TectonicPlateNode[] nodes2, float[] dist2, int arrayHold, float[] localDataScratch, float[] pairResultsScratch, float[] finalRasterScratch, Map<RegionPos, WorldRegion> localNeighborCache) {
        Arrays.fill(nearestNodes, null);
        Arrays.fill(distances, Float.MAX_VALUE);
        Arrays.fill(nodes2, null);
        Arrays.fill(dist2, Float.MAX_VALUE);
        int scale = RegionPlates.MINOR_PLATE_SIZE;
        int scanZone = 8;

        // 1. Primary Scan Zone (Nearest Nodes)
        for (int xO = -scanZone; xO <= scanZone; xO++) {
            for (int zO = -scanZone; zO <= scanZone; zO++) {
                int x = xPos + (xO * scale);
                int z = zPos + (zO * scale);

                int regX = RegionPos.fromBlockCoord(x);
                int regZ = RegionPos.fromBlockCoord(z);

                // Use local cache to avoid redundant map lookups and RegionPos allocations
                WorldRegion region = getCachedRegion(regX, regZ, neighbors, localNeighborCache);
                if (region == null) continue;

                TectonicPlateNode nearbyNode = region.getNbtData().plates().closestNode(x, z);
                if (nearbyNode == null) continue;

                float distance = nearbyNode.getWeightedDistance(xPos, zPos);

                boolean shouldInsert = true;
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

        // 2. Secondary Scan Zone (nodes2)
        for (int xO = -1; xO <= 1; xO++) {
            for (int zO = -1; zO <= 1; zO++) {
                int x = xPos + (xO * scale);
                int z = zPos + (zO * scale);

                int regX = RegionPos.fromBlockCoord(x);
                int regZ = RegionPos.fromBlockCoord(z);

                WorldRegion region = getCachedRegion(regX, regZ, neighbors, localNeighborCache);
                if (region == null) continue;

                TectonicPlateNode nearbyNode = region.getNbtData().plates().closestNode(x, z);
                if (nearbyNode == null) continue;

                float distance = nearbyNode.getWeightedDistance(xPos, zPos);

                boolean shouldInsert = true;
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

        makePlateData(dist2, nodes2, localDataScratch);
        finalMap[buoyancyI] = localDataScratch[0];
        finalMap[distI] = localDataScratch[1];
        float localAge = localDataScratch[2];
        finalMap[ageI] = localAge;
        finalMap[swellI] = localDataScratch[3];

        if (nearestNodes[1] == null) return finalMap;

        int validNodeCount = 0;
        while (validNodeCount < nearestNodes.length && nearestNodes[validNodeCount] != null) {
            validNodeCount++;
        }
        if (validNodeCount < 2) {
            finalMap[weightI] = nearestNodes[0].getWeight();
            return finalMap;
        }

        float closestDist = distances[0];
        TectonicPlateNode closestNode = nearestNodes[0];

        calculatePlateRasterization(validNodeCount, nearestNodes, distances, closestNode, closestDist, finalRasterScratch, pairResultsScratch);

        finalMap[weightI] = finalRasterScratch[weightI];
        finalMap[edgeI] = finalRasterScratch[edgeI];

        int centerRegX = RegionPos.fromBlockCoord(xPos);
        int centerRegZ = RegionPos.fromBlockCoord(zPos);
        WorldRegion regionAt = getCachedRegion(centerRegX, centerRegZ, neighbors, localNeighborCache);

        float volcanicness = 0.0f;
        if (regionAt != null) {
            try {
                float min = 0.35f;
                float noiseVal = (float) regionAt.layers().record().volcanicness().getNoise().noise(xPos, zPos);
                volcanicness = Math.max((noiseVal - min) / (1.0f - min), 0.0f);
            } catch (Exception ignored) {}
        }

        float tectonicIntensity = 1.0f;
        if (regionAt != null) {
            try {
                tectonicIntensity = 0.75f + ((float) regionAt.layers().record().tectonicIntensity().getNoise().noise(xPos, zPos) * 0.25f);
            } catch (Exception ignored) {}
        }

        finalMap[heightI] = finalRasterScratch[heightI] * tectonicIntensity;
        finalMap[volcanicI] = Mth.clamp(finalRasterScratch[volcanicI] + (volcanicness * volcanicness * localAge * localAge), 0.0f, 1.0f);
        finalMap[stressI] = finalRasterScratch[stressI];
        finalMap[buoyancyI] = localDataScratch[0];

        return finalMap;
    }

    // Helper for local region caching without object allocation thrashing
    private static WorldRegion getCachedRegion(int regX, int regZ, Map<RegionPos, WorldRegion> sourceMap, Map<RegionPos, WorldRegion> cache) {
        // If using Java, you can pool or use a temporary lookup object if RegionPos is immutable.
        // Ideally, pass a reusable wrapper or use primitive long keys if performance is critical.
        RegionPos tempPos = new RegionPos(regX, regZ);
        return cache.computeIfAbsent(tempPos, sourceMap::get);
    }

    private static void makePlateData(float[] distances, TectonicPlateNode[] nodes, float[] outResult) {
        float closestDist = distances[0];
        TectonicPlateNode closest = nodes[0];

        float effectRange = RegionPlates.MINOR_PLATE_SIZE;
        int validNodeCount = 0;

        float buoyancies = closest.getBuoyancy();
        float age = closest.getAge();
        float swell = closest.getSwelling();

        float total = 1.0f;
        float maxParentDist = RegionPlates.MAJOR_PLATE_SIZE;
        float distToParent = closest.getDistToParent() / maxParentDist;

        while (validNodeCount < nodes.length && nodes[validNodeCount] != null) {
            validNodeCount++;
        }

        for (int i = 0; i < validNodeCount; i++) {
            float range = effectRange;
            TectonicPlateNode otherNode = nodes[i];
            float otherDist = distances[i];
            if (closest.getId() != otherNode.getId()) range = effectRange / 2f;

            float distanceDifference = otherDist - closestDist;
            float d2 = Math.clamp(1.0f - (distanceDifference / range), 0.0f, 1.0f);
            float pairBias = (d2 * d2 * (3.0f - (2.0f * d2)));

            float parentDist = Math.clamp(otherNode.getDistToParent() / maxParentDist, 0.0f, 1.0f);
            distToParent += parentDist * pairBias;
            buoyancies += otherNode.getBuoyancy() * pairBias;
            age += otherNode.getAge() * pairBias;
            swell += otherNode.getSwelling() * pairBias;
            total += pairBias;
        }

        outResult[0] = buoyancies / total;
        outResult[1] = distToParent / total;
        outResult[2] = age / total;
        outResult[3] = swell / total;
    }

    private static void calculatePlateRasterization(int validNodeCount, TectonicPlateNode[] nearestNodes, float[] distances, TectonicPlateNode closestNode, float closestDist, float[] finalRaster, float[] pairResultsScratch) {
        float boundaryWidth = RegionPlates.MAJOR_PLATE_SIZE * 0.35f;
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
        float multiplier = Math.clamp(dist12 / (boundaryWidth / 2f), 0.0f, 1.0f);

        final float blendFactor = 0.25f;
        float total = 1.0f;

        for (int i = 0; i < validNodeCount; i++) {
            TectonicPlateNode otherNode = nearestNodes[i];
            float otherDist = distances[i];
            if (closestNode.getId() == otherNode.getId()) continue;

            float distanceDifference = otherDist - closestDist;
            float d = Math.clamp(1.0f - (distanceDifference / boundaryWidth), 0.0f, 1.0f);
            float pairWeight = (d * d * (3.0f - (2.0f * d)));

            float pairFarWeight = d;
            float d2 = Math.clamp(1.0f - (distanceDifference / (boundaryWidth / 2f)), 0.0f, 1.0f);
            float pairWeight2 = (d2 * d2 * (3.0f - (2.0f * d2)));

            getRasterizedDeformation(closestNode, otherNode, otherDist, closestDist, boundaryWidth, pairResultsScratch);

            float effectiveDeform = pairResultsScratch[0] * pairWeight;
            if (effectiveDeform > 0.0f) {
                if (effectiveDeform > highestDeform) {
                    subHighDeform += highestDeform * blendFactor;
                    highestDeform = effectiveDeform;
                } else {
                    subHighDeform += effectiveDeform;
                }
            } else {
                if (effectiveDeform < lowestDeform) {
                    subLowDeform += lowestDeform * blendFactor;
                    lowestDeform = effectiveDeform;
                } else {
                    subLowDeform += effectiveDeform;
                }
            }
            totalVolcanicness += pairResultsScratch[1] * pairWeight;
            totalStress += pairResultsScratch[2] * pairFarWeight * 2.0f;

            weights += otherNode.getWeight() * pairWeight2;
            total += pairWeight2;
        }

        finalRaster[weightI] = weights / total;
        float edge1 = 1.0f - multiplier;

        finalRaster[edgeI] = edge1;
        finalRaster[heightI] = highestDeform + subHighDeform + lowestDeform + subLowDeform;
        finalRaster[volcanicI] = Math.clamp(totalVolcanicness, 0.0f, 1.0f);
        finalRaster[stressI] = Math.clamp(totalStress, 0.0f, 1.0f);
    }

    public static void getRasterizedDeformation(TectonicPlateNode closestNode, TectonicPlateNode otherNode, float otherDist, float closestDist, float boundaryWidth, float[] resultsOut) {
        boolean lockedHeightOffset = false;
        float finalHeightOff = 0.0f;
        float distWithSideBias = otherDist - closestDist;
        float distToEdge = Math.abs(distWithSideBias);

        resultsOut[0] = 0.0f; // height
        resultsOut[1] = 0.0f; // volcanic / stress accumulator

        if (distToEdge >= boundaryWidth) {
            lockedHeightOffset = true;
        }

        float bF = 1.0f - (distToEdge / boundaryWidth);
        float boundaryFadeSmooth = bF * bF * (3.0f - (2.0f * bF));

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

                float convergenceShift = makeConvergence(distToEdge, collisionSpeed, weightDifference, boundaryWidth, resultsOut);
                float divergenceShift = makeDivergence(distToEdge, separationSpeed, boundaryWidth, resultsOut);
                float shearDeformation = makeShear(transformSpeed, distToEdge, boundaryWidth, resultsOut);

                float heightOffset = convergenceShift + divergenceShift + shearDeformation;
                finalHeightOff = heightOffset * boundaryFadeSmooth;
            }
        }

        resultsOut[0] = finalHeightOff;
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

        float mountainRadius = boundaryWidth * 0.25f;
        float mDistanceRatio = distanceToEdge / mountainRadius;

        float collisionGaussian = (float) Math.exp(-mDistanceRatio * mDistanceRatio);
        float collisionShift = WildernessConstants.MAX_MOUNTAIN_HEIGHT * Math.clamp(1.0f - Math.abs(subdueBias), 0.0f, 1.0f) * collisionGaussian;
        float subdueShift;
        float volcanicArcFactor = 0.0f;

        if (wShift < 0.0f) {
            float trenchRatio = distanceToEdge / (boundaryWidth * 0.3f);
            if (trenchRatio > 1.0f) {
                subdueShift = 0.0f;
            } else {
                subdueShift = -WildernessConstants.MAX_TRENCH_DEPTH * (1.0f - (trenchRatio * trenchRatio));
            }
        } else {
            float arcPeakDist = boundaryWidth * 0.25f;
            if (distanceToEdge < arcPeakDist) {
                float t = distanceToEdge / arcPeakDist;
                float smoothT = (t * t * (3.0f - (2.0f * t)));
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

        results[1] += volcanicArcFactor * Math.abs(subdueBias) * convergenceSpeed;
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
        results[1] += divergenceSpeed * riftGaussian;

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
        CompletableFuture<Void>[] futures = new CompletableFuture[THREAD_COUNT];
        int samples = resolution / THREAD_COUNT;
        int scale = RegionPos.REGION_SIZE / resolution;
        int arrayHold = 12;

        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                // Allocate scratch buffers once per thread, not per pixel!
                TectonicPlateNode[] nearestNodes = new TectonicPlateNode[arrayHold];
                float[] distances = new float[arrayHold];
                TectonicPlateNode[] nodes2 = new TectonicPlateNode[9];
                float[] dist2 = new float[9];
                float[] localDataScratch = new float[4];
                float[] pairResultsScratch = new float[2];
                float[] finalRasterScratch = new float[5];
                Map<RegionPos, WorldRegion> localNeighborCache = new HashMap<>();

                int trueX;
                int trueZ;
                float[] rasterLocal;
                float r;

                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;

                        localNeighborCache.clear(); // Clear cache per pixel or keep per row if stable

                        rasterLocal = calculateRasterizedPlatesForPosition(
                                trueX, trueZ, neighbors, nearestNodes, distances, nodes2, dist2,
                                arrayHold, localDataScratch, pairResultsScratch, finalRasterScratch, localNeighborCache
                        );

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
        RegionFloatMap weight = worldRegion.layers().getPlateWeights();
        RegionFloatMap age = worldRegion.layers().getAge();
        RegionFloatMap distParent = worldRegion.layers().getDistToParent();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap rockiness = worldRegion.layers().getRockiness();
        RegionFloatMap erosion = worldRegion.layers().getErodability();
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap map = worldRegion.layers().getPlateaus();
        int resolution = map.getResolution();

        float maxContinentForBuyancyInfluence = 0.35f;

        float[] fMap = new float[resolution * resolution];
        CompletableFuture<Void>[] futures = new CompletableFuture[THREAD_COUNT];
        int samples = resolution / THREAD_COUNT;
        int scale = RegionPos.REGION_SIZE / resolution;

        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float distEdge;
                float swellFilter;
                float buoyFilter;
                float volcanicFilter;
                float volcanicFilter2;
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


                        float weightLocal = Math.clamp(((weight.getGlobal(trueX, trueZ) - 0.65f) / 0.35f), -1.0f, 1.0f);
                        float buoyancyInf = Math.clamp(weightLocal,0,maxContinentForBuyancyInfluence) / maxContinentForBuyancyInfluence;
                        float buoyancyLocal = buoyancy.getGlobal(trueX, trueZ) * 0.45f * (buoyancyInf * 0.5f + 0.5f);
                        float cont = Math.clamp(buoyancyLocal + weightLocal, 0, 1.5f);


                        distEdge = 1 - distParent.getGlobal(trueX, trueZ);

                        swellFilter = filterMap(swell, trueX, trueZ, 0.15f, 0.50f);
                        buoyFilter = filterMap(buoyancy, trueX, trueZ, 0.25f, 0.85f);
                        volcanicFilter = filterMap(volcanic, trueX, trueZ, 0.5f, 0.85f);
                        volcanicFilter2 = filterMap(volcanic, trueX, trueZ, 0.15f, 0.6f);
                        stressFilter = filterMap(stress, trueX, trueZ, 0.15f, 0.45f);
                        buoyOrSwell = Math.max(buoyFilter, swellFilter);
                        r = filterMap(rockiness, trueX, trueZ, -0.5f, 0.75f);
                        e = filterMap(erosion, trueX, trueZ, -0.25f, 0.75f);
                        effectiveErosion = e * (r * 0.5f + 0.5f);
                        distFilter = Math.clamp(1.0f - distEdge * 3.5f, 0, 1);
                        distInv = Math.clamp((distEdge - 0.25f) * 5, 0, 1);

                        ageFilter = filterMap(age, trueX, trueZ, 0.35f, 0.65f);

                        filter1 = distInv * ageFilter * Math.max(buoyOrSwell,volcanicFilter2);
                        filter2 = distFilter * volcanicFilter * buoyOrSwell * effectiveErosion * cont;
                        filter3 = distFilter * stressFilter * buoyOrSwell * cont;
                        filter4 = buoyOrSwell * effectiveErosion * cont;

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
        RegionFloatMap stressMap = worldRegion.layers().getTectonicStress();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap rockiness = worldRegion.layers().getRockiness();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap plateaus = worldRegion.layers().getPlateaus();
        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        int resolution = worldRegion.layers().getHilliness().getResolution();
        RegionFloatMap hills = worldRegion.layers().getHilliness();

        float[] hMap = new float[resolution * resolution];
        CompletableFuture<Void>[] futures = new CompletableFuture[THREAD_COUNT];
        int samples = resolution / THREAD_COUNT;
        int scale = RegionPos.REGION_SIZE / resolution;
        for (int thread = 0; thread < THREAD_COUNT; thread++) {
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
                float stress;
                float stressSqr;
                float reverseErosionEffective;
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
                        stress = Math.min(stressMap.getGlobal(trueX,trueZ) * 1.25f,1.0f);
                        stressSqr = stress * stress;
                        volc = filterMap(volcanic, trueX, trueZ, 0.25f, 0.65f);
                        erod = 1 - filterMap(erodability, trueX, trueZ, -0.5f, 0.90f);
                        rock = filterMap(rockiness, trueX, trueZ, -0.25f, 0.85f);
                        reverseErosionEffective = erod * (0.5f + (rock * 0.5f));
                        float plateauFilter = 1 - Math.clamp(plateauF * 0.5f,0.0f,1.0f);
                        float hillNoise = (float) worldRegion.layers().record().hills().getNoise().noise(trueX, trueZ);
                        float buoySwellHillFilter = buoyOrSwell * volc * (0.5f + (erod * 0.5f));
                        float filterForHills = Math.max( Math.max( buoySwellHillFilter, stressSqr * (erod * 0.5f + 0.5f)), reverseErosionEffective);
                        hMap[xAdapted + zPos] = Math.clamp((plateauFilter * filterForHills) * hillNoise,0,1);

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
        Noise2D plateauRiverCarve = worldRegion.layers().record().plateauBadlandCarve().getNoise();
        Noise2D plateauTableCarve = worldRegion.layers().record().plateauCanyonCarve().getNoise();
        Noise2D mountains = worldRegion.layers().record().mountains().getNoise();
        RegionFloatMap vegetation = worldRegion.layers().getVegetation();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap humidity = worldRegion.layers().getHumidity();
        RegionFloatMap stress = worldRegion.layers().getTectonicStress();
        RegionFloatMap weight = worldRegion.layers().getPlateWeights();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap hilliness = worldRegion.layers().getHilliness();
        RegionFloatMap plateauOffset = worldRegion.layers().getPlateaus();

        Noise2D islandModulator = worldRegion.layers().record().islandModulator().getNoise();

        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap heightmap = worldRegion.layers().getHeightmapBasic();
        RegionFloatMap continentalness = worldRegion.layers().getContinentalness();
        int resolution = heightmap.getResolution();
        int resolutionCont = continentalness.getResolution();


        float[] heightFmap = new float[resolution * resolution];
        float[] continentalFMap = new float[resolutionCont * resolutionCont];
        CompletableFuture<Void>[] futures = new CompletableFuture[THREAD_COUNT];
        int samples = resolution / THREAD_COUNT;
        int scale = RegionPos.REGION_SIZE / resolution;
        int samplesC = resolutionCont / THREAD_COUNT;
        int scaleC = RegionPos.REGION_SIZE / resolutionCont;
        float maxContinentForBuyancyInfluence = 0.35f;



        for (int thread = 0; thread < THREAD_COUNT; thread++) {
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
                float pBaseE;
                float stressL;
                float v;
                float artifacts;
                float localElevation;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;
                        buoyancyLocal = buoyancy.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        weightLocal = Math.clamp(((weight.upsampleFromNeighbors(trueX, trueZ, neighbors) - 0.7f) / 0.3f), -1.25f, 1.25f);
                        plateAge = age.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        swelling = Math.clamp((1+ swell.upsampleFromNeighbors(trueX, trueZ, neighbors)) / 2,0,1);



                        float continents = Math.clamp((buoyancyLocal * 0.45f) + weightLocal, -1.5f, 1.5f);

                        float c = Math.abs(Math.clamp(continents * 1.5f, -1.0f, 1.0f));
                        float contElevationFactor = (c * c * (3.0f - 2.0f * c)) * Math.signum(continents);
                        float cFilter = Math.clamp(-continents,0.0f,0.5f) * 2;

                        pEOf = plateElevationOffset.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        float pEOfModulator =  1 - ((float) Mth.smoothstep(Math.clamp((pEOf + 16) * 1.1f,0,32) / 32));

                        v = volcanic.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        float cTI = filter((float) islandModulator.noise(trueX,trueZ),0.15f,0.5f);
                        float pEOfPost = Mth.lerp(cFilter,pEOf,pEOfModulator * Math.max(pEOf,v) * cTI);

                        stressL = stress.upsampleFromNeighbors(trueX, trueZ, neighbors);

                        float effectivePlateOffsetMultiplier = (float) Mth.lerp(filter(stressL,0.0f,0.5f),1.0f,mountains.noise(trueX,trueZ) * 0.5f + 0.5f);
                        plateHeightVariation = (pEOfPost * ((1 - plateAge) * 0.25f + 0.75f) * effectivePlateOffsetMultiplier)
                                + (contElevationFactor * WildernessConstants.CONTINENTAL_ELEVATION_MIN_MAX)
                                + (swelling * WildernessConstants.SWELL_HILL_SCALE);

                        float plat = plateauOffset.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        pBaseE = plat * WildernessConstants.BASE_PLATEAU_UPLIFT;




                        artifacts = (v * (1 - filter(plateAge, 0.1f, 0.5f)) * WildernessConstants.VOLCANIC_ARTIFACT_SCALE);

                        localElevation = plateHeightVariation + pBaseE + artifacts;


                        heightFmap[xAdapted + zPos] = 64 + localElevation;

                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();


        heightmap.updateMap(heightFmap);
        heightmap.setStatus(RegionFloatMap.Status.COMPLETE);

        float eCCont = 64.0f;
        float eStartCont = 32.0f;
        for (int thread = 0; thread < THREAD_COUNT; thread++) {
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
                        float elevation = heightmap.getGlobal(trueX,trueZ);

                        weightLocal = Math.clamp(((weight.upsampleFromNeighbors(trueX, trueZ, neighbors) - 0.7f) / 0.3f), -1.25f, 1.25f);
                        buoyancyLocal = buoyancy.upsampleFromNeighbors(trueX, trueZ, neighbors) * 0.35f;

                        float continentalPre = Math.clamp(buoyancyLocal + weightLocal, -1.5f, 1.5f);
                        float cDelta = Math.clamp(-continentalPre,0.0f,0.5f) * 2;
                        float eDelta = Mth.clamp(elevation - eStartCont, 0.0f,eCCont) / eCCont;
                        float cAdd = Mth.lerp(cDelta, eDelta * 0.25f,eDelta);
                        float deepSea = Math.clamp(-continentalPre - 0.5f,0.0f,0.5f) * 2.0f;
                        continentalFMap[xAdapted + zPos] = continentalPre + Math.clamp((cAdd * (1 - deepSea)),0.0f,0.5f);
                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();


        continentalness.updateMap(continentalFMap);
        continentalness.setStatus(RegionFloatMap.Status.COMPLETE);

        worldRegion.layers().setHeightmapBasic(heightmap);
        worldRegion.layers().setContinentalness(continentalness);
    }

    public static void createRiverShapeAndHeightmaps(RegionPos pos, ServerLevel level, WorldRegion worldRegion, Map<RegionPos, WorldRegion> neighbors) {


        RegionFloatMap heightmapBasic = worldRegion.layers().getHeightmapBasic();

        Noise2D peakCellular = worldRegion.layers().record().mountainPeaks().getNoise();
        Noise2D peakSimplex = worldRegion.layers().record().peakFilter().getNoise();
        Noise2D mountains = worldRegion.layers().record().mountains().getNoise();
        Noise2D artifacts = worldRegion.layers().record().artifacts().getNoise();
        RegionFloatMap erodability = worldRegion.layers().getErodability();
        RegionFloatMap stress = worldRegion.layers().getTectonicStress();
        RegionFloatMap plateaus = worldRegion.layers().getPlateaus();
        RegionFloatMap atmosphericPressure = worldRegion.layers().getAtmospherePressure();
        RegionFloatMap edges = worldRegion.layers().getPlateEdges();
        RegionFloatMap volcanic = worldRegion.layers().getVolcanicness();
        RegionFloatMap hilliness = worldRegion.layers().getHilliness();
        RegionFloatMap plateAge = worldRegion.layers().getAge();
        RegionFloatMap rockiness = worldRegion.layers().getRockiness();
        RegionFloatMap humidity = worldRegion.layers().getHumidity();
        RegionFloatMap temperature = worldRegion.layers().getTemperature();
        RegionFloatMap continentalness = worldRegion.layers().getContinentalness();


        Noise2D plateauBadlandCarve = worldRegion.layers().record().plateauBadlandCarve().getNoise();
        Noise2D plateauCanyonCarve = worldRegion.layers().record().plateauCanyonCarve().getNoise();
        Noise2D pillarArtifacts = worldRegion.layers().record().pilarArtifacts().getNoise();
        RegionFloatMap vegetation = worldRegion.layers().getVegetation();
        Noise2D islandModulator = worldRegion.layers().record().islandModulator().getNoise();

        int minX = RegionPos.toBlockCoord(pos.x());
        int minZ = RegionPos.toBlockCoord(pos.z());
        RegionFloatMap elevations = worldRegion.layers().getElevation();
        RegionFloatMap glacial = worldRegion.layers().getGlacialThickness();
        RegionFloatMap rivers = worldRegion.layers().getRivers();
        int riverRes = rivers.getResolution();


        CompletableFuture<Void>[] futuresR = new CompletableFuture[THREAD_COUNT];
        int maxRiverRange = RegionRivers.RIVER_SEARCH_RANGE + 1;
        int padding = 64;
        int resolution = elevations.getResolution();

        int paddingR = 64;

        int mapWidthR = riverRes + (paddingR * 2);

        int samplesR = mapWidthR / THREAD_COUNT;
        int scaleR = RegionPos.REGION_SIZE / riverRes;
        float[] riversMap = new float[mapWidthR * mapWidthR];

        int riverSearchRange = RegionRivers.RIVER_SIZE;
        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samplesR;
            int endX = startX + samplesR;
            futuresR[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;
                float finalInfluence;
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * mapWidthR;

                    for (int zPos = 0; zPos < mapWidthR; zPos++) {
                        trueX = (xPos - paddingR) * scaleR + minX;
                        trueZ = (zPos - paddingR) * scaleR + minZ;
                        finalInfluence = 0.0f;
                        searchLoop:
                        for (int xO = -maxRiverRange; xO <= maxRiverRange; xO++) {
                            for (int zO = -maxRiverRange; zO <= maxRiverRange; zO++) {
                                int xOff = riverSearchRange * xO;
                                int zOff = riverSearchRange * zO;
                                RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(trueX + xOff), RegionPos.fromBlockCoord(trueZ + zOff));
                                boolean sameRegion = pos0.x() == pos.x() && pos0.z() == pos.z();
                                WorldRegion r = sameRegion ? worldRegion : neighbors.get(pos0);
                                if (r == null) continue;
                                RiverNode n;

                                n = r.getNbtData().rivers().getNodeAtBlock(trueX + xOff, trueZ + zOff);

                                if (n == null || !n.isEnabled()|| !n.touches(trueX,trueZ)) continue;

                                for (RiverPoint point : n.getPoints()) {

                                    float d0 = point.getDistance(trueX, trueZ);
                                    float radLimit = (point.radius());
                                    if (d0 >= radLimit * 2) continue;
                                    if (d0 < radLimit) {
                                        finalInfluence = 1.0f;
                                        if (point.elevation() < 64.0f){
                                            float e = 1 - ((64.0f - point.elevation()) / RiverNode.MAX_DEPTH);
                                            finalInfluence = Math.max(finalInfluence, finalInfluence * e);
                                        }else{
                                            break searchLoop;
                                        }
                                    } else {
                                        float d00 = d0 - radLimit;
                                        float d0delta = Math.clamp(d00 / radLimit * 2.0f, 0.0f, 1.0f);
                                        float inf = 1 - d0delta;

                                        if (point.elevation() < 64.0f){
                                            float e = 1 - ((64.0f - point.elevation()) / RiverNode.MAX_DEPTH);
                                            inf = inf * e;
                                        }

                                        finalInfluence = Math.max(finalInfluence, inf);
                                    }
                                }
                            }
                        }
                        float c = continentalness.upsampleFromNeighbors(trueX,trueZ,neighbors);
                        float cFilter = 1.0f - Mth.clamp(-c + 0.05f,0.0f,0.25f) / 0.2f;


                        riversMap[xAdapted+zPos] = finalInfluence * cFilter;
                    }
                }
            });
        }
        CompletableFuture.allOf(futuresR).join();

        rivers.updateMap(trimEdges(riversMap,paddingR,riverRes,mapWidthR));
        rivers.setStatus(RegionFloatMap.Status.COMPLETE);
        worldRegion.layers().setRivers(rivers);
        int mapWidth = resolution + (padding * 2);
        float[] heightmapMap = new float[mapWidth * mapWidth];
        float[] finalElevation = new float[resolution * resolution];

        int samples = mapWidth / THREAD_COUNT;
        int samplesFinal = resolution / THREAD_COUNT;
        int scale = RegionPos.REGION_SIZE / resolution;

        CompletableFuture<Void>[] futures = new CompletableFuture[THREAD_COUNT];

        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;

                List<RiverPoint> points = new ArrayList<>();
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * mapWidth;

                    for (int zPos = 0; zPos < mapWidth; zPos++) {
                        trueX = (xPos - padding) * scale + minX;
                        trueZ = (zPos - padding) * scale + minZ;

                        points.clear();
                        for (int xO = -maxRiverRange; xO <= maxRiverRange; xO++) {
                            for (int zO = -maxRiverRange; zO <= maxRiverRange; zO++) {
                                int xOff = riverSearchRange * xO;
                                int zOff = riverSearchRange * zO;
                                RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(trueX + xOff), RegionPos.fromBlockCoord(trueZ + zOff));
                                boolean sameRegion = pos0.x() == pos.x() && pos0.z() == pos.z();
                                WorldRegion r = sameRegion ? worldRegion : neighbors.get(pos0);
                                if (r == null) continue;
                                RiverNode n;

                                n = r.getNbtData().rivers().getNodeAtBlock(trueX + xOff, trueZ + zOff);

                                if (n == null || !n.isEnabled()|| !n.touches(trueX,trueZ)) continue;

                                points.addAll(n.getPoints());
                            }
                        }


                        float elevation = heightmapBasic.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);
                        Pair<Float,Float> map;
                        if (points.isEmpty()){
                            map = Pair.of(0.0f,elevation);
                        }else{
                            map = calculateHeightFromRivers(trueX, trueZ, points, elevation);
                        }
                        float riverness = map.first;
                        float postRiverElevation = map.second;

                        heightmapMap[xAdapted + zPos] = postRiverElevation;
                    }
                }
            });
        }

        CompletableFuture.allOf(futures).join();

        float windErosionFactor = 0.5f;
        float maxThermalErosionHeightDifference = 32.0f;

        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samplesFinal;
            int endX = startX + samplesFinal;
            futures[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;

                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;

                    for (int zPos = 0; zPos < resolution; zPos++) {
                        trueX = xPos * scale + minX;
                        trueZ = zPos * scale + minZ;
                        int paddingX = padding + xPos;
                        int paddingZ = padding + zPos;
                        float elevation = heightmapMap[paddingX * mapWidth + paddingZ];

                        float erosion = filter(erodability.upsampleFromNeighbors(trueX,trueZ,neighbors),-0.5f,0.5f) * 0.5f + 0.5f;
                        float thermalFactor = 0.15f + (0.85f * filter(temperature.upsampleFromNeighbors(trueX,trueZ,neighbors),-0.5f, 0.5f));
                        float heightSum = elevation;
                        Vec2 sumDifferences = Vec2.ZERO;
                        int count = 1;

                        float riv = Mth.clamp(upsampleFromFloatMap(paddingR,0,resolution,riverRes,riversMap,xPos,zPos) * 2,0.0f,1.0f);


                        float maxHeight = elevation;
                        float minHeight = elevation;

                        for (int xO = -2; xO <= 2; xO++) {
                            for (int zO = -2; zO <= 2; zO++) {
                                if (zO == 0 && xO == 0) continue;
                                int paddedX = paddingX + xO;
                                int paddedZ = paddingZ + zO;

                                int paddedIndex = paddedX * mapWidth + paddedZ;
                                float heightAt = heightmapMap[paddedIndex];


                                if (heightAt > maxHeight) {
                                    maxHeight = heightAt;
                                }
                                if (heightAt < minHeight) {
                                    minHeight = heightAt;
                                }
                                heightSum += heightAt;
                                float distSq = (float) (xO * xO + zO * zO);
                                float heightDifference = (heightAt - elevation) / distSq;
                                sumDifferences = sumDifferences.add(new Vec2(heightDifference * xO, heightDifference * zO));
                                count++;
                            }
                        }

                        float elevationPost = Mth.lerp(erosion,elevation, heightSum / count);


                        if (maxHeight > elevationPost) {
                            float diff = maxHeight - elevationPost;
                            if (diff >= maxThermalErosionHeightDifference) {
                                float excess = diff - maxThermalErosionHeightDifference;
                                float delta = Math.min(excess / (maxThermalErosionHeightDifference * 2),1.0f);
                                float delta2 = delta * delta;
                                elevationPost += excess * delta2 * thermalFactor;
                            }
                        }
                        if (minHeight < elevationPost) {
                            float diff = elevationPost - minHeight;
                            if (diff >= maxThermalErosionHeightDifference) {
                                float excess = diff - maxThermalErosionHeightDifference;
                                float delta = Math.min(excess / (maxThermalErosionHeightDifference * 2),1.0f);
                                float delta2 = delta * delta;
                                elevationPost -= excess * delta2 * thermalFactor;
                            }
                        }

                        Vec2 uphill = sumDifferences.scale(1.0f / count);
                        Vec2 windVector = GenerationUtils.windVector(atmosphericPressure,trueX,trueZ,neighbors);
                        if (uphill.length() >= 0.001f && windVector.length() >= 0.001f) {
                            float dot = windVector.normalized().dot(uphill.normalized());
                            float windStrength = windVector.length();
                            float elevationDelta = Math.max(elevationPost / WildernessConstants.MAX_ELEVATION_Y_LEVEL,0.01f);

                            if (dot > 0.0f){
                                elevationPost += (-dot * windStrength * uphill.length() * elevationDelta * windErosionFactor);
                            }else{
                                elevationPost += (-dot * windStrength * elevationDelta * (windErosionFactor * 0.5f));
                            }
                        }

                        if(elevationPost < elevation){
                            elevationPost = Mth.lerp((riv * 0.75f) + 0.25f,elevationPost,elevation);
                        }
                        if (Float.isNaN(elevationPost)) {
                            elevationPost = elevation;
                        }


                        double peaks1 = peakCellular.noise(trueX, trueZ);
                        double peaks2 = Mth.smoothstep(peakSimplex.noise(trueX, trueZ));
                        float peaks = (float) Math.max(peaks1 * peaks2,peaks2 * peaks2);
                        float mountainFlat = (float) mountains.noise(trueX,trueZ);
                        float mountainSq = mountainFlat * mountainFlat;
                        float shatter = filter((float) artifacts.noise(trueX,trueZ),-0.5f,0.5f);
                        float erod = filter(erodability.upsampleFromNeighbors(trueX,trueZ,neighbors),-0.5f,0.5f);
                        float age = plateAge.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);

                        float mountainFactor = (1-erod) * age * 0.5f + (1-erod) * 0.25f + age * 0.25f;

                        float mountain = Mth.lerp(mountainFactor,mountainSq, mountainFlat * 0.75f);

                        float rocks = rockiness.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);
                        float temp = temperature.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);
                        float hum = humidity.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);

                        float frostFactor = Math.max(-temp,0.0f) * Math.max(hum,0.0f);
                        float localStress = stress.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);
                        float localVolcanicness = volcanic.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);
                        float edge = edges.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);

                        float plateau = plateaus.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);
                        float hills = hilliness.upsampleFromNeighborsBufferSafe(trueX, trueZ,neighbors);

                        float shatterFilter = Math.max(Math.max(plateau,hills * 0.65f),edge) * (Math.min(localStress + localVolcanicness * 0.5f, 0.5f) * 0.5f + 0.5f) * (Math.max(0.0f,rocks * 0.5f) + 0.5f);
                        float mountainFilter = localStress * (localVolcanicness * 0.25f + 0.75f);
                        float peakFilter = localStress * (1.0f - (age * 0.5f)) * (0.5f + (frostFactor * 0.5f));

                        float shatterOffset = shatter * WildernessConstants.MAX_ARTIFACT_ELEVATION * shatterFilter * (1-(riv * 0.9f));
                        float peaksOffset = Math.max(peaks * WildernessConstants.MAX_PEAK_VARIATION * peakFilter * (1-riv),0.0f);
                        float mountainOffset = Math.max(mountain * WildernessConstants.MAX_MOUNTAIN_LOCAL_VARIATION * mountainFilter * (1-(riv * 0.9f)),0.0f);

                        float er2 = erodability.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        float veg = vegetation.upsampleFromNeighbors(trueX, trueZ, neighbors);

                        float stressL = stress.upsampleFromNeighbors(trueX, trueZ, neighbors);

                        float pBaseE = plateau * WildernessConstants.PLATEAU_MODULABLE_HEIGHT;

                        float erosionFilter = filter(er2, -0.35f, 0.25f) * 0.75f + 0.25f;

                        float pBadlands = Math.min(1.0f - (filter((float) plateauBadlandCarve.noise(trueX, trueZ), -0.75f, 0.75f)
                                * erosionFilter
                                * (1 - filter(veg, -0.15f, 0.5f) * 0.75f)), 1.0f);

                        float pCanyon = 1.0f - (filter((float) plateauCanyonCarve.noise(trueX, trueZ), -0.75f, 0.5f)
                                * erosionFilter
                                * (filter(hum, 0.0f, 0.35f))
                                * (1 - (filter(stressL, 0.1f, 0.85f) * 0.5f)));

                        float eFiltStep = filter(er2, -0.5f, 0.5f) * 0.75f;

                        float scaleToStep = Math.clamp(1 - eFiltStep, 0, 1) * WildernessConstants.PLATEAU_STEPS_MAX;
                        float scaleToStepSt = Math.clamp(1 - eFiltStep * 0.5f, 0, 1) * 0.15f;

                        float pFilterE = Math.max(pCanyon, pBadlands * pBadlands);

                        float plateauElevation = pBaseE * steppedNoBlend(pFilterE,scaleToStepSt);

                        float platStepped = Math.max(stepped(plateauElevation, scaleToStep), 0);
                        float hillO = filter(hills, 0.05f, 0.95f) * ((1 - (filter(er2, -0.5f, 0.85f))) * 0.5f + 0.5f) * WildernessConstants.MOUNT_HILL_ELEVATION;
                        float hillElev = ((1 - plateau) * hillO) + (plateau * stepped(hillO, scaleToStep));

                        float offsetPillars = stepped( filter ((float) pillarArtifacts.noise(trueX,trueZ),0.35f,1.0f) * Math.max(filter(rocks,0.0f,0.65f) * 0.5f,plateau) * WildernessConstants.PILLAR_HEIGHTS,0.1f);

                        float continents = continentalness.upsampleFromNeighbors(trueX, trueZ, neighbors);
                        float cTI = Math.max((float) islandModulator.noise(trueX,trueZ) + localVolcanicness,0.15f) * 0.5f + 0.5f;
                        float cFilter = Mth.lerp(Math.clamp((-continents + 0.05f) / 0.35f,0.0f, 1.0f), 1.0f, cTI);

                        float details1 = shatterOffset + mountainOffset + peaksOffset;
                        float details2 = ((1 - plateau) * details1) + (plateau * stepped(details1, scaleToStep) + offsetPillars) * cFilter;
                        float offset = details2 + ((platStepped + hillElev) * (1-riv));


                        finalElevation[xAdapted + zPos] = elevationPost + offset;
                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();

        elevations.updateMap(finalElevation);
        elevations.setStatus(RegionFloatMap.Status.COMPLETE);
        worldRegion.layers().setElevation(elevations);


        int glacialRes = glacial.getResolution();
        float[] glacialMap = new float[glacialRes * glacialRes];

        int samplesG = glacialRes / THREAD_COUNT;
        int scaleG = RegionPos.REGION_SIZE / glacialRes;

        CompletableFuture<Void>[] futuresG = new CompletableFuture[THREAD_COUNT];

        for (int thread = 0; thread < THREAD_COUNT; thread++) {
            int startX = thread * samplesG;
            int endX = startX + samplesG;
            futuresG[thread] = CompletableFuture.runAsync(() -> {
                int trueX;
                int trueZ;

                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * glacialRes;

                    for (int zPos = 0; zPos < glacialRes; zPos++) {
                        trueX = xPos * scaleG + minX;
                        trueZ = zPos * scaleG + minZ;

                        float temp = temperature.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);
                        float hum = humidity.upsampleFromNeighborsBufferSafe(trueX,trueZ,neighbors);

                        float frostFactor = Math.max(-temp,0.0f) * Math.max(hum,0.0f);
                        if (frostFactor <= 0.0f) {
                            glacialMap[xAdapted + zPos] = 0.0f;
                            continue;
                        }

                        float elevation = heightmapMap[convertIndex(glacialRes,resolution,padding,xPos,zPos)];

                        Vec2 slopes = Vec2.ZERO;

                        for (int xO = -2; xO <= 2; xO++) {
                            for (int zO = -2; zO <= 2; zO++) {
                                if (zO == 0 && xO == 0) continue;

                                float heightAt = heightmapMap[convertIndex(glacialRes,resolution,padding,xPos + xO,zPos + zO)];

                                float distSq = (float) (xO * xO + zO * zO);
                                float heightDifference = (heightAt - elevation) / distSq;
                                slopes = slopes.add(new Vec2(heightDifference * xO, heightDifference * zO));
                            }
                        }
                        float slope = Math.max(slopes.length(),1.0f);

                        float glacialMultiplier = (1-slope) * frostFactor;
                        float erosion = filter(erodability.upsampleFromNeighbors(trueX,trueZ,neighbors),-0.5f,0.5f) * 0.5f + 0.5f;

                        glacialMap[xAdapted + zPos] = Math.max(glacialMultiplier * erosion,0.0f);



                    }
                }
            });
        }
        CompletableFuture.allOf(futuresG).join();


        glacial.updateMap(glacialMap);
        glacial.setStatus(RegionFloatMap.Status.COMPLETE);
        worldRegion.layers().setGlacialThickness(glacial);
    }


    private static Pair<Float, Float> calculateHeightFromRivers(int xPos, int zPos, List<RiverPoint> points, float originalHeight) {
        float maxInfluence = 0.0f;
        float totalDepth = 0.0f;

        float totalWeight = 0.0f;
        float weightedHeightSum = 0.0f;

        int pointCount = points.size();
        for (int i = 0; i < pointCount; i++) {
            RiverPoint point = points.get(i);
            float distSqr = point.distanceSqr(xPos, zPos);

            if (distSqr > RIVER_LIMIT_SQR) continue;

            float distance = (float) Math.sqrt(distSqr);
            if (Float.isNaN(distance) || distance <= 0) {
                distance = 0.0f;
            }

            float weight;
            float distDelta;

            float radius = point.radius();
            float pointDepth;

            float targetHeight;

            float pointElevation = point.elevation();
            boolean flag = originalHeight > 68 && pointElevation < 63;
            float elevation = flag ? Math.min(originalHeight, 64 - point.depth()) : pointElevation;

            if (distance <= radius) {
                distDelta = distance / radius;
                float w = 1 - Math.clamp(distDelta, 0.0f, 1.0f);
                weight = 1.0f + w;

                pointDepth = w * point.depth();

            } else {
                float range = Math.clamp(radius * 1.5f, 8.0f, 64.0f);
                distDelta = (distance - radius) / range;

                if (distDelta > 1.0f) continue;

                weight = distDelta * distDelta * (3.0f - 2.0f * distDelta);
                pointDepth = 0.0f;
            }
            targetHeight = Math.min(elevation,originalHeight);

            if (weight < 0.01f) continue;

            weightedHeightSum += targetHeight * weight;
            totalDepth += pointDepth * weight;
            totalWeight += weight;
            maxInfluence = Math.max(maxInfluence,weight);

        }

        if (totalWeight <= 0.01f){
            return Pair.of(0.0f, originalHeight);
        }


        float blendedRiverHeight = weightedHeightSum / totalWeight;

        float targetDepth = totalDepth / totalWeight;
        if (blendedRiverHeight < 64.0f){
            float e = 1 - ((64.0f - blendedRiverHeight) / RiverNode.MAX_DEPTH);
            targetDepth = targetDepth * e;
        }
        if (originalHeight < 64.0f){
            float e = 1 - ((64.0f - originalHeight) / RiverNode.MAX_DEPTH);
            maxInfluence = maxInfluence * e;
            targetDepth = targetDepth * e;
        }

        return Pair.of(Math.min(maxInfluence,1.0f), Mth.lerp(Math.min(maxInfluence,1.0f), originalHeight, blendedRiverHeight) - targetDepth);

    }

    public static float stepped(float originalTerrain, float scaleToStep) {
        float times = originalTerrain / Math.max(scaleToStep, 0.0001f);
        float base = (float) Math.floor(times);
        float smooth = times - base;

        float smoothed = (float) Mth.smoothstep(Mth.smoothstep(smooth));

        return (base + smoothed) * scaleToStep;
    }

    public static float steppedNoBlend(float originalTerrain, float scaleToStep) {
        float times = originalTerrain / Math.max(scaleToStep, 0.0001f);
        float base = (float) Math.floor(times);

        return base * scaleToStep;
    }

    public static WaterFlowData makeChunkFlowRiverData(int xPos, int zPos, float elevationAt, RegionPos pos, WorldRegion worldRegion, RegionMemory memory) {
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


        WaterFlowData data = WaterFlowData.mixAll(xPos,zPos,flowData,elevationAt);
        worldRegion.release();
        return data;
    }

    private static float[] trimEdges(float[] paddedMap,int paddingSize,int resolution, int paddedResolution) {
        float[] map = new float[resolution * resolution];

        for (int x = 0; x < resolution; x++) {
            int startPos = ((x + paddingSize) * paddedResolution) + paddingSize;
            int endPos = x * resolution;
            System.arraycopy(paddedMap, startPos, map, endPos, resolution);
        }

        return map;
    }

    private static float upsampleFromFloatMap(int paddingSmallest, int paddingBiggest,int resolutionBiggest, int resolutionSmallest, float[] sampleFrom, int indexX, int indexZ){
        if (resolutionBiggest < resolutionSmallest) {
            throw new IllegalArgumentException("Resolution 1 is not bigger than Resolution 2");
        }
        int scaleTo = resolutionBiggest / resolutionSmallest;

        int paddingOffset = paddingSmallest - (paddingBiggest / scaleTo);

        int iX = Math.floorDiv(indexX, scaleTo) + paddingOffset;
        int iZ = Math.floorDiv(indexZ, scaleTo) + paddingOffset;

        float deltaX = Math.floorMod(indexX, scaleTo) / (float) scaleTo;
        float deltaZ = Math.floorMod(indexZ, scaleTo) / (float) scaleTo;

        int resScale = (paddingSmallest * 2) + resolutionSmallest;
        float i00 = sampleFrom[iX * resScale + iZ];
        float i10 = sampleFrom[(iX + 1) * resScale + iZ];
        float i01 = sampleFrom[iX * resScale + (iZ + 1)];
        float i11 = sampleFrom[(iX + 1) * resScale + (iZ) + 1];

        return (float) Mth.lerp2(deltaX,deltaZ,i00,i10,i01,i11);
    }

    public static int convertIndex(int resolution, int resolutionTo, int padding, int x, int z) {
        int scale = resolutionTo / resolution;

        int targetTotalWidth = resolutionTo + (padding * 2);

        int targetX = (x * scale) + padding;
        int targetZ = (z * scale) + padding;

        return targetX * targetTotalWidth + targetZ;
    }


}
