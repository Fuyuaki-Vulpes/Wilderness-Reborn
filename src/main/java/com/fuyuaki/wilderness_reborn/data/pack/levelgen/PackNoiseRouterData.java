package com.fuyuaki.wilderness_reborn.data.pack.levelgen;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.data.pack.worldgen.PackTerrainProvider;
import com.fuyuaki.wilderness_reborn.world.level.levelgen.ModNoiseRouterData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.stream.Stream;

public class PackNoiseRouterData {
    public static final float GLOBAL_OFFSET = -0.50375F;
    private static final float ORE_THICKNESS = 0.08F;
    private static final double VEININESS_FREQUENCY = 1.5;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5;
    private static final double SURFACE_DENSITY_THRESHOLD = 1.5625;
    private static final double CHEESE_NOISE_TARGET = -0.703125;
    public static final int ISLAND_CHUNK_DISTANCE = 64;
    public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    public static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    public static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = createKey("overworld/base_3d_noise");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> PLATEAU_OR_HILL = createKey("overworld/plateau_or_hill");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = createKey("overworld/offset");
    public static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("overworld/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
    public static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
    private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
    private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
    private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = createKey("overworld_amplified/factor");
    private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = createKey("overworld_amplified/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = createKey("overworld_amplified/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");


    private static final ResourceKey<DensityFunction> RIVER_PATH = createKey("overworld/rivers/path");
    private static final ResourceKey<DensityFunction> RIVER_FLOW = createKey("overworld/rivers/flow");
    private static final ResourceKey<DensityFunction> RIVERS = createKey("overworld/rivers/offset");

    private static ResourceKey<DensityFunction> createKey(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.withDefaultNamespace(location));
    }


    private static ResourceKey<DensityFunction> createKeyMod(String location) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, WildernessRebornMod.mod(location));
    }


    public static Holder<? extends DensityFunction> bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        context.register(ZERO, DensityFunctions.zero());
        int i = DimensionType.MIN_Y * 2;
        int j = DimensionType.MAX_Y * 2;
        context.register(Y, DensityFunctions.yClampedGradient(i, j, i, j));
        DensityFunction shiftX = registerAndWrap(
                context, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(noiseLookup.getOrThrow(Noises.SHIFT))))
        );
        DensityFunction shiftZ = registerAndWrap(
                context, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(noiseLookup.getOrThrow(Noises.SHIFT))))
        );
        context.register(BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded(0.25, 0.125, 80.0, 160.0, 8.0));
        Holder<DensityFunction> continents = context.register(
                CONTINENTS,
                DensityFunctions.flatCache(
                        DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.CONTINENTALNESS))
                )
        );
        Holder<DensityFunction> erosion = context.register(
                EROSION,
                DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.EROSION)))
        );


        DensityFunction ridges = registerAndWrap(
                context,
                RIDGES,
                DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.RIDGE)))
        );
        context.register(RIDGES_FOLDED, peaksAndValleys(ridges));
        DensityFunction jaggedness = DensityFunctions.noise(noiseLookup.getOrThrow(Noises.JAGGED), 1500.0, 0.0);






        registerTerrainNoises(context, densityLookup, jaggedness, continents, erosion, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);

        context.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseLookup));
        context.register(
                SPAGHETTI_2D_THICKNESS_MODULATOR,
                DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseLookup.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3))
        );
        context.register(SPAGHETTI_2D, spaghetti2D(densityLookup, noiseLookup));
        context.register(ENTRANCES, entrances(densityLookup, noiseLookup));
        context.register(NOODLE, noodle(densityLookup, noiseLookup));
        return context.register(PILLARS, pillars(noiseLookup));

    }



    private static void registerTerrainNoises(
            BootstrapContext<DensityFunction> context,
            HolderGetter<DensityFunction> densityFunctionRegistry,
            DensityFunction jaggedNoise,
            Holder<DensityFunction> continentalness,
            Holder<DensityFunction> erosion,
            ResourceKey<DensityFunction> offsetKey,
            ResourceKey<DensityFunction> factorKey,
            ResourceKey<DensityFunction> jaggednessKey,
            ResourceKey<DensityFunction> depthKey,
            ResourceKey<DensityFunction> slopedCheeseKey,
            boolean amplified
    ) {
        DensityFunctions.Spline.Coordinate coordsContinentalness = new DensityFunctions.Spline.Coordinate(continentalness);
        DensityFunctions.Spline.Coordinate coordsErosion = new DensityFunctions.Spline.Coordinate(erosion);
        DensityFunctions.Spline.Coordinate coordsRidges = new DensityFunctions.Spline.Coordinate(densityFunctionRegistry.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate coordsValleysAndPeaks = new DensityFunctions.Spline.Coordinate(densityFunctionRegistry.getOrThrow(RIDGES_FOLDED));
        DensityFunctions.Spline.Coordinate coordsHillCurvature = new DensityFunctions.Spline.Coordinate(densityFunctionRegistry.getOrThrow(ModNoiseRouterData.HILL_CURVATURE));
        DensityFunctions.Spline.Coordinate coordsSpikes = new DensityFunctions.Spline.Coordinate(densityFunctionRegistry.getOrThrow(ModNoiseRouterData.HILL_CURVATURE));

        DensityFunction elevationOffset = DensityFunctions.mul(
                DensityFunctions.constant(0.05F),
                getFunction(densityFunctionRegistry, ModNoiseRouterData.ELEVATION));

        DensityFunction worldOffset = registerAndWrap(
                context,
                offsetKey,
                splineWithBlending(
                        DensityFunctions.add(
                                DensityFunctions.add(
                                        DensityFunctions.constant(-0.50375F),
                                        DensityFunctions.lerp(DensityFunctions.add(
                                                        DensityFunctions.constant(-1.0F),
                                                        DensityFunctions.mul(getFunction(densityFunctionRegistry, CONTINENTS).abs(), DensityFunctions.constant(2.0F))
                                                ),
                                                DensityFunctions.constant(0.0F),
                                                elevationOffset
                                                )
                                ),
                                DensityFunctions.spline(
                                        PackTerrainProvider.overworldOffset(
                                                coordsContinentalness, coordsErosion, coordsValleysAndPeaks, coordsHillCurvature, coordsSpikes, amplified
                                        )
                                )
                        ),
                        DensityFunctions.blendOffset()
                )
        );
        DensityFunction overworldFactor = registerAndWrap(
                context,
                factorKey,
                splineWithBlending(
                        DensityFunctions.spline(
                                PackTerrainProvider.overworldFactor(
                                        coordsContinentalness,
                                        coordsErosion,
                                        coordsRidges,
                                        coordsValleysAndPeaks,
                                        amplified
                                )
                        ),
                        BLENDING_FACTOR
                )
        );
        DensityFunction depthGradient = registerAndWrap(
                context, depthKey, DensityFunctions.add(DensityFunctions.yClampedGradient(-256, 1024, 1.5, -2.5), worldOffset

                )
        );
        DensityFunction jaggedness = registerAndWrap(
                context,
                jaggednessKey,
                splineWithBlending(
                        DensityFunctions.spline(
                                PackTerrainProvider.overworldJaggedness(
                                        coordsContinentalness,
                                        coordsErosion,
                                        coordsRidges,
                                        coordsValleysAndPeaks,
                                        amplified
                                )
                        ),
                        BLENDING_JAGGEDNESS
                )
        );
        DensityFunction noiseFilter = getFunction(densityFunctionRegistry, ModNoiseRouterData.NOISE_FILTER);
        DensityFunction jaggednessPost = DensityFunctions.mul(jaggedness, jaggedNoise.halfNegative());
        DensityFunction factorWithJaggedness = noiseGradientDensity(overworldFactor, DensityFunctions.add(depthGradient, jaggednessPost));
        context.register(slopedCheeseKey,
                DensityFunctions.lerp(
                        noiseFilter,
                        DensityFunctions.add(factorWithJaggedness, DensityFunctions.mul(getFunction(densityFunctionRegistry, ModNoiseRouterData.OVERWORLD_NOISE_2),DensityFunctions.constant(0.5F))),
                        DensityFunctions.add(factorWithJaggedness, DensityFunctions.mul(getFunction(densityFunctionRegistry, ModNoiseRouterData.OVERWORLD_NOISE),DensityFunctions.constant(0.5F))
                        )
                )
        );
    }

    private static DensityFunction registerAndWrap(
            BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction value
    ) {
        return new DensityFunctions.HolderHolder(context.register(key, value));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctionRegistry, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctionRegistry.getOrThrow(key));
    }
    private static DensityFunction peaksAndValleys(DensityFunction densityFunction) {
        return DensityFunctions.mul(
                DensityFunctions.add(
                        DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)
                ),
                DensityFunctions.constant(-3.0)
        );
    }

    public static float peaksAndValleys(float weirdness) {
        return -(Math.abs(Math.abs(weirdness) - 0.6666667F) - 0.33333334F) * 3.0F;
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParameters) {
        DensityFunction densityfunction = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction1, DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction entrances(HolderGetter<DensityFunction> densityFunctionRegistry, HolderGetter<NormalNoise.NoiseParameters> noiseParameters) {
        DensityFunction densityfunction = DensityFunctions.cacheOnce(DensityFunctions.noise(noiseParameters.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction densityfunction2 = DensityFunctions.weirdScaledSampler(
                densityfunction, noiseParameters.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction densityfunction3 = DensityFunctions.weirdScaledSampler(
                densityfunction, noiseParameters.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
        );
        DensityFunction densityfunction4 = DensityFunctions.add(DensityFunctions.max(densityfunction2, densityfunction3), densityfunction1).clamp(-1.0, 1.0);
        DensityFunction densityfunction5 = getFunction(densityFunctionRegistry, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction6 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction densityfunction7 = DensityFunctions.add(
                DensityFunctions.add(densityfunction6, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0)
        );
        return DensityFunctions.cacheOnce(DensityFunctions.min(densityfunction7, DensityFunctions.add(densityfunction5, densityfunction4)));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParameters) {
        DensityFunction densityfunction = getFunction(densityFunctions, Y);
        int i = -64;
        int j = -60;
        int k = 320;
        DensityFunction densityfunction1 = yLimitedInterpolatable(
                densityfunction, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.NOODLE), 1.0, 1.0), -256, 1024, -1
        );
        DensityFunction densityfunction2 = yLimitedInterpolatable(
                densityfunction, DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -256, 1024, 0
        );
        double d0 = 2.6666666666666665;
        DensityFunction densityfunction3 = yLimitedInterpolatable(
                densityfunction, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -256, 1024, 0
        );
        DensityFunction densityfunction4 = yLimitedInterpolatable(
                densityfunction, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -256, 1024, 0
        );
        DensityFunction densityfunction5 = DensityFunctions.mul(
                DensityFunctions.constant(1.5), DensityFunctions.max(densityfunction3.abs(), densityfunction4.abs())
        );
        return DensityFunctions.rangeChoice(
                densityfunction1, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(densityfunction2, densityfunction5)
        );
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParameters) {
        double d0 = 25.0;
        double d1 = 0.3;
        DensityFunction densityfunction = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction densityfunction1 = DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction densityfunction2 = DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction densityfunction3 = DensityFunctions.add(DensityFunctions.mul(densityfunction, DensityFunctions.constant(2.0)), densityfunction1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction3, densityfunction2.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> densityFunctionRegistry, HolderGetter<NormalNoise.NoiseParameters> noiseParameters) {
        DensityFunction densityfunction = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction densityfunction1 = DensityFunctions.weirdScaledSampler(
                densityfunction, noiseParameters.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2
        );
        DensityFunction densityfunction2 = DensityFunctions.mappedNoise(noiseParameters.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction densityfunction3 = getFunction(densityFunctionRegistry, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction densityfunction4 = DensityFunctions.add(densityfunction2, DensityFunctions.yClampedGradient(-256, 1024, 8.0, -40.0)).abs();
        DensityFunction densityfunction5 = DensityFunctions.add(densityfunction4, densityfunction3).cube();
        double d0 = 0.083;
        DensityFunction densityfunction6 = DensityFunctions.add(densityfunction1, DensityFunctions.mul(DensityFunctions.constant(0.083), densityfunction3));
        return DensityFunctions.max(densityfunction6, densityfunction5).clamp(-1.0, 1.0);
    }


    public static DensityFunction underground(
            HolderGetter<DensityFunction> densityFunctionRegistry, HolderGetter<NormalNoise.NoiseParameters> noiseParameters, DensityFunction slopedCheese
    ) {
        DensityFunction densityfunction = getFunction(densityFunctionRegistry, SPAGHETTI_2D);
        DensityFunction densityfunction1 = getFunction(densityFunctionRegistry, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityfunction2 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction densityfunction3 = DensityFunctions.mul(DensityFunctions.constant(4.0), densityfunction2.square());
        DensityFunction cheeseCaves = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction densityfunction5 = DensityFunctions.add(
                DensityFunctions.add(DensityFunctions.constant(0.27), cheeseCaves).clamp(-1.0, 1.0),
                DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), slopedCheese)).clamp(0.0, 0.5)
        );
        DensityFunction densityfunction6 = DensityFunctions.add(densityfunction3, densityfunction5);
        DensityFunction densityfunction7 = DensityFunctions.min(
                DensityFunctions.min(densityfunction6, getFunction(densityFunctionRegistry, ENTRANCES)), DensityFunctions.add(densityfunction, densityfunction1)
        );
        DensityFunction densityfunction8 = getFunction(densityFunctionRegistry, PILLARS);
        DensityFunction densityfunction9 = DensityFunctions.rangeChoice(
                densityfunction8, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), densityfunction8
        );
        return DensityFunctions.max(densityfunction7, densityfunction9);
    }

    private static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction densityfunction = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(densityfunction), DensityFunctions.constant(0.64)).squeeze();
    }

    public static NoiseRouter overworld(
            HolderGetter<DensityFunction> densityFunctionRegistry, HolderGetter<NormalNoise.NoiseParameters> noiseParameters, boolean large, boolean amplified
    ) {
        DensityFunction densityfunction = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction densityfunction1 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction densityfunction2 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction densityfunction3 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction densityfunction4 = getFunction(densityFunctionRegistry, SHIFT_X);
        DensityFunction densityfunction5 = getFunction(densityFunctionRegistry, SHIFT_Z);
        DensityFunction densityfunction6 = DensityFunctions.shiftedNoise2d(
                densityfunction4, densityfunction5, 0.25, noiseParameters.getOrThrow(large ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE)
        );
        DensityFunction densityfunction7 = DensityFunctions.shiftedNoise2d(
                densityfunction4, densityfunction5, 0.25, noiseParameters.getOrThrow(large ? Noises.VEGETATION_LARGE : Noises.VEGETATION)
        );
        DensityFunction densityfunction8 = getFunction(densityFunctionRegistry, large ? FACTOR_LARGE : (amplified ? FACTOR_AMPLIFIED : FACTOR));
        DensityFunction densityfunction9 = getFunction(densityFunctionRegistry, large ? DEPTH_LARGE : (amplified ? DEPTH_AMPLIFIED : DEPTH));
        DensityFunction densityfunction10 = noiseGradientDensity(DensityFunctions.cache2d(densityfunction8), densityfunction9);
        DensityFunction densityfunction11 = getFunction(densityFunctionRegistry, large ? SLOPED_CHEESE_LARGE : (amplified ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
        DensityFunction densityfunction12 = DensityFunctions.min(
                densityfunction11, DensityFunctions.mul(DensityFunctions.constant(5.0), getFunction(densityFunctionRegistry, ENTRANCES))
        );
        DensityFunction densityfunction13 = DensityFunctions.rangeChoice(
                densityfunction11, -1000000.0, 1.5625, densityfunction12, underground(densityFunctionRegistry, noiseParameters, densityfunction11)
        );
        DensityFunction densityfunction14 = DensityFunctions.min(postProcess(slideOverworld(amplified, densityfunction13)), getFunction(densityFunctionRegistry, NOODLE));
        DensityFunction densityfunction15 = getFunction(densityFunctionRegistry, Y);
        int i = Stream.of(PackOreVeinifier.VeinType.values()).mapToInt(p_224495_ -> p_224495_.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int j = Stream.of(PackOreVeinifier.VeinType.values()).mapToInt(p_224457_ -> p_224457_.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction densityfunction16 = yLimitedInterpolatable(
                densityfunction15, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), i, j, 0
        );
        float f = 4.0F;
        DensityFunction densityfunction17 = yLimitedInterpolatable(
                densityfunction15, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), i, j, 0
        )
                .abs();
        DensityFunction densityfunction18 = yLimitedInterpolatable(
                densityfunction15, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), i, j, 0
        )
                .abs();
        DensityFunction densityfunction19 = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(densityfunction17, densityfunction18));
        DensityFunction densityfunction20 = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(
                densityfunction,
                densityfunction1,
                densityfunction2,
                densityfunction3,
                densityfunction6,
                densityfunction7,
                getFunction(densityFunctionRegistry, large ? CONTINENTS_LARGE : CONTINENTS),
                getFunction(densityFunctionRegistry, large ? EROSION_LARGE : EROSION),
                densityfunction9,
                getFunction(densityFunctionRegistry, RIDGES),
                slideOverworld(amplified, DensityFunctions.add(densityfunction10, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)),
                densityfunction14,
                densityfunction16,
                densityfunction19,
                densityfunction20
        );
    }

    private static DensityFunction slideOverworld(boolean amplified, DensityFunction densityFunction) {
        return slide(densityFunction, -256, 1024, amplified ? 16 : 80, amplified ? 0 : 64, -0.078125, 0, 24, amplified ? 0.4 : 0.1171875);
    }




    protected static NoiseRouter none() {
        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );
    }

    private static DensityFunction splineWithBlending(DensityFunction minFunction, DensityFunction maxFunction) {
        DensityFunction densityfunction = DensityFunctions.lerp(DensityFunctions.blendAlpha(), maxFunction, minFunction);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityfunction));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction minFunction, DensityFunction maxFunction) {
        DensityFunction densityfunction = DensityFunctions.mul(maxFunction, minFunction);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), densityfunction.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction whenInRange, int minY, int maxY, int whenOutOfRange) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, minY, maxY + 1, whenInRange, DensityFunctions.constant(whenOutOfRange)));
    }

    private static DensityFunction slide(
            DensityFunction input, int minY, int height, int topStartOffset, int topEndOffset, double topDelta, int bottomStartOffset, int bottomEndOffset, double bottomDelta
    ) {
        DensityFunction densityfunction1 = DensityFunctions.yClampedGradient(minY + height - topStartOffset, minY + height - topEndOffset, 1.0, 0.0);
        DensityFunction $$9 = DensityFunctions.lerp(densityfunction1, topDelta, input);
        DensityFunction densityfunction2 = DensityFunctions.yClampedGradient(minY + bottomStartOffset, minY + bottomEndOffset, 0.0, 1.0);
        return DensityFunctions.lerp(densityfunction2, bottomDelta, $$9);
    }

    protected static final class QuantizedSpaghettiRarity {
        protected static double getSphaghettiRarity2D(double value) {
            if (value < -0.75) {
                return 0.5;
            } else if (value < -0.5) {
                return 0.75;
            } else if (value < 0.5) {
                return 1.0;
            } else {
                return value < 0.75 ? 2.0 : 3.0;
            }
        }

        protected static double getSpaghettiRarity3D(double value) {
            if (value < -0.5) {
                return 0.75;
            } else if (value < 0.0) {
                return 1.0;
            } else {
                return value < 0.5 ? 1.5 : 2.0;
            }
        }
    }

    public static DensityFunction shiftedNoise3d(
            DensityFunction shiftX, DensityFunction shiftZ, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData
    ) {
        return new DensityFunctions.ShiftedNoise(shiftX, DensityFunctions.zero(), shiftZ, xzScale, 0.0, new DensityFunction.NoiseHolder(noiseData));
    }

}

