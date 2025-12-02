package com.fuyuaki.r_wilderness.api.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    public static final ModConfigSpec.LongValue MIN_Z = BUILDER
            .comment("Minimum Z Coordinate for Latitude Rotation")
            .defineInRange("minZ", -1000000, Long.MIN_VALUE, 0);

    public static final ModConfigSpec.LongValue MAX_Z = BUILDER
            .comment("Minimum Z Coordinate for Latitude Rotation")
            .defineInRange("maxZ", 1000000, 0, Long.MAX_VALUE);


    public static final ModConfigSpec.LongValue Z_MARGIN = BUILDER
            .comment("Shifts the min and max Z. The idea is that the day-night cycle breaks near poles, so this can help by moving the poles out of the map")
            .defineInRange("zMargin", 100, 0, Long.MAX_VALUE);


    public static final ModConfigSpec.IntValue STAR_COUNT = BUILDER
            .comment("sets the amount of stars in the sky.")
            .defineInRange("starCount", 7500, 0, Integer.MAX_VALUE);




    public static final ModConfigSpec SPEC = BUILDER.build();

}
