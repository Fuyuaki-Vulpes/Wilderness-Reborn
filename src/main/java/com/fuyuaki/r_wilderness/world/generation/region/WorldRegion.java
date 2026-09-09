package com.fuyuaki.r_wilderness.world.generation.region;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.data.*;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

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

    public float caveLikelihood(float surfaceY,int x, int y, int z, RegionMemory memory){
        if (y <= WildernessConstants.CAVES_BOTTOM || y > surfaceY){
            return -1.0f;
        }


        float depthBelowSurface = surfaceY - y;
        float surfacePenalty = Math.clamp((16.0f - depthBelowSurface) / 16.0f, 0.0f, 1.0f);

        float stress = this.layers.getTectonicStress().getUpsampled(x,z,memory);
        float volcanicness = this.layers.getVolcanicness().getUpsampled(x,z,memory);
        float rockiness = this.layers.getRockiness().getUpsampled(x,z,memory);

        float ySpaghetti = y * 1.25f;
        float spaghetti1 =  (float) this.layers.record().spaghettiWorm1().noise(x,ySpaghetti,z);
        float spaghetti2 =  (float) this.layers.record().spaghettiWorm2().noise(x,ySpaghetti,z);
        float spaghettiThickness =  (float) this.layers.record().spaghettiThickness().noise(x,ySpaghetti,z);
        float spaghettiRarity =  Math.min((float) this.layers.record().spaghettiRarity().noise(x,ySpaghetti,z),0.0f);
        float spaghettiWorm = (spaghetti1 * spaghetti1) + (spaghetti2 * spaghetti2);

        float spaghettiThreshold = 0.025f + (stress * 0.035f) + (rockiness * 0.005f) + (spaghettiThickness * 0.025f);

        float spaghetti = spaghettiThreshold - spaghettiWorm - (surfacePenalty * 0.05f) + spaghettiRarity;

        float yPore = y * 2.1f;
        float poreValue = (float) this.layers.record().cavePores().noise(x,yPore,z);
        float poreThickness =  (float) this.layers.record().cavePoresThickness().noise(x,yPore,z);
        float poreRarity = Math.min((float) this.layers.record().cavePoreRarity().noise(x,y,z),0.0f);

        float poreThreshold = 0.75f - (poreThickness * 0.15f) - (volcanicness * 0.25f);
        float pores = poreValue - poreThreshold - (surfacePenalty * 0.25f) + poreRarity;

        float yRidge = y * 6.0f;
        float ridgeValue =  1 + (float) Math.min(this.layers.record().ridgeCaves().noise(x,yRidge,z) + 0.01f,0.0f);
        float ridgeThickness =  (float) this.layers.record().ridgeCaveThickness().noise(x,yRidge,z);
        float ridgeRarity =  Math.min((float) this.layers.record().ridgeCaveRarity().noise(x,y,z),0.0f) * 0.1f;

        float ridgeThreshold = 0.15f
                + (ridgeThickness * 0.15f)
                + (stress * 0.25f);
        float ridges = ridgeThreshold - (1 - ridgeValue) - (surfacePenalty * 0.02f) + ridgeRarity;
        float yLimiter;
        if (y > WildernessConstants.CAVES_BOTTOM_CENTER) {
            yLimiter = 0.0f;
        }else{

            float minYLimit = y -WildernessConstants.CAVES_BOTTOM;
            float yGap = WildernessConstants.CAVES_BOTTOM_CENTER - WildernessConstants.CAVES_BOTTOM;
            float yNegDelta = minYLimit / yGap;
            yLimiter = Math.min(1 - yNegDelta,0.0f);
        }

        return Math.max(Math.max(spaghetti,pores),ridges) - yLimiter;
    }

    public double computeBarrierFor(int x, int y, int z, RegionMemory memory) {
        float barrier = (float) this.layers.record().aquiferBarrier().noise(x,y,z);
        return barrier;
    }

    public double computeFloodednessFor(int x, int y, int z, RegionMemory memory) {
        float flood =  (float) this.layers.record().aquiferFloodedness().noise(x,y,z);
        return flood;
    }

    public double computeFluidSpreadFor(int x, int y, int z, RegionMemory memory) {
        float spread =  (float) this.layers.record().aquiferSpread().noise(x,y,z);
        return spread;
    }

    public BlockState computeFluidTypeFor(int x, int y, int z, Aquifer.FluidStatus globalFluid, RegionMemory memory) {
        float lava = (float) this.layers.record().aquiferLava().noise(x,y,z);
        return lava >= 0.3f ? Blocks.LAVA.defaultBlockState() :  globalFluid.fluidType();
    }
}