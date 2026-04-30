package com.fuyuaki.r_wilderness.world.level.levelgen.hydrology;

import com.fuyuaki.r_wilderness.world.level.levelgen.hydrology.river.Flow;

import java.util.Set;

public interface HydrologyCell {
    Set<HydrologyCell> getNeighbors();

    void updateNeighbors(HydrologyContext context);

    void setNeighbor(NeighborDirection direction, HydrologyCell cell);

    int centralHeight();

    Flow getFlow();

    void update(HydrologyContext context);
}
