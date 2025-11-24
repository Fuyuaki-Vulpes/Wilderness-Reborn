package com.fuyuaki.r_wilderness.world.generation.hydrology;

import net.minecraft.core.Direction;

import java.util.Set;

public interface HydrologyCell {
    Set<HydrologyCell> getNeighbors();

    void updateNeighbors(HydrologyContext context);

    void setNeighbor(NeighborDirection direction, HydrologyCell cell);

    int centralHeight();

    Flow getFlow();

    void update(HydrologyContext context);
}
