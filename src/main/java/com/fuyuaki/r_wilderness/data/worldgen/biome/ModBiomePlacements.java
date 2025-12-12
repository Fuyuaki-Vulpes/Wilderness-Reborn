package com.fuyuaki.r_wilderness.data.worldgen.biome;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomePlacement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class ModBiomePlacements {

    private static final TerrainParameters.Range IMPOSSIBLE = range(-999,-990,-985);


    private static final TerrainParameters.Range HOT = range(0.75,0.85, 2.5);
    private static final TerrainParameters.Range WARM = range(0.35,0.75);
    private static final TerrainParameters.Range LUKEWARM = range(0.15,0.35);
    private static final TerrainParameters.Range NEUTRAL = range(-0.35,0.35);
    private static final TerrainParameters.Range COOL = range(-0.35,0.-0.15);
    private static final TerrainParameters.Range COLD = range(-0.75,-0.35);
    private static final TerrainParameters.Range FREEZING = range(-2.5,-0.85, -0.75);

    private static final TerrainParameters.Range NEGATIVE = range(-2.5,-0.5,0.0);
    private static final TerrainParameters.Range POSITIVE = range(0.0,0.5,2.5);
    private static final TerrainParameters.Range FOREST_VEGETATION = range(0.45,1.5);

    private static final TerrainParameters.Range VERY_LOW = range(-10,-0.95,-0.85);
    private static final TerrainParameters.Range LOW = range(-0.85,-0.6);
    private static final TerrainParameters.Range LOWISH = range(-0.6,-0.35);
    private static final TerrainParameters.Range HIGHISH = range(0.35,0.6);
    private static final TerrainParameters.Range HIGH = range(0.6,0.85);
    private static final TerrainParameters.Range VERY_HIGH = range(0.85,0.95,10);

    private static final TerrainParameters.Range FULL = range(-1.5,0,1.5);
    private static final TerrainParameters.Range NONE = range(0,0);

    private static final TerrainParameters.Range CONTINENT_INLAND = range(-0.2,0.15,100);
    public static final TerrainParameters.Range CONTINENTAL_SHORE = range(-0.1, 0.1);
    public static final TerrainParameters.Range CONTINENTALNESS_OCEAN = range(-0.75, 0);
    public static final TerrainParameters.Range CONTINENTALNESS_DEEP_OCEAN = range(-1.5, -0.45);

    public static final TerrainParameters.Range MAGICALNESS_LOW = range(-1.5,0.15);
    public static final TerrainParameters.Range MAGICALNESS_ANY = range(-1.5,1.5);
    public static final TerrainParameters.Range TECTONIC_ANY = range(-6,0, 1.0);
    public static final TerrainParameters.Range TECTONIC_LOW = range(-6,-0.5, 0.1);
    public static final TerrainParameters.Range TECTONIC_FOREST = range(-10,0, 0.5);
    public static final TerrainParameters.Range TECTONIC_GROVES = range(-3,0.15, 1.0);
    public static final TerrainParameters.Range TECTONICS_BADLAND = rangeMax(-3.5, 0.3);

    public static final TerrainParameters.Range HIGHLANDS_AREA = rangeMax(0.15, 0.5);
    public static final TerrainParameters.Range HIGHLANDS_NONE = range(-1.5,0,0.15);

    private static final TerrainParameters.Range HEIGHT_RANGE_ANY = range(WildernessConstants.WORLD_BOTTOM,WildernessConstants.BUILD_HEIGHT);
    private static final TerrainParameters.Range HEIGHT_RANGE_NATURAL = range(WildernessConstants.WORLD_BOTTOM,72,240);
    private static final TerrainParameters.Range HEIGHT_RANGE_SHALLOW = range(WildernessConstants.WORLD_BOTTOM,64,92);
    private static final double HEIGHT_BOTTOM = WildernessConstants.WORLD_BOTTOM;
    private static final double HEIGHT_TOP = WildernessConstants.BUILD_HEIGHT;
    private static final double HEIGHT_SEA = WildernessConstants.SEA_LEVEL;


    public static final ResourceKey<RebornBiomePlacement> VOID = register("void");

    //OFF LAND
    public static final ResourceKey<RebornBiomePlacement> OCEAN = register("ocean");
    public static final ResourceKey<RebornBiomePlacement> DEEP_OCEAN = register("deep_ocean");
    public static final ResourceKey<RebornBiomePlacement> WARM_OCEAN = register("warm_ocean");
    public static final ResourceKey<RebornBiomePlacement> LUKEWARM_OCEAN = register("lukewarm_ocean");
    public static final ResourceKey<RebornBiomePlacement> DEEP_LUKEWARM_OCEAN = register("deep_lukewarm_ocean");
    public static final ResourceKey<RebornBiomePlacement> COLD_OCEAN = register("cold_ocean");
    public static final ResourceKey<RebornBiomePlacement> DEEP_COLD_OCEAN = register("deep_cold_ocean");
    public static final ResourceKey<RebornBiomePlacement> FROZEN_OCEAN = register("frozen_ocean");
    public static final ResourceKey<RebornBiomePlacement> DEEP_FROZEN_OCEAN = register("deep_frozen_ocean");
    public static final ResourceKey<RebornBiomePlacement> MUSHROOM_ISLAND = register("mushrooms");

    //FLATLAND
    public static final ResourceKey<RebornBiomePlacement> PLAINS = register("plains");
    public static final ResourceKey<RebornBiomePlacement> SUNFLOWER_PLAINS = register("sunflower_plains");
    public static final ResourceKey<RebornBiomePlacement> SNOWY_PLAINS = register("snowy_plains");
    public static final ResourceKey<RebornBiomePlacement> ICE_SPIKES = register("ice_spikes");
    public static final ResourceKey<RebornBiomePlacement> DESERT = register("desert");

    //FOREST
    public static final ResourceKey<RebornBiomePlacement> FOREST = register("forest");
    public static final ResourceKey<RebornBiomePlacement> FLOWER_FOREST = register("flower_forest");
    public static final ResourceKey<RebornBiomePlacement> BIRCH_FOREST = register("birch_forest");
    public static final ResourceKey<RebornBiomePlacement> OLD_GROWTH_BIRCH_FOREST = register("old_growth_birch_forest");
    public static final ResourceKey<RebornBiomePlacement> TAIGA = register("taiga");
    public static final ResourceKey<RebornBiomePlacement> OLD_GROWTH_PINE_TAIGA = register("old_growth_pine_taiga");
    public static final ResourceKey<RebornBiomePlacement> OLD_GROWTH_SPRUCE_TAIGA = register("old_growth_spruce_taiga");
    public static final ResourceKey<RebornBiomePlacement> SNOWY_TAIGA = register("snowy_taiga");
    public static final ResourceKey<RebornBiomePlacement> DARK_FOREST = register("dark_forest");
    public static final ResourceKey<RebornBiomePlacement> PALE_GARDEN = register("pale_garden");
    public static final ResourceKey<RebornBiomePlacement> JUNGLE = register("jungle");
    public static final ResourceKey<RebornBiomePlacement> BAMBOO_JUNGLE = register("bamboo_jungle");
    public static final ResourceKey<RebornBiomePlacement> SPARSE_JUNGLE = register("sparse_jungle");
    public static final ResourceKey<RebornBiomePlacement> SAVANNA = register("savanna");
    public static final ResourceKey<RebornBiomePlacement> SAVANNA_PLATEAU = register("savanna_highlands");
    public static final ResourceKey<RebornBiomePlacement> WINDSWEPT_SAVANNA = register("windswept_savanna");


    //WETLANDS
    public static final ResourceKey<RebornBiomePlacement> SWAMP = register("swamp");
    public static final ResourceKey<RebornBiomePlacement> MANGROVE_SWAMP = register("mangrove_swamp");

    //HIGHLANDS
    public static final ResourceKey<RebornBiomePlacement> MEADOW = register("meadow");
    public static final ResourceKey<RebornBiomePlacement> GROVE = register("grove");
    public static final ResourceKey<RebornBiomePlacement> CHERRY_GROVE = register("cherry_grove");
    public static final ResourceKey<RebornBiomePlacement> BADLANDS = register("badlands");
    public static final ResourceKey<RebornBiomePlacement> ERODED_BADLANDS = register("eroded_badlands");
    public static final ResourceKey<RebornBiomePlacement> WOODED_BADLANDS = register("wooded_badlands");

    //SLOPES
    public static final ResourceKey<RebornBiomePlacement> MEADOW_MOUNTAIN = register("meadow_mountain");
    public static final ResourceKey<RebornBiomePlacement> SNOWY_SLOPES = register("snowy_slopes");
    public static final ResourceKey<RebornBiomePlacement> WINDSWEPT_HILLS = register("windswept_hills");
    public static final ResourceKey<RebornBiomePlacement> WINDSWEPT_GRAVELLY_HILLS = register("windswept_gravelly_hills");
    public static final ResourceKey<RebornBiomePlacement> WINDSWEPT_FOREST = register("windswept_forest");

    //SHORE
    public static final ResourceKey<RebornBiomePlacement> BEACH = register("beach");
    public static final ResourceKey<RebornBiomePlacement> SNOWY_BEACH = register("snowy_beach");
    public static final ResourceKey<RebornBiomePlacement> STONY_SHORE = register("stony_shore");

    //RIVERS

    public static final ResourceKey<RebornBiomePlacement> RIVER = register("river");
    public static final ResourceKey<RebornBiomePlacement> FROZEN_RIVER = register("frozen_river");
    //PEAKS
    public static final ResourceKey<RebornBiomePlacement> JAGGED_PEAKS = register("jagged_peaks");
    public static final ResourceKey<RebornBiomePlacement> FROZEN_PEAKS = register("frozen_peaks");
    public static final ResourceKey<RebornBiomePlacement> STONY_PEAKS = register("stony_peaks");

    //CAVES
    public static final ResourceKey<RebornBiomePlacement> DRIPSTONE_CAVES = register("dripstone_caves");
    public static final ResourceKey<RebornBiomePlacement> BARREN_CAVES = register("barren_caves");
    public static final ResourceKey<RebornBiomePlacement> DEEP_DARK = register("deep_dark");
    public static final ResourceKey<RebornBiomePlacement> LUSH_CAVES = register("lush_caves");


    public static void bootstrap(BootstrapContext<RebornBiomePlacement> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);

        placement(context, PLAINS,
                getBiomeHolder(lookup, Biomes.PLAINS),
                target(
                        NEUTRAL, //Temperature
                        NEUTRAL, //Humidity
                        LOW, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HILLY, SHORE, NORMAL, MOUNTAIN, HIGHLANDS)
        );
        placement(context, SUNFLOWER_PLAINS,
                getBiomeHolder(lookup, Biomes.SUNFLOWER_PLAINS),
                target(
                        NEUTRAL, //Temperature
                        NEUTRAL, //Humidity
                        LOWISH, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, //Terrain Height
                        HIGH //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, MOUNTAIN, HIGHLANDS)
        );
        placement(context, SNOWY_PLAINS,
                getBiomeHolder(lookup, Biomes.SNOWY_PLAINS),
                target(
                        COLD, //Temperature
                        NEUTRAL, //Humidity
                        LOW, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HILLY, SHORE, NORMAL, MOUNTAIN, HIGHLANDS)
        );
        placement(context, ICE_SPIKES,
                getBiomeHolder(lookup, Biomes.ICE_SPIKES),
                target(
                        FREEZING, //Temperature
                        POSITIVE, //Humidity
                        VERY_LOW, //Vegetation Density
                        POSITIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HILLY, SHORE, NORMAL, MOUNTAIN, HIGHLANDS)
        );

        placement(context, FOREST,
                getBiomeHolder(lookup, Biomes.FOREST),
                target(
                        LUKEWARM, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, FLOWER_FOREST,
                getBiomeHolder(lookup, Biomes.FLOWER_FOREST),
                target(
                        LUKEWARM, //Temperature
                        HIGH, //Humidity
                        VERY_HIGH, //Vegetation Density
                        LOW, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );

        placement(context, BIRCH_FOREST,
                getBiomeHolder(lookup, Biomes.BIRCH_FOREST),
                target(
                        NEUTRAL, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        NEGATIVE, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        LOW //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, OLD_GROWTH_BIRCH_FOREST,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_BIRCH_FOREST),
                target(
                        NEUTRAL, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        range(-10, 0.75), //Tectonic Activity
                        FULL, //Highlands
                        POSITIVE, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        LOW //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, DARK_FOREST,
                getBiomeHolder(lookup, Biomes.DARK_FOREST),
                target(
                        COOL, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        HIGH //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, PALE_GARDEN,
                getBiomeHolder(lookup, Biomes.PALE_GARDEN),
                target(
                        COOL, //Temperature
                        NEUTRAL, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEUTRAL, //Rockiness
                        range(0.6, 1.5), //Continentalness
                        range(-5.5, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        VERY_HIGH, //Weirdness
                        HIGHISH
                ),
                List.of(
                        HILLY, NORMAL, HIGHLANDS)
        );
        placement(context, JUNGLE,
                getBiomeHolder(lookup, Biomes.JUNGLE),
                target(
                        HOT, //Temperature
                        HIGH, //Humidity
                        VERY_HIGH, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, SPARSE_JUNGLE,
                getBiomeHolder(lookup, Biomes.SPARSE_JUNGLE),
                target(
                        HOT, //Temperature
                        HIGH, //Humidity
                        POSITIVE, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );

        placement(context, BAMBOO_JUNGLE,
                getBiomeHolder(lookup, Biomes.BAMBOO_JUNGLE),
                target(
                        HOT, //Temperature
                        HIGH, //Humidity
                        HIGH, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        HIGH //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );


        placement(context, SWAMP,
                getBiomeHolder(lookup, Biomes.SWAMP),
                target(
                        WARM, //Temperature
                        HIGH, //Humidity
                        range(-0.6, 0.25), //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        range(-5, -0.5), //Tectonic Activity
                        NONE, //Highlands
                        VERY_HIGH, //Erosion
                        HEIGHT_RANGE_SHALLOW, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, MANGROVE_SWAMP,
                getBiomeHolder(lookup, Biomes.MANGROVE_SWAMP),
                target(
                        WARM, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        range(-5, -0.5), //Tectonic Activity
                        NONE, //Highlands
                        VERY_HIGH, //Erosion
                        HEIGHT_RANGE_SHALLOW, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        SHORE, NORMAL, HIGHLANDS)
        );

        placement(context, TAIGA,
                getBiomeHolder(lookup, Biomes.TAIGA),
                target(
                        COLD, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        NEUTRAL //Weirdness
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, OLD_GROWTH_PINE_TAIGA,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_PINE_TAIGA),
                target(
                        COLD, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        POSITIVE, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        LOW // WEIRDNESS
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS, MOUNTAIN)
        );
        placement(context, OLD_GROWTH_SPRUCE_TAIGA,
                getBiomeHolder(lookup, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
                target(
                        COLD, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        NEGATIVE, //Erosion
                        HEIGHT_RANGE_NATURAL, // Terrain Height
                        LOW // WEIRDNESS
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS)
        );
        placement(context, SNOWY_TAIGA,
                getBiomeHolder(lookup, Biomes.SNOWY_TAIGA),
                target(
                        FREEZING, //Temperature
                        HIGH, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HILLY, SHORE, NORMAL, HIGHLANDS, MOUNTAIN)
        );

        placement(context, SAVANNA,
                getBiomeHolder(lookup, Biomes.SAVANNA),
                target(
                        HOT, //Temperature
                        NEGATIVE, //Humidity
                        NEUTRAL, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_FOREST, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HILLY,SHORE, NORMAL)
        );


        placement(context, SAVANNA_PLATEAU,
                getBiomeHolder(lookup, Biomes.SAVANNA_PLATEAU),
                target(
                        HOT, //Temperature
                        NEGATIVE, //Humidity
                        NEUTRAL, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_GROVES, //Tectonic Activity
                        rangeMax(0.5,1.0), //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        HIGHLANDS),
                List.of(TERRAIN_NORMAL,UNSTABLE)
        );

        placement(context, DESERT,
                getBiomeHolder(lookup, Biomes.DESERT),
                target(
                        HOT, //Temperature
                        VERY_LOW, //Humidity
                        VERY_LOW, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_LOW, //Tectonic Activity
                        HIGHLANDS_NONE, //Highlands
                        range(HIGH,VERY_HIGH), //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        SHORE, NORMAL, MOUNTAIN, HIGHLANDS),
                List.of(TERRAIN_NORMAL,UNSTABLE,ERODED_MOUNTAINS)
        );

        placement(context, MEADOW,
                getBiomeHolder(lookup, Biomes.MEADOW),
                target(
                        COLD, //Temperature
                        POSITIVE, //Humidity
                        NEUTRAL, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_GROVES, //Tectonic Activity
                        HIGHLANDS_AREA, //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN, HIGHLANDS)
        );
        placement(context, CHERRY_GROVE,
                getBiomeHolder(lookup, Biomes.CHERRY_GROVE),
                target(
                        NEUTRAL, //Temperature
                        HIGH, //Humidity
                        POSITIVE, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_GROVES, //Tectonic Activity
                        HIGHLANDS_AREA, //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN, HIGHLANDS)
        );
        placement(context, GROVE,
                getBiomeHolder(lookup, Biomes.GROVE),
                target(
                        COLD, //Temperature
                        NEUTRAL, //Humidity
                        FOREST_VEGETATION, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONIC_GROVES, //Tectonic Activity
                        HIGHLANDS_AREA, //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN, HIGHLANDS)
        );

        placement(context, MEADOW_MOUNTAIN,
                getBiomeHolder(lookup, Biomes.MEADOW),
                target(
                        NEUTRAL, //Temperature
                        range(-0.5, 1.0), //Humidity
                        NEUTRAL, //Vegetation Density
                        range(-1.0,0.25, 0.75), //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(MOUNTAINOUS)
        );
        placement(context, SNOWY_SLOPES,
                getBiomeHolder(lookup, Biomes.SNOWY_SLOPES),
                target(
                        COLD, //Temperature
                        NEUTRAL, //Humidity
                        LOW, //Vegetation Density
                        range(-1.0, 0.5), //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(CLIFF_MOUNTAINS,REGULAR_MOUNTAINS)
        );
        placement(context, WINDSWEPT_HILLS,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_HILLS),
                target(
                        NEUTRAL, //Temperature
                        NEUTRAL, //Humidity
                        NEGATIVE, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(MOUNTAINOUS)
        );
        placement(context, WINDSWEPT_GRAVELLY_HILLS,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_GRAVELLY_HILLS),
                target(
                        WARM, //Temperature
                        NEGATIVE, //Humidity
                        NEGATIVE, //Vegetation Density
                        POSITIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        NEGATIVE, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(MOUNTAINOUS)
        );
        placement(context, WINDSWEPT_FOREST,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_FOREST),
                target(
                        WARM, //Temperature
                        POSITIVE, //Humidity
                        POSITIVE, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(MOUNTAINOUS)
        );
        placement(context, WINDSWEPT_SAVANNA,
                getBiomeHolder(lookup, Biomes.WINDSWEPT_SAVANNA),
                target(
                        HOT, //Temperature
                        NEGATIVE, //Humidity
                        NEUTRAL, //Vegetation Density
                        NEUTRAL, //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        rangeMax(0, 1.0), //Tectonic Activity
                        rangeMinZero(0.15), //Highlands
                        FULL, //Erosion
                        range(80, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        MOUNTAIN,HIGHLANDS),
                List.of(MOUNTAINOUS)
        );

        placement(context, BADLANDS,
                getBiomeHolder(lookup, Biomes.BADLANDS),
                target(
                        range(WARM,HOT), //Temperature
                        range(VERY_LOW,LOW), //Humidity
                        VERY_LOW, //Vegetation Density
                        range(NEUTRAL,VERY_HIGH), //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONICS_BADLAND, //Tectonic Activity
                        rangeMax(-0.15, 0.5), //Highlands
                        range(VERY_LOW, NEUTRAL), //Erosion
                        range(64, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        HILLY,MOUNTAIN,HIGHLANDS),
                List.of(TERRAIN_BADLANDS,FAULT_SURROUNDINGS)
        );
        placement(context, ERODED_BADLANDS,
                getBiomeHolder(lookup, Biomes.ERODED_BADLANDS),
                target(
                        range(WARM,HOT), //Temperature
                        range(VERY_LOW,LOW), //Humidity
                        VERY_LOW, //Vegetation Density
                        range(NEUTRAL,VERY_HIGH), //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONICS_BADLAND, //Tectonic Activity
                        rangeMax(-0.15, 0.5), //Highlands
                        range(NEUTRAL, HIGH), //Erosion
                        range(64, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        HILLY,MOUNTAIN,HIGHLANDS),
                List.of(TERRAIN_BADLANDS,FAULT_SURROUNDINGS)
        );

        placement(context, WOODED_BADLANDS,
                getBiomeHolder(lookup, Biomes.WOODED_BADLANDS),
                target(
                        range(WARM,HOT), //Temperature
                        range(VERY_LOW,LOW), //Humidity
                        range(LOW,NEUTRAL), //Vegetation Density
                        range(NEUTRAL,VERY_HIGH), //Rockiness
                        CONTINENT_INLAND, //Continentalness
                        TECTONICS_BADLAND, //Tectonic Activity
                        rangeMax(-0.15, 0.5), //Highlands
                        range(VERY_LOW, HIGH), //Erosion
                        range(64, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        HILLY,MOUNTAIN,HIGHLANDS),
                List.of(TERRAIN_BADLANDS,FAULT_SURROUNDINGS)
        );


        placement(context, JAGGED_PEAKS,
                getBiomeHolder(lookup, Biomes.JAGGED_PEAKS),
                target(
                        NEUTRAL, //Temperature
                        FULL, //Humidity
                        NEUTRAL, //Vegetation Density
                        rangeMax(0.25, 1.0), //Rockiness
                        range(-0.5, 1.5), //Continentalness
                        rangeMax(0.25, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        rangeMin(-1.0, 0.25), //Erosion
                        range(160, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        PEAK, MOUNTAIN),
                List.of(CLIFF_MOUNTAINS,REGULAR_MOUNTAINS)
        );
        placement(context, FROZEN_PEAKS,
                getBiomeHolder(lookup, Biomes.FROZEN_PEAKS),
                target(
                        FREEZING, //Temperature
                        FULL, //Humidity
                        NEUTRAL, //Vegetation Density
                        rangeMax(0.0, 0.75), //Rockiness
                        range(-0.5, 1.5), //Continentalness
                        rangeMax(0.25, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        rangeMin(-1.0, 0.25), //Erosion
                        range(160, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        PEAK, MOUNTAIN),
                List.of(CLIFF_MOUNTAINS,REGULAR_MOUNTAINS)
        );
        placement(context, STONY_PEAKS,
                getBiomeHolder(lookup, Biomes.STONY_PEAKS),
                target(
                        WARM, //Temperature
                        range(-1.0, 0.0), //Humidity
                        NEUTRAL, //Vegetation Density
                        rangeMax(0.25, 1.0), //Rockiness
                        range(-0.5, 1.5), //Continentalness
                        rangeMax(0.25, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        rangeMin(-1.0, 0.25), //Erosion
                        range(160, HEIGHT_TOP) // Terrain Height
                ),
                List.of(
                        PEAK, MOUNTAIN),
                List.of(CLIFF_MOUNTAINS,REGULAR_MOUNTAINS)
        );

        placement(context, BEACH,
                getBiomeHolder(lookup, Biomes.BEACH),
                target(
                        WARM, //Temperature
                        NEGATIVE, //Humidity
                        LOW, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENTAL_SHORE, //Continentalness
                        range(-10, 0.0), //Tectonic Activity
                        range(0.0, 0.25), //Highlands
                        range(-0.1, 0.2, -1.5), //Erosion
                        HEIGHT_RANGE_SHALLOW // Terrain Height
                ),
                List.of(
                        SHORE)
        );

        placement(context, SNOWY_BEACH,
                getBiomeHolder(lookup, Biomes.SNOWY_BEACH),
                target(
                        COLD, //Temperature
                        NEGATIVE, //Humidity
                        LOW, //Vegetation Density
                        NEGATIVE, //Rockiness
                        CONTINENTAL_SHORE, //Continentalness
                        range(-10, 0.0), //Tectonic Activity
                        range(0.0, 0.25), //Highlands
                        range(-0.1, 0.2, 1.5), //Erosion
                        HEIGHT_RANGE_SHALLOW // Terrain Height
                ),
                List.of(
                        SHORE)
        );

        placement(context, STONY_SHORE,
                getBiomeHolder(lookup, Biomes.STONY_SHORE),
                target(
                        FULL, //Temperature
                        NEGATIVE, //Humidity
                        VERY_LOW, //Vegetation Density
                        HIGH, //Rockiness
                        CONTINENTAL_SHORE, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        range(0.0, 1.0), //Highlands
                        range(-1.5, -0.35, 0.1), //Erosion
                        HEIGHT_RANGE_SHALLOW // Terrain Height
                ),
                List.of(
                        SHORE)
        );

        placement(context, RIVER,
                getBiomeHolder(lookup, Biomes.RIVER),
                target(
                        COLD, //Temperature
                        NEGATIVE, //Humidity
                        LOW, //Vegetation Density
                        FULL, //Rockiness
                        FULL, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        range(0.0, 1.0), //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        TYPE_RIVER)
        );
        placement(context, FROZEN_RIVER,
                getBiomeHolder(lookup, Biomes.FROZEN_RIVER),
                target(
                        NEUTRAL, //Temperature
                        NEGATIVE, //Humidity
                        LOW, //Vegetation Density
                        HIGH, //Rockiness
                        FULL, //Continentalness
                        FULL, //Tectonic Activity
                        range(0.0, 1.0), //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_NATURAL // Terrain Height
                ),
                List.of(
                        TYPE_RIVER)
        );

        placement(context, OCEAN,
                getBiomeHolder(lookup, Biomes.OCEAN),
                target(
                        NEUTRAL, //Temperature
                        range(-1.0, 1.0), //Humidity
                        FULL, //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_OCEAN, //Continentalness
                        rangeMax(-10, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        NEAR_SHORE, SEA)
        );

        placement(context, DEEP_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_OCEAN),
                target(
                        NEUTRAL, //Temperature
                        range(-1.0, 1.0), //Humidity
                        NEUTRAL, //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_DEEP_OCEAN, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        FAR_SEA)
        );


        placement(context, WARM_OCEAN,
                getBiomeHolder(lookup, Biomes.WARM_OCEAN),
                target(
                        WARM, //Temperature
                        range(-1.0, 1.0), //Humidity
                        range(-1.0, 0), //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_OCEAN, //Continentalness
                        rangeMax(-10, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        NEAR_SHORE, SEA)
        );

        placement(context, LUKEWARM_OCEAN,
                getBiomeHolder(lookup, Biomes.LUKEWARM_OCEAN),
                target(
                        LUKEWARM, //Temperature
                        range(-1.0, 1.0), //Humidity
                        range(0.3, 1.0), //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_OCEAN, //Continentalness
                        rangeMax(-10, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        NEAR_SHORE, SEA)
        );

        placement(context, DEEP_LUKEWARM_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_LUKEWARM_OCEAN),
                target(
                        LUKEWARM, //Temperature
                        range(-1.0, 1.0), //Humidity
                        range(0.3, 1.0), //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_DEEP_OCEAN, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        FAR_SEA)
        );


        placement(context, COLD_OCEAN,
                getBiomeHolder(lookup, Biomes.COLD_OCEAN),
                target(
                        COLD, //Temperature
                        range(-1.0, 1.0), //Humidity
                        FULL, //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_OCEAN, //Continentalness
                        rangeMax(-10, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        NEAR_SHORE, SEA)
        );

        placement(context, DEEP_COLD_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_COLD_OCEAN),
                target(
                        COLD, //Temperature
                        range(-1.0, 1.0), //Humidity
                        FULL, //Vegetation Density
                        FULL,
                        CONTINENTALNESS_DEEP_OCEAN, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        FAR_SEA)
        );


        placement(context, FROZEN_OCEAN,
                getBiomeHolder(lookup, Biomes.FROZEN_OCEAN),
                target(
                        FREEZING, //Temperature
                        range(-1.0, 0.2), //Humidity
                        FULL, //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_OCEAN, //Continentalness
                        rangeMax(-10, 1.0), //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        NEAR_SHORE, SEA)
        );

        placement(context, DEEP_FROZEN_OCEAN,
                getBiomeHolder(lookup, Biomes.DEEP_FROZEN_OCEAN),
                target(
                        FREEZING, //Temperature
                        range(-1.0, 0.2), //Humidity
                        FULL, //Vegetation Density
                        FULL, //Rockiness
                        CONTINENTALNESS_DEEP_OCEAN, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM, HEIGHT_SEA - 4) // Terrain Height
                ),
                List.of(
                        FAR_SEA)
        );


        placement(context, MUSHROOM_ISLAND,
                getBiomeHolder(lookup, Biomes.MUSHROOM_FIELDS),
                target(
                        NEUTRAL, //Temperature
                        range(-1.0, 1.0), //Humidity
                        FULL, //Vegetation Density
                        range(-1.0, 1.0), //Rockiness
                        CONTINENTALNESS_DEEP_OCEAN, //Continentalness
                        TECTONIC_ANY, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_ANY, // Terrain Height
                        FULL,
                        POSITIVE
                ),
                List.of(
                        ISLAND)
        );


        placement(context, DRIPSTONE_CAVES,
                getBiomeHolder(lookup, Biomes.DRIPSTONE_CAVES),
                target(
                        NEUTRAL, //Temperature
                        POSITIVE, //Humidity
                        LOWISH, //Vegetation Density
                        HIGHISH, //Rockiness
                        FULL, //Continentalness
                        FULL, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_ANY // Terrain Height
                ),
                List.of(
                        CAVE)
        );

        placement(context, BARREN_CAVES,
                getBiomeHolder(lookup, RBiomes.BARREN_CAVES),
                target(
                        FULL, //Temperature
                        NEGATIVE, //Humidity
                        NEGATIVE, //Vegetation Density
                        FULL, //Rockiness
                        FULL, //Continentalness
                        FULL, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_ANY // Terrain Height
                ),
                List.of(
                        CAVE)
        );
        placement(context, LUSH_CAVES,
                getBiomeHolder(lookup, Biomes.LUSH_CAVES),
                target(
                        POSITIVE, //Temperature
                        POSITIVE, //Humidity
                        HIGHISH, //Vegetation Density
                        HIGHISH, //Rockiness
                        FULL, //Continentalness
                        FULL, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        HEIGHT_RANGE_ANY // Terrain Height
                ),
                List.of(
                        CAVE)
        );
        placement(context, DEEP_DARK,
                getBiomeHolder(lookup, Biomes.DEEP_DARK),
                target(
                        COLD, //Temperature
                        FULL, //Humidity
                        FULL, //Vegetation Density
                        FULL, //Rockiness
                        FULL, //Continentalness
                        FULL, //Tectonic Activity
                        FULL, //Highlands
                        FULL, //Erosion
                        range(HEIGHT_BOTTOM,-64), // Terrain Height
                        FULL,
                        range(0.25,1.5)
                ),
                List.of(
                        CAVE),
                List.of(VERY_DEEP_UNDERGROUND)
        );


        placement(context, VOID,
                getBiomeHolder(lookup, Biomes.THE_VOID),
                target(
                        IMPOSSIBLE, //Temperature
                        IMPOSSIBLE, //Humidity
                        IMPOSSIBLE, //Vegetation Density
                        IMPOSSIBLE, //Rockiness
                        IMPOSSIBLE, //Continentalness
                        IMPOSSIBLE, //Tectonic Activity
                        IMPOSSIBLE, //Highlands
                        IMPOSSIBLE, //Erosion
                        IMPOSSIBLE, // Terrain Height
                        IMPOSSIBLE,
                        IMPOSSIBLE
                ),
                List.of(PEAK)
        );
    }

    private static Holder.@NotNull Reference<Biome> getBiomeHolder(HolderGetter<Biome> holdergetter,ResourceKey<Biome> key) {
        return holdergetter.getOrThrow(key);
    }

    private static void placement(BootstrapContext<RebornBiomePlacement> context, ResourceKey<RebornBiomePlacement> name,Holder<Biome> biomeHolder, TerrainParameters.Target target, List<RebornBiomePlacement.Type> types) {
        context.register(name,new RebornBiomePlacement(name.identifier(),
                biomeHolder,target,types
        ));
    }
    private static void placement(BootstrapContext<RebornBiomePlacement> context, ResourceKey<RebornBiomePlacement> name,Holder<Biome> biomeHolder, TerrainParameters.Target target, List<RebornBiomePlacement.Type> types, List<RebornBiomePlacement.TerrainStates> states) {
        context.register(name,new RebornBiomePlacement(name.identifier(),
                biomeHolder,target,types, Optional.of(states)
        ));
    }

    private static ResourceKey<RebornBiomePlacement> register(String name) {
        return ResourceKey.create(WildRegistries.REBORN_BIOME_PLACEMENT_KEY, RWildernessMod.modLocation(name));
    }


    public static TerrainParameters.Range rangeMax(double min, double mean){
        return new TerrainParameters.Range(min, mean,mean + 25.0);
    }
    public static TerrainParameters.Range rangeMaxZero(double min){
        return new TerrainParameters.Range(min, 0.0,0.0);
    }
    public static TerrainParameters.Range rangeMin(double mean, double max){
        return new TerrainParameters.Range(mean - 25.0, mean,max);
    }
    public static TerrainParameters.Range rangeMinZero(double max){
        return new TerrainParameters.Range(0,0,max);
    }
    public static TerrainParameters.Range range(double min, double max){
        double r = max - min;
        return new TerrainParameters.Range(min, min + r/2,max);
    }
    public static TerrainParameters.Range range(double min, double mean, double max){
        return new TerrainParameters.Range(min,mean,max);
    }

    public static TerrainParameters.Range range(TerrainParameters.Range min, TerrainParameters.Range max){
        double mean = (min.mean() + max.mean())/2;
        return range(min.min(),mean,max.max());
    }


    private static TerrainParameters.Target target(
            TerrainParameters.Range temperature,
            TerrainParameters.Range humidity,
            TerrainParameters.Range vegetationDensity,
            TerrainParameters.Range rockiness,
            TerrainParameters.Range continentalness,
            TerrainParameters.Range tectonicActivity,
            TerrainParameters.Range highlands,
            TerrainParameters.Range erosion,
            TerrainParameters.Range terrainHeight
    ){
        return new TerrainParameters.Target(temperature,humidity,vegetationDensity,rockiness,continentalness,tectonicActivity,highlands,erosion,terrainHeight);
    }

    private static TerrainParameters.Target target(
            TerrainParameters.Range temperature,
            TerrainParameters.Range humidity,
            TerrainParameters.Range vegetationDensity,
            TerrainParameters.Range rockiness,
            TerrainParameters.Range continentalness,
            TerrainParameters.Range tectonicActivity,
            TerrainParameters.Range highlands,
            TerrainParameters.Range erosion,
            TerrainParameters.Range terrainHeight,
            TerrainParameters.Range weirdness
    ){
        return new TerrainParameters.Target(temperature,humidity,vegetationDensity,rockiness,continentalness,tectonicActivity,highlands,erosion,terrainHeight,weirdness, MAGICALNESS_ANY);
    }
    private static TerrainParameters.Target target(
            TerrainParameters.Range temperature,
            TerrainParameters.Range humidity,
            TerrainParameters.Range vegetationDensity,
            TerrainParameters.Range rockiness,
            TerrainParameters.Range continentalness,
            TerrainParameters.Range tectonicActivity,
            TerrainParameters.Range highlands,
            TerrainParameters.Range erosion,
            TerrainParameters.Range terrainHeight,
            TerrainParameters.Range weirdness,
            TerrainParameters.Range magicalness
    ){
        return new TerrainParameters.Target(temperature,humidity,vegetationDensity,rockiness,continentalness,tectonicActivity,highlands,erosion,terrainHeight,weirdness, magicalness);
    }


    private static final RebornBiomePlacement.Type NORMAL = RebornBiomePlacement.Type.NEUTRAL;
    private static final RebornBiomePlacement.Type TYPE_RIVER = RebornBiomePlacement.Type.RIVER;
    private static final RebornBiomePlacement.Type CAVE = RebornBiomePlacement.Type.CAVE;
    private static final RebornBiomePlacement.Type PEAK = RebornBiomePlacement.Type.PEAK;
    private static final RebornBiomePlacement.Type MOUNTAIN = RebornBiomePlacement.Type.MOUNTAIN;
    private static final RebornBiomePlacement.Type HILLY = RebornBiomePlacement.Type.MOUNTAIN;
    private static final RebornBiomePlacement.Type HIGHLANDS = RebornBiomePlacement.Type.HIGHLANDS;
    private static final RebornBiomePlacement.Type SHORE = RebornBiomePlacement.Type.SHORE;
    private static final RebornBiomePlacement.Type NEAR_SHORE = RebornBiomePlacement.Type.NEAR_SHORE;
    private static final RebornBiomePlacement.Type SEA = RebornBiomePlacement.Type.SEA;
    private static final RebornBiomePlacement.Type FAR_SEA = RebornBiomePlacement.Type.FAR_SEA;
    private static final RebornBiomePlacement.Type ISLAND = RebornBiomePlacement.Type.ISLAND;


    private static final RebornBiomePlacement.TerrainStates TERRAIN_NORMAL = RebornBiomePlacement.TerrainStates.NORMAL;
    private static final RebornBiomePlacement.TerrainStates UNSTABLE = RebornBiomePlacement.TerrainStates.UNSTABLE;
    private static final RebornBiomePlacement.TerrainStates TERRAIN_BADLANDS = RebornBiomePlacement.TerrainStates.BADLANDS;
    private static final RebornBiomePlacement.TerrainStates DIVERGING_PLATES = RebornBiomePlacement.TerrainStates.DIVERGING_PLATES;
    private static final RebornBiomePlacement.TerrainStates TRENCH = RebornBiomePlacement.TerrainStates.TRENCH;
    private static final RebornBiomePlacement.TerrainStates FAULT_SURROUNDINGS = RebornBiomePlacement.TerrainStates.FAULT_SURROUNDINGS;
    private static final RebornBiomePlacement.TerrainStates FAULT = RebornBiomePlacement.TerrainStates.FAULT;
    private static final RebornBiomePlacement.TerrainStates CLIFF_MOUNTAINS = RebornBiomePlacement.TerrainStates.CLIFF_MOUNTAINS;
    private static final RebornBiomePlacement.TerrainStates MOUNTAINOUS = RebornBiomePlacement.TerrainStates.MOUNTAINOUS;
    private static final RebornBiomePlacement.TerrainStates REGULAR_MOUNTAINS = RebornBiomePlacement.TerrainStates.REGULAR_MOUNTAINS;
    private static final RebornBiomePlacement.TerrainStates ERODED_MOUNTAINS = RebornBiomePlacement.TerrainStates.ERODED_MOUNTAINS;
    private static final RebornBiomePlacement.TerrainStates VOLCANIC = RebornBiomePlacement.TerrainStates.VOLCANIC;
    private static final RebornBiomePlacement.TerrainStates DEEP_UNDERGROUND = RebornBiomePlacement.TerrainStates.DEEP_UNDERGROUND;
    private static final RebornBiomePlacement.TerrainStates VERY_DEEP_UNDERGROUND = RebornBiomePlacement.TerrainStates.VERY_DEEP_UNDERGROUND;
    private static final RebornBiomePlacement.TerrainStates DEEPEST_UNDERGROUND = RebornBiomePlacement.TerrainStates.DEEPEST_UNDERGROUND;
}
