package com.fuyuaki.wilderness_reborn.data.worldgen;

import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoiseRouterData;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoises;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModWorldGenConstants;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.NoiseRouterFunctions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Arrays;

public class ModTerrainProvider extends NoiseRouterFunctions {
    private static final float DEEP_OCEAN_CONTINENTALNESS = -0.51F;
    private static final float OCEAN_CONTINENTALNESS = -0.4F;
    private static final float PLAINS_CONTINENTALNESS = 0.1F;
    private static final float BEACH_CONTINENTALNESS = -0.15F;
    private static final ToFloatFunction<Float> NO_TRANSFORM = ToFloatFunction.IDENTITY;


    public static void makeNoiseRouter(BootstrapContext<DensityFunction> context,
                                       HolderGetter<DensityFunction> densityLookup,
                                       HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
                                       DensityFunction shiftX,
                                       DensityFunction shiftZ,
                                       ResourceKey<DensityFunction> elevationKey,
                                       ResourceKey<DensityFunction> offsetKey,
                                       ResourceKey<DensityFunction> barrierKey,
                                       ResourceKey<DensityFunction> fluidLevelFloodednessKey,
                                       ResourceKey<DensityFunction> fluidLevelSpreadKey,
                                       ResourceKey<DensityFunction> lavaNoiseKey,
                                       ResourceKey<DensityFunction> temperatureKey,
                                       ResourceKey<DensityFunction> vegetationKey,
                                       ResourceKey<DensityFunction> depthKey,
                                       ResourceKey<DensityFunction> initialDensityWithoutJaggednessKey,
                                       ResourceKey<DensityFunction> topographyFinalDensityKey,
                                       ResourceKey<DensityFunction> veinToggleKey,
                                       ResourceKey<DensityFunction> veinRidgedKey,
                                       ResourceKey<DensityFunction> veinGapKey) {

        context.register(lavaNoiseKey, DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.AQUIFER_LAVA), 0.75, 0.75));
        context.register(temperatureKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.TEMPERATURE)));
        context.register(vegetationKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.VEGETATION)));
        context.register(veinGapKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_GAP)));
        context.register(veinRidgedKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_VEIN_A)));
        context.register(veinToggleKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_VEININESS)));
        context.register(fluidLevelFloodednessKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, (noiseLookup.getOrThrow(ModNoises.AQUIFER_FLOOD))));
        context.register(fluidLevelSpreadKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.AQUIFER_SPREAD)));

        context.register(barrierKey,
                DensityFunctions.add(
                        DensityFunctions.mul(
                                getCachedFunction(densityLookup,ModNoiseRouterData.CAVE_ENDOGENES),
                                DensityFunctions.constant(-0.6F)
                        ).clamp(0.0F,2.0F),
                        DensityFunctions.noise(noiseLookup.getOrThrow(Noises.AQUIFER_BARRIER), 0.75, 0.5)
                )
        );


        DensityFunction worldOffset = registerAndWrap(context, offsetKey,
                DensityFunctions.add(
                        DensityFunctions.constant(-0.70F),
                        DensityFunctions.cacheOnce(
                                getFunction(densityLookup, elevationKey)

                        )
                )
        );

        DensityFunction depthGradient = registerAndWrap(context, depthKey,
                DensityFunctions.add(
                        worldOffset,
                        DensityFunctions.yClampedGradient(ModWorldGenConstants.WORLD_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT, ModWorldGenConstants.BOTTOM_DENSITY, ModWorldGenConstants.TOP_DENSITY)
                )

        );

        context.register(
                initialDensityWithoutJaggednessKey,
                                DensityFunctions.cacheOnce(depthGradient)
                        .clamp(-64, 64)
        );

        context.register(
                topographyFinalDensityKey,
                DensityFunctions.interpolated(
                                DensityFunctions.min(
                                        getFunction(densityLookup, ModNoiseRouterData.R_SLOPED_CHEESE),
                                        DensityFunctions.add(
                                                getFunction(densityLookup, ModNoiseRouterData.TERRAIN_CAVES),
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(2.0F),
                                                        DensityFunctions.mul(
                                                                DensityFunctions.constant(-10.0F),
                                                                depthGradient.abs()
                                                        )
                                                ).clamp(0.0F,64.0F)
                                        )
                                        )
                )

        );


    }

    public static void makeTerrain(
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            BootstrapContext<DensityFunction> context,
            ResourceKey<DensityFunction> terrainPre,
            ResourceKey<DensityFunction> elevationKey,
            ResourceKey<DensityFunction> erosionKey,
            ResourceKey<DensityFunction> continentsKey,
            ResourceKey<DensityFunction> ridgesKey,
            ResourceKey<DensityFunction> tectonicTerrainKey
    ) {

        Holder<DensityFunction> landErosion = densityLookup.getOrThrow(ModNoiseRouterData.LAND_EROSION);

        makeTerrainPreTectonicActivity(densityLookup, context, terrainPre);

        makeTectonicBasedTerrain(context,densityLookup, tectonicTerrainKey);


        context.register(
                elevationKey,
                DensityFunctions.cacheOnce(
                                DensityFunctions.add(
                                        getFunction(densityLookup,terrainPre),
                                        getFunction(densityLookup,tectonicTerrainKey)
                                )
                        )

        );



        context.register(
                erosionKey,
                DensityFunctions.cache2d(
                        DensityFunctions.spline(
                                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                                        .addPoint(-1.5F, -1.5F)
                                        .addPoint(-1.0F, -1.0F)
                                        .addPoint(0.0F, 0.0F)
                                        .addPoint(1.0F, 1.0F)
                                        .addPoint(1.5F, 1.5F)
                                        .build()
                        )
                )
        );


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> elevationContinentalOffset =
                CubicSpline.builder(splineCoordinatesFrom(
                                getCachedFunction(densityLookup,tectonicTerrainKey)
                        ), NO_TRANSFORM)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(0.1F, 0.1F)
                        .addPoint(0.5F, 0.5F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> elevationSeaOffset =
                CubicSpline.builder(splineCoordinatesFrom(
                                getCachedFunction(densityLookup,tectonicTerrainKey)
                        ), NO_TRANSFORM)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(0.1F, 0.5F)
                        .addPoint(0.5F, 1.5F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> elevationIslandOffset =
                CubicSpline.builder(splineCoordinatesFrom(
                                getCachedFunction(densityLookup,tectonicTerrainKey)
                        ), NO_TRANSFORM)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(0.1F, -0.5F)
                        .addPoint(0.5F, -1.5F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> elevationLandOffset =
                CubicSpline.builder(splineCoordinatesFrom(
                                getCachedFunction(densityLookup,tectonicTerrainKey)
                        ), NO_TRANSFORM)
                        .addPoint(-1.0F, -1.5F)
                        .addPoint(-0.5F, -0.5F)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(0.5F, 0.0F)
                        .addPoint(1.0F, 0.25F)
                        .build();


        context.register(
                continentsKey,
                DensityFunctions.cache2d(
                        DensityFunctions.add(
                                DensityFunctions.mul(
                                        DensityFunctions.constant(0.8F),
                                        getFunction(densityLookup,ModNoiseRouterData.LAND_CONTINENTS)
                                ),
                                DensityFunctions.spline(
                                        CubicSpline.builder(splineCoordinatesFrom(
                                                        getCachedFunction(densityLookup,ModNoiseRouterData.LAND_CONTINENTS)
                                                ), NO_TRANSFORM)
                                                .addPoint(-1.5F, elevationIslandOffset)
                                                .addPoint(-1.0F, elevationSeaOffset)
                                                .addPoint(-0.15F, elevationContinentalOffset)
                                                .addPoint(0.15F, 0.0F)
                                                .addPoint(1.0F, elevationLandOffset)
                                                .build()
                                )
                        )
                )
        );


        context.register(
                ridgesKey,
                DensityFunctions.cache2d(
                        DensityFunctions.spline(
                                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                                        .addPoint(-1.5F, -1.5F)
                                        .addPoint(-1.0F, -1.0F)
                                        .addPoint(0.0F, 0.0F)
                                        .addPoint(1.0F, 1.0F)
                                        .addPoint(1.5F, 1.5F)
                                        .build()
                        )
                )
        );

    }

    private static void makeTerrainPreTectonicActivity(HolderGetter<DensityFunction> densityLookup, BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> terrainPre) {
        Holder<DensityFunction> landContinents = densityLookup.getOrThrow(ModNoiseRouterData.LAND_CONTINENTS);
        Holder<DensityFunction> landErosion = densityLookup.getOrThrow(ModNoiseRouterData.LAND_EROSION);

        Holder<DensityFunction> terrainBase = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_BASE);
        Holder<DensityFunction> terrainBaseSmooth = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_BASE_SMOOTH);

        Holder<DensityFunction> terrainSmoothness = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_SMOOTHNESS);


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainCoastalSeaShallow =
                splineMinMax(terrainBaseSmooth,-0.05F,-0.025F,1.5F);
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainCoastalSea =
                splineMinMax(terrainBaseSmooth,-0.1F,-0.05F,1.5F);
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSea =
                splineMinMax(terrainBaseSmooth,-0.5F,-0.1F,1.5F);
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDeep =
                splineMinMax(terrainBaseSmooth,-1.0F,-0.2F,1.5F);
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDeepEroded =
                splineMinMax(terrainBaseSmooth,-1.0F,-0.8F,1.5F);


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainContinentsLowErosionJagged =
                splineAutoDerivative(
                        landContinents,
                        Pair.of(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F)),
                        Pair.of(-1.0F, terrainDeepEroded),
                        Pair.of(-0.75F, terrainDeep),
                        Pair.of(-0.5F, terrainSea),
                        Pair.of(-0.20F, terrainCoastalSea),
                        Pair.of(-0.12F, splineMinMax(terrainBase,0.0F,0.1F,1.5F)),
                        Pair.of(0.45F, splineMinMax(terrainBase,0.0F,0.5F,1.5F)),
                        Pair.of(0.7F, splineMinMaxSharp(terrainBase,0.1F,0.9F,1.5F))
                );
