package com.fuyuaki.r_wilderness.client.gui.components.debug;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.generation.region.WorldRegion;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DebugEntryRegionStatus implements DebugScreenEntry {
    public static final Identifier GROUP = RWildernessMod.modLocation("regions");

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        if (serverOrClientLevel == null || serverOrClientLevel.getServer() == null) return;
        ServerLevel level = serverOrClientLevel.getServer().overworld();

        RegionMemory regionMemory = RegionMemory.getInstance(level);
        List<WorldRegion> activeRegions = regionMemory.activeRegionList();
        displayer.addToGroup(GROUP, "Loaded Regions:");
        for (WorldRegion region : activeRegions) {
            displayer.addToGroup(GROUP, region.toString() + ", Queries:" + region.queryCount());
        }

        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity != null) {
            BlockPos feetPos = entity.blockPosition();
                displayer.addToGroup(GROUP, "Current Region:");
                WorldRegion region = regionMemory.getRegionOfIndex(new RegionPos(feetPos).pack());
                displayer.addToGroup(GROUP, region.toString() + ", Queries:" + region.queryCount());

        }
    }

}
