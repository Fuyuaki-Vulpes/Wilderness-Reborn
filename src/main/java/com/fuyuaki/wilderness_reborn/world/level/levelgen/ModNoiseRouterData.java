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
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseRouterData extends NoiseRouterFunctions {


    //TERRAIN
    public static final ResourceKey<DensityFunction> TERRAIN_BASE = createKey("terrain/base");
    public static final ResourceKey<DensityFunction> TERRAIN_BASE_SMOOTH = createKey("terrain/base_smooth");
    public static final ResourceKey<DensityFunction> TERRAIN_SMOOTHNESS = createKey("terrain/smoothness");
    public static final ResourceKey<DensityFunction> TERRAIN_TECTONIC = createKey("terrain/tectonic");
    public static final ResourceKey<DensityFunction> TERRAIN_PLATEAU = createKey("terrain/plateau");
    public static final ResourceKey<DensityFunction> TERRAIN_CAVES = createKey("terrain/caves");


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


    public static final ResourceKey<DensityFunction> CAVE_NOODLE = createKey("cave/noodle");
    public static final ResourceKey<DensityFunction> CAVE_PILLARS = createKey("cave/detail/pillars");
    public static final ResourceKey<DensityFunction> CAVE_DETAIL_PILLARS = createKey("cave/detail/pillars_secondary");
    public static final ResourceKey<DensityFunction> CAVE_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<DensityFunction> CAVE_EXOGENES = createKey("cave/exogenes");
    public static final ResourceKey<DensityFunction> CAVE_ENDOGENES = createKey("cave/endogenes");


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

    //ELEVATION
    public static final ResourceKey<DensityFunction> OFFSET = createKey("offset");
    public static final ResourceKey<DensityFunction> ELEVATION = createKey("elevation");

    //BIOME
    public static final ResourceKey<DensityFunction> BIOME_VARIATION = createKey("biome/variation");


    public static Holder<? extends DensityFunction> bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        DensityFunction shiftX = getFunction(densityLookup, PackNoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(densityLookup, PackNoiseRouterData.SHIFT_Z);

        context.register(TERRAIN_BASE,
                DensityFunctions.flatCache(
                        DensityFunctions.lerp(
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_BLENDER)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                )
                        )
                )
        );


        context.register(TERRAIN_BASE_SMOOTH,
                DensityFunctions.flatCache(
                        DensityFunctions.lerp(
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_BLENDER_SMOOTH)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                )
                        )
                )
        );


        simpleShiftedNoiseDensity(context, TERRAIN_SMOOTHNESS, shiftX, shiftZ, noiseLookup, ModNoises.TERRAIN_SMOOTHNESS, 0.25F);


        context.register(LAND_CONTINENTS,
                DensityFunctions.flatCache(
                        DensityFunctions.add(
                                DensityFunctions.constant(-0.2F),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F,
                                        noiseLookup.getOrThrow(ModNoises.LAND_CONTINENTS))


                        )
                )
        );

        simpleShiftedNoiseDensity(context, LAND_EROSION, shiftX, shiftZ, noiseLookup, ModNoises.LAND_EROSION, 0.25F);
        simpleShiftedNoiseDensity(context, BIOME_VARIATION, shiftX, shiftZ, noiseLookup, ModNoises.BIOME_VARIATION, 0.25F);

        context.register(GEO_TECTONIC_PLATES,
                DensityFunctions.flatCache(
                        DensityFunctions.mul(
                                DensityFunctions.constant(10.0F),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F,
                                        noiseLookup.getOrThrow(ModNoises.GEO_TECTONICS))
                        ).clamp(-1.0F, 1.0F)
                )
        );

        context.register(TERRAIN_TECTONIC,
                DensityFunctions.cacheOnce(
                        DensityFunctions.lerp(
                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_BLENDER),
                                        0.25F, 0.1F
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_A)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TECTONIC_TERRAIN_B)
                                )
                        )
                )
        );


        context.register(TERRAIN_PLATEAU,
                DensityFunctions.cacheOnce(
                        DensityFunctions.lerp(
                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.TERRAIN_PLATEAU_BLENDER),
                                        0.25F, 0.1F
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_PLATEAU_A)
                                ),
                                DensityFunctions.shiftedNoise2d(
                                        shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_PLATEAU_B)
                                )
                        ).clamp(-2.0F, 2.0F)
                )
        );


        context.register(TECTONIC_EDGES,
                DensityFunctions.flatCache(
                        DensityFunctions.mul(
                                DensityFunctions.constant(-1.2F),
                                DensityFunctions.add(
                                        DensityFunctions.constant(
                                                -0.52F
                                        ),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(7.0F),
                                                DensityFunctions.shiftedNoise2d(
                                                        shiftX, shiftZ, 0.25F,
                                                        noiseLookup.getOrThrow(ModNoises.GEO_TECTONICS)
                                                ).abs()
                                        )
                                )
                        )
                )
        );


        simpleShiftedNoiseDensity(context, TECTONIC_DIRECTION, shiftX, shiftZ, noiseLookup, ModNoises.TECTONIC_DIRECTION, 0.25F);
        simpleShiftedNoiseDensity(context, TECTONIC_RANDOMNESS, shiftX, shiftZ, noiseLookup, ModNoises.TECTONIC_RANDOMNESS, 0.25F);
        simpleShiftedNoiseDensity(context, TECTONIC_ACTIVITY, shiftX, shiftZ, noiseLookup, ModNoises.TECTONIC_FACTOR_ACTIVITY, 0.25F);


        context.register(LAND_NOISE,
                DensityFunctions.flatCache(
                        DensityFunctions.mul(
                                getFunction(densityLookup, R_CONTINENTALNESS),
                                DensityFunctions.mul(
                                        DensityFunctions.lerp(
                                                DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.LAND_NOISE_BLENDER_2)).clamp(-1, 1),
                                                DensityFunctions.lerp(
                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.LAND_NOISE_BLENDER)
                                                        ),

                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_A)
                                                        ),

                                                        DensityFunctions.shiftedNoise2d(
                                                                shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.TERRAIN_B)
                                                        )
                                                ),
                                                DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.LAND_NOISE_C))
                                        ),
                                        DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25F, noiseLookup.getOrThrow(ModNoises.LAND_NOISE_STRENGHT)).clamp(-1, 1)
                                )
                        ).abs()
                )

        );





        context.register(CAVE_NOODLE,
                DensityFunctions.add(
                        DensityFunctions.constant(-0.1F),
                        DensityFunctions.add(
                                DensityFunctions.mul(
                                        DensityFunctions.constant(2.0F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_NOODLES_FILTER)).abs()
                                ),
                                DensityFunctions.add(
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(5.0F),
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_NOODLES_DENSITY),
                                                        0.25F, 0.75F
                                                )
                                        ).clamp(-0.5F, 10.0F),
                                        DensityFunctions.mul(
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_NOODLES),
                                                                0.75F, 1.5F)
                                                        .abs(),
                                                DensityFunctions.constant(2.5F)
                                        )
                                )
                        )
                )


        );


        context.register(CAVE_PILLARS,
                DensityFunctions.add(
                        DensityFunctions.add(
                                DensityFunctions.yClampedGradient(32,72,0.0F,-1.5F),
                                DensityFunctions.yClampedGradient(72,128,0.0F,-2.5F)
                        ),
                DensityFunctions.mul(
                        DensityFunctions.add(
                                DensityFunctions.mul(
                                        DensityFunctions.constant(2.0F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_PILLAR), 10, 0.1F)
                                ),
                                DensityFunctions.add(
                                        DensityFunctions.constant(-1.25F),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(-1.0F),
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_PILLAR_RARITY), 1.5F, 1.5F)
                                        )
                                )
                        ),
                        DensityFunctions.add(
                                DensityFunctions.constant(0.55F),
                                DensityFunctions.mul(
                                        DensityFunctions.constant(0.55F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_PILLAR_DENSITY))
                                )
                        ).cube()
                )
                )

        );
        context.register(CAVE_DETAIL_PILLARS,
                DensityFunctions.add(
                        DensityFunctions.add(
                                DensityFunctions.yClampedGradient(32,72,0.0F,-1.5F),
                                DensityFunctions.yClampedGradient(72,128,0.0F,-1.5F)
                        ),
                DensityFunctions.mul(
                        DensityFunctions.constant(5.0F),

                        DensityFunctions.mul(
                                DensityFunctions.add(
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(2.0F),
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_SECONDARY_PILLAR), 25.0F, 0.5F)
                                        ),
                                        DensityFunctions.add(
                                                DensityFunctions.constant(-1.5F),
                                                DensityFunctions.mul(
                                                        DensityFunctions.constant(-0.75F),
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_SECONDARY_PILLAR_RARITY), 2.0F, 2.0F)
                                                )
                                        )
                                ),
                                DensityFunctions.add(
                                        DensityFunctions.constant(1.0F),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(0.50F),
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_SECONDARY_PILLAR_DENSITY), 2.0F, 2.0F)
                                        )
                                ).cube()
                        )
                )
                ).clamp(0.00F, 1.5F)
        );


        context.register(CAVE_CRACKS,
                DensityFunctions.rangeChoice(
                        getFunction(densityLookup, CAVE_PILLARS),
                        0.05F,
                        1000.0F,
                        getFunction(densityLookup, CAVE_PILLARS),
                        DensityFunctions.add(
                                DensityFunctions.mul(
                                        DensityFunctions.constant(
                                                1.5F
                                        ),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_CRACKS_FREQUENCY),
                                                0.25F, 1.5F
                                        )
                                ).clamp(0.0F, 5.0F),
                                DensityFunctions.add(
                                        DensityFunctions.constant(0.5F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_CRACKS),
                                                0.5F, 10.0F)
                                )
                        )
                )

        );

        context.register(CAVE_EXOGENES,
                DensityFunctions.add(
                        DensityFunctions.constant(0.15F),
                        DensityFunctions.mul(
                                DensityFunctions.constant(1.5F),
                                DensityFunctions.add(
                                        DensityFunctions.constant(0.25F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_EXOGENES),
                                                0.5F, 3.0F)
                                )
                        )
                )


        );

        context.register(CAVE_ENDOGENES,
                DensityFunctions.add(
                        DensityFunctions.add(
                                DensityFunctions.yClampedGradient(0,72,0.0F,0.5F),
                                DensityFunctions.yClampedGradient(72,128,0.0F,1.0F)
                        ),
                        DensityFunctions.rangeChoice(
                                getFunction(densityLookup, CAVE_PILLARS),
                                0.001F,
                                1000.0F,
                                getFunction(densityLookup, CAVE_PILLARS),
                                DensityFunctions.add(
                                        DensityFunctions.constant(0.55F),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(1.5F),
                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_ENDOGENES),
                                                        0.75F, 3.0F)
                                        )
                                )

                        )
                )

        );


        ModTerrainProvider.slopedCheeseAndCaves(context, densityLookup, noiseLookup, shiftX, shiftZ, R_SLOPED_CHEESE, TERRAIN_CAVES);

        ModTerrainProvider.makeNoiseRouter(context, densityLookup, noiseLookup, shiftX, shiftZ, ELEVATION, OFFSET, R_BARRIER, R_FLUID_LEVEL_FLOODEDNESS, R_FLUID_LEVEL_SPREAD, R_LAVA_NOISE, R_TEMPERATURE, R_VEGETATION, R_DEPTH, R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS, R_TOPOGRAPHY_FINAL_DENSITY, R_VEIN_TOGGLE, R_VEIN_RIDGED, R_VEIN_GAP);

        ModTerrainProvider.makeTerrain(
                densityLookup, noiseLookup, context,
                SURFACE_BASE, ELEVATION, R_EROSION, R_CONTINENTALNESS, R_RIDGES,
                SURFACE_MOUNTAINS_TECTONICS
        );

        return densityLookup.getOrThrow(R_TOPOGRAPHY_FINAL_DENSITY);

    }

    private static void simpleShiftedNoiseDensity(BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction shiftX, DensityFunction shiftZ, HolderGetter<NormalNoise.NoiseParameters> noiseLookup, ResourceKey<NormalNoise.NoiseParameters> noise, float scale) {
        context.register(key,
                DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(
                                shiftX, shiftZ, scale,
                                noiseLookup.getOrThrow(noise)
                        )


                )
        );
    }

    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, WildernessRebornMod.mod(location));
    }


    private static ResourceKey<DensityFunction> old(String location) {
        return createKey("old/" + location);
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