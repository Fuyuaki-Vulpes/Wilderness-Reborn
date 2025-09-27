package com.fuyuaki.r_wilderness.world.level.biome;

public record BiomeNoiseRange(float min, float center, float max) {

    public BiomeNoiseRange(float min, float max){
        this(min, (min + max) / 2 ,max);
    }

    public static BiomeNoiseRange byTolerance(float center, float tolerance){
        return new BiomeNoiseRange(center - tolerance, center, center + tolerance);
    }

    public static BiomeNoiseRange byOffset(float min,float center, float max){
        return new BiomeNoiseRange(center - min, center, center + max);
    }


}
