package com.fuyuaki.r_wilderness.world.generation.distant_horizons;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import net.neoforged.fml.ModList;

public class DHApiEventHandler extends DhApiLevelLoadEvent {
    @Override
    public void onLevelLoad(DhApiEventParam<EventParam> dhApiEventParam) {
            WildDHChunkWorldGen.registerForLevel(dhApiEventParam.value.levelWrapper);
    }
}
