package com.fuyuaki.r_wilderness.world.generation.hydrology;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

public class WaterCell extends SavedData {

    public final CellCoordinates position;
    private boolean generated = false;
    private List<WaterBody> waterBodies = new ArrayList<>();
    private List<WaterBody> overlappingBodies = new ArrayList<>();
    private Step step = Step.EMPTY;


    public WaterCell(CellCoordinates position) {
        this.position = position;
    }

    public WaterCell(CellCoordinates position,List<WaterBody> bodiesList,List<WaterBody> overlapping) {
        this.position = position;
        this.waterBodies = bodiesList;
        this.overlappingBodies = overlapping;
        this.setGenerated();
    }

    public void setStep(Step step){
        this.step = step;
    }

    public void ensureLoaded(HydrologyPreloader loader){
        for (int xOff = -1; xOff < 1; xOff++){
            for (int zOff = -1; zOff < 1; zOff++){
                if(!(xOff == 0 && zOff == 0)){
                    CellCoordinates newCoord = position.withOffset(xOff,zOff);
                    if ((loader.has(newCoord) && !loader.isGenerated(newCoord)) || !loader.has(newCoord)){
                        loader.generate(newCoord);
                    }
                }
            }
        }
        this.setStep(Step.LOADED);
    }

    public void addOverlapping(WaterBody body){
        this.overlappingBodies.add(body);
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated() {
        this.generated = true;
    }

    public void clear() {
        this.generated = false;
        this.waterBodies.clear();
    }

    public List<WaterBody> getWaterBodies() {
        return waterBodies;
    }

    public void generate() {
        this.setStep(Step.GENERATED);
    }


    public static class CellCoordinates {

        public static final CellCoordinates ZERO = new CellCoordinates(0,0);
        private static final int CELL_SECTION_SIZE = 512;

        public final int x;
        public final int z;

        public CellCoordinates(int xPos, int zPos) {
            this.x = xPos;
            this.z = zPos;
        }

        public CellCoordinates(long packedPos) {
            this.x = (int) packedPos;
            this.z = (int) (packedPos >> 32);
        }

        public ChunkPos minChunkPos() {
            return new ChunkPos(sectionX(), sectionZ());
        }

        public ChunkPos maxChunkPos() {
            return new ChunkPos(sectionX(CELL_SECTION_SIZE), sectionZ(CELL_SECTION_SIZE));
        }

        public ChunkPos midChunkPos() {
            return new ChunkPos(sectionX(CELL_SECTION_SIZE / 2), sectionZ(CELL_SECTION_SIZE / 2));
        }

        public boolean contains(ChunkPos pos) {
            return pos.x >= this.sectionX()
                    && pos.z >= this.sectionZ()
                    && pos.x <= this.sectionX(CELL_SECTION_SIZE)
                    && pos.z <= this.sectionZ(CELL_SECTION_SIZE);
        }

        public int sectionX(int offset) {
            return x << 9 + offset;
        }

        public int sectionZ(int offset) {
            return z << 9 + offset;
        }

        public int sectionX() {
            return x << 9;
        }

        public int sectionZ() {
            return z << 9;
        }

        public static CellCoordinates fromChunk(ChunkPos pos){
            return new CellCoordinates(
                    pos.x >> 9,
                    pos.z >> 9
            );
        }

        public static int getX(long chunkAsLong) {
            return (int) (chunkAsLong & 4294967295L);
        }

        public static int getZ(long chunkAsLong) {
            return (int) (chunkAsLong >>> 32 & 4294967295L);
        }

        @Override
        public String toString() {
            return "[" + this.x + ", " + this.z + "]";
        }

        public static long asLong(int x, int z) {
            return x & 4294967295L | (z & 4294967295L) << 32;
        }

        public long toLong() {
            return asLong(this.x, this.z);
        }


        @Override
        public int hashCode() {
            return hash(this.x, this.z);
        }

        public static int hash(int x, int z) {
            int i = 1664525 * x + 1013904223;
            int j = 1664525 * (z ^ -559038737) + 1013904223;
            return i ^ j;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else {
                return other instanceof CellCoordinates position && this.x == position.x && this.z == position.z;
            }
        }

        public CellCoordinates withOffset(int xOff, int zOff) {
            return new CellCoordinates(this.x + xOff, this.z + zOff);
        }
    }

    public enum Step{
        EMPTY,
        GENERATED,
        LOADED
    }
}
