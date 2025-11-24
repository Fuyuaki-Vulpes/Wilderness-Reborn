package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class RBiomes {
    public static final ResourceKey<Biome> BARREN_CAVES = biome("barren_caves");


    public static ResourceKey<Biome> biome(String name){
        return ResourceKey.create(Registries.BIOME, RWildernessMod.modLocation(name));
    }
}
