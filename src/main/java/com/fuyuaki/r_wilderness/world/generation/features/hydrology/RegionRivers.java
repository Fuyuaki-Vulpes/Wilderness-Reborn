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
import java.util.Map;
import java.util.Optional;

public class RegionRivers {

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int RIVER_GRID_SIZE = 32;
    public static final int RIVER_GRID_SQUARED = RIVER_GRID_SIZE * RIVER_GRID_SIZE;
    public static final int RIVER_SIZE = RegionPos.REGION_SIZE / RIVER_GRID_SIZE;
    public static final int RIVER_BITS = Integer.numberOfTrailingZeros(RIVER_SIZE);


    public static final int BLOCK_TO_RIVERS_MASK = RIVER_SIZE - 1;
    public static final int BANDING = 2;
    public static final int RIVER_SEARCH_RANGE = BANDING / 2;
    private static final float ELEVATION_DIFFERENCE_TO_CLIFF = 8.0f;
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
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                int truePosX = Mth.floor(RegionPos.toBlockCoord(pos.x()) + RiverNode.worldPosition(cellX));
                int truePosZ = Mth.floor(RegionPos.toBlockCoord(pos.z()) + RiverNode.worldPosition(cellZ));
                long hash = RiverNode.coordinateHash(truePosX, truePosZ, seed);
                float centerX = RiverNode.getSaltedFloat(hash, 0) * 0.7f + 0.15f;
                float centerZ = RiverNode.getSaltedFloat(hash, 1) * 0.7f + 0.15f;