//                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
//                        .addPoint(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F))
//                        .addPoint(-1.0F, terrainDeepEroded)
//                        .addPoint(-0.75F, terrainDeep)
//                        .addPoint(-0.5F, terrainSea,0.5F)
//                        .addPoint(-0.20F, terrainCoastalSea,0.2F)
//                        .addPoint(-0.10F, splineMinMax(terrainBase,0.0F,0.1F,1.5F),0.1F)
//                        .addPoint(0.35F, splineMinMax(terrainBase,0.0F,0.5F,1.5F))
//                        .addPoint(0.7F, splineMinMaxSharp(terrainBase,0.0F,0.9F,1.5F))
//                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainContinentsHighErosionJagged =
                splineAutoDerivative(
                        landContinents,
                        Pair.of(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.1F,1.5F)),
                        Pair.of(-1.0F, terrainDeepEroded),
                        Pair.of(-0.5F, terrainSea),
                        Pair.of(-0.20F, terrainCoastalSeaShallow),
                        Pair.of(-0.12F, splineMinMax(terrainBase,0.0F,0.05F,1.5F)),
                        Pair.of(0.6F, splineMinMax(terrainBase,0.0F,0.2F,1.5F)),
                        Pair.of(1.0F, splineMinMaxSharp(terrainBase,0.1F,0.5F,1.5F))
                );
