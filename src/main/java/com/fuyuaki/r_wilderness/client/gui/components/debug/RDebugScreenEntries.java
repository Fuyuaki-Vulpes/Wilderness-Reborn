package com.fuyuaki.r_wilderness.client.gui.components.debug;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RDebugScreenEntries {
    private static final Map<Identifier, DebugScreenEntry> ENTRIES_BY_ID = new HashMap<>();

    public static final Identifier LOADED_REGIONS_LIST = RWildernessMod.modLocation("loaded_regions_list");

}