                this.riverNodes[nodeIndex(cellX, cellZ)] = new RiverNode(pos, cellX, cellZ,this.record, centerX, centerZ, hash, worldRegion);
            }
        }


        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

                float slope = 0.0f;
                float score = Float.NEGATIVE_INFINITY;
                float continentRiver = worldRegion.layers().getContinentalness().getGlobal(Mth.floor(riverNode.getWorldX()), Mth.floor(riverNode.getWorldZ()));
                RiverNode riverToFlowTo = null;
                float bestE = Float.MAX_VALUE;
                boolean skipFlag = continentRiver <= 0.0f && riverNode.getElevation() <= WildernessConstants.SEA_LEVEL;
                if(!skipFlag){
                    for (int xO = -RIVER_SEARCH_RANGE; xO <= RIVER_SEARCH_RANGE; xO++) {
                        for (int zO = -RIVER_SEARCH_RANGE; zO <= RIVER_SEARCH_RANGE; zO++) {
                            if (zO == 0 && xO == 0) continue;
                            int thisCellX = cellX + xO;
                            int thisCellZ = cellZ + zO;
                            if (!(thisCellX >= 0 && thisCellX < RIVER_GRID_SIZE && thisCellZ >= 0 && thisCellZ < RIVER_GRID_SIZE)) {
                                continue;
                            }
                            RiverNode riverB = riverNodes[nodeIndex(thisCellX, thisCellZ)];
                            float thisHeightD = riverNode.getElevation() - riverB.getElevation();
                            if (thisHeightD < 0) continue;
                            float thisDist = riverNode.getDistance(riverB.getWorldX(), riverB.getWorldZ());
                            float thisSlope = thisHeightD / (thisDist * 0.25f);
                            float continent = continentRiver - worldRegion.layers().getContinentalness().getGlobal(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ));
                            float thisScore = thisSlope + (continent);
                            if (thisScore > score || riverB.getElevation() <=32 && bestE > 32 ) {
                                score = thisScore;
                                slope = thisSlope;
                                bestE = riverB.getElevation();
                                riverToFlowTo = riverB;
                            }
                        }
                    }
                }
                if (skipFlag || riverToFlowTo == null) {
                    riverNode.setFlowTo(RiverNode.WorldPosition.OUT_OF_BOUNDS);
                    riverNode.setAsEndOfRiverAndCalculate(Vec2.ZERO);
                } else {
                    int worldX = cellX * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
                    int worldZ = cellZ * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
                    long hash = RiverNode.coordinateHash(worldX, worldZ, seed);
                    riverNode.setAndCalculateDownhillConnection(riverToFlowTo, slope,hash);
                    riverToFlowTo.updateFlow(riverNode,Vec2.ZERO);
                    riverNodes[nodeIndex(riverToFlowTo.getPosition().x(), riverToFlowTo.getPosition().z())] = riverToFlowTo;

                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }
    }


    public void processRiverTerrainAdaptation(Map<RegionPos, WorldRegion> neighbors, WorldRegion thisRegion) {
        int step = 4;
        int scanRange = RIVER_SIZE / step;

        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

                int cellCenterX = nodeToBlock(cellX) + (RIVER_SIZE / 2);
                int cellCenterZ = nodeToBlock(cellZ) + (RIVER_SIZE / 2);

                float lowestElevation = riverNode.getElevation();
                float offsetX = riverNode.getOffset().x;
                float offsetZ = riverNode.getOffset().y;
                float minE = 0.0f;
                if (riverNode.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS){
                    minE = getNodeAt(riverNode.getFlowTo()).getElevation();
                }
                RegionPos p = thisRegion.getPos();

                for (int xO = 0; xO <= scanRange; xO++) {
                    for (int zO = 0; zO <= scanRange; zO++) {
                        if (zO == 0 && xO == 0) continue;
                        int xOff = cellCenterX + (xO * step) + p.getMinBlockX();
                        int zOff = cellCenterZ + (zO * step) + p.getMinBlockZ();

                        float offsetXAt = ((float) xO / RIVER_SIZE / 2 + 1.0f) / 2;
                        float offsetZAt = ((float) zO / RIVER_SIZE / 2 + 1.0f) / 2;


                        float elevationAt = thisRegion.layers().getHeightmapBasic().upsampleFromNeighbors(xOff,zOff,neighbors);
                        if (elevationAt < lowestElevation){
                            lowestElevation = Math.max(elevationAt,minE);
                            offsetX = offsetXAt;
                            offsetZ = offsetZAt;
                        }
                    }

                    riverNode.setElevation(lowestElevation);
                    riverNode.setOffset(new Vec2(offsetX, offsetZ));
                    riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
                }
            }
        }
    }

    public void computeEntireRivers(RegionPos pos, Map<RegionPos, WorldRegion> neighbors, WorldRegion thisRegion, long seed) {
        int offset = 8;
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

                float slope = riverNode.getSlopeTo();
                RiverNode bestMatch = null;
                boolean bestMatchIsOutside = false;
                boolean changed = false;

                Vec2 terrainTangent = Vec2.ZERO;
                float elevation0 = riverNode.getElevation();


                float continentRiver = thisRegion.layers().getContinentalness().getGlobal(Mth.floor(riverNode.getWorldX()), Mth.floor(riverNode.getWorldZ()));
                float score = Float.NEGATIVE_INFINITY;
                boolean skipFlag = continentRiver <= 0.0f && riverNode.getElevation() <= WildernessConstants.SEA_LEVEL;
                if(!skipFlag){
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
                    float bestE = Float.MAX_VALUE;
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
                                continent = continentRiver - region.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ),neighbors);
                                foundOutside = true;
                            } else {
                                riverB = riverNodes[nodeIndex(thisCellX, thisCellZ)];
                                continent = continentRiver - thisRegion.layers().getContinentalness().upsampleFromNeighbors(pos.getMinBlockX() + nodeToBlock(thisCellX), pos.getMinBlockZ() + nodeToBlock(thisCellZ),neighbors);
                            }
                            if (riverB == null) continue;
                            if (riverNode.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS && riverNode.getFlowTo().x() == riverB.getWorldPosition().x() && riverNode.getFlowTo().z() == riverB.getWorldPosition().z()) {
                                continue;
                            }
                            float thisHeightD = riverNode.getElevation() - riverB.getElevation();
                            if (thisHeightD < 0) continue;
                            float thisDist = riverNode.getDistance(riverB.getWorldX(), riverB.getWorldZ());
                            float thisSlope = thisHeightD / (thisDist * 0.25f);
                            float thisScore = thisSlope + continent;

                            if (thisScore > score || riverB.getElevation() <=32 && bestE > 32 ) {
                                score = thisScore;
                                changed = true;
                                bestE = riverB.getElevation();
                                slope = thisSlope;
                                bestMatch = riverB;
                                bestMatchIsOutside = foundOutside;
                            }
                        }
                    }
                }else{
                    changed = true;
                    bestMatch = null;
                    riverNode.setFlowTo(RiverNode.WorldPosition.OUT_OF_BOUNDS);
                }
                if (!changed) continue;
                if (bestMatch == null) {
                    riverNode.setAsEndOfRiverAndCalculate(terrainTangent);
                } else {
                    int worldX = cellX * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
                    int worldZ = cellZ * RegionPos.REGION_SIZE / RegionRivers.RIVER_GRID_SIZE;
                    long hash = RiverNode.coordinateHash(worldX, worldZ, seed);
                    riverNode.setAndCalculateDownhillConnection(bestMatch, slope,hash);
                    if (!bestMatchIsOutside) {
                        bestMatch.updateFlow(riverNode,terrainTangent);
                        riverNodes[nodeIndex(bestMatch.getPosition().x(), bestMatch.getPosition().z())] = bestMatch;
                    }

                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }


        for (int cellX = -BANDING; cellX < RIVER_GRID_SIZE + BANDING; cellX++) {
            for (int cellZ = -BANDING; cellZ < RIVER_GRID_SIZE + BANDING; cellZ++) {
                if (!(cellX >= 0 && cellX < RIVER_GRID_SIZE && cellZ >= 0 && cellZ < RIVER_GRID_SIZE)) {
                    int xO;
                    int zO;
                    if (cellX < 0){
                        xO = -1;
                    }else if (cellX > RIVER_GRID_SIZE){
                        xO = 1;
                    }else{
                        xO = 0;
                    }
                    if (cellZ < 0){
                        zO = -1;
                    }else if (cellZ > RIVER_GRID_SIZE){
                        zO = 1;
                    }else {
                        zO = 0;
                    }
                    RegionPos regPos = new RegionPos(pos.x() + xO, pos.z() + zO);
                    int xPos = RegionPos.toBlockCoord(regPos.x()) + nodePosition(cellX);
                    int zPos = RegionPos.toBlockCoord(regPos.z()) + nodeToBlock(cellZ);
                    RiverNode node = neighbors.get(regPos).getNbtData().rivers().getNodeAtBlock(xPos, zPos);
                    if (node != null && node.getFlowTo() != RiverNode.WorldPosition.OUT_OF_BOUNDS) {


                        double nodeFlowX = node.getFlowTo().x();
                        double nodeFlowZ = node.getFlowTo().z();
                        if (nodeFlowX > pos.getMinBlockX() && nodeFlowX < pos.getMaxBlockX()
                                && nodeFlowZ >  pos.getMinBlockZ() && nodeFlowZ < pos.getMaxBlockZ()) {

                            RiverNode riverFlowTo = getNodeAt(node.getFlowTo());

                            Vec2 terrainTangent = Vec2.ZERO;
                            float elevation0 = riverFlowTo.getElevation();
                            for (int xOff = -1; xOff <= 1; xOff++) {
                                for (int zOff = -1; zOff <= 1; zOff++) {
                                    if (xOff == 0 && zOff == 0) continue;
                                    int x = Mth.floor(riverFlowTo.getWorldX() + (xOff * offset));
                                    int z = Mth.floor(riverFlowTo.getWorldZ() + (zOff * offset));
                                    RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(x), RegionPos.fromBlockCoord(z));
                                    WorldRegion region = thisRegion.getPos().equals(pos0) ? thisRegion : neighbors.get(pos0);
                                    float distance = (xOff != 0 && zOff != 0) ? 1.4f : 1.0f;

                                    float elevationPoint = region.layers().getHeightmapBasic().upsampleFromNeighbors(x,z,neighbors);
                                    float elevationDifference = (elevation0 - elevationPoint) / distance;
                                    terrainTangent = terrainTangent.add(new Vec2(
                                            xOff * elevationDifference, zOff * elevationDifference
                                    ));
                                }
                            }
                            riverFlowTo.updateFlow(node,terrainTangent);
                            this.riverNodes[nodeIndex(riverFlowTo.getPosition())] = riverFlowTo;
                        }
                    }

                }
            }
        }
    }

    public int nodeIndex(RiverNode.GridPoint position) {
        return nodeIndex(position.x(), position.z());
    }

    public void postProcessRiverCliffsAndFlow(RegionPos pos, Map<RegionPos, WorldRegion> neighbors,WorldRegion thisRegion, long seed) {
        int offset = 8;
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

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
                    RiverPoint river1 = RiverPoint.bendPoint(river0, river4, 0.25f,thisRegion.layers().record().riverSway().getNoise(),thisRegion.layers().record().riverRandom().getNoise(), maxDiversion);
                    RiverPoint river2 = RiverPoint.bendPoint(river0, river4, 0.50f,thisRegion.layers().record().riverSway().getNoise(),thisRegion.layers().record().riverRandom().getNoise(), maxDiversion);
                    RiverPoint river3 = RiverPoint.bendPoint(river0, river4, 0.75f,thisRegion.layers().record().riverSway().getNoise(),thisRegion.layers().record().riverRandom().getNoise(), maxDiversion);
                    for (int d = 0; d < distTo; d++) {
                        float delta = d / distTo;
                        RiverPoint p0 = interpolated4PointRiver(river0,river1,river2,river3,river4,delta,thisRegion.layers().record().riverSway().getNoise(),lastElevation);
                        float interpX = p0.worldX();
                        float interpZ = p0.worldZ();


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
                        riverNode.setCliff(new RiverNode.Cliff(startDelta, endDelta,startElevation, endElevation));
                    }
                }
                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }
    }


    public static RiverPoint interpolated4PointRiver(RiverPoint river0, RiverPoint river1, RiverPoint river2, RiverPoint river3,RiverPoint river4, float delta, Noise2D noise, float lastElevation) {
        RiverPoint first;
        RiverPoint second;
        if (delta < 0.25f){
            first = river0;
            second = river1;
        }else if (delta < 0.5f){
            first = river1;
            second = river2;
        }else if (delta < 0.75f){
            first = river2;
            second = river3;
        }else{
            first = river3;
            second = river4;
        }
        float delta0 = (delta * 4.0f) % 1.0f;
        return RiverPoint.interpolated(first, second, delta0,noise,delta,river0,river4, lastElevation);
    }

    public void generatePointsForAllRivers(RegionPos pos, WorldRegion region, Map<RegionPos, WorldRegion> neighbors, long seed) {
        for (int cellX = 0; cellX < RIVER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < RIVER_GRID_SIZE; cellZ++) {
                RiverNode riverNode = riverNodes[nodeIndex(cellX, cellZ)];

                riverNode.generatePoints(pos,region,neighbors,seed);

                riverNodes[nodeIndex(cellX, cellZ)] = riverNode;
            }
        }
        this.generatedPoints = true;
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
