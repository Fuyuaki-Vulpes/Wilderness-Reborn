package com.fuyuaki.wilderness_reborn.data.worldgen;

import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
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

        context.register(barrierKey, DensityFunctions.noise(noiseLookup.getOrThrow(Noises.AQUIFER_BARRIER), 0.75, 0.5));
        context.register(lavaNoiseKey, DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.AQUIFER_LAVA), 0.75, 0.75));
        context.register(temperatureKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.TEMPERATURE)));
        context.register(vegetationKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(ModNoises.VEGETATION)));
        context.register(veinGapKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_GAP)));
        context.register(veinRidgedKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_VEIN_A)));
        context.register(veinToggleKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.ORE_VEININESS)));
        context.register(fluidLevelFloodednessKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25,(noiseLookup.getOrThrow(ModNoises.AQUIFER_FLOOD))));
        context.register(fluidLevelSpreadKey, DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25,noiseLookup.getOrThrow(ModNoises.AQUIFER_SPREAD)));





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
                        DensityFunctions.yClampedGradient(ModWorldGenConstants.WORLD_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT, ModWorldGenConstants.BOTTOM_DENSITY,ModWorldGenConstants.TOP_DENSITY)
                )
        );

        context.register(
                initialDensityWithoutJaggednessKey,
//                DensityFunctions.add(
//                                       DensityFunctions.mul(
//                                               DensityFunctions.spline(
//                                                       CubicSpline.builder(splineCoordinatesFrom(getCachedFunction(densityLookup,continentsKey)))
//                                                               .addPoint(0.0F,0.0F)
//                                                               .addPoint(0.5F,1.0F)
//                                                               .build()
//                                               ),
//                                               DensityFunctions.mul(
//                                                       DensityFunctions.constant(
//                                                               0.2F),
//                                                       DensityFunctions.add(
//                                                               DensityFunctions.add(
//                                                                       getFunction(densityLookup, ModNoiseRouterData.SURFACE_NOISE_A),
//                                                                       getFunction(densityLookup, ModNoiseRouterData.SURFACE_NOISE_B)
//                                                               ),
//                                                               getFunction(densityLookup, ModNoiseRouterData.SURFACE_NOISE_C)
//                                                       ).squeeze().abs()
//                                               )
//                                       ),
                                DensityFunctions.cacheOnce(depthGradient)
//                )
                .clamp(-64, 64)
        );

        context.register(
                topographyFinalDensityKey,
                DensityFunctions.interpolated(
                                DensityFunctions.add(
                                        DensityFunctions.constant(0.1),
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(-0.1),
                                                        DensityFunctions.rangeChoice(
                                                                getFunction(densityLookup, initialDensityWithoutJaggednessKey),
                                                                -1000000.0, 1.5625,
                                                                getFunction(densityLookup, ModNoiseRouterData.R_SLOPED_CHEESE),
                                                                getFunction(densityLookup, ModNoiseRouterData.CAVES)
                                                        )
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



        context.register(
                continentsKey,
                DensityFunctions.cache2d(
                        DensityFunctions.mul(
                                DensityFunctions.constant(0.95F),
                                getFunction(densityLookup,ModNoiseRouterData.LAND_CONTINENTS)
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
                        Pair.of(0.35F, splineMinMax(terrainBase,0.0F,0.5F,1.5F)),
                        Pair.of(0.7F, splineMinMaxSharp(terrainBase,0.0F,0.9F,1.5F))
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
                        Pair.of(0.5F, splineMinMax(terrainBase,0.0F,0.2F,1.5F)),
                        Pair.of(1.0F, splineMinMaxSharp(terrainBase,00.5F,1.5F))
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
                        Pair.of(0.35F, splineMinMax(terrainBaseSmooth,0.0F,0.35F,1.5F)),
                        Pair.of(0.7F, splineMinMax(terrainBaseSmooth,0.0F,0.65F,1.5F))
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
                        Pair.of(0.5F, splineMinMax(terrainBaseSmooth,0.0F,0.2F,1.5F)),
                        Pair.of(1.0F, splineMinMax(terrainBaseSmooth,0.0F,0.4F,1.5F))
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

        // DIRECTION -> Negative = Away from Each-other, Positive = Towards Each-other

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainApart =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineZeroMaxSharp(tectonicTerrainSmooth,0.2F,1.5F))
                        .addPoint(1.0F, splineZeroMax(tectonicTerrainSmooth,0.1F,0.3F))
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainFold =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineZeroMaxSharp(tectonicTerrain,0.2F,2.0F))
                        .addPoint(1.0F, splineZeroMax(tectonicTerrainSmooth,0.2F,1.2F))
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueLand =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineZeroMaxSharp(tectonicTerrain,0.7F,2.5F))
                        .addPoint(1.0F, splineZeroMax(tectonicTerrain,0.2F,0.9F))
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueOcean =
                CubicSpline.builder(splineCoordinatesFrom(landErosion), NO_TRANSFORM)
                        .addPoint(-1.0F, splineZeroMaxVerySharp(tectonicTerrain,-0.9F,-0.3F),1.0F)
                        .addPoint(1.0F, splineZeroMax(tectonicTerrain,-0.5F,-0.1F))
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdue =
                CubicSpline.builder(splineCoordinatesFrom(tectonicPlates), NO_TRANSFORM)
                        .addPoint(-0.5F, terrainSubdueOcean)
                        .addPoint(0.5F, terrainSubdueLand)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueOpposite =
                CubicSpline.builder(splineCoordinatesFrom(tectonicPlates), NO_TRANSFORM)
                        .addPoint(-0.1F, terrainSubdueOcean)
                        .addPoint(0.1F, terrainSubdueLand)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainSubdueRandomized =
                CubicSpline.builder(splineCoordinatesFrom(tectonicRandomness), NO_TRANSFORM)
                        .addPoint(-0.1F, terrainSubdue)
                        .addPoint(0.1F, terrainSubdueOpposite)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainCollision =
                CubicSpline.builder(splineCoordinatesFrom(landContinents), NO_TRANSFORM)
                        .addPoint(-0.20F, terrainSubdueRandomized)
                        .addPoint(0.15F, terrainFold)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> terrainMovementBased =
                CubicSpline.builder(splineCoordinatesFrom(tectonicDirection), NO_TRANSFORM)
                        .addPoint(-0.5F, terrainApart)
                        .addPoint(-0.15F,0.0F)
                        .addPoint(0.15F,0.0F)
                        .addPoint(0.5F, terrainCollision)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tectonicActivityBasedTerrain =
                CubicSpline.builder(splineCoordinatesFrom(tectonicActivity), NO_TRANSFORM)
                        .addPoint(-0.75F, 0.0F)
                        .addPoint(0.5F, terrainMovementBased)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tectonicEdgeTerrain =
                CubicSpline.builder(splineCoordinatesFrom(tectonicEdges), NO_TRANSFORM)
                        .addPoint(0.0F, 0.0F)
                        .addPoint(0.75F, tectonicActivityBasedTerrain)
                        .build();


        context.register(
                tectonicSurfaceKey,
                DensityFunctions.cacheOnce(
                                DensityFunctions.spline(
                                        tectonicEdgeTerrain
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

    private static void makeTerrainOld(
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            BootstrapContext<DensityFunction> context,
            ResourceKey<DensityFunction> elevationKey,
            ResourceKey<DensityFunction> erosionKey,
            ResourceKey<DensityFunction> continentsKey,
            ResourceKey<DensityFunction> ridgesKey) {

        Holder<DensityFunction> continents = densityLookup.getOrThrow(ModNoiseRouterData.CONTINENT_LANDMASS);
        Holder<DensityFunction> riverMask = densityLookup.getOrThrow(ModNoiseRouterData.GEO_RIVERS_AND_VALLEYS);
        Holder<DensityFunction> biomeVariation = densityLookup.getOrThrow(ModNoiseRouterData.BIOME_VARIATION);
        Holder<DensityFunction> elevationMedian = densityLookup.getOrThrow(ModNoiseRouterData.MOUNTAIN_ELEVATION_OFFSET);
        Holder<DensityFunction> hillsAndMountains = densityLookup.getOrThrow(ModNoiseRouterData.HILLS_AND_MOUNTAINS);
        Holder<DensityFunction> erosion = densityLookup.getOrThrow(ModNoiseRouterData.GEO_WEATHERING_EROSION);
        Holder<DensityFunction> plateauValleys = densityLookup.getOrThrow(ModNoiseRouterData.GEO_RIVERS_AND_VALLEYS_PLATEAU);
        Holder<DensityFunction> plateauValleyDepth = densityLookup.getOrThrow(ModNoiseRouterData.PLATEAU_VALLEY_DEPTH);
        Holder<DensityFunction> plateauMask = densityLookup.getOrThrow(ModNoiseRouterData.PLATEAU_MASK);

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> riversAndValleys =
                CubicSpline.builder(splineCoordinatesFrom(riverMask), NO_TRANSFORM)
                        .addPoint(-1, 0.05F)
                        .addPoint(-0.75F, 0.05F)
                        .addPoint(-0.50F, -0.1F, -0.3F)
                        .addPoint(-0.35F, -0.2F)
                        .addPoint(-0.15F, -0.1F, 0.3F)
                        .addPoint(0.10F, 0.05F)
                        .addPoint(1.0F, 0.05F)
                        .build();




        context.register(
                erosionKey,
                DensityFunctions.cache2d(
                        DensityFunctions.spline(
                                CubicSpline.builder(splineCoordinatesFrom(erosion), NO_TRANSFORM)
                                        .addPoint(-1.5F, -1.5F)
                                        .addPoint(-1.0F, -1.0F)
                                        .addPoint(0.0F, 0.0F)
                                        .addPoint(1.0F, 1.0F)
                                        .addPoint(1.5F, 1.5F)
                                        .build()
                        )
                )
        );



        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> seaMountains =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,-0.8F,-0.7F))
                        .addPoint(-0.5F, splineMinMax(elevationMedian,-0.7F,-0.6F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,-0.4F,-0.3F), 0.3F)
                        .addPoint(0.8F, splineMinMax(elevationMedian,-0.2F,-0.2F), 0.2F)
                        .addPoint(1.0F, splineMinMax(elevationMedian,0.0F,0.1F), 0.5F)
                        .addPoint(1.2F, splineMinMax(elevationMedian,0.3F,0.5F), 0.4F)
                        .build();



        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> deepSeaMountains =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,-1.9F,-1.7F))
                        .addPoint(-0.5F, splineMinMax(elevationMedian,-1.7F,-1.5F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,-0.4F,-0.3F), 0.3F)
                        .addPoint(0.8F, splineMinMax(elevationMedian,0.1F,0.3F), 0.2F)
                        .addPoint(1.0F, splineMinMax(elevationMedian,0.3F,0.5F), 0.5F)
                        .addPoint(1.2F, splineMinMax(elevationMedian,0.5F,0.8F), 0.4F)
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> shallowMountainsBaseSpline =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,0.05F,0.05F))
                        .addPoint(-0.5F, splineMinMax(elevationMedian,0.05F,0.08F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,0.05F,0.12F), 0.4F)
                        .addPoint(1.0F, splineMinMax(elevationMedian,0.05F,0.3F), 0.4F)
                        .addPoint(1.2F, splineMinMax(elevationMedian,0.1F,0.5F), 0.2F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> shallowMountains =
                CubicSpline.builder(splineCoordinatesFrom(riverMask), NO_TRANSFORM)
                        .addPoint(-1.0F, shallowMountainsBaseSpline)
                        .addPoint(-0.50F, -0.05F, -0.3F)
                        .addPoint(-0.35F, -0.1F)
                        .addPoint(-0.15F, -0.05F, 0.3F)
                        .addPoint(0.4F, shallowMountainsBaseSpline)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> minorHillsBaseSpline =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,0.05F,0.1F))
                        .addPoint(-0.5F, splineMinMax(elevationMedian,0.05F,0.15F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,0.1F,0.15F))
                        .addPoint(1.0F, splineMinMax(elevationMedian,0.1F,0.15F), 0.4F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> minorHills =
                CubicSpline.builder(splineCoordinatesFrom(riverMask), NO_TRANSFORM)
                        .addPoint(-1.0F, minorHillsBaseSpline)
                        .addPoint(-0.50F, -0.05F, -0.3F)
                        .addPoint(-0.35F, -0.1F)
                        .addPoint(-0.15F, -0.05F, 0.3F)
                        .addPoint(0.4F, minorHillsBaseSpline)
                        .build();




        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> mediumMountains =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,0.35F,0.7F))
                        .addPoint(-0.5F,  splineMinMax(elevationMedian,0.6F,0.8F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,1.2F,1.6F), 0.3F)
                        .addPoint(0.8F, splineMinMax(elevationMedian,1.5F,1.9F), 0.2F)
                        .addPoint(1.0F, splineMinMax(elevationMedian,1.9F,2.1F), 0.5F)
                        .addPoint(1.2F, splineMinMax(elevationMedian,2.1F,2.4F), 0.4F)
                        .build();




        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> highMountains =
                CubicSpline.builder(splineCoordinatesFrom(hillsAndMountains), NO_TRANSFORM)
                        .addPoint(-1.0F, splineMinMax(elevationMedian,0.5F,0.8F))
                        .addPoint(-0.5F, splineMinMax(elevationMedian,0.7F,0.9F))
                        .addPoint(0.5F, splineMinMax(elevationMedian,1.5F,1.8F), 0.3F)
                        .addPoint(0.8F, splineMinMax(elevationMedian,1.8F,2.4F), 0.2F)
                        .addPoint(1.0F, splineMinMax(elevationMedian,2.1F,2.5F), 0.5F)
                        .addPoint(1.2F, splineMinMax(elevationMedian,2.5F,2.8F), 0.4F)
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> erosionMountainsSpline =
                CubicSpline.builder(splineCoordinatesFrom(erosion), NO_TRANSFORM)
                        .addPoint(-1.0F, highMountains)
                        .addPoint(-0.75F, highMountains)
                        .addPoint(-0.45F, mediumMountains)
                        .addPoint(-0.15F, shallowMountains)
                        .addPoint(0.3F, minorHills)
                        .addPoint(0.75F, riversAndValleys)
                        .addPoint(0.8F, 0.05F)
                        .addPoint(0.9F, 0.01F)
                        .addPoint(1.0F, -0.02F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> plateauBottoms =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleyDepth))
                        .addPoint(-1.0F,-0.5F)
                        .addPoint(0.0F,0.0F)
                        .addPoint(1.0F,0.1F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point,DensityFunctions.Spline.Coordinate> plateauSemiBottom =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleyDepth))
                        .addPoint(-1.0F,-0.3F)
                        .addPoint(0.0F,0.0F)
                        .addPoint(1.0F,0.15F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> lowPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleys), NO_TRANSFORM)
                        .addPoint(-1.0F, plateauBottoms)
                        .addPoint(-0.8F, plateauSemiBottom, 0.5F)
                        .addPoint(-0.6F, 0.3F)
                        .addPoint(1.0F, 0.4F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> mediumPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleys), NO_TRANSFORM)
                        .addPoint(-1.0F, plateauBottoms)
                        .addPoint(-0.8F, plateauSemiBottom, 0.5F)
                        .addPoint(-0.6F, 0.5F)
                        .addPoint(1.0F, 0.6F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> tallPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleys), NO_TRANSFORM)
                        .addPoint(-1.0F, plateauBottoms)
                        .addPoint(-0.8F, plateauSemiBottom, 0.5F)
                        .addPoint(-0.5F, 0.7F)
                        .addPoint(1.0F, 0.8F)
                        .build();
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> veryTallPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(plateauValleys), NO_TRANSFORM)
                        .addPoint(-1.0F, plateauBottoms)
                        .addPoint(-0.8F, plateauSemiBottom, 0.5F)
                        .addPoint(-0.5F, 1.0F)
                        .addPoint(1.0F, 1.15F)
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> erosionPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(erosion), NO_TRANSFORM)
                        .addPoint(-1.0F, veryTallPlateauSpline)
                        .addPoint(-0.85F, veryTallPlateauSpline)
                        .addPoint(-0.8F, tallPlateauSpline)
                        .addPoint(-0.7F, tallPlateauSpline)
                        .addPoint(-0.6F, mediumPlateauSpline)
                        .addPoint(-0.5F, mediumPlateauSpline)
                        .addPoint(-0.4F, lowPlateauSpline)
                        .addPoint(0.0F, shallowMountains)
                        .addPoint(0.5F, minorHills)
                        .addPoint(0.75F, 0.05F)
                        .addPoint(1.0F, 0.05F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> riverPlateauSpline =
                CubicSpline.builder(splineCoordinatesFrom(riverMask), NO_TRANSFORM)
                        .addPoint(-0.7F, erosionPlateauSpline)
                        .addPoint(-0.50F, -0.05F, -0.3F)
                        .addPoint(-0.35F, -0.1F)
                        .addPoint(-0.15F, -0.05F, 0.3F)
                        .addPoint(0.1F, erosionPlateauSpline)
                        .build();


        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> erosionSpline =
                CubicSpline.builder(splineCoordinatesFrom(plateauMask), NO_TRANSFORM)
                        .addPoint(-1.0F, erosionMountainsSpline)
                        .addPoint(0.6F, erosionMountainsSpline)
                        .addPoint(0.8F, riverPlateauSpline)
                        .addPoint(1.0F, riverPlateauSpline)
                        .build();


        CubicSpline.Builder<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> baseElevation =
                CubicSpline.builder(splineCoordinatesFrom(continents), NO_TRANSFORM)
                        .addPoint(-1.5F, erosionSpline)
                        .addPoint(-0.8F, seaMountains)
                        .addPoint(-0.7F, deepSeaMountains)
                        .addPoint(-0.45F, seaMountains)
                        .addPoint(-0.16F, erosionSpline,0.2F)
                        .addPoint(0.2F, erosionSpline)
                        .addPoint(1.0F, erosionSpline);



        context.register(
                elevationKey,
                DensityFunctions.flatCache(
                        DensityFunctions.spline(
                                baseElevation.build()
                        )
                )
        );

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> islands =
                CubicSpline.builder(splineCoordinatesFrom(densityLookup.getOrThrow(elevationKey)), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.6F)
                        .addPoint(-0.5F, -0.75F)
                        .addPoint(-0.1F, -0.9F)
                        .addPoint(0.1F, -1.25F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> seas =
                CubicSpline.builder(splineCoordinatesFrom(densityLookup.getOrThrow(elevationKey)), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.8F)
                        .addPoint(-0.5F, -0.6F)
                        .addPoint(-0.1F, -0.19F)
                        .addPoint(0.05F, -0.12F)
                        .addPoint(0.3F, 0.0F)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> shores =
                CubicSpline.builder(splineCoordinatesFrom(densityLookup.getOrThrow(elevationKey)), NO_TRANSFORM)
                        .addPoint(-1.0F, -0.6F)
                        .addPoint(-0.5F, -0.35F)
                        .addPoint(-0.1F, -0.19F)
                        .addPoint(0.1F, -0.05F)
                        .addPoint(0.2F, 0.0F)
                        .build();





        context.register(
                continentsKey,
                DensityFunctions.cache2d(
                        DensityFunctions.spline(
                                CubicSpline.builder(splineCoordinatesFrom(continents), NO_TRANSFORM)
                                        .addPoint(-1.2F, islands)
                                        .addPoint(-1.0F, islands)
                                        .addPoint(-0.8F, seas)
                                        .addPoint(-0.4F, seas)
                                        .addPoint(-0.25F, shores)
                                        .addPoint(-0.15F, 0.0F)
                                        .addPoint(0.3F, 0.2F)
                                        .addPoint(1.0F, 1.5F)
                                        .build()
                        )
                )
        );


        context.register(ridgesKey,
                DensityFunctions.flatCache(
                        DensityFunctions.cache2d(
                                DensityFunctions.mul(
                                        DensityFunctions.spline(
                                                CubicSpline.builder(splineCoordinatesFrom(erosion), NO_TRANSFORM)
                                                        .addPoint(-0.6F, 1.0F)
                                                        .addPoint(-0.1F,
                                                                CubicSpline.builder(splineCoordinatesFrom(riverMask), NO_TRANSFORM)
                                                                        .addPoint(-1.0F, 1.0F)
                                                                        .addPoint(-0.75F, 1.0F)
                                                                        .addPoint(-0.50F, 0.35F, -0.6F)
                                                                        .addPoint(-0.35F, 0.0F)
                                                                        .addPoint(-0.15F, 0.35F, 0.6F)
                                                                        .addPoint(-0.05F, 1.0F)
                                                                        .addPoint(1.0F, 1.0F)
                                                                        .build())
                                                        .build()
                                        ),
                                        DensityFunctions.mul(
                                                DensityFunctions.spline(
                                                        CubicSpline.builder(splineCoordinatesFrom(biomeVariation), NO_TRANSFORM)
                                                                .addPoint(-0.0001F, -1)
                                                                .addPoint(0.0001F, 1)
                                                                .build()
                                                ),
                                                DensityFunctions.mul(

                                                        DensityFunctions.spline(
                                                                CubicSpline.builder(
                                                                                splineCoordinatesFrom(
                                                                                        DensityFunctions.cache2d(getFunction(densityLookup, ModNoiseRouterData.HILLS_AND_MOUNTAINS)
                                                                                        )
                                                                                ), NO_TRANSFORM)
                                                                        .addPoint(-1.0F, 0.25F, 0.0F)
                                                                        .addPoint(-0.5F, 0.25F, 0.7F)
                                                                        .addPoint(0.0F, 0.3F)
                                                                        .addPoint(0.4F, 0.3F)
                                                                        .addPoint(0.85F, 0.62F, 1.05F)
                                                                        .addPoint(1.0F, 0.63F)
                                                                        .build()
                                                        ),

                                                        DensityFunctions.spline(
                                                                CubicSpline.builder(
                                                                                splineCoordinatesFrom(
                                                                                        DensityFunctions.cache2d(getFunction(densityLookup, ModNoiseRouterData.PLATEAU_MASK)
                                                                                        )
                                                                                ), NO_TRANSFORM)
                                                                        .addPoint(0.4F, 1.0F)
                                                                        .addPoint(0.8F, 0.7F)
                                                                        .build()
                                                        )
                                                )


                                        )
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
            ResourceKey<DensityFunction> caves) {

        DensityFunction densityNoJaggedness =
                DensityFunctions.cacheOnce(getFunction(densityLookup,ModNoiseRouterData.R_INITIAL_DENSITY_WITHOUT_JAGGEDNESS));
        

        context.register(
                slopedCheese,
                DensityFunctions.cacheOnce(
                                densityNoJaggedness
                )

        );


        context.register(caves,
                            DensityFunctions.constant(1.0F)
//                        DensityFunctions.min(
//                                DensityFunctions.add(
//                                        DensityFunctions.add(
//                                                DensityFunctions.constant(0.45F),
//                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_ENTRANCES), 0.75F, 0.5F)
//                                        ),
//                                        DensityFunctions.yClampedGradient(-30, 30, 0.3, 0)
//                                ),
//                                DensityFunctions.add(
//                                        DensityFunctions.mul(
//                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_RARITY), 0.25, 0.25).abs(),
//                                                DensityFunctions.constant(0.25)
//                                                ),
//                                        DensityFunctions.min(
//                                                DensityFunctions.min(
//                                                        DensityFunctions.min(
//                                                                DensityFunctions.mul(
//                                                                        DensityFunctions.constant(4.0),
//                                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_NOODLES)
//                                                                ),
//                                                                DensityFunctions.mul(
//                                                                        DensityFunctions.constant(4.0),
//                                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_FRACTURE)
//                                                                )
//                                                        ),
//                                                        DensityFunctions.min(
//                                                                DensityFunctions.mul(
//                                                                        DensityFunctions.constant(10.0),
//                                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_GROTTO)
//                                                                ),
//                                                                DensityFunctions.mul(
//                                                                        DensityFunctions.constant(4.0),
//                                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_SPAGHETTI)
//                                                                )
//                                                        )
//                                                ),
//                                                DensityFunctions.min(
//                                                                DensityFunctions.mul(
//                                                                        DensityFunctions.constant(0.5),
//                                                                        getFunction(densityLookup, ModNoiseRouterData.CAVE_CAVERNS)
//                                                                )
//                                                        ,
//                                                        DensityFunctions.mul(
//                                                                DensityFunctions.constant(4.0),
//                                                                getFunction(densityLookup, ModNoiseRouterData.CAVE_PITS)
//                                                        )
//                                                )
//                                        )
//                                )
//                        )

        );


    }

    public static void noodle(
            BootstrapContext<DensityFunction> context,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            HolderGetter<DensityFunction> densityLookup,
            ResourceKey<DensityFunction> branchesKey) {
        DensityFunction densityfunction = getFunction(densityLookup, PackNoiseRouterData.Y);

        context.register(branchesKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.rangeChoice(
                                yLimitedInterpolatable(
                                        densityfunction, DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_NOODLE), 1.0, 1.0), ModWorldGenConstants.CAVES_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT, -1
                                ),
                                -1000000,
                                0,
                                DensityFunctions.constant(64),
                                DensityFunctions.add(
                                        yLimitedInterpolatable(
                                                densityfunction,
                                                DensityFunctions.mappedNoise(noiseLookup.getOrThrow(ModNoises.CAVE_NOODLE_THICKNESS),
                                                        1.0,
                                                        1.0,
                                                        -0.2,
                                                        -0.4),
                                                ModWorldGenConstants.CAVES_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT,
                                                0
                                        ),
                                        DensityFunctions.mul(
                                                DensityFunctions.constant(1.5),
                                                DensityFunctions.max(
                                                        yLimitedInterpolatable(
                                                                densityfunction,
                                                                DensityFunctions.noise(
                                                                        noiseLookup.getOrThrow(ModNoises.CAVE_NOODLE_RIDGE_1),
                                                                        2.5, 2.5),
                                                                ModWorldGenConstants.CAVES_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT,
                                                                0
                                                        ).abs(),
                                                        yLimitedInterpolatable(
                                                                densityfunction,
                                                                DensityFunctions.noise(
                                                                        noiseLookup.getOrThrow(ModNoises.CAVE_NOODLE_RIDGE_2),
                                                                        2.5, 2.5),
                                                                ModWorldGenConstants.CAVES_BOTTOM, ModWorldGenConstants.BUILD_HEIGHT,
                                                                0
                                                        ).abs()
                                                )
                                        )
                                )

                        )
                )
        );

    }

    public static void spaghetti(
            BootstrapContext<DensityFunction> context,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            HolderGetter<DensityFunction> densityLookup,
            ResourceKey<DensityFunction> branchesLargeKey) {

        DensityFunction function = DensityFunctions.cacheOnce(DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_RARITY),
                2.0,
                1.0)
        );
        context.register(branchesLargeKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.cacheOnce(
                                        DensityFunctions.mul(
                                                DensityFunctions.mappedNoise(noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_MODULATOR),
                                                        0.0, -0.1),
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(-0.4),
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_ROUGHNESS), 1, 1).abs()
                                                )
                                        )
                                ),
                                DensityFunctions.add(
                                        DensityFunctions.max(
                                                DensityFunctions.weirdScaledSampler(
                                                        function,
                                                        noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_1),
                                                        DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
                                                ),
                                                DensityFunctions.weirdScaledSampler(
                                                        function,
                                                        noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_2),
                                                        DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
                                                )

                                        ),
                                        DensityFunctions.add(DensityFunctions.constant(-0.1F),
                                                DensityFunctions.mul(
                                                        DensityFunctions.constant(0.1),
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_SPAGHETTI_3D_THICKNESS))
                                                ))
                                ).clamp(-1, 1)
                        )
                )
        );
    }

    public static void pits(
            BootstrapContext<DensityFunction> context,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            ResourceKey<DensityFunction> pitsKey) {
        context.register(pitsKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.yClampedGradient(ModWorldGenConstants.WORLD_BOTTOM, 120, 3, 1),
                                DensityFunctions.add(
                                        DensityFunctions.yClampedGradient(320, ModWorldGenConstants.BUILD_HEIGHT, 1, 10),
                                        DensityFunctions.add(
                                                DensityFunctions.mul(
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_PIT_RARITY), 10, 10),
                                                        DensityFunctions.constant(2.0F)
                                                ),
                                                DensityFunctions.mul(
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_PITS), 3, 0.5),
                                                        DensityFunctions.constant(2.5F)
                                                )
                                        )
                                )

                        )
                )
        );
    }

    public static void caverns(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            DensityFunction shiftX,
            DensityFunction shiftZ,
            ResourceKey<DensityFunction> cavernsKey) {
        context.register(cavernsKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.add(
                                        DensityFunctions.yClampedGradient(
                                                -10, ModWorldGenConstants.BUILD_HEIGHT, -0.05, 2.0F
                                        ),
                                        DensityFunctions.noise(
                                                noiseLookup.getOrThrow(ModNoises.CAVE_CAVERNS), 0.25F, 0.25F
                                        )
                                ),
                                DensityFunctions.mul(
                                        DensityFunctions.add(
                                                DensityFunctions.mul(
                                                        DensityFunctions.constant(2),
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_CAVERN_PILLARS), 20, 0.3)
                                                ),
                                                DensityFunctions.add(
                                                        DensityFunctions.constant(-1),
                                                        DensityFunctions.mul(
                                                                DensityFunctions.constant(-1),
                                                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_CAVERN_PILLARS_RARITY), 1, 1)
                                                        )
                                                )
                                        ),
                                        DensityFunctions.add(
                                                DensityFunctions.constant(0.55),
                                                DensityFunctions.mul(
                                                        DensityFunctions.constant(0.55),
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_CAVERN_PILLARS_THICKNESS), 1, 1)
                                                )
                                        ).cube()
                                )

                        )
                )
        );
    }

    public static void grotto(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            DensityFunction shiftX,
            DensityFunction shiftZ,
            ResourceKey<DensityFunction> grottoKey) {
        context.register(grottoKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.add(
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_GROTTO), 0.25, 0.25),
                                        DensityFunctions.constant(0.6F)
                                ),
                                DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_GROTTO_RARITY), 0.25, 0.25).clamp(0, 1000)
                        )
                )
        );
    }

    public static void fracture(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityLookup,
            HolderGetter<NormalNoise.NoiseParameters> noiseLookup,
            DensityFunction shiftX,
            DensityFunction shiftZ,
            ResourceKey<DensityFunction> fractureKey) {
        context.register(fractureKey,
                DensityFunctions.cacheOnce(
                        DensityFunctions.add(
                                DensityFunctions.add(
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_FRACTURE_1), 5, 1).abs(),
                                        DensityFunctions.mul(
                                                DensityFunctions.add(
                                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVE_FRACTURE_2), 6, 6).clamp(0, 1),
                                                        DensityFunctions.constant(-0.25)
                                                ),
                                                DensityFunctions.constant(0.5)
                                        )
                                ),
                                DensityFunctions.constant(0.1)
                        )
                )
        );
    }
}