package com.fuyuaki.r_wilderness.util;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class PreemptiveGenerationCollector {
    public static final PreemptiveGenerationCollector INSTANCE = new PreemptiveGenerationCollector();

    protected List<BiomePlacementData> biomePlacements;

    public PreemptiveGenerationCollector() {
    }


    public static PreemptiveGenerationCollector instance(){
        return INSTANCE;
    }

    public void init(MinecraftServer server){
        RegistryAccess registryAccess = server.registryAccess();
        this.biomePlacements = registryAccess.lookupOrThrow(RRegistries.REBORN_BIOME_PLACEMENT_KEY).stream().toList();
    }

    public void flush(){
        this.biomePlacements = List.of();
    }

    public List<BiomePlacementData> getKnownBiomePlacements() {
        return biomePlacements;
    }
}
