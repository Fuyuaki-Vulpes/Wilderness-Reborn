package com.fuyuaki.r_wilderness.util;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class PreemptiveGenerationCollector {
    public static final PreemptiveGenerationCollector INSTANCE = new PreemptiveGenerationCollector();

    public PreemptiveGenerationCollector() {
    }


    public static PreemptiveGenerationCollector instance(){
        return INSTANCE;
    }

}
