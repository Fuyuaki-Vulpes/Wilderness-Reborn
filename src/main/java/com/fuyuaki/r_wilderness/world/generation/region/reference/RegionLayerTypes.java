package com.fuyuaki.r_wilderness.world.generation.region.reference;

import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;

public enum RegionLayerTypes {
    PLATE_WEIGHTS(0, "Plate Weights", "plate_weights","weights",RegionFloatMap.Resolution.R_512),
    PLATE_EDGES(1, "Plate Edges", "plate_edges","edges",RegionFloatMap.Resolution.R_512),
    PLATE_ELEVATION_OFFSETS(2, "Plate Elevation Offsets", "plate_local_offset","plateoffset",RegionFloatMap.Resolution.R_512),
    VOLCANICNESS(3, "Volcanicness", "volcanicness","volcanicness",RegionFloatMap.Resolution.R_512),
    TECTONIC_STRESS(4, "Tectonic Stress", "tectonic_stress","stress",RegionFloatMap.Resolution.R_512),
    PLATE_BUOYANCY(5, "Plate Local Buoyancy", "plate_buoyancy","buoyancy",RegionFloatMap.Resolution.R_512),
    PLATE_DIST_TO_PARENT(6, "Plate Dist to Parent", "plate_dist_to_parent","distparent",RegionFloatMap.Resolution.R_512),
    PLATE_AGE(7, "Plate Age", "plate_age","plateage",RegionFloatMap.Resolution.R_512),
    PLATE_SWELLING(8, "Plate Swelling", "plate_swelling","plateswelling",RegionFloatMap.Resolution.R_512),
    PLATEAUS(9, "Plateaus", "plateaus","plateaus",RegionFloatMap.Resolution.R_512),
    ELEVATION(10, "Elevation", "elevation","elevation",RegionFloatMap.Resolution.R_2048),
    ATMOSPHERE_PRESSURE(11, "Atmospheric Pressure", "atmosphere_pressure","atmospherepressure",RegionFloatMap.Resolution.R_64),
    TEMPERATURE(12, "Temperature", "temperature","temperature",RegionFloatMap.Resolution.R_128),
    ERODABILITY(13, "Erodability", "erodability","erodability",RegionFloatMap.Resolution.R_128),
    VEGETATION(14, "Vegetation", "vegetation","vegetation",RegionFloatMap.Resolution.R_128),
    HUMIDITY(15, "Humidity", "humidity","humidity",RegionFloatMap.Resolution.R_128),
    ROCKINESS(16, "Rockiness", "rockiness","rockiness",RegionFloatMap.Resolution.R_128),
    MAGICALNESS(17, "Magicalness", "magicalness","magicalness",RegionFloatMap.Resolution.R_128),
    HEIGHTMAP_BASIC(18, "Heightmap Basic","heightmap_basic","heightmapbasic", RegionFloatMap.Resolution.R_1024),
    CONTINENTALNESS(19, "Continentalness","continentalness","continentalness", RegionFloatMap.Resolution.R_256),
    HILLS(20, "Hills","hills","hills", RegionFloatMap.Resolution.R_512),
    RIVER_INFLUENCE(21, "Rivers","rivers","rivers", RegionFloatMap.Resolution.R_1024),
    GLACIAL_THICKNESS(22, "Glacial Thickness","glacial_thickness","glacial", RegionFloatMap.Resolution.R_512),
    ;

    private final int index;
    private final String name;
    private final int defaultResolution;
    private final String serializedName;
    private final String saveName;

    RegionLayerTypes(int index, String name, String serializedName, String saveName, int defaultResolution) {
        this.index = index;
        this.name = name;
        this.serializedName = serializedName;
        this.defaultResolution = defaultResolution;
        this.saveName = saveName;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getDefaultResolution() {
        return defaultResolution;
    }

    public String getSerializedName() {
        return serializedName;
    }
    public String getSaveName() {
        return saveName;
    }

    public static RegionLayerTypes fromCommandKey(String commandKey) {
        for (RegionLayerTypes layer : RegionLayerTypes.values()) {
            if (layer.serializedName.equals(commandKey)) return layer;
        }
        return RegionLayerTypes.valueOf(commandKey);
    }

    public int scale() {
        return RegionPos.REGION_SIZE / this.getDefaultResolution();
    }
}