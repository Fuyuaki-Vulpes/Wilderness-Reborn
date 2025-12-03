package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModNoiseRouterData extends NoiseRouterFunctions {


    public static final ResourceKey<DensityFunction> TERRAIN_CAVES = createKey("terrain/caves");

    public static final ResourceKey<DensityFunction> AQUIFERS = createKey("aquifers");
    public static final ResourceKey<DensityFunction> CAVE_NOODLE = createKey("cave/noodle");
    public static final ResourceKey<DensityFunction> CAVE_CRACKS = createKey("cave/cracks");
    public static final ResourceKey<DensityFunction> CAVE_EXOGENES = createKey("cave/exogenes");
    public static final ResourceKey<DensityFunction> CAVE_ENDOGENES = createKey("cave/endogenes");


    public static Holder<? extends DensityFunction> bootstrap(BootstrapContext<DensityFunction> context) {
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);

        context.register(TERRAIN_CAVES,
                DensityFunctions.flatCache(
                        DensityFunctions.add(
                                DensityFunctions.yClampedGradient(32, 256, 0, 1),
                                DensityFunctions.add(
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_DENSITY)).clamp(0, 1),
                                        DensityFunctions.min(
                                                DensityFunctions.min(getFunction(densityLookup, CAVE_NOODLE), getFunction(densityLookup, CAVE_CRACKS)),
                                                DensityFunctions.min(getFunction(densityLookup, CAVE_ENDOGENES), getFunction(densityLookup, CAVE_EXOGENES)
                                                )
                                        )
                                )
                        )
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


        context.register(CAVE_CRACKS,
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
                                DensityFunctions.yClampedGradient(0, 72, 0.0F, 0.5F),
                                DensityFunctions.yClampedGradient(72, 128, 0.0F, 1.0F)
                        ),
                        DensityFunctions.add(
                                DensityFunctions.constant(0.55F),
                                DensityFunctions.mul(
                                        DensityFunctions.constant(1.5F),
                                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.CAVES_ENDOGENES),
                                                0.75F, 3.0F)
                                )
                        )
                )
        );


        context.register(AQUIFERS,
                DensityFunctions.flatCache(
                        DensityFunctions.noise(noiseLookup.getOrThrow(ModNoises.AQUIFERS),
                                0.25F, 1.0F)
                )
        );

        return densityLookup.getOrThrow(CAVE_ENDOGENES);

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
        return ResourceKey.create(Registries.DENSITY_FUNCTION, RWildernessMod.modLocation(location));
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