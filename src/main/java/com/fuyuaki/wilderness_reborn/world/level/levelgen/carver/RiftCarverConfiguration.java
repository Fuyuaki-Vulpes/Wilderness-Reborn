package com.fuyuaki.wilderness_reborn.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class RiftCarverConfiguration extends CarverConfiguration {
    public static final Codec<RiftCarverConfiguration> CODEC = RecordCodecBuilder.create(
            p_158984_ -> p_158984_.group(
                            CarverConfiguration.CODEC.forGetter(p_158990_ -> p_158990_),
                            FloatProvider.CODEC.fieldOf("vertical_rotation").forGetter(p_158988_ -> p_158988_.verticalRotation),
                            FloatProvider.CODEC.fieldOf("rotation").forGetter(p_158988_ -> p_158988_.rotation),
                            RiftShapeConfiguration.CODEC.fieldOf("shape").forGetter(p_158986_ -> p_158986_.shape)
                    )
                    .apply(p_158984_, RiftCarverConfiguration::new)
    );
    public final FloatProvider verticalRotation;
    public final FloatProvider rotation;
    public final RiftShapeConfiguration shape;

    public RiftCarverConfiguration(
            float probability,
            HeightProvider y,
            FloatProvider yScale,
            VerticalAnchor lavaLevel,
            CarverDebugSettings debugSettings,
            HolderSet<Block> replaceable,
            FloatProvider verticalRotation,
            FloatProvider rotation,
            RiftShapeConfiguration shape
    ) {
        super(probability, y, yScale, lavaLevel, debugSettings, replaceable);
        this.verticalRotation = verticalRotation;
        this.rotation = verticalRotation;
        this.shape = shape;
    }

    public RiftCarverConfiguration(CarverConfiguration config, FloatProvider verticalRotation,FloatProvider rotation, RiftShapeConfiguration shape) {
        this(config.probability, config.y, config.yScale, config.lavaLevel, config.debugSettings, config.replaceable, rotation, verticalRotation, shape);
    }

    public static class RiftShapeConfiguration {
        public static final Codec<RiftShapeConfiguration> CODEC = RecordCodecBuilder.create(
                p_159007_ -> p_159007_.group(
                                FloatProvider.CODEC.fieldOf("distance_factor").forGetter(p_159019_ -> p_159019_.distanceFactor),
                                FloatProvider.CODEC.fieldOf("thickness").forGetter(p_159017_ -> p_159017_.thickness),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("width_smoothness").forGetter(p_159015_ -> p_159015_.widthSmoothness),
                                FloatProvider.CODEC.fieldOf("horizontal_radius_factor").forGetter(p_159013_ -> p_159013_.horizontalRadiusFactor),
                                Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter(p_159011_ -> p_159011_.verticalRadiusDefaultFactor),
                                Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter(p_159009_ -> p_159009_.verticalRadiusCenterFactor)
                        )
                        .apply(p_159007_, RiftShapeConfiguration::new)
        );
        public final FloatProvider distanceFactor;
        public final FloatProvider thickness;
        public final int widthSmoothness;
        public final FloatProvider horizontalRadiusFactor;
        public final float verticalRadiusDefaultFactor;
        public final float verticalRadiusCenterFactor;

        public RiftShapeConfiguration(
                FloatProvider distanceFactor, FloatProvider thickness, int widthSmoothness, FloatProvider horizontalRadiusFactor, float verticalRadiusDefaultFactor, float verticalRadiusCenterFactor
        ) {
            this.widthSmoothness = widthSmoothness;
            this.horizontalRadiusFactor = horizontalRadiusFactor;
            this.verticalRadiusDefaultFactor = verticalRadiusDefaultFactor;
            this.verticalRadiusCenterFactor = verticalRadiusCenterFactor;
            this.distanceFactor = distanceFactor;
            this.thickness = thickness;
        }
    }}
