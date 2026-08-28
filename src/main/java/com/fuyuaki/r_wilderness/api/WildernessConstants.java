package com.fuyuaki.r_wilderness.api;

public class WildernessConstants {

    public static final int BUILD_HEIGHT = 768;
    public static final int CAVES_TOP = 416;
    public static final int CAVES_BASE_TOP = 32;
    public static final int CAVES_BOTTOM = -240;
    public static final int CAVES_BOTTOM_CENTER = -192;
    public static final int WORLD_BOTTOM = -256;
    public static final int WORLD_HEIGHT =  Math.abs(WORLD_BOTTOM) + BUILD_HEIGHT ;

    public static final float BOTTOM_DENSITY = 1.5F;
    public static final float TOP_DENSITY = -1.5F;

    public static final int SEA_LEVEL = 63;

    public static final float CLOUD_HEIGHT = 364;


    public static final boolean DISABLE_CARVERS = true;
    public static final float PLATEAU_STEPS_MAX = 8.0F;
    public static long DAY_PERIOD = 24000;
    public static long EARTH_ORBIT_PERIOD = 365 * DAY_PERIOD;


    public static final float SHEAR_AMPLITUDE = 16.0f;
    public static final float MAX_MOUNTAIN_HEIGHT = 256.0f;
    public static final float MAX_TRENCH_DEPTH = 64.0f;
    public static final float MAX_ARC_HEIGHT = 120.0f;
    public static final float RIFT_VALLEY_UPSHIFT = 16.0f;
    public static final float RIFT_VALLEY_DEPTH = 96.0f;
    public static final float BASE_PLATEAU_UPLIFT = 64.0f;
    public static final float CONTINENTAL_ELEVATION_MIN_MAX = 64.0f;
    public static final float RIVER_CONTINENT_ELEVATION_BOOST = 12.0f;
    public static final float SWELL_HILL_SCALE = 32.0f;
    public static final float VOLCANIC_ARTIFACT_SCALE = 32.0f;
    public static final float MAX_MOUNTAIN_LOCAL_VARIATION = 96.0f;
    public static final float MOUNT_HILL_ELEVATION = 32.0f;

    public static final float MAX_ELEVATION_Y_LEVEL = Math.max(Math.max(MAX_MOUNTAIN_HEIGHT, MAX_ARC_HEIGHT),Math.max(RIFT_VALLEY_UPSHIFT,SHEAR_AMPLITUDE))
            + BASE_PLATEAU_UPLIFT
            + CONTINENTAL_ELEVATION_MIN_MAX
            + SWELL_HILL_SCALE
            + VOLCANIC_ARTIFACT_SCALE
            + MAX_MOUNTAIN_LOCAL_VARIATION
            + MOUNT_HILL_ELEVATION
            + 64.0f;
}
