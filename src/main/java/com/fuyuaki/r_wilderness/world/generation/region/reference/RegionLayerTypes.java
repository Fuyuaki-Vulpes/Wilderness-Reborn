package com.fuyuaki.r_wilderness.world.generation.region.reference;

import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;

public enum RegionLayerTypes {
    PLATE_WEIGHTS(0, "Plate Weights", "plate_weights",RegionFloatMap.Resolution.R_512),
    PLATE_EDGES(1, "Plate Edges", "plate_edges",RegionFloatMap.Resolution.R_512),
    PLATE_ELEVATION_OFFSETS(2, "Plate Elevation Offsets", "plate_local_offset",RegionFloatMap.Resolution.R_512),
    VOLCANICNESS(3, "Volcanicness", "volcanicness",RegionFloatMap.Resolution.R_512),
    TECTONIC_STRESS(4, "Tectonic Stress", "tectonic_stress",RegionFloatMap.Resolution.R_512),
    PLATE_BUOYANCY(5, "Plate Local Buoyancy", "plate_buoyancy",RegionFloatMap.Resolution.R_512),
    PLATE_DIST_TO_PARENT(6, "Plate Dist to Parent", "plate_dist_to_parent",RegionFloatMap.Resolution.R_512),
    PLATE_AGE(7, "Plate Age", "plate_age",RegionFloatMap.Resolution.R_512),
    PLATE_SWELLING(8, "Plate Swelling", "plate_swelling",RegionFloatMap.Resolution.R_512),
    PLATEAUS(9, "Plateaus", "plateaus",RegionFloatMap.Resolution.R_256),
    ELEVATION(10, "Elevation", "elevation",RegionFloatMap.Resolution.R_4096),
    ATMOSPHERE_PRESSURE(11, "Atmospheric Pressure", "atmosphere_pressure",RegionFloatMap.Resolution.R_128),
    TEMPERATURE(12, "Temperature", "temperature",RegionFloatMap.Resolution.R_256),
    ERODABILITY(13, "Erodability", "erodability",RegionFloatMap.Resolution.R_256),
    VEGETATION(14, "Vegetation", "vegetation",RegionFloatMap.Resolution.R_256),
    HUMIDITY(15, "Humidity", "humidity",RegionFloatMap.Resolution.R_256),
    ROCKINESS(16, "Rockiness", "rockiness",RegionFloatMap.Resolution.R_256),
    MAGICALNESS(17, "Magicalness", "magicalness",RegionFloatMap.Resolution.R_256),
    HEIGHTMAP_BASIC(18, "Heightmap Basic","heightmap_basic", RegionFloatMap.Resolution.R_2048),
    CONTINENTALNESS(19, "Continentalness","continentalness", RegionFloatMap.Resolution.R_512),
    HILLS(20, "Hills","hills", RegionFloatMap.Resolution.R_256),
    RIVER_INFLUENCE(21, "Rivers","rivers", RegionFloatMap.Resolution.R_1024),
    ;

    private final int index;
    private final String name;
    private final int defaultResolution;
    private final String serializedName;

    RegionLayerTypes(int index, String name, String serializedName, int defaultResolution) {
        this.index = index;
        this.name = name;
        this.serializedName = serializedName;
        this.defaultResolution = defaultResolution;
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

    public static RegionLayerTypes fromCommandKey(String commandKey) {
        for (RegionLayerTypes layer : RegionLayerTypes.values()) {
            if (layer.serializedName.equals(commandKey)) return layer;
        }
        return RegionLayerTypes.valueOf(commandKey);
    }
}