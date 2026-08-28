package com.fuyuaki.r_wilderness.world.generation.progress;

import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPhase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

public class RegionProgress {
    private RegionPhase phase = RegionPhase.EMPTY;

    public RegionPhase getPhase() {
        return phase;
    }

    public void upgrade(RegionPhase phase){
        if (this.phase.isAtLeast(phase)){
            return;
        }
        this.phase = phase;
    }

}

