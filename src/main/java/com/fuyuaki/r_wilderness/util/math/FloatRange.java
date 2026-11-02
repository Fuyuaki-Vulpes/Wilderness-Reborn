package com.fuyuaki.r_wilderness.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FloatRange (float min, float mean, float max) {


    public static final Codec<FloatRange> CODEC = RecordCodecBuilder.create(
            p_223591_ -> p_223591_.group(
                            Codec.FLOAT.fieldOf("min").forGetter(FloatRange::min),
                            Codec.FLOAT.fieldOf("mean").forGetter(FloatRange::mean),
                            Codec.FLOAT.fieldOf("max").forGetter(FloatRange::max)
                    )
                    .apply(p_223591_, FloatRange::new)
    );
    public static final FloatRange ZERO = new FloatRange(0.0f, 0.0f, 0.0f);
    public static final FloatRange ANY = new FloatRange(-1.0f, 0.0f, 1.0f);
    public static final FloatRange MILD = new FloatRange(-0.15f, 0.0f, 0.15f);

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FloatRange(
                float min1, float mean1, float max1
        ) && min1 == this.min && mean1 == this.mean && max1 == this.max;
    }

    public static FloatRange of(float min, float max) {
        float mean = min + (max-min) / 2.0f;
        return of(min,mean,max);
    }
    public static FloatRange of(float min, float mean, float max) {
        return new FloatRange(min,mean,max);
    }


    public boolean accepts(float value) {
        return value >= min && value <= max;
    }

    public float affinity(float value) {


        float p;
        float a;

        if (value > mean) {
            p = max - mean;
            a = value - mean;

        } else {
            p = mean - min;
            a = mean - value;

        }

        float n;
        if (p == 0) {
            n = a;
        } else {
            n = (a / p);
        }

        if (!accepts(value)) {
            return (1 - n) - 1;
        }

        return 1 - n;

    }

    public float affinityHarsh(float value) {


        float p;
        float a;

        if (value > mean) {
            p = max - mean;
            a = value - mean;

        } else {
            p = mean - min;
            a = mean - value;

        }

        float n;
        if (p == 0) {
            n = a;
        } else {
            n = (a / p);
        }

        if (!accepts(value)) {
            return (1 - n) - 1000;
        }

        return 1 - n;

    }
}