//                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
//                        .addPoint(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F))
//                        .addPoint(-1.0F, terrainDeepEroded)
//                        .addPoint(-0.5F, terrainSea,0.5F)
//                        .addPoint(-0.20F, terrainCoastalSeaShallow,0.2F)
//                        .addPoint(-0.10F, splineMinMax(terrainBase,0.0F,0.05F,1.5F),0.1F)
//                        .addPoint(0.5F, splineMinMax(terrainBase,0.0F,0.2F,1.5F))
//                        .addPoint(1.0F, splineMinMaxSharp(terrainBase,00.5F,1.5F))
//                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainContinentsLowErosionSmooth =
                splineAutoDerivative(
                        landContinents,
                        Pair.of(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F)),
                        Pair.of(-1.0F, terrainDeepEroded),
                        Pair.of(-0.75F, terrainDeep),
                        Pair.of(-0.5F, terrainSea),
                        Pair.of(-0.20F, terrainCoastalSea),
                        Pair.of(-0.12F, splineMinMax(terrainBaseSmooth,0.0F,0.075F,1.5F)),
                        Pair.of(0.45F, splineMinMax(terrainBaseSmooth,0.0F,0.35F,1.5F)),
                        Pair.of(0.8F, splineMinMax(terrainBaseSmooth,0.1F,0.65F,1.5F))
                );
