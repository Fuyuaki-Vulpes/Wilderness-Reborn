package com.fuyuaki.wilderness_reborn.data.pack.worldgen;

import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class PackTerrainProvider {
    private static final float DEEP_OCEAN_CONTINENTALNESS = -0.51F;
    private static final float OCEAN_CONTINENTALNESS = -0.4F;
    private static final float PLAINS_CONTINENTALNESS = 0.1F;
    private static final float BEACH_CONTINENTALNESS = -0.15F;
    private static final ToFloatFunction<Float> NO_TRANSFORM = ToFloatFunction.IDENTITY;
    private static final ToFloatFunction<Float> AMPLIFIED_OFFSET = ToFloatFunction.createUnlimited(p_236651_ -> p_236651_ < 0.0F ? p_236651_ : p_236651_ * 2.0F);
    private static final ToFloatFunction<Float> AMPLIFIED_FACTOR = ToFloatFunction.createUnlimited(p_236649_ -> 1.25F - 6.25F / (p_236649_ + 5.0F));
    private static final ToFloatFunction<Float> AMPLIFIED_JAGGEDNESS = ToFloatFunction.createUnlimited(p_236641_ -> p_236641_ * 2.0F);

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldOffset(I continents, I erosion, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> tofloatfunction = amplified ? AMPLIFIED_OFFSET : NO_TRANSFORM;
        CubicSpline<C, I> cubicspline = buildErosionOffsetSpline(erosion, ridgesFolded, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, tofloatfunction);
        CubicSpline<C, I> cubicspline1 = buildErosionOffsetSpline(erosion, ridgesFolded, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, tofloatfunction);
        CubicSpline<C, I> cubicspline2 = buildErosionOffsetSpline(erosion, ridgesFolded, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, tofloatfunction);
        CubicSpline<C, I> cubicspline3 = buildErosionOffsetSpline(erosion, ridgesFolded, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, tofloatfunction);
        return CubicSpline.<C, I>builder(continents, tofloatfunction)
                .addPoint(-1.1F, 0.044F)
                .addPoint(-1.02F, -0.2222F)
                .addPoint(-0.78F, -0.40F)
                .addPoint(-0.51F, -0.2222F)
                .addPoint(-0.30F, -0.12F)
                .addPoint(-0.195F, -0.06F)
                .addPoint(-0.13F, cubicspline)
                .addPoint(0.0F, cubicspline1)
                .addPoint(0.25F, cubicspline2)
                .addPoint(1.0F, cubicspline3)
                .build();
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldFactor(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> tofloatfunction = amplified ? AMPLIFIED_FACTOR : NO_TRANSFORM;
        return CubicSpline.<C, I>builder(continents, NO_TRANSFORM)
                .addPoint(-0.19F, 3.95F)
                .addPoint(-0.15F, getErosionFactor(erosion, ridges, ridgesFolded, 6.25F, true, NO_TRANSFORM))
                .addPoint(-0.1F, getErosionFactor(erosion, ridges, ridgesFolded, 5.47F, true, tofloatfunction))
                .addPoint(0.03F, getErosionFactor(erosion, ridges, ridgesFolded, 5.08F, true, tofloatfunction))
                .addPoint(0.06F, getErosionFactor(erosion, ridges, ridgesFolded, 4.69F, false, tofloatfunction))
                .build();
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldJaggedness(I continents, I erosion, I ridges, I ridgesFolded, boolean amplified) {
        ToFloatFunction<Float> tofloatfunction = amplified ? AMPLIFIED_JAGGEDNESS : NO_TRANSFORM;
        float f = 0.65F;
        return CubicSpline.<C, I>builder(continents, tofloatfunction)
                .addPoint(-0.11F, 0.0F)
                .addPoint(0.03F, buildErosionJaggednessSpline(erosion, ridges, ridgesFolded, 1.0F, 0.5F, 0.0F, 0.0F, tofloatfunction))
                .addPoint(0.65F, buildErosionJaggednessSpline(erosion, ridges, ridgesFolded, 1.0F, 1.0F, 1.0F, 0.0F, tofloatfunction))
                .build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionJaggednessSpline(
            I erosion, I ridges, I ridgesFolded, float highErosionHighWeirdness, float lowErosionHighWeirdness, float highErosionMidWeirdness, float lowErosionMidWeirdness, ToFloatFunction<Float> transform
    ) {
        float f = -0.5775F;
        CubicSpline<C, I> cubicspline = buildRidgeJaggednessSpline(ridges, ridgesFolded, highErosionHighWeirdness, highErosionMidWeirdness, transform);
        CubicSpline<C, I> cubicspline1 = buildRidgeJaggednessSpline(ridges, ridgesFolded, lowErosionHighWeirdness, lowErosionMidWeirdness, transform);
        return CubicSpline.<C, I>builder(erosion, transform)
                .addPoint(-1.0F, cubicspline)
                .addPoint(-0.78F, cubicspline1)
                .addPoint(-0.5775F, cubicspline1)
                .addPoint(-0.375F, 0.0F)
                .build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildRidgeJaggednessSpline(
            I ridges, I ridgesFolded, float highWeirdnessMagnitude, float midWeirdnessMagnitude, ToFloatFunction<Float> transform
    ) {
        float f = PackNoiseRouterData.peaksAndValleys(0.4F);
        float f1 = PackNoiseRouterData.peaksAndValleys(0.56666666F);
        float f2 = (f + f1) / 2.0F;
        CubicSpline.Builder<C, I> builder = CubicSpline.builder(ridgesFolded, transform);
        builder.addPoint(f, 0.0F);
        if (midWeirdnessMagnitude > 0.0F) {
            builder.addPoint(f2, buildWeirdnessJaggednessSpline(ridges, midWeirdnessMagnitude, transform));
        } else {
            builder.addPoint(f2, 0.0F);
        }

        if (highWeirdnessMagnitude > 0.0F) {
            builder.addPoint(1.0F, buildWeirdnessJaggednessSpline(ridges, highWeirdnessMagnitude, transform));
        } else {
            builder.addPoint(1.0F, 0.0F);
        }

        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildWeirdnessJaggednessSpline(
            I ridges, float magnitude, ToFloatFunction<Float> transform
    ) {
        float f = 0.63F * magnitude;
        float f1 = 0.3F * magnitude;
        return CubicSpline.<C, I>builder(ridges, transform).addPoint(-0.01F, f).addPoint(0.01F, f1).build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> getErosionFactor(
            I erosion, I ridges, I ridgesFolded, float value, boolean higherValues, ToFloatFunction<Float> transform
    ) {
        CubicSpline<C, I> cubicspline = CubicSpline.<C, I>builder(ridges, transform).addPoint(-0.2F, 6.3F).addPoint(0.2F, value).build();
        CubicSpline.Builder<C, I> builder = CubicSpline.<C, I>builder(erosion, transform)
                .addPoint(-0.6F, cubicspline)
                .addPoint(-0.5F, CubicSpline.<C, I>builder(ridges, transform).addPoint(-0.05F, 6.3F).addPoint(0.05F, 2.67F).build())
                .addPoint(-0.35F, cubicspline)
                .addPoint(-0.25F, cubicspline)
                .addPoint(-0.1F, CubicSpline.<C, I>builder(ridges, transform).addPoint(-0.05F, 2.67F).addPoint(0.05F, 6.3F).build())
                .addPoint(0.03F, cubicspline);
        if (higherValues) {
            CubicSpline<C, I> cubicspline1 = CubicSpline.<C, I>builder(ridges, transform).addPoint(0.0F, value).addPoint(0.1F, 0.625F).build();
            CubicSpline<C, I> cubicspline2 = CubicSpline.<C, I>builder(ridgesFolded, transform).addPoint(-0.9F, value).addPoint(-0.69F, cubicspline1).build();
            builder.addPoint(0.35F, value).addPoint(0.45F, cubicspline2).addPoint(0.55F, cubicspline2).addPoint(0.62F, value);
        } else {
            CubicSpline<C, I> cubicspline3 = CubicSpline.<C, I>builder(ridgesFolded, transform).addPoint(-0.7F, cubicspline).addPoint(-0.15F, 1.37F).build();
            CubicSpline<C, I> cubicspline4 = CubicSpline.<C, I>builder(ridgesFolded, transform).addPoint(0.45F, cubicspline).addPoint(0.7F, 1.56F).build();
            builder.addPoint(0.05F, cubicspline4)
                    .addPoint(0.4F, cubicspline4)
                    .addPoint(0.45F, cubicspline3)
                    .addPoint(0.55F, cubicspline3)
                    .addPoint(0.58F, value);
        }

        return builder.build();
    }

    private static float calculateSlope(float y1, float y2, float x1, float x2) {
        return (y2 - y1) / (x2 - x1);
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildMountainRidgeSplineWithPoints(
            I ridgesFolded, float magnitude, boolean useMaxSlope, ToFloatFunction<Float> transform
    ) {
        CubicSpline.Builder<C, I> builder = CubicSpline.builder(ridgesFolded, transform);
        float f = -0.7F;
        float f1 = -1.0F;
        float f2 = mountainContinentalness(-1.0F, magnitude, -0.7F);
        float f3 = 1.0F;
        float f4 = mountainContinentalness(1.0F, magnitude, -0.7F);
        float f5 = calculateMountainRidgeZeroContinentalnessPoint(magnitude);
        float f6 = -0.65F;
        if (-0.65F < f5 && f5 < 1.0F) {
            float f14 = mountainContinentalness(-0.65F, magnitude, -0.7F);
            float f8 = -0.75F;
            float f9 = mountainContinentalness(-0.75F, magnitude, -0.7F);
            float f10 = calculateSlope(f2, f9, -1.0F, -0.75F);
            builder.addPoint(-1.0F, f2, f10);
            builder.addPoint(-0.75F, f9);
            builder.addPoint(-0.65F, f14);
            float f11 = mountainContinentalness(f5, magnitude, -0.7F);
            float f12 = calculateSlope(f11, f4, f5, 1.0F);
            float f13 = 0.01F;
            builder.addPoint(f5 - 0.01F, f11);
            builder.addPoint(f5, f11, f12);
            builder.addPoint(1.0F, f4, f12);
        } else {
            float f7 = calculateSlope(f2, f4, -1.0F, 1.0F);
            if (useMaxSlope) {
                builder.addPoint(-1.0F, Math.max(0.2F, f2));
                builder.addPoint(0.0F, Mth.lerp(0.5F, f2, f4), f7);
            } else {
                builder.addPoint(-1.0F, f2, f7);
            }

            builder.addPoint(1.0F, f4, f7);
        }

        return builder.build();
    }

    private static float mountainContinentalness(float heightFactor, float magnitude, float cutoffHeight) {
        float f = 1.17F;
        float f1 = 0.46082947F;
        float f2 = 1.0F - (1.0F - magnitude) * 0.5F;
        float f3 = 0.5F * (1.0F - magnitude);
        float f4 = (heightFactor + 1.17F) * 0.46082947F;
        float f5 = f4 * f2 - f3;
        return heightFactor < cutoffHeight ? Math.max(f5, -0.2222F) : Math.max(f5, 0.0F);
    }

    private static float calculateMountainRidgeZeroContinentalnessPoint(float input) {
        float f = 1.17F;
        float f1 = 0.46082947F;
        float f2 = 1.0F - (1.0F - input) * 0.5F;
        float f3 = 0.5F * (1.0F - input);
        return f3 / (0.46082947F * f2) - 1.17F;
    }

    public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionOffsetSpline(
            I erosion,
            I ridgesFolded,
            float v0,
            float v1,
            float v2,
            float magnitude,
            float v3,
            float v4,
            boolean extended,
            boolean useMaxSlope,
            ToFloatFunction<Float> transform
    ) {
        float f = 0.6F;
        float f1 = 0.5F;
        float f2 = 0.5F;
        CubicSpline<C, I> mountainSplineM0 = buildMountainRidgeSplineWithPoints(ridgesFolded, Mth.lerp(magnitude, 0.6F, extended ? 2.5F : 1.5F), useMaxSlope, transform);
        CubicSpline<C, I> mountainSplineM1 = buildMountainRidgeSplineWithPoints(ridgesFolded, Mth.lerp(magnitude, 0.6F, 1.0F), useMaxSlope, transform);
        CubicSpline<C, I> mountainSplineM2 = buildMountainRidgeSplineWithPoints(ridgesFolded, magnitude, useMaxSlope, transform);

        CubicSpline<C, I> ridgeSpline0 = curveSpline(
                ridgesFolded, v0 - 0.15F, 0.5F * magnitude, Mth.lerp(0.5F, 0.5F, 0.5F) * magnitude, 0.5F * magnitude, 0.6F * magnitude, 0.5F, transform
        );
        CubicSpline<C, I> ridgeSpline1 = curveSpline(
                ridgesFolded, v0, v3 * magnitude, v1 * magnitude, 0.5F * magnitude, 0.6F * magnitude, 0.5F, transform
        );
        CubicSpline<C, I> ridgeSpline2 = curveSpline(ridgesFolded, v0, v3, v3, v1, v2, 0.5F, transform);
        CubicSpline<C, I> ridgeSpline3 = curveSpline(ridgesFolded, v0, v3, v3, v1, v2, 0.5F, transform);
        CubicSpline<C, I> ridgeSpline4 = CubicSpline.<C, I>builder(ridgesFolded, transform)
                .addPoint(-1.0F, v0)
                .addPoint(-0.4F, ridgeSpline2)
                .addPoint(0.0F, v2 + 0.07F)
                .build();
        CubicSpline<C, I> ridgeSpline5 = curveSpline(ridgesFolded, -0.02F, v4, v4, v1, v2, 0.30F, transform);


        CubicSpline.Builder<C, I> builder = CubicSpline.<C, I>builder(erosion, transform)
                .addPoint(-0.85F, mountainSplineM0)
                .addPoint(-0.7F, mountainSplineM1)
                .addPoint(-0.4F, mountainSplineM2)
                .addPoint(-0.35F, ridgeSpline0)
                .addPoint(-0.1F, ridgeSpline1)
                .addPoint(0.2F, ridgeSpline2);
        if (extended) {
            builder.addPoint(0.4F, ridgeSpline3).addPoint(0.45F, ridgeSpline4).addPoint(0.55F, ridgeSpline4).addPoint(0.58F, ridgeSpline3);
        }

        builder.addPoint(0.7F, ridgeSpline5);

        return builder.build();
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> curveSpline(
            I ridgesFolded, float fullNegative, float midNeg, float midpoint, float midPos, float fullPositive, float minSmoothing, ToFloatFunction<Float> transform
    ) {
        float f = Math.max(0.5F * (midNeg - fullNegative), minSmoothing);
        float f1 = 5.0F * (midpoint - midNeg);
        return CubicSpline.<C, I>builder(ridgesFolded, transform)
                .addPoint(-1.0F, fullNegative, f)
                .addPoint(-0.4F, midNeg, Math.min(f, f1))
                .addPoint(0.0F, midpoint, f1)
                .addPoint(0.4F, midPos, 2.0F * (midPos - midpoint))
                .addPoint(1.0F, fullPositive, 0.7F * (fullPositive - midPos))
                .build();
    }


}
