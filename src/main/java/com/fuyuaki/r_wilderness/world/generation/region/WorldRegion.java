package com.fuyuaki.r_wilderness.world.generation.region;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.data.*;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldRegion {
    private AtomicInteger queries = new AtomicInteger(0);
    private volatile SerializableRegionData nbtData;
    private volatile RegionLayers layers;
    private RegionPhase status;
    private boolean hasBeenSaved = false;
    private boolean readyToLoad = false;

    public WorldRegion(RegionNoisesRecord record, RegionPos pos, long seed) {
        this.hasBeenSaved = true;
        this.status = RegionPhase.EMPTY;
        this.nbtData = SerializableRegionData.makeNew(pos, seed,record);
        this.layers = RegionLayers.makeEmpty(record);
    }

    public WorldRegion(SerializableRegionData nbtData, RegionLayers layers) {
        this.status = nbtData.status();
        this.nbtData = nbtData;
        this.layers = layers;
    }

    public int queryCount() {
        return queries.get();
    }

    public void query(){
        queries.getAndIncrement();
    }
    public void release(){
        queries.decrementAndGet();
    }

    public void setNbtData(SerializableRegionData nbtData) {
        this.nbtData = nbtData;
    }
    public SerializableRegionData getNbtData() {
        return this.nbtData;
    }

    public void setLayers(RegionLayers layers) {
        this.layers = layers;
    }

    public RegionLayers layers() {
        return layers;
    }

    public boolean hasBeenSaved() {
        return hasBeenSaved;
    }

    public void setSaved() {
        this.hasBeenSaved = true;
    }
    public void updateStatus(RegionPhase status) {
        if (status.ordinal() < this.getPhase().ordinal()) {
            throw new IllegalStateException(
                    "Region " + getPos() + " status moved backward: "
                            + getPhase() + " -> " + status
            );
        }

        this.nbtData.setStatus(status);
        this.status = status;
        this.hasBeenSaved = false;
    }
    public RegionPhase getPhase() {
        return this.status;
    }


    public RegionPos getPos(){
        return this.nbtData.position();
    }

    @Override
    public String toString() {
        return this.getPos() + ":" + this.getPhase();
    }

    public void writeData(DataOutputStream output) throws IOException {
        layers.write(output);
    }
    public static WorldRegion read(RegionNoisesRecord record, DataInputStream inputStream,RegionManager manager,RegionPos pos) throws IOException {
        SerializableRegionData nbt = SerializableRegionData.read(inputStream,record);

        RegionLayers layers = RegionLayers.read(record, inputStream);
        return new WorldRegion(nbt, layers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WorldRegion other)) return false;
        return this.getPos().x() == other.getPos().x() && this.getPos().z() == other.getPos().z() && this.status.equals(other.status);
    }

    public float getDistanceTo(LivingEntity player) {
        float xDist = 0.0f;
        float zDist = 0.0f;
        float playerX = (float) player.getX();
        float playerZ = (float) player.getZ();
        if (playerX >= this.getPos().getMaxBlockX()) {
            xDist = playerX - this.getPos().getMinBlockX();

        } else if (playerX <= this.getPos().getMinBlockX()) {
            xDist =  this.getPos().getMinBlockX() - playerX;
        }
        if (playerZ >= this.getPos().getMaxBlockZ()) {
            zDist = playerZ - this.getPos().getMinBlockZ();
        }else if (playerZ <= this.getPos().getMinBlockZ()) {
            zDist =  this.getPos().getMinBlockZ() - playerZ;
        }
        return Mth.sqrt(xDist * xDist + zDist * zDist);
    }

    public boolean isReadyToLoad() {
        return this.readyToLoad;
    }

    public void setReadyToLoad() {
        this.readyToLoad = true;
    }
}