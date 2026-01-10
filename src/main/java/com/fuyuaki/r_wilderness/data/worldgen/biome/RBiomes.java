package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public class RBiomes {

    //SURFACE
    public static final ResourceKey<Biome> ALPINE_TUNDRA = biome("alpine_tundra");
    public static final ResourceKey<Biome> BOG = biome("bog");
    public static final ResourceKey<Biome> GRAVELLY_RIVER = biome("gravelly_river");
    public static final ResourceKey<Biome> LUSH_MEADOW = biome("lush_meadow");
    public static final ResourceKey<Biome> LUSH_RIVER = biome("lush_river");
    public static final ResourceKey<Biome> MAPLE_FOREST = biome("maple_forest");
    public static final ResourceKey<Biome> OASIS = biome("oasis");
    public static final ResourceKey<Biome> CAATINGA = biome("caatinga");
    public static final ResourceKey<Biome> SPARSE_CHERRY_GROVE = biome("sparse_cherry_grove");
    public static final ResourceKey<Biome> SPARSE_TAIGA = biome("sparse_taiga");
    public static final ResourceKey<Biome> TUNDRA = biome("tundra");
    public static final ResourceKey<Biome> UNDERWATER_FOREST = biome("underwater_forest");
    public static final ResourceKey<Biome> POLAR_DESERT = biome("polar_desert");
    public static final ResourceKey<Biome> CERRADO = biome("cerrado");
    public static final ResourceKey<Biome> DENSE_SAVANNA = biome("dense_savanna");


    //CAVE
    public static final ResourceKey<Biome> BARREN_CAVES = biome("barren_caves");
    public static final ResourceKey<Biome> CRYSTALLINE_GROTTO = biome("crystalline_grotto");


    public static ResourceKey<Biome> biome(String name){
        return ResourceKey.create(Registries.BIOME, RWildernessMod.modLocation(name));
    }
}
