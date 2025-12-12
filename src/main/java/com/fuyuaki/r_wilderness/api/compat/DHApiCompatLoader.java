package com.fuyuaki.r_wilderness.api.compat;

import com.fuyuaki.r_wilderness.world.generation.distant_horizons.DHApiEventHandler;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;

public class DHApiCompatLoader {
    public static void execute() {
        DhApiEventRegister.on(DhApiLevelLoadEvent.class, new DHApiEventHandler());
    }
}
