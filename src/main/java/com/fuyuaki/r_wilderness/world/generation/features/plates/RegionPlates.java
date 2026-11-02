package com.fuyuaki.r_wilderness.world.generation.features.plates;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.Optional;

public class RegionPlates {
    public static final int MAJOR_PLATE_GRID_SIZE = 4;
    public static final int MAJOR_PLATE_GRID_SQUARED = MAJOR_PLATE_GRID_SIZE * MAJOR_PLATE_GRID_SIZE;
    public static final int MINOR_PLATE_GRID_SIZE = 32;
    public static final int MINOR_PLATE_GRID_SQUARED =  MINOR_PLATE_GRID_SIZE * MINOR_PLATE_GRID_SIZE;

    public static final int MAJOR_PLATE_SIZE = RegionPos.REGION_SIZE / MAJOR_PLATE_GRID_SIZE;
    public static final int MAJOR_PLATE_BITS = Integer.numberOfTrailingZeros(MAJOR_PLATE_SIZE);

    public static final int MINOR_PLATE_SIZE = RegionPos.REGION_SIZE / MINOR_PLATE_GRID_SIZE;
    public static final int MINOR_PLATE_BITS = Integer.numberOfTrailingZeros(MINOR_PLATE_SIZE);

    private MajorTectonicPlate[] majorPlates = new MajorTectonicPlate[MAJOR_PLATE_GRID_SQUARED];
    private TectonicPlateNode[] plateNodes = new TectonicPlateNode[MINOR_PLATE_GRID_SQUARED];
    private final RegionNoisesRecord record;


    public RegionPlates(MajorTectonicPlate[] majorPlates, TectonicPlateNode[] plateNodes, RegionNoisesRecord record){
        this.majorPlates = Arrays.copyOf(majorPlates,majorPlates.length);
        this.plateNodes = Arrays.copyOf(plateNodes,plateNodes.length);
        this.record = record;
    }

    public RegionPlates(RegionPos pos, long seed, RegionNoisesRecord record) {
        this.record = record;
        for (int x = 0; x < MAJOR_PLATE_GRID_SIZE; x++) {
            for (int z = 0; z < MAJOR_PLATE_GRID_SIZE; z++) {
                majorPlates[majorIndex(x,z)] = new MajorTectonicPlate(pos,x,z,seed);
            }
        }
        generateMinorPlates(pos,this.majorPlates,seed);
    }

