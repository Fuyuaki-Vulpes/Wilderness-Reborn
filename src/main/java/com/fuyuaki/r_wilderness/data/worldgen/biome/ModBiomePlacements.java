package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementTags;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomeTargetProfile;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public abstract class ModBiomePlacements {
    public static final ResourceKey<BiomePlacementData> VOID = register("void");

    //OFF LAND
    public static final ResourceKey<BiomePlacementData> OCEAN = register("ocean");
    public static final ResourceKey<BiomePlacementData> DEEP_OCEAN = register("deep_ocean");
    public static final ResourceKey<BiomePlacementData> WARM_OCEAN = register("warm_ocean");
    public static final ResourceKey<BiomePlacementData> LUKEWARM_OCEAN = register("lukewarm_ocean");
    public static final ResourceKey<BiomePlacementData> DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
    public static final ResourceKey<BiomePlacementData> COLD_OCEAN = register("cold_ocean");
    public static final ResourceKey<BiomePlacementData> DEEP_COLD_OCEAN = register("deep_cold_ocean");
    public static final ResourceKey<BiomePlacementData> FROZEN_OCEAN = register("frozen_ocean");
    public static final ResourceKey<BiomePlacementData> DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
    public static final ResourceKey<BiomePlacementData> MUSHROOM_ISLAND = register("mushrooms");

    //FLATLAND
    public static final ResourceKey<BiomePlacementData> PLAINS = register("hills");
    public static final ResourceKey<BiomePlacementData> SUNFLOWER_PLAINS = register("sunflower_plains");
    public static final ResourceKey<BiomePlacementData> SNOWY_PLAINS = register("snowy_plains");
    public static final ResourceKey<BiomePlacementData> ICE_SPIKES = register("ice_spikes");
    public static final ResourceKey<BiomePlacementData> DESERT = register("desert");

    //FOREST
    public static final ResourceKey<BiomePlacementData> FOREST = register("forest");
    public static final ResourceKey<BiomePlacementData> FLOWER_FOREST = register("flower_forest");
    public static final ResourceKey<BiomePlacementData> BIRCH_FOREST = register("birch_forest");
    public static final ResourceKey<BiomePlacementData> OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
    public static final ResourceKey<BiomePlacementData> TAIGA = register("taiga");
    public static final ResourceKey<BiomePlacementData> OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
    public static final ResourceKey<BiomePlacementData> OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
    public static final ResourceKey<BiomePlacementData> SNOWY_TAIGA = register("snowy_taiga");
    public static final ResourceKey<BiomePlacementData> DARK_FOREST = register("dark_forest");
    public static final ResourceKey<BiomePlacementData> PALE_GARDEN = register("pale_garden");
    public static final ResourceKey<BiomePlacementData> JUNGLE = register("jungle");
    public static final ResourceKey<BiomePlacementData> BAMBOO_JUNGLE = register("bamboo_jungle");
    public static final ResourceKey<BiomePlacementData> SPARSE_JUNGLE = register("sparse_jungle");
    public static final ResourceKey<BiomePlacementData> SAVANNA = register("savanna");
    public static final ResourceKey<BiomePlacementData> SAVANNA_PLATEAU = register("savanna_highlands");
    public static final ResourceKey<BiomePlacementData> WINDSWEPT_SAVANNA = register("windswept_savanna");


    //WETLANDS
    public static final ResourceKey<BiomePlacementData> SWAMP = register("swamp");
    public static final ResourceKey<BiomePlacementData> MANGROVE_SWAMP = register("mangrove_swamp");

    //HIGHLANDS
    public static final ResourceKey<BiomePlacementData> MEADOW = register("meadow");
    public static final ResourceKey<BiomePlacementData> GROVE = register("grove");
    public static final ResourceKey<BiomePlacementData> CHERRY_GROVE = register("cherry_grove");
    public static final ResourceKey<BiomePlacementData> BADLANDS = register("badlands");
    public static final ResourceKey<BiomePlacementData> ERODED_BADLANDS = register("eroded_badlands");
    public static final ResourceKey<BiomePlacementData> WOODED_BADLANDS = register("wooded_badlands");

    //SLOPES
    public static final ResourceKey<BiomePlacementData> MEADOW_MOUNTAIN = register("meadow_mountain");
    public static final ResourceKey<BiomePlacementData> SNOWY_SLOPES = register("snowy_slopes");
    public static final ResourceKey<BiomePlacementData> WINDSWEPT_HILLS = register("windswept_hills");
    public static final ResourceKey<BiomePlacementData> WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
    public static final ResourceKey<BiomePlacementData> WINDSWEPT_FOREST = register("windswept_forest");

    //SHORE
    public static final ResourceKey<BiomePlacementData> BEACH = register("beach");
    public static final ResourceKey<BiomePlacementData> SNOWY_BEACH = register("snowy_beach");
    public static final ResourceKey<BiomePlacementData> STONY_SHORE = register("stony_shore");

    //RIVERS

    public static final ResourceKey<BiomePlacementData> RIVER = register("river");
    public static final ResourceKey<BiomePlacementData> FROZEN_RIVER = register("frozen_river");
    //PEAKS
    public static final ResourceKey<BiomePlacementData> JAGGED_PEAKS = register("jagged_peaks");
    public static final ResourceKey<BiomePlacementData> FROZEN_PEAKS = register("frozen_peaks");
    public static final ResourceKey<BiomePlacementData> STONY_PEAKS = register("stony_peaks");

    //CAVES
    public static final ResourceKey<BiomePlacementData> DRIPSTONE_CAVES = register("dripstone_caves");
    public static final ResourceKey<BiomePlacementData> BARREN_CAVES = register("barren_caves");
    public static final ResourceKey<BiomePlacementData> DEEP_DARK = register("deep_dark");
    public static final ResourceKey<BiomePlacementData> LUSH_CAVES = register("lush_caves");
    public static final ResourceKey<BiomePlacementData> SULFUR_CAVES = register("sulfur_caves");


    public static void bootstrap(BootstrapContext<BiomePlacementData> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);

        placement(context, PLAINS,
                getBiomeHolder(lookup, Biomes.PLAINS),
                BiomeTargetProfile.Builder.grasslandTemplate().mildHumidity()

        );
        placement(context, SUNFLOWER_PLAINS,
                getBiomeHolder(lookup, Biomes.SUNFLOWER_PLAINS),
                BiomeTargetProfile.Builder.grasslandTemplate().damp()

        );
        placement(context, SNOWY_PLAINS,
                getBiomeHolder(lookup, Biomes.SNOWY_PLAINS),
                BiomeTargetProfile.Builder.coldGrasslandTemplate()

        );
        placement(context, ICE_SPIKES,
                getBiomeHolder(lookup, Biomes.ICE_SPIKES),
                BiomeTargetProfile.Builder.coldGrasslandTemplate().freezing().wet().barren()
        );

        placement(context, FOREST,
                getBiomeHolder(lookup, Biomes.FOREST),
                BiomeTargetProfile.Builder.forestTemplate().mildHumidity()

        );
        placement(context, FLOWER_FOREST,
                getBiomeHolder(lookup, Biomes.FLOWER_FOREST),
                BiomeTargetProfile.Builder.forestTemplate().damp()
        );

        placement(context, BIRCH_FOREST,
                getBiomeHolder(lookup, Biomes.BIRCH_FOREST),
                BiomeTargetProfile.Builder.forestTemplate().cool().mildHumidity()

        );
        placement(context, OLD_GROWTH_BIRCH_FOREST,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_BIRCH_FOREST),
                BiomeTargetProfile.Builder.forestTemplate().cool().mildHumidity().tagged(OLD_LAND)

        );
        placement(context, DARK_FOREST,
                getBiomeHolder(lookup, Biomes.DARK_FOREST),
                BiomeTargetProfile.Builder.forestTemplate().dense().cool().mildHumidity()

        );
        placement(context, PALE_GARDEN,
                getBiomeHolder(lookup, Biomes.PALE_GARDEN),
                BiomeTargetProfile.Builder.forestTemplate().cool().mystical().dry()

        );
        placement(context, JUNGLE,
                getBiomeHolder(lookup, Biomes.JUNGLE),
                BiomeTargetProfile.Builder.hotForestTemplate().dense().damp()

        );
        placement(context, SPARSE_JUNGLE,
                getBiomeHolder(lookup, Biomes.SPARSE_JUNGLE),
                BiomeTargetProfile.Builder.hotGroveTemplate().damp()

        );

        placement(context, BAMBOO_JUNGLE,
                getBiomeHolder(lookup, Biomes.BAMBOO_JUNGLE),
                BiomeTargetProfile.Builder.hotForestTemplate().dense().wet()

        );

        placement(context, SWAMP,
                getBiomeHolder(lookup, Biomes.SWAMP),
                BiomeTargetProfile.Builder.wetlandTemplate().flat()
        );
        placement(context, MANGROVE_SWAMP,
                getBiomeHolder(lookup, Biomes.MANGROVE_SWAMP),
                BiomeTargetProfile.Builder.wetlandTemplate().lush()
        );

        placement(context, TAIGA,
                getBiomeHolder(lookup, Biomes.TAIGA),
                BiomeTargetProfile.Builder.coldForestTemplate().cool()

        );
        placement(context, OLD_GROWTH_PINE_TAIGA,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_PINE_TAIGA),
                BiomeTargetProfile.Builder.coldForestTemplate().cool().mildHumidity().tagged(OLD_LAND)

        );
        placement(context, OLD_GROWTH_SPRUCE_TAIGA,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                BiomeTargetProfile.Builder.coldForestTemplate().cool().damp().tagged(OLD_LAND)

        );
        placement(context, SNOWY_TAIGA,
                getBiomeHolder(lookup, Biomes.SNOWY_TAIGA),
                BiomeTargetProfile.Builder.coldForestTemplate().damp().flat()
        );

        placement(context, SAVANNA,
                getBiomeHolder(lookup, Biomes.SAVANNA),
                BiomeTargetProfile.Builder.hotGrasslandTemplate().dry()

        );


        placement(context, SAVANNA_PLATEAU,
                getBiomeHolder(lookup, Biomes.SAVANNA_PLATEAU),
                BiomeTargetProfile.Builder.hotGrasslandTemplate().dry().tagged(PLATEAU)

        );

        placement(context, DESERT,
                getBiomeHolder(lookup, Biomes.DESERT),
                BiomeTargetProfile.Builder.hotGrasslandTemplate().barren().scorching().arid().flat()

        );

        placement(context, MEADOW,
                getBiomeHolder(lookup, Biomes.MEADOW),
                BiomeTargetProfile.Builder.groveTemplate().tagged(HIGHLAND)

        );
        placement(context, CHERRY_GROVE,
                getBiomeHolder(lookup, Biomes.CHERRY_GROVE),
                BiomeTargetProfile.Builder.coldGroveTemplate().cool().tagged(HIGHLAND)

        );
        placement(context, GROVE,
                getBiomeHolder(lookup, Biomes.GROVE),
                BiomeTargetProfile.Builder.coldGroveTemplate().tagged(HIGHLAND)

        );

        placement(context, MEADOW_MOUNTAIN,
                getBiomeHolder(lookup, Biomes.MEADOW),
                BiomeTargetProfile.Builder.grasslandTemplate().damp().mountainous().tagged(MOUNTAINS)

        );
        placement(context, SNOWY_SLOPES,
                getBiomeHolder(lookup, Biomes.SNOWY_SLOPES),
                BiomeTargetProfile.Builder.grasslandTemplate().mountainous().cold().tagged(HIGHLAND)

        );
        placement(context, WINDSWEPT_HILLS,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_HILLS),
                BiomeTargetProfile.Builder.grasslandTemplate().rugged().mountainous().cool().tagged(WINDSWEPT,HIGHLAND)

        );
        placement(context, WINDSWEPT_GRAVELLY_HILLS,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                BiomeTargetProfile.Builder.grasslandTemplate().rugged().mountainous().hot().tagged(WINDSWEPT,HIGHLAND)

        );
        placement(context, WINDSWEPT_FOREST,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_FOREST),
                BiomeTargetProfile.Builder.forestTemplate().tagged(WINDSWEPT)

        );
        placement(context, WINDSWEPT_SAVANNA,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_SAVANNA),
                BiomeTargetProfile.Builder.hotGrasslandTemplate().dry().tagged(WINDSWEPT)

        );

        placement(context, BADLANDS,
                getBiomeHolder(lookup, Biomes.BADLANDS),
                BiomeTargetProfile.Builder.grasslandTemplate().flat().barren().dry().tagged(PLATEAU)

        );
        placement(context, ERODED_BADLANDS,
                getBiomeHolder(lookup, Biomes.ERODED_BADLANDS),
                BiomeTargetProfile.Builder.grasslandTemplate().flat().damp().tagged(PLATEAU)


        );

        placement(context, WOODED_BADLANDS,
                getBiomeHolder(lookup, Biomes.WOODED_BADLANDS),
                BiomeTargetProfile.Builder.groveTemplate().flat().tagged(PLATEAU)

        );


        placement(context, JAGGED_PEAKS,
                getBiomeHolder(lookup, Biomes.JAGGED_PEAKS),
                BiomeTargetProfile.Builder.coldGrasslandTemplate().tagged(PEAK).cool().rocky().mountainous()

        );
        placement(context, FROZEN_PEAKS,
                getBiomeHolder(lookup, Biomes.FROZEN_PEAKS),
                BiomeTargetProfile.Builder.coldGrasslandTemplate().tagged(PEAK).freezing()
        );
        placement(context, STONY_PEAKS,
                getBiomeHolder(lookup, Biomes.STONY_PEAKS),
                BiomeTargetProfile.Builder.grasslandTemplate().tagged(PEAK).warm().rugged().barren().mountainous()

        );

        placement(context, BEACH,
                getBiomeHolder(lookup, Biomes.BEACH),
                BiomeTargetProfile.Builder.shoreTemplate()

        );

        placement(context, SNOWY_BEACH,
                getBiomeHolder(lookup, Biomes.SNOWY_BEACH),
                BiomeTargetProfile.Builder.shoreTemplate().cold()

        );

        placement(context, STONY_SHORE,
                getBiomeHolder(lookup, Biomes.STONY_SHORE),
                BiomeTargetProfile.Builder.rockyShoreTemplate()

        );

        placement(context, RIVER,
                getBiomeHolder(lookup, Biomes.RIVER),
                BiomeTargetProfile.Builder.grasslandTemplate().temperate().tagged(RIVER_TAG)

        );
        placement(context, FROZEN_RIVER,
                getBiomeHolder(lookup, Biomes.FROZEN_RIVER),
                BiomeTargetProfile.Builder.coldGrasslandTemplate().freezing().tagged(RIVER_TAG)
        );

        placement(context, OCEAN,
                getBiomeHolder(lookup, Biomes.OCEAN),
                BiomeTargetProfile.Builder.oceanTemplate()

        );

        placement(context, DEEP_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_OCEAN),
                BiomeTargetProfile.Builder.deepOceanTemplate()
        );


        placement(context, WARM_OCEAN,
                getBiomeHolder(lookup, Biomes.WARM_OCEAN),
                BiomeTargetProfile.Builder.warmOceanTemplate()
        );

        placement(context, LUKEWARM_OCEAN,
                getBiomeHolder(lookup, Biomes.LUKEWARM_OCEAN),
                BiomeTargetProfile.Builder.warmOceanTemplate()
                        .grove()

        );

        placement(context, DEEP_LUKEWARM_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_LUKEWARM_OCEAN),
                BiomeTargetProfile.Builder.deepWarmOceanTemplate()
                        .grove()

        );


        placement(context, COLD_OCEAN,
                getBiomeHolder(lookup, Biomes.COLD_OCEAN),
                BiomeTargetProfile.Builder.coldOceanTemplate()

        );

        placement(context, DEEP_COLD_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_COLD_OCEAN),
                BiomeTargetProfile.Builder.deepColdOceanTemplate()

        );


        placement(context, FROZEN_OCEAN,
                getBiomeHolder(lookup, Biomes.FROZEN_OCEAN),
                BiomeTargetProfile.Builder.coldOceanTemplate()
                        .freezing()

        );

        placement(context, DEEP_FROZEN_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_FROZEN_OCEAN),
                BiomeTargetProfile.Builder.deepColdOceanTemplate()
                        .freezing()

        );


        placement(context, MUSHROOM_ISLAND,
                getBiomeHolder(lookup, Biomes.MUSHROOM_FIELDS),
                BiomeTargetProfile.Builder.islandTemplate()
                        .whimsy().sparse()

        );


        placement(context, DRIPSTONE_CAVES,
                getBiomeHolder(lookup, Biomes.DRIPSTONE_CAVES),
                BiomeTargetProfile.Builder.caveTemplate().damp()
                        .inland().sparse()
        );

        placement(context, SULFUR_CAVES,
                getBiomeHolder(lookup, Biomes.SULFUR_CAVES),
                BiomeTargetProfile.Builder.caveTemplate().withTemperature(-1.0f,1.0f)
                        .dormantVolcano().inland()
        );

        placement(context, BARREN_CAVES,
                getBiomeHolder(lookup, RBiomes.BARREN_CAVES),
                BiomeTargetProfile.Builder.caveTemplate()
                        .barren().tagged(CAVE)
        );
        placement(context, LUSH_CAVES,
                getBiomeHolder(lookup, Biomes.LUSH_CAVES),
                BiomeTargetProfile.Builder.caveTemplate()
                        .warm().withVegetation(0.0f,1.0f).wet()
        );
        placement(context, DEEP_DARK,
                getBiomeHolder(lookup, Biomes.DEEP_DARK),
                BiomeTargetProfile.Builder.caveTemplate()
                        .positiveMagicalness().mountainous().tagged(DEEPSLATE_LAYER,DEEP_UNDERGROUND)
        );


        placement(context, VOID,
                getBiomeHolder(lookup, Biomes.THE_VOID),
                BiomeTargetProfile.builder().impossible()

        );
    }

    private static BiomeTargetProfile.Builder builder(){
        return BiomeTargetProfile.builder();
    }

    private static Holder.Reference<Biome> getBiomeHolder(HolderGetter<Biome> holdergetter,ResourceKey<Biome> key) {
        return holdergetter.getOrThrow(key);
    }

    private static void placement(BootstrapContext<BiomePlacementData> context, ResourceKey<BiomePlacementData> name, Holder<Biome> biomeHolder, BiomeTargetProfile.Builder profile) {
        context.register(name,new BiomePlacementData(name.identifier(),
                biomeHolder,profile.build()));
    }

    private static ResourceKey<BiomePlacementData> register(String name) {
        return ResourceKey.create(WildRegistries.REBORN_BIOME_PLACEMENT_KEY, RWildernessMod.modLocation(name));
    }




    private static final BiomePlacementTags RIVER_TAG = BiomePlacementTags.RIVER;
    private static final BiomePlacementTags ISLAND = BiomePlacementTags.ISLAND;
    private static final BiomePlacementTags WINDSWEPT = BiomePlacementTags.WINDSWEPT;
    private static final BiomePlacementTags TRENCH = BiomePlacementTags.TRENCH;
    private static final BiomePlacementTags PLATEAU = BiomePlacementTags.PLATEAU;
    private static final BiomePlacementTags OLD_LAND = BiomePlacementTags.OLD_LAND;
    private static final BiomePlacementTags HIGHLAND = BiomePlacementTags.HIGHLAND;
    private static final BiomePlacementTags MOUNTAINS = BiomePlacementTags.MOUNTAINS;
    private static final BiomePlacementTags PEAK = BiomePlacementTags.PEAK;
    private static final BiomePlacementTags BELOW_SEA_LEVEL = BiomePlacementTags.BELOW_SEA_LEVEL;
    private static final BiomePlacementTags CAVE = BiomePlacementTags.CAVE;
    private static final BiomePlacementTags DEEP_UNDERGROUND = BiomePlacementTags.DEEP_UNDERGROUND;
    private static final BiomePlacementTags VERY_DEEP_UNDERGROUND = BiomePlacementTags.VERY_DEEP_UNDERGROUND;
    private static final BiomePlacementTags DEEPSLATE_LAYER = BiomePlacementTags.DEEPSLATE_LAYER;
    private static final BiomePlacementTags SCHINITE_LAYER = BiomePlacementTags.SCHINITE_LAYER;
    private static final BiomePlacementTags MAGNEISS_LAYER = BiomePlacementTags.MAGNEISS_LAYER;
    private static final BiomePlacementTags MALATITE_LAYER = BiomePlacementTags.MALATITE_LAYER;
}
