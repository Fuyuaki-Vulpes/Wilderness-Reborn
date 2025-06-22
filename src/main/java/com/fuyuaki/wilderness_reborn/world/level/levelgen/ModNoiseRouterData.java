package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
import com.fuyuaki.wilderness_reborn.data.worldgen.ModTerrainProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseRouterData extends NoiseRouterFunctions{


    //TERRAIN
    public static final ResourceKey<DensityFunction> TERRAIN_BASE = createKey("terrain/base");
    public static final ResourceKey<DensityFunction> TERRAIN_BASE_SMOOTH = createKey("terrain/base_smooth");
    public static final ResourceKey<DensityFunction> TERRAIN_SMOOTHNESS = createKey("terrain/smoothness");
    public static final ResourceKey<DensityFunction> TERRAIN_TECTONIC = createKey("terrain/tectonic");
    public static final ResourceKey<DensityFunction> TERRAIN_TECTONIC_SMOOTH = createKey("terrain/tectonic_smooth");


    public static final ResourceKey<DensityFunction> SURFACE_BASE = createKey("surface/base");
    public static final ResourceKey<DensityFunction> SURFACE_MOUNTAINS_TECTONICS = createKey("surface/surface_mountains_tectonics");

    public static final ResourceKey<DensityFunction> LAND_CONTINENTS = createKey("land/continents");
    public static final ResourceKey<DensityFunction> LAND_EROSION = createKey("land/erosion");
    public static final ResourceKey<DensityFunction> LAND_NOISE = createKey("land/noise");

    public static final ResourceKey<DensityFunction> GEO_TECTONIC_PLATES = createKey("geo/tectonic_plates");


    public static final ResourceKey<DensityFunction> TECTONIC_EDGES = createKey("tectonic_plates/edges");
    public static final ResourceKey<DensityFunction> TECTONIC_DIRECTION = createKey("tectonic_plates/direction");
    public static final ResourceKey<DensityFunction> TECTONIC_RANDOMNESS = createKey("tectonic_plates/randomness");
    public static final ResourceKey<DensityFunction> TECTONIC_ACTIVITY = createKey("tectonic_plates/activity");


    //ROUTER PARAMETERS
    public static final ResourceKey<DensityFunction> R_BARRIER = createKey("router/barrier");
    public static final ResourceKey<DensityFunction> R_FLUID_LEVEL_FLOODEDNESS = createKey("router/fluid_level_floodedness");
    public static final ResourceKey<DensityFunction> R_FLUID_LEVEL_SPREAD = createKey("router/fluid_level_spread");
    public static final ResourceKey<DensityFunction> R_LAVA_NOISE = createKey("router/lava_noise");
    public static final ResourceKey<DensityFunction> R_TEMPERATURE = createKey("router/temperature");
    public static final ResourceKey<DensityFunction> R_VEGETATION = createKey("router/vegetation");
    public static final ResourceKey<DensityFunction> R_CONTINENTALNESS = createKey("router/continentalness");
    public static final ResourceKey<DensityFunction> R_EROSION = createKey("router/erosion");
    public static final ResourceKey<DensityFunction> R_DEPTH = createKey("router/depth");
    public static final ResourceKey<DensityFunction> R_RIDGES = createKey("router/ridges");
    public static final ResourceKey<DensityFunction> R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS = createKey("router/initial_density_without_jaggedness");
    public static final ResourceKey<DensityFunction> R_TOPOGRAPHY_FINAL_DENSITY = createKey("router/topography");
    public static final ResourceKey<DensityFunction> R_VEIN_TOGGLE = createKey("router/vein_toggle");
    public static final ResourceKey<DensityFunction> R_VEIN_RIDGED = createKey("router/vein_ridged");
    public static final ResourceKey<DensityFunction> R_VEIN_GAP = createKey("router/vein_gap");

    public static final ResourceKey<DensityFunction> R_SLOPED_CHEESE = createKey("sloped_cheese");

    //CAVES
    public static final ResourceKey<DensityFunction> CAVES = createKey("caves/caves_final");
    public static final ResourceKey<DensityFunction> CAVE_NOODLES = createKey("caves/noodle");
    public static final ResourceKey<DensityFunction> CAVE_SPAGHETTI = createKey("caves/spaghetti");
    public static final ResourceKey<DensityFunction> CAVE_PITS = createKey("caves/pits");
    public static final ResourceKey<DensityFunction> CAVE_CAVERNS = createKey("caves/caverns");
    public static final ResourceKey<DensityFunction> CAVE_GROTTO = createKey("caves/grotto");
    public static final ResourceKey<DensityFunction> CAVE_FRACTURE = createKey("caves/fracture");

    //DETAILS
    public static final ResourceKey<DensityFunction> TECTONIC_ACTIVITY_OLD = createKey("detail/tectonic_activity");
    public static final ResourceKey<DensityFunction> BIOME_VARIATION = createKey("detail/biome_variation");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("detail/jaggedness");
    public static final ResourceKey<DensityFunction> PLATEAU_VALLEY_DEPTH = createKey("detail/plateau_valley_depth");


    //MASKS
    public static final ResourceKey<DensityFunction> RIVER_MASK = createKey("masks/river_mask");
    public static final ResourceKey<DensityFunction> PLATEAU_MASK = createKey("masks/plateau");


    //SPLINE BASES
    public static final ResourceKey<DensityFunction> PLATE_NOISE = createKey("spline_bases/plates");
    public static final ResourceKey<DensityFunction> CRATERS_NOISE = createKey("spline_bases/craters");

    //ELEVATION
    public static final ResourceKey<DensityFunction> TECTONIC_FORMATION = createKey("elevation/tectonic_formation");
    public static final ResourceKey<DensityFunction> TOPOGRAPHY = createKey("elevation/topography");
    public static final ResourceKey<DensityFunction> TOPOGRAPHY_BASIC = createKey("elevation/topography_basic");
    public static final ResourceKey<DensityFunction> BASIC_ELEVATION = createKey("elevation/basic_elevation");
    public static final ResourceKey<DensityFunction> SURFACE_NOISE_A = createKey("elevation/noise_a");
    public static final ResourceKey<DensityFunction> SURFACE_NOISE_B = createKey("elevation/noise_b");
    public static final ResourceKey<DensityFunction> SURFACE_NOISE_C = createKey("elevation/noise_c");
    public static final ResourceKey<DensityFunction> GENERAL_LANDMASS = createKey("elevation/landmass");
    public static final ResourceKey<DensityFunction> CONTINENT_LANDMASS = createKey("elevation/continent_landmass");

    public static final ResourceKey<DensityFunction> HILLS_AND_MOUNTAINS = createKey("elevation/mountains");
    public static final ResourceKey<DensityFunction> MOUNTAIN_ELEVATION_OFFSET = createKey("elevation/mountain_elevation_offset");


    public static final ResourceKey<DensityFunction> GEO_WEATHERING_EROSION = createKey("geomorphology/weathering_and_erosion");
    public static final ResourceKey<DensityFunction> GEO_CRATERS = createKey("geomorphology/craters");
    public static final ResourceKey<DensityFunction> GEO_RIDGES = createKey("geomorphology/ridges");
    public static final ResourceKey<DensityFunction> GEO_RIVERS_AND_VALLEYS = createKey("geomorphology/rivers_and_valleys");
    public static final ResourceKey<DensityFunction> GEO_RIVERS_AND_VALLEYS_PLATEAU = createKey("geomorphology/rivers_and_valleys_plateau");




    public static Holder<? extends DensityFunction> bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        DensityFunction shiftX = getFunction(densityLookup, PackNoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(densityLookup, PackNoiseRouterData.SHIFT_Z);

        context.register(TERRAIN_BASE,DensityFunctions.flatCache(
                DensityFunctions.interpolated(
                        DensityFunctions.lerp(
                                        DensityFunctions.shiftedNoise2d(
                                                shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_BLENDER)
                                        ),

                                                DensityFunctions.shiftedNoise2d(
                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                                ),

                                                DensityFunctions.shiftedNoise2d(
                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                                )
                        )
                )
            )
        );


        context.register(TERRAIN_BASE_SMOOTH,DensityFunctions.flatCache(
                DensityFunctions.interpolated(
                        DensityFunctions.lerp(
                                        DensityFunctions.shiftedNoise2d(
                                                shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_BLENDER_SMOOTH)
                                        ),
                                                DensityFunctions.shiftedNoise2d(
                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                                ),
                                                DensityFunctions.shiftedNoise2d(
                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                                )
                        )
                )
            )
        );



        context.register(TERRAIN_SMOOTHNESS,
                DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                            DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_SMOOTHNESS))
                        )
                )
        );

        context.register(LAND_CONTINENTS,
                DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                                DensityFunctions.add(
                                        DensityFunctions.constant(-0.2F),
                                        DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.LAND_CONTINENTS))

                                )
                                )
                )
        );

        context.register(LAND_EROSION,
                DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                            DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.LAND_EROSION))
                        )
                )
        );
        context.register(GEO_TECTONIC_PLATES,
                DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                                DensityFunctions.mul(
                                        DensityFunctions.constant(10.0F),
                                        DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.GEO_TECTONICS))
                                ).clamp(-1.0F,1.0F)
                        )
                )
        );

        context.register(TERRAIN_TECTONIC,DensityFunctions.cacheOnce(
                        DensityFunctions.interpolated(
                                                        DensityFunctions.lerp(
                                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_BLENDER),
                                                                        0.25F,0.05F
                                                                ),
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_A)
                                                                ),
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_B)
                                                                )
                                                        )

                        )
                )
        );

        context.register(TERRAIN_TECTONIC_SMOOTH,DensityFunctions.cacheOnce(
                        DensityFunctions.interpolated(
                                                        DensityFunctions.lerp(
                                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_BLENDER_SMOOTH),
                                                                        0.25F,0.05F
                                                                ),
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_SMOOTH_A)
                                                                ),
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_SMOOTH_B)
                                                                )
                                                        )

                        )
                )
        );



        context.register(  TECTONIC_EDGES,
                DensityFunctions.flatCache(
                        DensityFunctions.mul(
                                DensityFunctions.constant(-1.8F),
                                DensityFunctions.add(
                                        DensityFunctions.constant(
                                        -0.45F
                                        ),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(2.75F),
                                                DensityFunctions.shiftedNoise2d
                                                        (shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.GEO_TECTONICS)
                                                        ).abs()
                                        )
                                )
                        )
                )
        );


        context.register(TECTONIC_DIRECTION,DensityFunctions.flatCache(
                        DensityFunctions.interpolated(

                                        DensityFunctions.shiftedNoise2d(
                                                shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_DIRECTION)
                                        )

                        )
                )
        );



        context.register(TECTONIC_RANDOMNESS,DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                                        DensityFunctions.shiftedNoise2d(
                                                shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_RANDOMNESS)
                                        )

                        )
                )
        );

        context.register(TECTONIC_ACTIVITY,
                DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                                DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TECTONIC_FACTOR_ACTIVITY))
                        )
                )
        );
        context.register(LAND_NOISE,DensityFunctions.flatCache(
                        DensityFunctions.interpolated(
                                DensityFunctions.mul(
                                        DensityFunctions.spline(
                                                CubicSpline.builder(splineCoordinatesFrom(getCachedFunction(densityLookup,R_CONTINENTALNESS)))
                                                        .addPoint(-1.0F,1.0F)
                                                        .addPoint(-0.6F,-1.0F)
                                                        .addPoint(-0.2F,-1.0F)
                                                        .addPoint(0.0F,1.0F)
                                                        .build()
                                        ),
                                        DensityFunctions.mul(
                                                DensityFunctions.lerp(
                                                        DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25,noiseLookup.getOrThrow(ModNoises.LAND_NOISE_BLENDER)).clamp(-1,1),
                                                        DensityFunctions.lerp(
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_BLENDER)
                                                                ),

                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                                                ),

                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                                                )
                                                        ),
                                                        DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25,noiseLookup.getOrThrow(ModNoises.LAND_NOISE_C))
                                                ),
                                                DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25F,noiseLookup.getOrThrow(ModNoises.LAND_NOISE_STRENGHT)).clamp(-1,1)
                                        )
                                ).squeeze().abs()
                                )
                )
        );


        context.register(SURFACE_NOISE_A, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.NOISE_A))));

        context.register(SURFACE_NOISE_B, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.NOISE_B))));

        context.register(SURFACE_NOISE_C, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.NOISE_C))));

        context.register(BASIC_ELEVATION,
                DensityFunctions.flatCache(
                        DensityFunctions.lerp(
                                DensityFunctions.add(
                                        DensityFunctions.constant(-0.5F),
                                        DensityFunctions.mul(
                                                getFunction(densityLookup, R_CONTINENTALNESS),
                                                DensityFunctions.constant(2.0F)
                                        ).clamp(-0.5,2)
                                ),
                                DensityFunctions.constant(0.02F),
                                DensityFunctions.mul(
                                        DensityFunctions.add(
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(0.35F),
                                                        getFunction(densityLookup,RIVER_MASK)).abs(),
                                                DensityFunctions.constant(0.5F)
                                        ).clamp(0.0F,1.0F),
                                        DensityFunctions.spline(
                                                CubicSpline.builder(splineCoordinatesFrom(
                                                                DensityFunctions.shiftedNoise2d(
                                                                        shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.ELEVATION)
                                                                )))
                                                        .addPoint(-1.0F,0.0F)
                                                        .addPoint(-0.65F,0.0F)
                                                        .addPoint(0.0F,0.05F)
                                                        .addPoint(0.5F,0.1F)
                                                        .addPoint(1.5F,0.2F)
                                                        .build()
                                        )
                                )
                        )
                )
        );


        context.register(RIVER_MASK,
                DensityFunctions.flatCache(
                        DensityFunctions.spline(
                                CubicSpline.builder(
                                        splineCoordinatesFrom(
                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.RIVER_MASK)
                                                        )
                                        ),ToFloatFunction.IDENTITY)
                                        .addPoint(-0.1F,0.0F)
                                        .addPoint(0.5F,0.2F,0.3F)
                                        .addPoint(1.0F,1.0F,0.6F)
                                        .build()
                        )
                )
        );
        context.register(PLATEAU_MASK,
                DensityFunctions.flatCache(
                        DensityFunctions.spline(
                                CubicSpline.builder(
                                        splineCoordinatesFrom(
                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.5F, noiseLookup.getOrThrow(ModNoises.PLATEAU_MASK)
                                                        )
                                        ),ToFloatFunction.IDENTITY)
                                        .addPoint(-0.1F,-1.0F)
                                        .addPoint(0.0F,-1.0F)
                                        .addPoint(0.2F,0.0F)
                                        .addPoint(0.5F,0.5F)
                                        .addPoint(0.8F,1.0F)
                                        .addPoint(1.0F,1.0F)
                                        .build()
                        )
                )
        );


        context.register(PLATE_NOISE,
                DensityFunctions.cache2d(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.PLATES))));

        context.register(BIOME_VARIATION,
                DensityFunctions.cache2d(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.5F, noiseLookup.getOrThrow(ModNoises.BIOME_VARIATION))));

        context.register(JAGGEDNESS,
                DensityFunctions.cache2d(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.JAGGEDNESS)
                        ).abs()
                )
        );

        context.register(PLATEAU_VALLEY_DEPTH,
                DensityFunctions.cache2d(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.PLATEAU_VALLEY_DEPTH)
                        )
                )
        );


        context.register(GENERAL_LANDMASS,
                DensityFunctions.cache2d(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.35F, noiseLookup.getOrThrow(ModNoises.LANDMASS))));


        context.register(HILLS_AND_MOUNTAINS,
                DensityFunctions.flatCache(
                        DensityFunctions.spline(
                                CubicSpline.builder(
                                                splineCoordinatesFrom(
                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.HILLS_AND_MOUNTAINS)
                                                        )
                                                ),ToFloatFunction.IDENTITY)
                                        .addPoint(-1.4F,-1.2F,0.9F)
                                        .addPoint(-1.0F,-1.0F,-0.3F)
                                        .addPoint(0.0F,0.0F,0.05F)
                                        .addPoint(1.0F,1.0F,0.3F)
                                        .addPoint(1.4F,1.2F,-0.9F)
                                        .build()
                        )
                )

        );

        context.register(MOUNTAIN_ELEVATION_OFFSET,
                DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.MOUNTAIN_ELEVATION_OFFSET)
                        )
                )
        );

        context.register(TECTONIC_FORMATION,
                DensityFunctions.flatCache(DensityFunctions.spline(CubicSpline.builder(
                                splineCoordinatesFrom(densityLookup.getOrThrow(PLATE_NOISE)), ToFloatFunction.IDENTITY)
                        .addPoint(-1.5F, -0.5F, 0.1F)
                        .addPoint(-1.0F, -0.4F, 0.3F)
                        .addPoint(-0.995F, -0.05F, 0.2F)
                        .addPoint(-0.990F, -0.2F, 0.1F)
                        .addPoint(-0.5F, 0.0F, 0.05F)
                        .addPoint(0.0F, 0.1F, 0.01F)
                        .addPoint(0.5F, 0.15F)
                        .addPoint(1.0F, 0.25F)
                        .build())));


        context.register(CONTINENT_LANDMASS,
                DensityFunctions.cache2d(
                        DensityFunctions.mul(
                                DensityFunctions.constant(1.4F),
                                DensityFunctions.add(
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(0.8F),
                                                getFunction(densityLookup, GENERAL_LANDMASS)
                                        ),
                                        getFunction(densityLookup, TECTONIC_FORMATION)
                                )
                        )
                )
        );

        context.register(GEO_WEATHERING_EROSION, DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(shiftX,shiftZ,0.25,noiseLookup.getOrThrow(ModNoises.WEATHERING))
        ));
        context.register(CRATERS_NOISE,
                DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.CRATERS))));

        context.register(GEO_CRATERS,
                DensityFunctions.flatCache(DensityFunctions.spline(CubicSpline.builder(
                                splineCoordinatesFrom(densityLookup.getOrThrow(CRATERS_NOISE)), ToFloatFunction.IDENTITY)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(1.0F, 0.0F)
                        .addPoint(1.5F, -0.5F)
                        .addPoint(2.0F, -1.0F)
                        .build())));

        context.register(GEO_RIDGES,
                DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.5, noiseLookup.getOrThrow(ModNoises.RIDGES))));

        context.register(GEO_RIVERS_AND_VALLEYS,
                DensityFunctions.add(
                        valleyMask(getFunction(densityLookup, GEO_RIDGES)),
                        getFunction(densityLookup,RIVER_MASK)
                )
        );

        context.register(GEO_RIVERS_AND_VALLEYS_PLATEAU,
                DensityFunctions.cache2d(
                        DensityFunctions.add(
                                DensityFunctions.constant(-0.55F),
                                DensityFunctions.mul(
                                        DensityFunctions.constant(2.0F),
                                        DensityFunctions.noise(
                                                noiseLookup.getOrThrow(ModNoises.PLATEAU_VALLEYS)
                                        ).abs()
                                )
                        )
                )
        );


        ModTerrainProvider.noodle(context, noiseLookup, densityLookup, CAVE_NOODLES);
        ModTerrainProvider.spaghetti(context, noiseLookup, densityLookup, CAVE_SPAGHETTI);
        ModTerrainProvider.pits(context, noiseLookup, CAVE_PITS);
        ModTerrainProvider.caverns(context, densityLookup, noiseLookup, shiftX, shiftZ, CAVE_CAVERNS);
        ModTerrainProvider.grotto(context, densityLookup, noiseLookup, shiftX, shiftZ, CAVE_GROTTO);
        ModTerrainProvider.fracture(context, densityLookup, noiseLookup, shiftX, shiftZ, CAVE_FRACTURE);

        ModTerrainProvider.slopedCheeseAndCaves(context, densityLookup, noiseLookup, shiftX, shiftZ, R_SLOPED_CHEESE, CAVES);

        ModTerrainProvider.makeNoiseRouter(context, densityLookup, noiseLookup, shiftX, shiftZ, TOPOGRAPHY_BASIC,TOPOGRAPHY, R_BARRIER, R_FLUID_LEVEL_FLOODEDNESS, R_FLUID_LEVEL_SPREAD, R_LAVA_NOISE, R_TEMPERATURE, R_VEGETATION, R_DEPTH, R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS, R_TOPOGRAPHY_FINAL_DENSITY, R_VEIN_TOGGLE, R_VEIN_RIDGED, R_VEIN_GAP);

        ModTerrainProvider.makeTerrain(
                densityLookup, noiseLookup, context,
                SURFACE_BASE, TOPOGRAPHY_BASIC, R_EROSION, R_CONTINENTALNESS, R_RIDGES,
                SURFACE_MOUNTAINS_TECTONICS
        );

        return context.register(TECTONIC_ACTIVITY_OLD, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.TECTONIC_ACTIVITY)));

    }

    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, WildernessRebornMod.mod(location));
    }


    private static DensityFunction valleyMask(DensityFunction densityFunction) {
        return DensityFunctions.mul(
                DensityFunctions.add(
                        DensityFunctions.constant(-0.87F),
                        DensityFunctions.add(
                                densityFunction.abs(),
                                DensityFunctions.constant(-0.9F)
                        ).abs()
                ),
                DensityFunctions.constant(-12.0F)
        );
    }

    public static float valleyMask(float weirdness) {
        return -(Math.abs(Math.abs(weirdness) - 0.66667F) - 0.33334F) * 3.0F;
    }
}
