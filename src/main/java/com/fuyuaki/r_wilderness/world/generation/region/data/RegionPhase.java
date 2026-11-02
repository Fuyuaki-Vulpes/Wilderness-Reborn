package com.fuyuaki.r_wilderness.world.generation.region.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum RegionPhase implements StringRepresentable{
    EMPTY("empty"),
    INITIALIZED("initialized"),
    RASTERIZATION("rasterization"),
    PLATEAU_AND_TERRAIN("plateau_terrain"),
    ROUGH_HEIGHTS("rough_heights"),
    RIVERS("rivers"),
    RIVERS_POST("rivers_post"),
    LOADED_AND_FINISHED("finished"),
    ;
    public static final Codec<RegionPhase> CODEC = StringRepresentable.fromEnum(RegionPhase::values);

    private final String name;
    RegionPhase(String name) {
        this.name = name;
    }
    public static RegionPhase fromString(String name) {
        for (RegionPhase phase : RegionPhase.values()) {
            if (phase.name.equals(name)) return phase;
        }
        return RegionPhase.valueOf(name);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isAtLeast(RegionPhase phase) {
        return this.ordinal() >= phase.ordinal();
    }

    public RegionPhase getPrevious() {
        if (this == EMPTY) return EMPTY;
        return values()[this.ordinal() - 1];
    }
    public RegionPhase getNext() {
        if (this == LOADED_AND_FINISHED) return LOADED_AND_FINISHED;
        return values()[this.ordinal() + 1];
    }

}
