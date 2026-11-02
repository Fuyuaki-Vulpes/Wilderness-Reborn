package com.fuyuaki.r_wilderness.world.generation.features.hydrology;

import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
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

public class RegionWaterBodies {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int WATER_GRID_SIZE = 32;
    public static final int WATER_GRID_SQUARED = WATER_GRID_SIZE * WATER_GRID_SIZE;
    public static final int WATER_SIZE = RegionPos.REGION_SIZE / WATER_GRID_SIZE;
    public static final int WATER_BITS = Integer.numberOfTrailingZeros(WATER_SIZE);


    public static final int BLOCK_TO_WATER_MASK = WATER_SIZE - 1;
    private WaterBody[] waterBodies = new WaterBody[WATER_GRID_SQUARED];
    private final RegionNoisesRecord record;

    public RegionWaterBodies(WaterBody[] waterBodies, RegionNoisesRecord record) {
        this.waterBodies = waterBodies;
        this.record = record;
    }

    public RegionWaterBodies(RegionNoisesRecord record) {
        this.record = record;
    }


    public void generateWaterBodies(RegionPos pos, long seed, WorldRegion worldRegion) {
        for (int cellX = 0; cellX < WATER_GRID_SIZE; cellX++) {
            for (int cellZ = 0; cellZ < WATER_GRID_SIZE; cellZ++) {
                int truePosX = RegionPos.toBlockCoord(pos.x()) + (cellX * RegionPos.REGION_SIZE / RegionWaterBodies.WATER_GRID_SIZE);
                int truePosZ = RegionPos.toBlockCoord(pos.z()) + ((cellZ * RegionPos.REGION_SIZE) / RegionWaterBodies.WATER_GRID_SIZE);
                long hash = WaterBody.coordinateHash(truePosX, truePosZ, seed);
                float centerX = WaterBody.getSaltedFloat(hash, 0) * 0.7f + 0.15f;
                float centerZ = WaterBody.getSaltedFloat(hash, 1) * 0.7f + 0.15f;

                this.waterBodies[bodyIndex(cellX, cellZ)] = new WaterBody(pos, cellX, cellZ,this.record, centerX, centerZ, hash, worldRegion);
            }
        }
    }


    public WaterBody getBodyAt(RiverNode.WorldPosition position) {
        return getBodyAtBlock(Mth.floor(position.x()),Mth.floor(position.z()));
    }

    public WaterBody getBodyAt(RiverNode.WorldPosition position,RegionPos pos, Map<RegionPos, WorldRegion> neighbors) {
        if (position != RiverNode.WorldPosition.OUT_OF_BOUNDS && position.x() != RiverNode.WorldPosition.OUT_OF_BOUNDS.x()) {

            RegionPos pos0 = new RegionPos(RegionPos.fromBlockCoord(Mth.floor(position.x())), RegionPos.fromBlockCoord(Mth.floor(position.z())));
            WaterBody water;
            if (pos0.x() == pos.x() && pos0.z() == pos.z()) {
                water = this.getBodyAt(position);
            } else {
                water = neighbors.get(pos0).getNbtData().waters().getBodyAt(position);
            }

            return water;
        }
        else return null;
    }


    public static int bodyIndex(int x, int z) {
        return x * WATER_GRID_SIZE + z;
    }

    public static int bodyPosition(int position) {
        return position >> WATER_BITS;
    }
    public static int bodyToBlock(int cell) {
        return cell << WATER_BITS;
    }

    public void update(int index, WaterBody waterBody) {
        this.waterBodies[index] = waterBody;
    }

    public static int localBlockInWaterGrid(int blockPos) {
        return blockPos & BLOCK_TO_WATER_MASK;
    }

    public WaterBody getBody(int index) {
        return this.waterBodies[index];
    }

    public WaterBody getBody(int x, int z) {
        return this.getBody(bodyIndex(x, z));
    }

    public WaterBody getBodyAtBlock(int xPos, int zPos) {
        int x = Math.floorMod(xPos, RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos, RegionPos.REGION_SIZE);
        int cellX = bodyPosition(x);
        int cellZ = bodyPosition(z);
        return this.getBody(bodyIndex(cellX, cellZ));
    }

    public int getBodyIndexForBlock(int xPos, int zPos) {
        int x = Math.floorMod(xPos, RegionPos.REGION_SIZE);
        int z = Math.floorMod(zPos, RegionPos.REGION_SIZE);
        int cellX = bodyPosition(x);
        int cellZ = bodyPosition(z);
        return bodyIndex(cellX, cellZ);
    }

    public static RegionWaterBodies readFrom(CompoundTag tag, RegionNoisesRecord record) throws IOException {
        Optional<CompoundTag> optionalWatersTag = tag.getCompound("waters");
        if (optionalWatersTag.isEmpty()) {
            return new RegionWaterBodies(record);
        }
        CompoundTag watersTag = optionalWatersTag.get();
        WaterBody[] bodyMap = new WaterBody[WATER_GRID_SQUARED];
        for (int x = 0; x < WATER_GRID_SIZE; x++) {
            for (int z = 0; z < WATER_GRID_SIZE; z++) {
                CompoundTag body = watersTag.getCompound("body_" + bodyIndex(x, z)).orElse(null);
                bodyMap[bodyIndex(x, z)] = body == null ? null : WaterBody.read(body);
            }
        }
        return new RegionWaterBodies(bodyMap, record);
    }

    public void put(CompoundTag tag) throws Throwable{
        CompoundTag bodyMapTag = new CompoundTag();
        if (this.waterBodies == null || this.waterBodies[0] == null) {
            return;
        }
        for (int x = 0; x < WATER_GRID_SIZE; x++) {
            for (int z = 0; z < WATER_GRID_SIZE; z++) {
                CompoundTag water = new CompoundTag();
                int index = bodyIndex(x, z);
                if (this.waterBodies[index] == null) {
                    continue;
                }
                try {
                    this.waterBodies[index].write(water);
                    bodyMapTag.put("body_" + index, water);
                } catch (Throwable e) {
                    LOGGER.error("Failed to insert body {} into tag {}",index,bodyMapTag, e);
                }
            }
        }
        if (bodyMapTag.isEmpty()) throw new Throwable("Tag cant be empty");
        tag.put("waters", bodyMapTag);
    }

}