//                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
//                        .addPoint(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F))
//                        .addPoint(-1.0F, terrainDeepEroded)
//                        .addPoint(-0.75F, terrainDeep)
//                        .addPoint(-0.5F, terrainSea,0.5F)
//                        .addPoint(-0.20F, terrainCoastalSea,0.2F)
//                        .addPoint(-0.10F, splineMinMax(terrainBaseSmooth,0.0F,0.075F,1.5F),0.1F)
//                        .addPoint(0.35F, splineMinMax(terrainBaseSmooth,0.0F,0.35F,1.5F))
//                        .addPoint(0.7F, splineMinMax(terrainBaseSmooth,0.0F,0.65F,1.5F))
//                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainContinentsHighErosionSmooth =
                splineAutoDerivative(
                        landContinents,
                        Pair.of(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.1F,1.5F)),
                        Pair.of(-1.0F, terrainDeepEroded),
                        Pair.of(-0.5F, terrainSea),
                        Pair.of(-0.20F, terrainCoastalSeaShallow),
                        Pair.of(-0.12F, splineMinMax(terrainBaseSmooth,0.0F,0.05F,1.5F)),
                        Pair.of(0.6F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F)),
                        Pair.of(1.0F, splineMinMax(terrainBaseSmooth,0.1F,0.4F,1.5F))
        );


