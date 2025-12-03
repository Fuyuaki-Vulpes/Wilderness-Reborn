package com.fuyuaki.r_wilderness.world.generation.hydrology;

import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HydrologyPreloader {

    private final Map<Long, WaterCell> cacheMap = new ConcurrentHashMap<>();
    private final TerrainParameters parameterMap;

    public HydrologyPreloader(TerrainParameters parameterMap) {
        this.parameterMap = parameterMap;
        this.generate(WaterCell.CellCoordinates.ZERO);
        this.ensureLoaded(ChunkPos.ZERO);
    }

    public void ensureLoaded(ChunkPos pos){
        WaterCell.CellCoordinates coords = WaterCell.CellCoordinates.fromChunk(pos);
        WaterCell cell = this.get(coords);
        if (cell == null){
            cell = this.generate(coords);
        }
        cell.ensureLoaded(this);
    }

    public boolean has(WaterCell.CellCoordinates coordinate){
        return cacheMap.containsKey(coordinate.toLong());
    }

    public TerrainParameters getParameterMap() {
        return parameterMap;
    }
    public HydrologyInfo hydrologyInfo(int x, int z){
        TerrainParameters.Sampled.River riverInfo = this.parameterMap.getRiverSampledAt(x,z);
        return new HydrologyInfo(
                this.parameterMap.yLevelAtWithCache(x,z),
                this.parameterMap.riverRawY(x,z),
                riverInfo.direction(),
                riverInfo.typeA(),
                riverInfo.typeB()
        );
    }

    public WaterCell get(WaterCell.CellCoordinates coordinate){
        return cacheMap.getOrDefault(coordinate.toLong(),null);
    }
    public boolean isGenerated(WaterCell.CellCoordinates coordinate){
        WaterCell cell = cacheMap.getOrDefault(coordinate.toLong(),null);
        if (cell == null) return false;
        return cell.isGenerated();
    }

    public WaterCell generate(WaterCell.CellCoordinates coordinate){
        WaterCell cell = get(coordinate);

        if (cell == null){
            cell = new WaterCell(coordinate);
        }

        cell.generate();
        this.cacheMap.put(coordinate.toLong(), cell);
        return cell;
    }

    public void update(WaterCell toUpdate){
        this.cacheMap.put(toUpdate.position.toLong(), toUpdate);
    }

}