    private void generateMinorPlates(RegionPos pos, MajorTectonicPlate[] majorPlates, long seed) {

        for (int cellX = 0; cellX < MINOR_PLATE_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < MINOR_PLATE_GRID_SIZE; cellZ++) {
                int truePosX = Mth.floor(RegionPos.toBlockCoord(pos.x()) + TectonicPlateNode.worldPosition(cellX));
                int truePosZ = Mth.floor(RegionPos.toBlockCoord(pos.z()) + TectonicPlateNode.worldPosition(cellZ));
                long hash = TectonicPlateNode.coordinateHash(truePosX, truePosZ, seed);
                float centerX = TectonicPlateNode.getSaltedFloat(hash, 0) * 0.7f + 0.15f;
                float centerZ = TectonicPlateNode.getSaltedFloat(hash, 1) * 0.7f + 0.15f;

                MajorTectonicPlate parent = fetchOrGenerateParent(majorPlates,pos,seed,cellX,cellZ,truePosX,truePosZ);

                plateNodes[nodeIndex(cellX,cellZ)] = new TectonicPlateNode(pos,cellX,cellZ,this.record,centerX,centerZ,hash,parent);
            }
        }
    }

    private MajorTectonicPlate fetchOrGenerateParent(MajorTectonicPlate[] majorPlates, RegionPos pos, long seed, int cellX, int cellZ, int truePosX, int truePosZ) {
        int xMaj = cellX >> 4;
        int zMaj = cellZ >> 4;
        MajorTectonicPlate bestParent = null;
        float distToParent = Float.MAX_VALUE;
        for (int xO = -1; xO <= 1; xO++) {
            for (int zO = -1; zO <= 1; zO++) {
                int xMajor = xMaj + xO;
                int zMajor = zMaj + zO;
                MajorTectonicPlate possibleParent;
                if (xMajor >= 0 && xMajor < MAJOR_PLATE_GRID_SIZE && zMajor >= 0 && zMajor < MAJOR_PLATE_GRID_SIZE) {
                    possibleParent = majorPlates[majorIndex(xMajor,zMajor)];
                } else {
                    int regionOffsetX = Math.floorDiv(xMajor, MAJOR_PLATE_GRID_SIZE);
                    int regionOffsetZ = Math.floorDiv(zMajor, MAJOR_PLATE_GRID_SIZE);
                    int wrappedXMajor = Math.floorMod(xMajor, MAJOR_PLATE_GRID_SIZE);
                    int wrappedZMajor = Math.floorMod(zMajor, MAJOR_PLATE_GRID_SIZE);
                    RegionPos neighborRegion = new RegionPos(pos.x() + regionOffsetX, pos.z() + regionOffsetZ);
                    possibleParent = new MajorTectonicPlate(neighborRegion, wrappedXMajor, wrappedZMajor, seed);
                }
                float distance = possibleParent.getWeightedDistance(truePosX,truePosZ);
                if (distance < distToParent) {
                    bestParent = possibleParent;
                    distToParent = distance;
                }
            }
        }
        return bestParent;
    }

    public MajorTectonicPlate getMajor(int index){
        return this.majorPlates[index];
    }
    public MajorTectonicPlate getMajor(int x, int z){
        return this.getMajor(majorIndex(x,z));
    }
    public MajorTectonicPlate getMajorAtBlock(int xPos, int zPos){
        int x = majorPlatePosition(xPos);
        int z = majorPlatePosition(zPos);
        return this.getMajor(majorIndex(x,z));
    }

    public TectonicPlateNode getNode(int index){
        return this.plateNodes[index];
    }
    public TectonicPlateNode getNode(int x, int z){
        return this.getNode(nodeIndex(x,z));
    }
    public TectonicPlateNode getNodeAtBlock(int xPos, int zPos){
        int x = Math.floorMod(xPos,RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos,RegionPos.REGION_SIZE);
        int cellX = nodePlatePosition(x);
        int cellZ = nodePlatePosition(z);
        return this.getNode(nodeIndex(cellX,cellZ));
    }
    public static int majorIndex(int x, int z){
        return x * MAJOR_PLATE_GRID_SIZE + z;
    }

    public static int nodeIndex(int x, int z){
        return x * MINOR_PLATE_GRID_SIZE + z;
    }


    public float getPlateDistance(int x, int z, TectonicPlateNode node){
        return node.getWeightedDistance(x,z);
    }

    public static int majorPlatePosition(int position){
        return position >> MAJOR_PLATE_BITS;
    }
    public static int nodePlatePosition(int position){
        return position >> MINOR_PLATE_BITS;
    }
    public static int majorPlateMinBlock(int position){
        return  position << MAJOR_PLATE_BITS;
    }
    public static int nodePlateMinBlock(int position){
        return position << MINOR_PLATE_BITS;
    }


    public static RegionPlates readFrom(CompoundTag tag,RegionNoisesRecord record) {
        Optional<CompoundTag> platesTag = tag.getCompound("plates");
        if (platesTag.isEmpty()) {
            return null;
        }
        CompoundTag platesMapTag = platesTag.get();
        CompoundTag majorPlatesTag = platesMapTag.getCompound("major").get();
        MajorTectonicPlate[] majorPlateMap = new MajorTectonicPlate[MAJOR_PLATE_GRID_SQUARED];
        for (int x = 0; x < MAJOR_PLATE_GRID_SIZE; x++) {
            for (int z = 0; z < MAJOR_PLATE_GRID_SIZE; z++) {
                CompoundTag plate = majorPlatesTag.getCompound("plate_" + majorIndex(x,z)).get();
                majorPlateMap[majorIndex(x,z)] = MajorTectonicPlate.read(plate);
            }
        }
        CompoundTag nodeMapTag = platesMapTag.getCompound("nodes").get();
        TectonicPlateNode[] nodeMap = new TectonicPlateNode[MINOR_PLATE_GRID_SQUARED];
        for (int x = 0; x < MINOR_PLATE_GRID_SIZE; x++) {
            for (int z = 0; z < MINOR_PLATE_GRID_SIZE; z++) {
                CompoundTag node = nodeMapTag.getCompound("node_" + nodeIndex(x,z)).get();
                nodeMap[nodeIndex(x,z)] = TectonicPlateNode.read(node);
            }
        }
        return new RegionPlates(majorPlateMap,nodeMap,record);
    }

    public void put(CompoundTag tag) {
        CompoundTag platesMapTag = new CompoundTag();
        CompoundTag majorPlatesTag = new CompoundTag();
        for (int x = 0; x < MAJOR_PLATE_GRID_SIZE; x++) {
            for (int z = 0; z < MAJOR_PLATE_GRID_SIZE; z++) {
                CompoundTag plate = new CompoundTag();
                this.majorPlates[majorIndex(x,z)].write(plate);
                majorPlatesTag.put("plate_" + majorIndex(x,z), plate);
            }
        }
        CompoundTag nodeMapTag = new CompoundTag();
        for (int x = 0; x < MINOR_PLATE_GRID_SIZE; x++) {
            for (int z = 0; z < MINOR_PLATE_GRID_SIZE; z++) {
                CompoundTag plate = new CompoundTag();
                this.plateNodes[nodeIndex(x,z)].write(plate);
                nodeMapTag.put("node_" + nodeIndex(x,z), plate);
            }
        }
        platesMapTag.put("nodes", nodeMapTag);
        platesMapTag.put("major", majorPlatesTag);
        tag.put("plates", platesMapTag);
    }

    public TectonicPlateNode closestNode(int xPos, int zPos) {
        int x = Math.floorMod(xPos,RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos,RegionPos.REGION_SIZE);
        int nodeX = nodePlatePosition(x);
        int nodeZ = nodePlatePosition(z);
        return this.getNode(nodeX, nodeZ);
    }

    public MajorTectonicPlate[] getMajorPlates() {
        return majorPlates;
    }

    public TectonicPlateNode[] getPlateNodes() {
        return plateNodes;
    }
}