//                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
//                        .addPoint(-1.1F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F))
//                        .addPoint(-1.0F, terrainDeepEroded)
//                        .addPoint(-0.5F, terrainSea,0.5F)
//                        .addPoint(-0.20F, terrainCoastalSeaShallow,0.2F)
//                        .addPoint(-0.10F, splineMinMax(terrainBaseSmooth,0.0F,0.05F,1.5F),0.1F)
//                        .addPoint(0.5F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F))
//                        .addPoint(1.0F, splineMinMax(terrainBaseSmooth,0.0F,0.4F,1.5F))
//                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainHighErosion =
                CubicSpline.builder(splineCoordinatesFrom(terrainSmoothness), NO_TRANSFORM)
                        .addPoint(-0.7F, terrainContinentsHighErosionSmooth)
                        .addPoint(0.7F, terrainContinentsHighErosionJagged)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainLowErosion =
                CubicSpline.builder(splineCoordinatesFrom(terrainSmoothness), NO_TRANSFORM)
                        .addPoint(-0.7F, terrainContinentsLowErosionSmooth)
                        .addPoint(0.7F, terrainContinentsLowErosionJagged)
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainErosion =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-0.7F, terrainLowErosion)
                        .addPoint(0.7F, terrainHighErosion)
                        .build();


        context.register(
                terrainPre,
                DensityFunctions.cacheOnce(
                        DensityFunctions.interpolated(
                                DensityFunctions.spline(
                                        terrainErosion
                                )
                        )
                )
        );
    }

    private static void makeTectonicBasedTerrain(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityLookup,
            ResourceKey<DensityFunction> tectonicSurfaceKey) {

        Holder<DensityFunction> landContinents = densityLookup.getOrThrow(ModNoiseRouterData.LAND_CONTINENTS);
        Holder<DensityFunction> landErosion = densityLookup.getOrThrow(ModNoiseRouterData.LAND_EROSION);

        Holder<DensityFunction> tectonicPlates = densityLookup.getOrThrow(ModNoiseRouterData.GEO_TECTONIC_PLATES);
        Holder<DensityFunction> tectonicEdges = densityLookup.getOrThrow(ModNoiseRouterData.TECTONIC_EDGES);
        Holder<DensityFunction> tectonicDirection = densityLookup.getOrThrow(ModNoiseRouterData.TECTONIC_DIRECTION);
        Holder<DensityFunction> tectonicRandomness = densityLookup.getOrThrow(ModNoiseRouterData.TECTONIC_RANDOMNESS);
        Holder<DensityFunction> tectonicActivity = densityLookup.getOrThrow(ModNoiseRouterData.TECTONIC_ACTIVITY);


        Holder<DensityFunction> tectonicTerrain = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_TECTONIC);
        Holder<DensityFunction> tectonicTerrainSmooth = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_TECTONIC_SMOOTH);
        Holder<DensityFunction> plateauTerrain = densityLookup.getOrThrow(ModNoiseRouterData.TERRAIN_PLATEAU);

        // DIRECTION -> Negative = Away from Each-other, Positive = Towards Each-other

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergeLand =
                CubicSpline.builder(splineCoordinatesFrom(tectonicTerrain), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.75F)
                        .addPoint(-0.6F, -0.6F)
                        .addPoint(-0.25F, -0.55F)
                        .addPoint(0.25F, -0.5F)
                        .addPoint(0.6F, 0.0F)
                        .addPoint(1.0F, 0.75F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergeLandEroded =
                CubicSpline.builder(splineCoordinatesFrom(tectonicTerrainSmooth), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.5F)
                        .addPoint(-0.3F, -0.45F)
                        .addPoint(0.3F, -0.5F)
                        .addPoint(1.0F, 0.75F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergeLandPlateau =
                CubicSpline.builder(splineCoordinatesFrom(plateauTerrain), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.75F)
                        .addPoint(1.0F, 0.75F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergeLandErodedPlateau =
                CubicSpline.builder(splineCoordinatesFrom(plateauTerrain), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.25F)
                        .addPoint(1.0F, 0.25F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDiverge =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, terrainDivergeLand)
                        .addPoint(1.0F, terrainDivergeLandEroded)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergePlateau =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, terrainDivergeLandPlateau)
                        .addPoint(1.0F, terrainDivergeLandErodedPlateau)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainDivergeRandomized =
                CubicSpline.builder(splineCoordinatesFrom(tectonicRandomness), NO_TRANSFORM)
                        .addPoint(-0.1F, terrainDiverge)
                        .addPoint(0.1F, terrainDivergePlateau)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainFold =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMaxSharp(tectonicTerrain,0.2F,2.0F))
                        .addPoint(1.0F, splineMinMax(tectonicTerrainSmooth,0.2F,1.2F))
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueLand =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMaxSharp(tectonicTerrain,0.7F,2.5F))
                        .addPoint(1.0F, splineMinMax(tectonicTerrain,0.4F,0.9F))
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueOcean =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMaxSharp(tectonicTerrain,-0.9F,-0.3F),1.0F)
                        .addPoint(1.0F, splineMinMax(tectonicTerrain,-0.5F,-0.1F))
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainTrench =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMaxSharp(tectonicTerrain,-0.9F,-0.7F))
                        .addPoint(1.0F, splineMinMax(tectonicTerrain,-0.8F,-0.5F))
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdue =
                CubicSpline.builder(splineCoordinatesFrom(tectonicPlates), NO_TRANSFORM)
                        .addPoint(-0.2F, terrainSubdueOcean)
                        .addPoint(0.15F, terrainSubdueLand)
                        .addPoint(0.5F, splineMinMax(plateauTerrain,0.0F,0.4F,1.0F))
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueOpposite =
                CubicSpline.builder(splineCoordinatesFrom(tectonicPlates), NO_TRANSFORM)
                        .addPoint(-0.5F, splineMinMax(plateauTerrain,0.0F,0.4F,1.0F))
                        .addPoint(-0.2F, terrainSubdueLand)
                        .addPoint(0.15F, terrainSubdueOcean)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueRandomized =
                CubicSpline.builder(splineCoordinatesFrom(tectonicRandomness), NO_TRANSFORM)
                        .addPoint(-0.1F, terrainSubdue)
                        .addPoint(0.1F, terrainSubdueOpposite)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainCollision =
                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
                        .addPoint(-0.50F, terrainTrench)
                        .addPoint(-0.35F, terrainSubdueRandomized)
                        .addPoint(-0.10F, terrainSubdueRandomized)
                        .addPoint(0.15F, terrainFold)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainMovementBased =
                CubicSpline.builder(splineCoordinatesFrom(tectonicDirection), NO_TRANSFORM)
                        .addPoint(-0.5F, terrainDivergeRandomized)
                        .addPoint(-0.15F,0.0F)
                        .addPoint(0.15F,0.0F)
                        .addPoint(0.5F, terrainCollision)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tectonicActivityBasedTerrain =
                CubicSpline.builder(splineCoordinatesFrom(tectonicActivity), NO_TRANSFORM)
                        .addPoint(-0.5F, 0.0F)
                        .addPoint(0.15F, terrainMovementBased)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tectonicEdgeTerrain =
                CubicSpline.builder(splineCoordinatesFrom(tectonicEdges), NO_TRANSFORM)
                        .addPoint(-0.1F, 0.0F)
                        .addPoint(0.75F, tectonicActivityBasedTerrain)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tectonicTerrainNoise =
                CubicSpline.builder(splineCoordinatesFrom(tectonicEdges), NO_TRANSFORM)
                        .addPoint(-1.5F, 0.0F)
                        .addPoint(0.50F,  splineZeroMax(tectonicTerrain,-0.15F,0.15F))
                        .addPoint(0.75F,  splineZeroMaxSharp(tectonicTerrain,-0.15F,0.2F))
                        .build();


        context.register(
                tectonicSurfaceKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.spline(
                                        tectonicEdgeTerrain
                                ),
                                DensityFunctions.spline(
                                        tectonicTerrainNoise
                                )
                        )

                        )
        );
    }


    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineMinMax(Holder<DensityFunction> function, float min, float max) {
         return splineAuto(function,
                 Pair.of(-1.0F,min),
                 Pair.of(1.0F,max)
         );
    }

    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineMinMax(Holder<DensityFunction> function, float min, float max,float range) {
         return splineAuto(function,
                 Pair.of(-range,min),
                 Pair.of(range,max)
         );
    }
    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineZeroMax(Holder<DensityFunction> function, float min, float max) {
        return CubicSpline.builder(splineCoordinatesFrom(function), NO_TRANSFORM)
                .addPoint(0.0F,min)
                .addPoint(1.0F,max)
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineZeroMaxSharp(Holder<DensityFunction> function, float min, float max) {
        return CubicSpline.builder(splineCoordinatesFrom(function), NO_TRANSFORM)
                .addPoint(0.0F,min,0.5F)
                .addPoint(1.0F,max, 1.2F)
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineZeroMaxVerySharp(Holder<DensityFunction> function, float min, float max) {

        return CubicSpline.builder(splineCoordinatesFrom(function), NO_TRANSFORM)
                .addPoint(0.0F,min,1.5F)
                .addPoint(1.0F,max, 1.8F)
                .build();
    }

    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineMinMaxSharp(Holder<DensityFunction> function, float min, float max,float range) {
         return CubicSpline.builder(splineCoordinatesFrom(function), NO_TRANSFORM)
                .addPoint(-range,min)
                .addPoint(range,max,0.4F)
                .build();
    }
    private static CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> splineMinMaxSharp(Holder<DensityFunction> function, float min, float max) {
         return CubicSpline.builder(splineCoordinatesFrom(function), NO_TRANSFORM)
                .addPoint(-1.0F,min)
                .addPoint(1.0F,max,0.4F)
                .build();
    }

    public static void slopedCheeseAndCaves(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            DensityFunction shiftX,
            DensityFunction shiftZ,
            ResourceKey<DensityFunction> slopedCheese,
            ResourceKey<DensityFunction> caves
    ) {

        DensityFunction densityNoJaggedness =
                DensityFunctions.cacheOnce(getFunction(densityLookup, ModNoiseRouterData.R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS));


        context.register(
                slopedCheese,
                DensityFunctions.cacheOnce(
                        DensityFunctions.interpolated(
                                DensityFunctions.add(
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(0.05F),
                                                getFunction(densityLookup, ModNoiseRouterData.LAND_NOISE)
                                        ),
                                        densityNoJaggedness
                                )
                        )
                )

        );


        context.register(caves,
                DensityFunctions.add(
                        DensityFunctions.add(
                                DensityFunctions.add(
                                        DensityFunctions.add(
                                                DensityFunctions.yClampedGradient(ModWorldGenConstants.CAVES_BOTTOM, ModWorldGenConstants.CAVES_BOTTOM_CENTER, 1.5F, 0.0F),
                                                DensityFunctions.yClampedGradient(ModWorldGenConstants.WORLD_BOTTOM, ModWorldGenConstants.CAVES_BOTTOM, 2.5F, 0.0F)
                                        ),
                                        DensityFunctions.yClampedGradient(ModWorldGenConstants.CAVES_TOP, ModWorldGenConstants.CAVES_BASE_TOP, 2.5F, 0.0F)
                                ),
                                DensityFunctions.mul(
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_DENSITY), 0.25F, 0.5F).abs().clamp(0.0F, 1000.0F),
                                        DensityFunctions.constant(2.5F)
                                )
                        ),
                        DensityFunctions.mul(
                                DensityFunctions.constant(1.5F),
                                DensityFunctions.min(
                                        DensityFunctions.add(
                                                DensityFunctions.min(

                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_ENDOGENES),
                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_EXOGENES)
                                                ),
                                                DensityFunctions.spline(
                                                        CubicSpline.builder(splineCoordinatesFrom(getCachedFunction(densityLookup, ModNoiseRouterData.CAVE_FILTER)))
                                                                .addPoint(-1.0F, 1.5F)
                                                                .addPoint(0.0F, 0.0F)
                                                                .addPoint(1.0F, 0.0F)
                                                                .build()
                                                )
                                        ),
                                        DensityFunctions.min(
                                                getFunction(densityLookup, ModNoiseRouterData.CAVE_CRACKS),
                                                getFunction(densityLookup, ModNoiseRouterData.CAVE_NOODLE)

                                        )
                                )
                        )
                )

        );


    }

    @SafeVarargs
    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> splineAutoDerivative(Holder<DensityFunction> coordinate, Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>>... points){
        CubicSpline.Builder<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> builder = CubicSpline.builder(splineCoordinatesFrom(coordinate),NO_TRANSFORM);
        for (int i = 0; i < points.length; i++){
            Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> pair = Arrays.stream(points).toList().get(i);
            float avg = (pair.getSecond().minValue() + pair.getSecond().maxValue()) / 2;
            float derivative = 0;
            float location = pair.getFirst();
            if (i > 0 && i < points.length - 1){
                Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> pairBefore = Arrays.stream(points).toList().get(i - 1);
                Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> pairAfter = Arrays.stream(points).toList().get(i + 1);

                float locationBefore = pairBefore.getFirst();
                float locationAfter = pairAfter.getFirst();

                float beforeAvg = (pairBefore.getSecond().minValue() + pairBefore.getSecond().maxValue()) / 2;
                float afterAvg = (pairAfter.getSecond().minValue() + pairAfter.getSecond().maxValue()) / 2;

                float beforeDif = avg - beforeAvg;
                float afterDif = avg - afterAvg;

                derivative = -(( afterDif + beforeDif) / ((locationBefore + location + locationAfter) / 2)) / 2;




            }else if (i > 0){
                Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> pairBefore = Arrays.stream(points).toList().get(i - 1);
                float beforeAvg = (pairBefore.getSecond().minValue() + pairBefore.getSecond().maxValue()) / 2;
                float beforeDif = avg - beforeAvg;
                float locationBefore = pairBefore.getFirst();


                derivative = -(beforeDif / ((locationBefore + location) / 2)) / 2;


            }else if (i < points.length - 1){
                Pair<Float,CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> pairAfter = Arrays.stream(points).toList().get(i + 1);
                float afterAvg = (pairAfter.getSecond().minValue() + pairAfter.getSecond().maxValue()) / 2;
                float afterDif = avg - afterAvg;
                float locationAfter = pairAfter.getFirst();


                derivative = -(afterDif / ((location + locationAfter) / 2)) / 2;

            }
            if (Float.isInfinite(derivative) || Float.isNaN(derivative)){
                derivative = 0;
            }

            builder.addPoint(pair.getFirst(),pair.getSecond(),derivative);
        }
        return builder.build();
    }

    @SafeVarargs
    private static CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> splineAuto(Holder<DensityFunction> coordinate, Pair<Float,Float>... points){
        CubicSpline.Builder<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> builder = CubicSpline.builder(splineCoordinatesFrom(coordinate),NO_TRANSFORM);
        for (int i = 0; i < points.length; i++){
            Pair<Float,Float> pair = Arrays.stream(points).toList().get(i);
            float location = pair.getFirst();
            float value = pair.getSecond();
            double derivative = 0;
            if (i > 0 && i < points.length - 1){

                Pair<Float,Float> pairBefore = Arrays.stream(points).toList().get(i - 1);
                Pair<Float,Float> pairAfter = Arrays.stream(points).toList().get(i + 1);

                float locationBefore = pairBefore.getFirst();
                float locationAfter = pairAfter.getFirst();

                float beforeValue = pairBefore.getSecond();
                float afterValue = pairAfter.getSecond();
                float beforeDif = value - beforeValue;
                float afterDif = value - afterValue;



                derivative = -((beforeDif + afterDif) / ((pairBefore.getFirst() + pair.getFirst() + pairAfter.getFirst()) / 3)) / 2;

            }else if (i > 0){
                Pair<Float,Float> pairBefore = Arrays.stream(points).toList().get(i - 1);
                float beforeValue = pairBefore.getSecond();
                float beforeDif = value - beforeValue;

                derivative = -(beforeDif / ((pairBefore.getFirst() + pair.getFirst()) / 2)) / 2;


            }else if (i < points.length - 1){
                Pair<Float,Float> pairAfter = Arrays.stream(points).toList().get(i + 1);
                float afterValue = pairAfter.getSecond();
                float afterDif = value - afterValue;



                derivative = -(afterDif / ((pair.getFirst() + pairAfter.getFirst()) / 2)) / 2;

            }
            if (Double.isInfinite(derivative) || Double.isNaN(derivative)){
                derivative = 0;
            }

            builder.addPoint(pair.getFirst(),pair.getSecond(), (float) derivative);
        }
        return builder.build();
    }
}