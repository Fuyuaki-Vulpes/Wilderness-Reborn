package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

public class RegionRivers {

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int RIVER_GRID_SIZE = 16;
    public static final int RIVER_GRID_SQUARED = RIVER_GRID_SIZE * RIVER_GRID_SIZE;
    public static final int RIVER_SIZE = RegionPos.REGION_SIZE / RIVER_GRID_SIZE;
    public static final int RIVER_BITS = Integer.numberOfTrailingZeros(RIVER_SIZE);


    public static final int BLOCK_TO_RIVERS_MASK = RIVER_SIZE - 1;
    public static final int BANDING = 2;
    public static final int RIVER_SEARCH_RANGE = BANDING / 2;
    private static final float ELEVATION_DIFFERENCE_TO_CLIFF = 12.0f;
    private RiverNode[] riverNodes = new RiverNode[RIVER_GRID_SQUARED];
    private final RegionNoisesRecord record;
    private boolean generatedPoints = false;

    public RegionRivers(RiverNode[] riverNodes, RegionNoisesRecord record) {
        this.riverNodes = riverNodes;
        this.record = record;
    }

    public RegionRivers(RegionNoisesRecord record) {
        this.record = record;
    }


    public void generateRiverNodes(RegionPos pos, long seed, WorldRegion worldRegion) {
        int step = 4;
        int scanRange = (RIVER_SIZE / step) - 2;

        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                int truePosX = RegionPos.toBlockCoord(pos.x()) + (cellX * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE);
                int truePosZ = RegionPos.toBlockCoord(pos.z()) + ((cellZ * RegionPos.REGION_SIZE) / RegionRivers.RIVER_GRID_SIZE);
                long hash = RiverNode.coordinateHash(truePosX, truePosZ, seed);

                float lowestElevation = Float.MAX_VALUE;

                float offsetX = 0.0f;
                float offsetZ = 0.0f;

                int cellWorldX = nodeToBlock(cellX) + pos.getMinBlockX();
                int cellWorldZ = nodeToBlock(cellZ) + pos.getMinBlockZ();

                for (int xO = 1; xO <= scanRange; xO++) {
                    for (int zO = 1; zO <= scanRange; zO++) {
                        if (zO == 0 && xO == 0) continue;
                        int xOff = cellWorldX + (xO * step);
                        int zOff = cellWorldZ + (zO * step);

                        float offsetXAt = (float) xO / RIVER_SIZE;
                        float offsetZAt = (float) zO / RIVER_SIZE;

                        float elevationAt = worldRegion.layers().getHeightmapBasic().getGlobal(xOff, zOff);
                        if (elevationAt < lowestElevation) {
                            lowestElevation = elevationAt;
                            offsetX = offsetXAt;
                            offsetZ = offsetZAt;
                        }
                    }
                }

                this.riverNodes[nodeIndex(cellX, cellZ)] = new RiverNode(pos, cellX, cellZ, offsetX, offsetZ, hash, worldRegion,lowestElevation);
            }
        }
    }

    public void computeEntireRivers(RegionPos pos, Map<RegionPos, WorldRegion> neighbors, WorldRegion thisRegion) {
        int offset = 16;
        List<RiverNode> possibleNodes = new ArrayList<>();
        for (int cellX = -BANDING; cellX < RIVER_GRID_SIZE + BANDING; cellX++) {
            for (int cellZ = -BANDING; cellZ < RIVER_GRID_SIZE + BANDING; cellZ++) {
                int cellPosX = cellX * RIVER_SIZE + pos.getMinBlockX();
                int cellPosZ = cellZ * RIVER_SIZE + pos.getMinBlockZ();
                RegionPos pos1 = RegionPos.fromBlockCoord(cellPosX,cellPosZ);
                WorldRegion r = pos1.equals(pos) ? thisRegion : neighbors.get(pos1);
                RiverNode node = r.getNbtData().rivers().getNodeAtBlock(cellPosX,cellPosZ);
                possibleNodes.add(node);
            }
        }
        RiverNode[] nodes = possibleNodes.toArray(new RiverNode[0]);

        Arrays.parallelSort(nodes);

        for (RiverNode riverNode : nodes) {
            int cellX = riverNode.getCellX();
            int cellZ = riverNode.getCellZ();
            RegionPos riverRegion = riverNode.getRegionPos();
            if (riverRegion.equals(pos)) {
                if (!riverNode.isEnabled()) continue;
                float slope = riverNode.getSlopeTo();
                RiverNode bestMatch = null;
                boolean bestMatchIsOutside = false;

                Vec2 terrainTangent = Vec2.ZERO;
                float elevation0 = riverNode.getElevation();


                float continentRiver = thisRegion.layers().getContinentalness().getGlobal(Mth.floor(riverNode.getWorldX()), Mth.floor(riverNode.getWorldZ()));
                float score = Float.NEGATIVE_INFINITY;

                for (int xO = -1; xO <= 1; xO++) {
                    for (int zO = -1; zO <= 1; zO++) {
                        if (xO == 0 && zO == 0) continue;
                        int x = Mth.floor(riverNode.getWorldX() + (xO * offset));
                        int z = Mth.floor(riverNode.getWorldZ() + (zO * offset));
                        RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(x), RegionPos.fromBlockCoord(z));
                        WorldRegion region = thisRegion.getPos().equals(pos0) ? thisRegion : neighbors.get(pos0);
                        float distance = (xO != 0 && zO != 0) ? 1.4f : 1.0f;

                        float elevationPoint = region.layers().getHeightmapBasic().upsampleFromNeighbors(x, z, neighbors);
                        float elevationDifference = (elevation0 - elevationPoint) / distance;
                        terrainTangent = terrainTangent.add(new Vec2(
                                xO * elevationDifference, zO * elevationDifference
                        ));
                    }
                }
                for (int xO = -RIVER_SEARCH_RANGE; xO <= RIVER_SEARCH_RANGE; xO++) {
                    for (int zO = -RIVER_SEARCH_RANGE; zO <= RIVER_SEARCH_RANGE; zO++) {
                        if (zO == 0 && xO == 0) continue;
                        int thisCellX = cellX + xO;
                        int thisCellZ = cellZ + zO;
                        RiverNode riverB;
                        boolean foundOutside = false;
                        float continent;
                        if (!(thisCellX >= 0 && thisCellX < RIVER_GRID_SIZE && thisCellZ >= 0 && thisCellZ < RIVER_GRID_SIZE)) {
                            float xOff = riverNode.getWorldX() + (xO * RIVER_SIZE);
                            float zOff = riverNode.getWorldZ() + (zO * RIVER_SIZE);
                            RegionPos regPos = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(xOff)), RegionPos.fromBlockCoord(Mth.floor(zOff)));
                            WorldRegion region = regPos.equals(pos) ? thisRegion : neighbors.get(regPos);
                            riverB = region.getNbtData().rivers().getNodeAtBlock(Mth.floor(xOff), Mth.floor(zOff));
                            if (riverB != null && riverB.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS
                                    && riverB.getFlowTo().x() == riverNode.getWorldX()
                                    && riverB.getFlowTo().z() == riverNode.getWorldZ()) {
                                riverNode.updateFlow(riverB, terrainTangent);
                            }
                            continent = region.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ), neighbors);
                            foundOutside = true;
                        } else {
                            riverB = riverNodes[nodeIndex(thisCellX, thisCellZ)];
                            continent = thisRegion.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ), neighbors);
                        }
                        if (riverB == null || !riverNode.isEnabled()) continue;

                        if (riverNode.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS && riverNode.getFlowTo().x() == riverB.getWorldPosition().x() && riverNode.getFlowTo().z() == riverB.getWorldPosition().z()) {
                            continue;
                        }
                        float thisHeightD = riverNode.getElevation() - riverB.getElevation();
                        if (thisHeightD < 0) continue;
                        float thisDist = riverNode.getDistance(riverB.getWorldX(), riverB.getWorldZ());
                        float thisSlope = thisHeightD / (thisDist * 0.25f);
                        float thisScore = getScore(neighbors,thisRegion,riverNode,riverB,continentRiver,continent,terrainTangent,thisDist,thisSlope);
                        if (thisScore > score) {
                            score = thisScore;
                            slope = thisSlope;
                            bestMatch = riverB;
                            bestMatchIsOutside = foundOutside;
                        }
                    }
                }

                if (bestMatch == null) {

                    score = Float.NEGATIVE_INFINITY;
                    for (int xO = -RIVER_SEARCH_RANGE * 2; xO <= RIVER_SEARCH_RANGE * 2; xO++) {
                        for (int zO = -RIVER_SEARCH_RANGE * 2; zO <= RIVER_SEARCH_RANGE * 2; zO++) {
                            if (zO == 0 && xO == 0) continue;
                            int thisCellX = cellX + xO;
                            int thisCellZ = cellZ + zO;
                            RiverNode riverB;
                            boolean foundOutside = false;
                            float continent;
                            if (!(thisCellX >= 0 && thisCellX < RIVER_GRID_SIZE && thisCellZ >= 0 && thisCellZ < RIVER_GRID_SIZE)) {
                                float xOff = riverNode.getWorldX() + (xO * RIVER_SIZE);
                                float zOff = riverNode.getWorldZ() + (zO * RIVER_SIZE);
                                RegionPos regPos = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(xOff)), RegionPos.fromBlockCoord(Mth.floor(zOff)));
                                WorldRegion region = regPos.equals(pos) ? thisRegion : neighbors.get(regPos);
                                riverB = region.getNbtData().rivers().getNodeAtBlock(Mth.floor(xOff), Mth.floor(zOff));
                                if (riverB != null && riverB.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS
                                        && riverB.getFlowTo().x() == riverNode.getWorldX()
                                        && riverB.getFlowTo().z() == riverNode.getWorldZ()) {
                                    riverNode.updateFlow(riverB, terrainTangent);
                                }
                                continent = region.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ), neighbors);
                                foundOutside = true;
                            } else {
                                riverB = riverNodes[nodeIndex(thisCellX, thisCellZ)];
                                continent = thisRegion.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ), neighbors);
                            }
                            if (riverB == null || !riverNode.isEnabled()) continue;

                            if (riverNode.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS && riverNode.getFlowTo().x() == riverB.getWorldPosition().x() && riverNode.getFlowTo().z() == riverB.getWorldPosition().z()) {
                                continue;
                            }
                            float thisHeightD = riverNode.getElevation() - riverB.getElevation();
                            if (thisHeightD < 0 || continent >= continentRiver) continue;
                            float thisDist = riverNode.getDistance(riverB.getWorldX(), riverB.getWorldZ());
                            float thisSlope = thisHeightD / (thisDist * 0.25f);
                            float thisScore = getScore(neighbors,thisRegion,riverNode,riverB,continentRiver,continent,terrainTangent,thisDist,thisSlope);
                            if (thisScore > score) {
                                score = thisScore;
                                slope = thisSlope;
                                bestMatch = riverB;
                                bestMatchIsOutside = foundOutside;
                            }
                        }
                    }
                    if (bestMatch == null){

                    }else {
                        riverNode.setAndCalculateDownhillConnection(bestMatch, slope, thisRegion.layers().record().riverRandom2().getNoise());
                        if (!bestMatchIsOutside) {
                            bestMatch.updateFlow(riverNode, terrainTangent);
                            riverNodes[nodeIndex(bestMatch.getPosition().x(), bestMatch.getPosition().z())] = bestMatch;
                        }

                    }

                    riverNode.setFlowTo(RiverNode.WorldPosition.OUT_OF_BOUNDS);
                    riverNode.setAsEndOfRiverAndCalculate(terrainTangent);
                } else {
                    riverNode.setAndCalculateDownhillConnection(bestMatch, slope, thisRegion.layers().record().riverRandom2().getNoise());
                    if (!bestMatchIsOutside) {
                        bestMatch.updateFlow(riverNode, terrainTangent);
                        riverNodes[nodeIndex(bestMatch.getPosition().x(), bestMatch.getPosition().z())] = bestMatch;
                    }

                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
            else {
                WorldRegion reg = neighbors.get(riverRegion);
                if (riverNode.isEnabled()) {
                    RiverNode bestMatch = null;
                    boolean bestMatchIsOutside = false;

                    Vec2 terrainTangent = Vec2.ZERO;
                    float elevation0 = riverNode.getElevation();


                    float continentRiver = reg.layers().getContinentalness().getGlobal(Mth.floor(riverNode.getWorldX()), Mth.floor(riverNode.getWorldZ()));
                    float score = Float.NEGATIVE_INFINITY;

                    for (int xOf = -1; xOf <= 1; xOf++) {
                        for (int zOf = -1; zOf <= 1; zOf++) {
                            if (xOf == 0 && zOf == 0) continue;
                            int x = Mth.floor(riverNode.getWorldX() + (xOf * offset));
                            int z = Mth.floor(riverNode.getWorldZ() + (zOf * offset));
                            RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(x), RegionPos.fromBlockCoord(z));
                            WorldRegion region = thisRegion.getPos().equals(pos0) ? thisRegion : neighbors.get(pos0);
                            float distance = (xOf != 0 && zOf != 0) ? 1.4f : 1.0f;

                            float elevationPoint = region.layers().getHeightmapBasic().upsampleFromNeighbors(x, z, neighbors);
                            float elevationDifference = (elevation0 - elevationPoint) / distance;
                            terrainTangent = terrainTangent.add(new Vec2(
                                    xOf * elevationDifference, zOf * elevationDifference
                            ));
                        }
                    }
                    for (int xOf = -RIVER_SEARCH_RANGE; xOf <= RIVER_SEARCH_RANGE; xOf++) {
                        for (int zOf = -RIVER_SEARCH_RANGE; zOf <= RIVER_SEARCH_RANGE; zOf++) {
                            if (zOf == 0 && xOf == 0) continue;
                            int thisCellX = cellX + xOf;
                            int thisCellZ = cellZ + zOf;
                            RiverNode riverB;
                            boolean foundOutside = false;

                            int targetWorldX = Mth.floor(riverNode.getWorldX() + (xOf * RIVER_SIZE));
                            int targetWorldZ = Mth.floor(riverNode.getWorldZ() + (zOf * RIVER_SIZE));

                            if (!(thisCellX >= 0 && thisCellX < RIVER_GRID_SIZE && thisCellZ >= 0 && thisCellZ < RIVER_GRID_SIZE)) {
                                RegionPos rPos = new RegionPos(RegionPos.fromBlockCoord(targetWorldX), RegionPos.fromBlockCoord(targetWorldZ));
                                WorldRegion region = rPos.equals(pos) ? thisRegion : neighbors.get(rPos);

                                riverB = region.getNbtData().rivers().getNodeAtBlock(targetWorldX, targetWorldZ);
                                if (riverB != null && riverB.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS
                                        && riverB.getFlowTo().x() == riverNode.getWorldX()
                                        && riverB.getFlowTo().z() == riverNode.getWorldZ()) {
                                    riverNode.updateFlow(riverB, terrainTangent);
                                }
                                foundOutside = true;
                            } else {
                                riverB = riverNodes[nodeIndex(thisCellX, thisCellZ)];
                            }

                            if (riverB == null) continue;
                            if (!riverB.isEnabled()) continue;

                            float continent = continentRiver - reg.layers().getContinentalness().upsampleFromNeighbors(targetWorldX, targetWorldZ, neighbors);
                            if (riverNode.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS && riverNode.getFlowTo().x() == riverB.getWorldPosition().x() && riverNode.getFlowTo().z() == riverB.getWorldPosition().z()) {
                                continue;
                            }
                            float thisHeightD = riverNode.getElevation() - riverB.getElevation();
                            if (thisHeightD < 0) continue;
                            float thisDist = riverNode.getDistance(riverB.getWorldX(), riverB.getWorldZ());
                            float thisSlope = thisHeightD / (thisDist * 0.25f);
                            float thisScore = getScore(neighbors,thisRegion,riverNode,riverB,continentRiver,continent,terrainTangent,thisDist,thisSlope);
                            if (thisScore > score) {
                                score = thisScore;
                                bestMatch = riverB;
                                bestMatchIsOutside = foundOutside;
                            }
                        }
                    }


                    if (!bestMatchIsOutside && bestMatch != null) {

                        bestMatch.updateFlow(riverNode, terrainTangent);
                        riverNodes[nodeIndex(bestMatch.getPosition().x(), bestMatch.getPosition().z())] = bestMatch;

                    }
                }

            }

        }
    }

    private static float getScore(Map<RegionPos, WorldRegion> neighbors, WorldRegion thisRegion, RiverNode riverNode, RiverNode riverTo, float continent, float continentTo, Vec2 terrainTangent, float distTo, float slopeTo) {
        float worldX = riverNode.getWorldX();
        float worldZ = riverNode.getWorldZ();

        float xDiff = riverTo.getWorldX() - worldX;
        float zDiff = riverTo.getWorldZ() - worldZ;

        float elevation = riverNode.getElevation();
        float elevationTo = riverTo.getElevation();

        int steps = Math.min(10,Mth.floor(riverNode.getDistance(riverTo.getWorldX(), riverTo.getWorldZ()) / 10));
        float minHeight = Float.MAX_VALUE;
        float maxHeight = 0.0f;
        for (float i = 0; i < steps; i++) {
            float xO = (i / steps) * xDiff;
            float zO = (i / steps) * zDiff;
            float xAt = worldX + xO;
            float zAt = worldZ + zO;

            float elevationAt = thisRegion.layers().getHeightmapBasic().upsampleFromNeighbors(Mth.floor(xAt),Mth.floor(zAt),neighbors);
            minHeight = Math.min(minHeight, elevationAt);
            maxHeight = Math.max(maxHeight, elevationAt);
        }

        float minDif = elevationTo - minHeight;
        float maxDif = maxHeight - elevation;

        float dif = elevation - elevationTo;

        float maxTolerance = dif * 2.5f;

        float continentalDifference = continent - continentTo;
        float continentalTolerance = 0.15f;
        float continentalScore = continentalDifference / continentalTolerance;

        float stressAt = thisRegion.layers().getTectonicStress().upsampleFromNeighbors(Mth.floor(riverTo.getWorldX()),Mth.floor(riverNode.getWorldZ()),neighbors);

        float slopeTolerance = 0.15f;
        float slopeScore =  slopeTo / slopeTolerance;

        float xDist = riverTo.getWorldX() - riverNode.getWorldX();
        float zDist = riverTo.getWorldZ() - riverNode.getWorldZ();

        Vec2 dist =  new Vec2(xDist, zDist);

        float alignmentScore = terrainTangent.dot(dist) * 1.5f;

        float elevationToSea = WildernessConstants.SEA_LEVEL - elevationTo;
        float seaTolerance = 16.0f;
        float elevationScore = Math.max((elevationToSea + (seaTolerance * 0.5f)) / seaTolerance,0.0f);

        return continentalScore + slopeScore + alignmentScore + elevationScore - (Math.max(minDif / maxTolerance , maxDif / maxTolerance) * 2.0f) - (stressAt * 3.0f);
    }

    public int nodeIndex(RiverNode.GridPoint position) {
        return nodeIndex(position.x(), position.z());
    }

    public void postProcessRiverCliffsAndFlow(RegionPos pos, Map<RegionPos, WorldRegion> neighbors,WorldRegion thisRegion, long seed) {
        int offset = 8;
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

                if (!riverNode.isEnabled()) continue;
                Vec2 terrainTangent = Vec2.ZERO;
                float elevation0 = riverNode.getElevation();
                for (int xO = -1; xO <= 1; xO++) {
                    for (int zO = -1; zO <= 1; zO++) {
                        if (xO == 0 && zO == 0) continue;
                        int x = Mth.floor(riverNode.getWorldX() + (xO * offset));
                        int z = Mth.floor(riverNode.getWorldZ() + (zO * offset));
                        RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(x), RegionPos.fromBlockCoord(z));
                        WorldRegion region = thisRegion.getPos().equals(pos0) ? thisRegion : neighbors.get(pos0);
                        float distance = (xO != 0 && zO != 0) ? 1.4f : 1.0f;

                        float elevationPoint = region.layers().getHeightmapBasic().upsampleFromNeighbors(x,z,neighbors);
                        float elevationDifference = (elevation0 - elevationPoint) / distance;
                        terrainTangent = terrainTangent.add(new Vec2(
                                xO * elevationDifference, zO * elevationDifference
                        ));
                    }
                }
                if (terrainTangent.lengthSquared() > 0.0001f) {
                    terrainTangent = terrainTangent.normalized();
                    riverNode.setFlow(riverNode.getFlow().add(terrainTangent.scale(0.75f)));
                }
                RiverNode.WorldPosition flowTo = riverNode.getFlowTo();
                if (flowTo != RiverNode.WorldPosition.OUT_OF_BOUNDS && flowTo.x() != RiverNode.WorldPosition.OUT_OF_BOUNDS.x()) {
                    RiverPoint river0 = riverNode.asPoint();

                    RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(flowTo.x())), RegionPos.fromBlockCoord(Mth.floor(flowTo.z())));
                    RiverNode flowToRiver;
                    if (pos0.x() == pos.x() && pos0.z() == pos.z()) {
                        flowToRiver = this.getNodeAt(flowTo);
                    } else {
                        flowToRiver = neighbors.get(pos0).getNbtData().rivers().getNodeAt(flowTo);
                    }

                    RiverPoint river4 = flowToRiver.asPoint();

                    float distTo = riverNode.getDistance(river4.worldX(), river4.worldZ()) + 1;

                    RiverPoint lastPoint = river0;
                    float startDelta = 0.0f;
                    float endDelta = 0.0f;

                    float startElevation = 0.0f;
                    float endElevation = 0.0f;

                    float startDifference = 0.0f;
                    boolean cliffFound = false;
                    float lastElevation = river0.elevation();
                    float maxDiversion = Math.min(distTo / 2,128.0f);
                    RiverPoint river2 = RiverPoint.bendPoint(river0, river4, 0.50f,thisRegion.layers().record().riverSway().getNoise(),thisRegion.layers().record().riverRandom().getNoise(), maxDiversion);
                    for (int d = 0; d < distTo; d++) {
                        float delta = d / distTo;
                        RiverPoint p0 = interpolated4PointRiver(river0, river2, river4,delta,thisRegion.layers().record().riverSway().getNoise(),lastElevation);
                        float interpX = p0.worldX();
                        float interpZ = p0.worldZ();
                        if(delta <= 0.1f || delta >= 0.9f)continue;

                        RegionPos regPos = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(interpX)),RegionPos.fromBlockCoord(Mth.floor(interpZ)));
                        WorldRegion region = regPos.equals(pos) ? thisRegion : neighbors.get(regPos);
                        float elevationHere = region.layers().getHeightmapBasic().upsampleFromNeighbors(Mth.floor(interpX),Mth.floor(interpZ),neighbors);
                        p0 = p0.withNewElevation(Mth.clamp(elevationHere,river4.elevation(),lastElevation));
                        lastElevation = elevationHere;
                        float elevationDifference = lastPoint.elevation() - p0.elevation();
                        if (elevationDifference >= ELEVATION_DIFFERENCE_TO_CLIFF && startDifference < ELEVATION_DIFFERENCE_TO_CLIFF) {
                            startDifference = elevationDifference;
                            startElevation = p0.elevation();
                            startDelta = delta;
                        } else if (elevationDifference < ELEVATION_DIFFERENCE_TO_CLIFF && startDifference >= ELEVATION_DIFFERENCE_TO_CLIFF) {
                            endDelta = delta;
                            endElevation = p0.elevation();
                            cliffFound = true;
                            break;
                        }
                        lastPoint = p0;
                    }

                    if (cliffFound) {
                        riverNode.setCliff(new RiverNode.Cliff(startDelta, endDelta,startElevation, endElevation,true));
                    }
                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }
    }


    public static RiverPoint interpolated4PointRiver(RiverPoint river0, RiverPoint river2, RiverPoint river4, float delta, Noise2D noise, float lastElevation) {
        RiverPoint first;
        RiverPoint second;
        if (delta < 0.5f){
            first = river0;
            second = river2;
        }else{
            first = river2;
            second = river4;
        }
        float delta0 = (delta * 2.0f) % 1.0f;
        return RiverPoint.interpolated(first, second, delta0,noise,delta,river0,river4, lastElevation);
    }

    public void generatePointsForAllRivers(RegionPos pos, WorldRegion region, Map<RegionPos, WorldRegion> neighbors) {
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];
                if (shouldGeneratePoints(riverNode)) {
                    riverNode.generatePoints(pos, region, neighbors);
                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }
        this.generatedPoints = true;
    }

    private static boolean shouldGeneratePoints(RiverNode river) {
        return river.isEnabled() &&
                 river.getElevation() > 48 &&
                (river.getFlowFrom() != RiverNode.WorldPosition.OUT_OF_BOUNDS && river.getFlowFrom().x() != RiverNode.WorldPosition.OUT_OF_BOUNDS.x())
                || (river.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS && river.getFlowTo().x() != RiverNode.WorldPosition.OUT_OF_BOUNDS.x()) ;
    }

    public void removeUnnecessaryRivers(){
        for (int cellX = BANDING; cellX < RIVER_GRID_SIZE - BANDING; cellX++) {
            for (int cellZ = BANDING; cellZ < RIVER_GRID_SIZE - BANDING; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];
                if (riverNode.getFlowTo() == RiverNode.WorldPosition.OUT_OF_BOUNDS && riverNode.getFlowFrom() == RiverNode.WorldPosition.OUT_OF_BOUNDS) {
                    riverNodes[nodeIndex(cellX, cellZ)] = null;
                }
            }
        }
    }

    public RiverNode getNodeAt(RiverNode.WorldPosition position) {
        return getNodeAtBlock(Mth.floor(position.x()),Mth.floor(position.z()));
    }
    public RiverNode getNodeAt(RiverNode.WorldPosition position,RegionPos pos, Map<RegionPos, WorldRegion> neighbors) {
        if (position != RiverNode.WorldPosition.OUT_OF_BOUNDS && position.x() != RiverNode.WorldPosition.OUT_OF_BOUNDS.x()) {

            RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(position.x())), RegionPos.fromBlockCoord(Mth.floor(position.z())));
            RiverNode river;
            if (pos0.x() == pos.x() && pos0.z() == pos.z()) {
                river = this.getNodeAt(position);
            } else {
                river = neighbors.get(pos0).getNbtData().rivers().getNodeAt(position);
            }

            return river;
        }
        else return null;
    }


    public static int nodeIndex(int x, int z) {
        return x * RIVER_GRID_SIZE + z;
    }

    public static int nodePosition(int position) {
        return position >> RIVER_BITS;
    }
    public static int nodeToBlock(int cell) {
        return cell << RIVER_BITS;
    }

    public void update(int index, RiverNode riverNode) {
        this.riverNodes[index] = riverNode;
    }

    public static int localBlockInRiverGrid(int blockPos) {
        return blockPos & BLOCK_TO_RIVERS_MASK;
    }

    public RiverNode getNode(int index) {
        return this.riverNodes[index];
    }

    public RiverNode getNode(int x, int z) {
        return this.getNode(nodeIndex(x, z));
    }

    public RiverNode getNodeAtBlock(int xPos, int zPos) {
        int x = Math.floorMod(xPos, RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos, RegionPos.REGION_SIZE);
        int cellX = nodePosition(x);
        int cellZ = nodePosition(z);
        return this.getNode(nodeIndex(cellX, cellZ));
    }

    public int getNodeIndexForBlock(int xPos, int zPos) {
        int x = Math.floorMod(xPos, RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos, RegionPos.REGION_SIZE);
        int cellX = nodePosition(x);
        int cellZ = nodePosition(z);
        return nodeIndex(cellX, cellZ);
    }

    public static RegionRivers readFrom(CompoundTag tag, RegionNoisesRecord record) throws IOException {
        Optional<CompoundTag> optionalRiversTag = tag.getCompound("rivers");
        if (optionalRiversTag.isEmpty()) {
            return new RegionRivers(record);
        }
        CompoundTag riversTag = optionalRiversTag.get();
        RiverNode[] nodeMap = new RiverNode[RIVER_GRID_SQUARED];
        for (int x = 0; x < RIVER_GRID_SIZE; x++) {
            for (int z = 0; z < RIVER_GRID_SIZE; z++) {
                CompoundTag node = riversTag.getCompound("node_" + nodeIndex(x, z)).orElse(null);
                nodeMap[nodeIndex(x, z)] = node == null ? null : RiverNode.read(node);
            }
        }
        return new RegionRivers(nodeMap, record);
    }

    public void put(CompoundTag tag) throws Throwable{
        CompoundTag nodeMapTag = new CompoundTag();
        if (this.riverNodes == null || this.riverNodes[0] == null) {
            return;
        }
        for (int x = 0; x < RIVER_GRID_SIZE; x++) {
            for (int z = 0; z < RIVER_GRID_SIZE; z++) {
                CompoundTag river = new CompoundTag();
                int index = nodeIndex(x, z);
                if (this.riverNodes[index] == null) {
                    continue;
                }
                try {
                    this.riverNodes[index].write(river);
                    nodeMapTag.put("node_" + index, river);
                } catch (Throwable e) {
                    LOGGER.error("Failed to insert node {} into tag {}",index,nodeMapTag, e);
                }
            }
        }
        if (nodeMapTag.isEmpty()) throw new Throwable("Tag cant be empty");
        tag.put("rivers", nodeMapTag);
    }

    public boolean regionHasGeneratedPoints() {
        return generatedPoints;
    }
}
