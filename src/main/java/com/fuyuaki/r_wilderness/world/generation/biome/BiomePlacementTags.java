package com.fuyuaki.r_wilderness.world.generation.biome;

import com.fuyuaki.r_wilderness.world.generation.GenerationUtils;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.reference.RegionLayers;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.List;

import static com.fuyuaki.r_wilderness.api.WildernessConstants.*;

public enum BiomePlacementTags implements StringRepresentable {
    RIVER("river"),
    ISLAND("island"),
    TIED_ISLAND("tied_island"),
    WINDSWEPT("windswept"),
    PLATEAU("plateau"),
    HIGHLAND("highland"),
    MOUNTAINS("mountains"),
    PEAK("peak"),
    OLD_LAND("old"),
    TRENCH ("trench"),
    CAVE ("cave"),
    DEEP_UNDERGROUND ("deep_underground"),
    VERY_DEEP_UNDERGROUND ("very_deep_underground"),
    BELOW_SEA_LEVEL("sea_level"),
    DEEPSLATE_LAYER ("deepslate"),
    SCHINITE_LAYER ("schinite"),
    MAGNEISS_LAYER ("magneiss"),
    MALATITE_LAYER ("malatite"),
    ;
    public static final Codec<BiomePlacementTags> CODEC = StringRepresentable.fromEnum(BiomePlacementTags::values);

    private final String serializedName;
    private static final float BLOCKS_FOR_HIGHLAND = 32;
    private static final float BLOCKS_FOR_MOUNTAIN = 64;
    private static final float BLOCKS_FOR_PEAK = 128;
    private static final float PLATEAU_PUNISHMENT = (PLATEAU_MODULABLE_HEIGHT * 0.5f) + BASE_PLATEAU_UPLIFT;
    private static final float BLOCKS_FOR_TRENCH = -32;
    public static final int BLOCKS_FOR_CAVE = 16;
    public static final int DEEP_THRESHOLD = -32;
    public static final int VERY_DEEP_THRESHOLD = -96;

    BiomePlacementTags(String serializedName) {
        this.serializedName = serializedName;
    }

    public static List<BiomePlacementTags> scan(WorldRegion region, RegionMemory memory, int x, int y,int z) {
        RegionLayers layers = region.layers();
        List<BiomePlacementTags> tags = new ArrayList<>();
        float baseE = layers.getHeightmapBasic().getUpsampled(x,z,memory);
        float elevation = layers.getElevation().getUpsampled(x,z,memory);

        if (layers.getRivers().getUpsampled(x,z,memory) >= 0.75f){
            tags.add(RIVER);
        }
        float continents = layers.getContinentalness().getUpsampled(x, z, memory);
        if (continents < -0.25f && (elevation >= SEA_LEVEL - 4 || tags.contains(RIVER))){
            if (layers.getPlateWeights().getUpsampled(x,z,memory) <= 0.45f){
                tags.add(ISLAND);
            }else{
                tags.add(TIED_ISLAND);
            }
        }
        float plateau = layers.getPlateaus().getUpsampled(x, z, memory);
        if (continents > -0.25f && elevation > 64.0f){
            if (plateau >= 0.25f){
                tags.add(PLATEAU);
            }
        }

        if (GenerationUtils.windsweptFactor(memory,region,x,z) >= 0.5f && elevation >= SEA_LEVEL){
            tags.add(WINDSWEPT);
        }
        if (layers.getAge().getUpsampled(x,z,memory) >= 0.75f){
            tags.add(OLD_LAND);
        }

        if (y > elevation - BLOCKS_FOR_CAVE) {
            float eDiff = elevation - baseE;
            if (continents > -0.25f) {
                if (eDiff > BLOCKS_FOR_HIGHLAND + (plateau * PLATEAU_PUNISHMENT * 0.5f) && elevation >= MIN_Y_HIGHLAND + (plateau * PLATEAU_PUNISHMENT)) {
                    tags.add(HIGHLAND);
                }
                if (eDiff > BLOCKS_FOR_MOUNTAIN + (plateau * PLATEAU_PUNISHMENT * 0.5f) && elevation >= MIN_Y_MOUNTAIN + (plateau * PLATEAU_PUNISHMENT)) {
                    tags.add(MOUNTAINS);
                }
            }


            if (eDiff > BLOCKS_FOR_PEAK + (plateau * PLATEAU_PUNISHMENT * 0.5f) && elevation >= MIN_Y_PEAK + (plateau * PLATEAU_PUNISHMENT)) {
                tags.add(PEAK);
            }
            if (eDiff < BLOCKS_FOR_TRENCH) {
                tags.add(TRENCH);
            }
        }
        if (elevation <= SEA_LEVEL){
            tags.add(BELOW_SEA_LEVEL);
        }
        if (elevation > y + BLOCKS_FOR_CAVE){
            tags.add(CAVE);
            if (y < DEEP_THRESHOLD){
                if (y < VERY_DEEP_THRESHOLD){
                    tags.add(VERY_DEEP_UNDERGROUND);
                }else{
                    tags.add(DEEP_UNDERGROUND);
                }

            }
        }

        if (y < DEEPSLATE_Y){
            if (y > SCHINITE_Y){
                tags.add(DEEPSLATE_LAYER);
            }else{
                if (y > MAGNEISS_Y){
                    tags.add(SCHINITE_LAYER);
                }else{
                    if (y > MALATITE_Y){
                        tags.add(MAGNEISS_LAYER);
                    }else{
                        tags.add(MALATITE_LAYER);
                    }
                }
            }
        }


        return tags;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
