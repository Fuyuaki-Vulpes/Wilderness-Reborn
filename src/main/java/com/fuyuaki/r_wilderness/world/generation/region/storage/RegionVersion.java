package com.fuyuaki.r_wilderness.world.generation.region.storage;

public record RegionVersion(short ID) {
    public static final short LATEST_VERSION = 1;

    public boolean isUpToDate() {
        return this.ID == LATEST_VERSION;
    }
}
