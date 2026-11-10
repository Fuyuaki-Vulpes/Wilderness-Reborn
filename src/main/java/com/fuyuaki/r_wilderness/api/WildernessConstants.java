package com.fuyuaki.r_wilderness.api;

public class WildernessConstants {

    public static final boolean DEBUG_DISABLE_FLUID_GENERATION = false;


    public static final int BUILD_HEIGHT = 1024;
    public static final int CAVES_BOTTOM = -240;
    public static final int CAVES_BOTTOM_CENTER = -192;
    public static final int WORLD_BOTTOM = -256;
    public static final int WORLD_HEIGHT =  Math.abs(WORLD_BOTTOM) + BUILD_HEIGHT ;

    public static final float BOTTOM_DENSITY = 1.5F;
    public static final float TOP_DENSITY = -1.5F;

    public static final float TERRAIN_DENSITY_VALUE = 0.3f;

    public static final int SEA_LEVEL = 63;
    public static final int CAVE_LAVA = -220;
    public static final int MAX_SPAWN_ELEVATION = 196;

    public static final float CLOUD_HEIGHT = 364;


    public static final boolean DISABLE_CARVERS = true;
    public static final float PLATEAU_STEPS_MAX = 16.0F;
    public static final int SEA_EROSION_BLOCKS = 32;
    public static final int DEEPSLATE_Y = 0;
    public static final int SCHINITE_Y = -64;
    public static final int MAGNEISS_Y = -128;
    public static final int MALATITE_Y = -192;
    public static final int LAYER_GRADIENT_BLOCKS = 8;
    public static final int MIN_Y_HIGHLAND = 96;
    public static final int MIN_Y_MOUNTAIN = 128;
    public static final int MIN_Y_PEAK = 256;
    public static long DAY_PERIOD = 24000;
    public static long EARTH_ORBIT_PERIOD = 365 * DAY_PERIOD;


    public static final float ISLAND_ELEVATION_MAX = 64.0f;

    public static final float SHEAR_AMPLITUDE = 16.0f;
    public static final float MAX_MOUNTAIN_HEIGHT = 320.0f;
    public static final float MAX_TRENCH_DEPTH = 148.0f;
    public static final float MAX_ARC_HEIGHT = 128.0f;
    public static final float RIFT_VALLEY_UPSHIFT = 16.0f;
    public static final float RIFT_VALLEY_DEPTH = 196.0f;
    public static final float BASE_PLATEAU_UPLIFT = 48.0f;
    public static final float PILLAR_HEIGHTS = 32.0f;
    public static final float PLATEAU_MODULABLE_HEIGHT = 96.0f;
    public static final float CONTINENTAL_ELEVATION_MIN_MAX = 48.0f;
    public static final float SWELL_HILL_SCALE = 32.0f;
    public static final float VOLCANIC_ARTIFACT_SCALE = 48.0f;
    public static final float MAX_MOUNTAIN_LOCAL_VARIATION = 48.0f;
    public static final float MAX_MOUNTAIN_CHUNK_VARIATION = 128.0f;
    public static final float MAX_PEAK_VARIATION = 32.0f;
    public static final float MAX_ARTIFACT_ELEVATION = 48.0f;
    public static final float MOUNT_HILL_ELEVATION = 48.0f;

    public static final float MAX_ELEVATION_Y_LEVEL = Math.max(Math.max(MAX_MOUNTAIN_HEIGHT, MAX_ARC_HEIGHT),Math.max(RIFT_VALLEY_UPSHIFT,SHEAR_AMPLITUDE))
            + BASE_PLATEAU_UPLIFT
            + PLATEAU_MODULABLE_HEIGHT
            + CONTINENTAL_ELEVATION_MIN_MAX
            + SWELL_HILL_SCALE
            + VOLCANIC_ARTIFACT_SCALE
            + MAX_MOUNTAIN_LOCAL_VARIATION
            + MAX_PEAK_VARIATION
            + MAX_ARTIFACT_ELEVATION
            + MOUNT_HILL_ELEVATION
            + PILLAR_HEIGHTS
            + 64.0f;
}
