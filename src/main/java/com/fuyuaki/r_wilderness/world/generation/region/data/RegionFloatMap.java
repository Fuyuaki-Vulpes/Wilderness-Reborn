package com.fuyuaki.r_wilderness.world.generation.region.data;

import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayerTypes;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.AtomicDoubleArray;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RegionFloatMap {
    private Status status;
    private final int index;
    private float[] map;
    private static final int NOISE_THREAD_COUNT = 8;
    private final int resolution;
    private final int scale;
    private final Noise2D noiseMap;



    public RegionFloatMap(int index, Noise2D noiseMap) {
        this.noiseMap = noiseMap;
        this.resolution = Resolution.R_512;
        this.scale = RegionPos.REGION_SIZE / this.resolution;
        this.status = Status.EMPTY;
        this.map = null;
        this.index = index;
    }

    public RegionFloatMap(int index, int resolution, Noise2D noiseMap) {
        this.resolution = resolution;
        this.noiseMap = noiseMap;
        this.scale = RegionPos.REGION_SIZE / this.resolution;
        this.status = Status.EMPTY;
        this.map = null;
        this.index = index;
    }

    public RegionFloatMap(int index, Status status, float[] map, Noise2D noiseMap) {
        this.noiseMap = noiseMap;
        this.resolution = Resolution.R_512;
        this.scale = RegionPos.REGION_SIZE / this.resolution;
        this.status = status;
        this.index = index;
        this.map = map;
    }

    public RegionFloatMap(int index, Status status, float[] map, int resolution, Noise2D noiseMap) {
        this.resolution = resolution;
        this.noiseMap = noiseMap;
        this.scale = RegionPos.REGION_SIZE / this.resolution;
        this.status = status;
        this.index = index;
        this.map = map;
    }

    public RegionFloatMap(RegionLayerTypes layer, Noise2D noise) {
        this(layer.getIndex(), layer.getDefaultResolution(), noise);
    }
    public int getIndex() {
        return index;
    }

    public static RegionFloatMap createMap(Noise2D noiseMap, RegionPos regionPos, RegionFloatMap emptyMap) {

        int minX = RegionPos.toBlockCoord(regionPos.x());
        int minZ = RegionPos.toBlockCoord(regionPos.z());
        int resolution = emptyMap.getResolution();
        int scale = emptyMap.getScale();
        float[] map = new float[resolution * resolution];
        CompletableFuture<Void>[] futures = new CompletableFuture[NOISE_THREAD_COUNT];
        int samples = resolution / NOISE_THREAD_COUNT;
        for (int thread = 0; thread < NOISE_THREAD_COUNT; thread++) {
            int startX = thread * samples;
            int endX = startX + samples;
            futures[thread] = CompletableFuture.runAsync(() -> {
                for (int xPos = startX; xPos < endX; xPos++) {
                    int xAdapted = xPos * resolution;
                    for (int zPos = 0; zPos < resolution; zPos++) {
                        map[xAdapted + zPos] = (float) noiseMap.noise(xPos * scale + minX, zPos * scale + minZ);
                    }
                }
            });
        }
        CompletableFuture.allOf(futures).join();
        emptyMap.updateMap(map);
        emptyMap.setStatus(Status.COMPLETE);
        return emptyMap;
    }



    public int getResolution() {
        return resolution;
    }

    public int getScale() {
        return scale;
    }

    public void updateMap(float[] newMap) {
        this.map = newMap;
    }

    public void updateMap(AtomicDoubleArray newMap) {
        this.map = new float[newMap.length()];
        for (int i = 0; i < newMap.length(); i++){
            this.map[i] = (float) newMap.get(i);
        }
    }

    public void updateMap(int index, float value) {
        this.map[index] = value;
    }
    public void updateMap(int x, int z, float value) {
        this.map[x * this.resolution + z] = value;
    }

    public float[] getMap() {
        return map;
    }

    public float get(int index){
        if (this.map == null) return Float.NaN;
        return this.map[index];
    }

    public float get(int x, int z){
        return this.get(x * this.resolution + z);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isComplete() {
        return this.status == Status.COMPLETE;
    }


    public static RegionFloatMap read(DataInputStream inputStream, RegionLayerTypes type, Noise2D noiseMap) throws IOException {
        String name = type.getSerializedName();

        String name2 = new String(inputStream.readNBytes(name.getBytes(StandardCharsets.UTF_8).length));

        int resolution;
        float[] map;
        if (!name2.equals(name)){
            throw new IOException("Map name for region " + name + " is does not match with the found name: " + name2);
        }
        int b = inputStream.readUnsignedByte();
        if (b == 0x8F){
            resolution = type.getDefaultResolution();
            int b2 = inputStream.readUnsignedByte();
            if(b2 == 0x88){
                map = fillMap(inputStream,resolution);
            }else {
                map = null;
            }
        }else {
            throw new IOException("Invalid map format");
        }
        RegionFloatMap regionFloatMap = new RegionFloatMap(type.getIndex(),Status.COMPLETE, map, resolution, noiseMap);
        return regionFloatMap;
    }

    private static float[] fillMap(DataInputStream inputStream, int resolution) throws IOException {
        float[] map = new float[resolution * resolution];
        for (int i = 0; i < map.length; i++){
            map[i] = inputStream.readFloat();
        }
        return map;
    }

    public void write(RegionLayerTypes type, DataOutputStream output) throws IOException {
        String name = type.getSerializedName();
        output.write(name.getBytes(StandardCharsets.UTF_8));

        output.writeByte(0x8F);
        if (this.map == null || this.status != Status.COMPLETE) {
            output.writeByte(0xAA);

            return;
        }
        output.writeByte(0x88);
        for (float f : this.map) {
            output.writeFloat(f);
        }
    }

    public float getGlobal(int x, int z) {
        return this.get(
                Math.floorMod(x, RegionPos.REGION_SIZE) / this.scale,
                Math.floorMod(z, RegionPos.REGION_SIZE) / this.scale
        );
    }

    public float getUpsampled(int x, int z, RegionMemory memory){
        int xLocal = Math.floorMod(x,RegionPos.REGION_SIZE);
        int zLocal = Math.floorMod(z,RegionPos.REGION_SIZE);
        int xScaled =  xLocal / this.scale;
        int zScaled = zLocal / this.scale;
        assert xScaled >= 0 && zScaled >= 0 && xLocal >= 0 && zLocal >= 0;
        float nw;
        float sw;
        float ne;
        float se;
        nw = this.get(xScaled,zScaled);
        int limit = this.resolution - 1;
        if (xScaled >= limit || zScaled >= limit){
            int xCorner = x - (Mth.floor(xLocal) % this.scale);
            int zCorner = z - (Mth.floor(zLocal) % this.scale);

            int shift = this.scale;

            if (zScaled >= limit){
                sw = (float) this.getOutsideArea(xCorner, zCorner + shift,memory);
            }else{
                sw = this.get(xScaled,zScaled + 1);
            }
            if (xScaled >= limit){
                ne = (float) this.getOutsideArea(xCorner + shift, zCorner,memory);
            }else {
                ne = this.get(xScaled + 1,zScaled);
            }
            se = (float) this.getOutsideArea(xCorner + shift, zCorner + shift,memory);


        }else{
            sw = this.get(xScaled,zScaled + 1);
            ne = this.get(xScaled + 1,zScaled);
            se = this.get(xScaled + 1,zScaled + 1);
        }
        float deltaX = (float) (Mth.floor(xLocal) % this.scale) /  this.scale;
        float deltaZ = (float) (Mth.floor(zLocal) % this.scale) /  this.scale;
        return (float) Mth.lerp2(
                deltaX, deltaZ,
                nw,ne,
                sw,se
        );
    }

    public float getUpsampledAtPhase(int x, int z, RegionMemory memory, RegionPhase phase){
        int xLocal = Math.floorMod(x,RegionPos.REGION_SIZE);
        int zLocal = Math.floorMod(z,RegionPos.REGION_SIZE);
        int xScaled =  xLocal / this.scale;
        int zScaled = zLocal / this.scale;
        assert xScaled >= 0 && zScaled >= 0 && xLocal >= 0 && zLocal >= 0;
        float nw;
        float sw;
        float ne;
        float se;
        nw = this.get(xScaled,zScaled);
        int limit = this.resolution - 1;
        if (xScaled >= limit || zScaled >= limit){
            int xCorner = x - (Mth.floor(xLocal) % this.scale);
            int zCorner = z - (Mth.floor(zLocal) % this.scale);

            int shift = this.scale;

            if (zScaled >= limit){
                sw = (float) this.getOutsideAreaAtPhase(xCorner, zCorner + shift,memory,phase);
            }else{
                sw = this.get(xScaled,zScaled + 1);
            }
            if (xScaled >= limit){
                ne = (float) this.getOutsideAreaAtPhase(xCorner + shift, zCorner,memory,phase);
            }else {
                ne = this.get(xScaled + 1,zScaled);
            }
            se = (float) this.getOutsideAreaAtPhase(xCorner + shift, zCorner + shift,memory,phase);


        }else{
            sw = this.get(xScaled,zScaled + 1);
            ne = this.get(xScaled + 1,zScaled);
            se = this.get(xScaled + 1,zScaled + 1);
        }
        float deltaX = (Mth.floor(xLocal) % (float) this.scale) / this.scale;
        float deltaZ = (Mth.floor(zLocal) % (float) this.scale) / this.scale;
        return (float) Mth.lerp2(
                deltaX, deltaZ,
                nw,ne,
                sw,se
        );
    }

    private float getOutsideArea(int xCorner, int zCorner, RegionMemory memory) {
        WorldRegion region = memory.getRegionAt(xCorner, zCorner);
        return region.layers().getOfIndex(this.getIndex()).getGlobal(xCorner, zCorner);
    }

    private float getOutsideAreaAtPhase(int xCorner, int zCorner, RegionMemory memory, RegionPhase phase) {
        WorldRegion region = memory.getRegionAt(xCorner, zCorner,phase);
        return region.layers().getOfIndex(this.getIndex()).getGlobal(xCorner, zCorner);
    }

    public Noise2D getNoiseMap() {
        return noiseMap;
    }

    public float upsampleFromNeighbors(int x, int z, Map<RegionPos, WorldRegion> neighbors) {
        int xLocal = Math.floorMod(x,RegionPos.REGION_SIZE);
        int zLocal = Math.floorMod(z,RegionPos.REGION_SIZE);
        int xScaled =  xLocal / this.scale;
        int zScaled = zLocal / this.scale;
        assert xScaled >= 0 && zScaled >= 0 && xLocal >= 0 && zLocal >= 0;
        float nw;
        float sw;
        float ne;
        float se;
        nw = this.get(xScaled,zScaled);
        int limit = this.resolution - 1;
        if (xScaled >= limit || zScaled >= limit){
            int xCorner = x - (Mth.floor(xLocal) % this.scale);
            int zCorner = z - (Mth.floor(zLocal) % this.scale);

            int shift = this.scale;

            if (zScaled >= limit){
                sw = (float) this.getOutsideArea(xCorner, zCorner + shift,neighbors);
            }else{
                sw = this.get(xScaled,zScaled + 1);
            }
            if (xScaled >= limit){
                ne = (float) this.getOutsideArea(xCorner + shift, zCorner,neighbors);
            }else {
                ne = this.get(xScaled + 1,zScaled);
            }
            se = (float) this.getOutsideArea(xCorner + shift, zCorner + shift,neighbors);


        }else{
            sw = this.get(xScaled,zScaled + 1);
            ne = this.get(xScaled + 1,zScaled);
            se = this.get(xScaled + 1,zScaled + 1);
        }
        float deltaX = (Mth.floor(xLocal) % (float) this.scale) /  this.scale;
        float deltaZ = (Mth.floor(zLocal) % (float) this.scale) /  this.scale;
        return (float) Mth.lerp2(
                deltaX, deltaZ,
                nw,ne,
                sw,se
        );

    }

    private float getOutsideArea(int xCorner, int zCorner, Map<RegionPos, WorldRegion> neighbors) {
        WorldRegion region = RegionMemory.getRegionAt(xCorner, zCorner,neighbors);
        if (region == null) {
            throw new NullPointerException("Region is null \n Neighbors: " + neighbors + " X: " + xCorner + " Z: " + zCorner);
        } else if (region.layers().getOfIndex(this.getIndex()) == null) {
            throw new NullPointerException("Region layer of index " + this.getIndex()  + "  null for Region: " + region);
        }
        return region.layers().getOfIndex(this.getIndex()).getGlobal(xCorner, zCorner);
    }

    public enum Status implements StringRepresentable {
        EMPTY("empty"),
        GENERATING("generating"),
        COMPLETE("complete"),
        ;
        private final String name;
        public static final Codec<Status> CODEC = StringRepresentable.fromEnum(Status::values);

        Status(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static Status fromString(String name) {
            for (Status status : Status.values()) {
                if (status.name.equals(name)) return status;
            }
            return Status.valueOf(name);
        }
    }
    public static class Resolution{
        public static final int R_4096 = 4096;
        public static final int R_2048 = 2048;
        public static final int R_1024 = 1024;
        public static final int R_512 = 512;
        public static final int R_256 = 256;
        public static final int R_128 = 128;
        public static final int R_64 = 64;
    }
}
