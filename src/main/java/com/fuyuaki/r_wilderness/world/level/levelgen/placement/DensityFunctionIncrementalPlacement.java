package com.fuyuaki.r_wilderness.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class DensityFunctionIncrementalPlacement extends RepeatingPlacement {
    public static final MapCodec<DensityFunctionIncrementalPlacement> CODEC = RecordCodecBuilder.mapCodec(
            grouped -> grouped.group(
                            IntProvider.codec(0, 256).fieldOf("count").forGetter(instance -> instance.count),
                            DensityFunction.CODEC.fieldOf("density_function").forGetter(instance -> instance.densityFunction),
                            Codec.FLOAT.fieldOf("density_min_exclusive").orElse(Float.MIN_VALUE).forGetter(instance -> instance.densityMinExclusive),
                            Codec.FLOAT.fieldOf("density_max_inclusive").orElse(Float.MAX_VALUE).forGetter(instance -> instance.densityMaxInclusive),
                            Codec.BOOL.fieldOf("linear").orElse(Boolean.FALSE).forGetter(instance -> instance.linear)
                    )
                    .apply(grouped, DensityFunctionIncrementalPlacement::new)
    );

    private final IntProvider count;
    private final Holder<DensityFunction> densityFunction;
    private final float densityMinExclusive;
    private final float densityMaxInclusive;
    private final boolean linear;


    private DensityFunctionIncrementalPlacement(IntProvider count, Holder<DensityFunction> densityFunction, float densityMinExclusive, float densityMaxInclusive, boolean linear) {
            this.count = count;
            this.densityFunction = densityFunction;
            this.densityMinExclusive = densityMinExclusive;
            this.densityMaxInclusive = densityMaxInclusive;
            this.linear = linear;
    }


    public static DensityFunctionIncrementalPlacement of(IntProvider count, Holder<DensityFunction> densityFunction, float densityMinExclusive, float densityMaxInclusive, boolean linear) {
        return new DensityFunctionIncrementalPlacement(count, densityFunction, densityMinExclusive,densityMaxInclusive,linear);
    }

    @Override
    protected int count(RandomSource random, BlockPos pos) {
        return 0;
    }


    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifierTypes.DENSITY_FUNCTION_INCREMENTAL_PLACEMENT.get();
    }
}
