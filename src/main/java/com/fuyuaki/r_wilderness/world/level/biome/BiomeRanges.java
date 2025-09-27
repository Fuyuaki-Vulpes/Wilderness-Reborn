package com.fuyuaki.r_wilderness.world.level.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.HashMap;
import java.util.Map;

public class BiomeRanges {

    public static final Map<ResourceKey<Biome>, BiomeByNoiseRange> BIOMES = new HashMap<>();

    public static void bootstrap(){
        BIOMES.put(Biomes.PLAINS,new BiomeByNoiseRange(
                BiomeNoiseRange.byTolerance(0.8F,0.5F),
                BiomeNoiseRange.byTolerance(0.4F,0.5F)
                ));

        setBiomes(
                new BiomeByNoiseRange(
                        BiomeNoiseRange.byTolerance(0.8F,0.5F),
                        BiomeNoiseRange.byTolerance(0.4F,0.5F)
                ),
                Biomes.PLAINS,Biomes.SUNFLOWER_PLAINS
        );

        setBiomes(
                new BiomeByNoiseRange(
                        BiomeNoiseRange.byTolerance(1.8F,0.3F),
                        BiomeNoiseRange.byTolerance(0.0F,0.5F)
                ),
                Biomes.DESERT,Biomes.SAVANNA
        );
    }


    @SafeVarargs
    private static void setBiomes(BiomeByNoiseRange range, ResourceKey<Biome>... biome){
        for (ResourceKey<Biome> key : biome){
            BIOMES.put(key,range);
        }
    }

}
