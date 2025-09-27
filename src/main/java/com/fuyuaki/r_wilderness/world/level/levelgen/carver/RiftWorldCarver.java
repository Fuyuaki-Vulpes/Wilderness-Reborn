package com.fuyuaki.r_wilderness.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.function.Function;

public class RiftWorldCarver extends WorldCarver<RiftCarverConfiguration> {
    public RiftWorldCarver(Codec<RiftCarverConfiguration> p_64711_) {
        super(p_64711_);
    }

    public boolean isStartChunk(RiftCarverConfiguration p_224797_, RandomSource p_224798_) {
        return p_224798_.nextFloat() <= p_224797_.probability;
    }

    public boolean carve(
            CarvingContext context,
            RiftCarverConfiguration configuration,
            ChunkAccess chunkAccess,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos chunkPos,
            CarvingMask carvingMask
    ) {
        int i = (this.getRange() * 2 - 1) * 16;
        double d0 = chunkPos.getBlockX(random.nextInt(16));
        int j = configuration.y.sample(random, context);
        double d1 = chunkPos.getBlockZ(random.nextInt(16));
        float f = random.nextFloat() * (float) (Math.PI * 2);
        float verticalRotation = configuration.verticalRotation.sample(random);
        float rotation = configuration.rotation.sample(random);
        double yScale = configuration.yScale.sample(random);
        float shapeThickness = configuration.shape.thickness.sample(random);
        int distanceFactor = (int)(i * configuration.shape.distanceFactor.sample(random));
        int l = 0;
        this.doCarve(context, configuration, chunkAccess, biomeAccessor, random.nextLong(), aquifer, d0, j, d1, shapeThickness, f, verticalRotation, rotation,0, distanceFactor, yScale, carvingMask);
        return true;
    }

    private void doCarve(
            CarvingContext context,
            RiftCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            long seed,
            Aquifer aquifer,
            double x,
            double y,
            double z,
            float thickness,
            float yaw,
            float pitch,
            float roll,
            int branchIndex,
            int branchCount,
            double horizontalVerticalRatio,
            CarvingMask carvingMask
    ) {
        RandomSource randomsource = RandomSource.create(seed);
        float[] afloat = this.initWidthFactors(context, config, randomsource);
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = branchIndex; i < branchCount; i++) {
            double d0 = 1.5 + Mth.sin(i * (float) Math.PI / branchCount) * thickness;
            double d1 = d0 * horizontalVerticalRatio;
            d0 *= config.shape.horizontalRadiusFactor.sample(randomsource);
            d1 = this.updateVerticalRadius(config, randomsource, d1, branchCount, i);
            float f2 = Mth.cos(pitch);
            float f3 = Mth.sin(pitch);
            x += Mth.cos(yaw) * f2;
            y += f3;
            z += Mth.sin(yaw) * f2;
            pitch *= 0.7F;
            pitch += f1 * 0.05F;
            yaw += f * 0.05F;
            f1 *= 0.8F;
            f *= 0.5F;
            f1 += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 2.0F;
            f += (randomsource.nextFloat() - randomsource.nextFloat()) * randomsource.nextFloat() * 4.0F;
            if (randomsource.nextInt(4) != 0) {
                if (!canReach(chunk.getPos(), x, z, i, branchCount, thickness)) {
                    return;
                }

                this.carveEllipsoid(
                        context,
                        config,
                        chunk,
                        biomeAccessor,
                        aquifer,
                        x,
                        y,
                        z,
                        d0,
                        d1,
                        carvingMask,
                        (p_159082_, p_159083_, p_159084_, p_159085_, p_159086_) -> this.shouldSkip(p_159082_, afloat, p_159083_, p_159084_, p_159085_, p_159086_)
                );
            }
        }
    }

    /**
     * Generates a random array full of width factors which are used to create the uneven walls of a ravine.
     * @return An array of length {@code context.getGenDepth()}, populated with values between 1.0 and 2.0 inclusive.
     */
    private float[] initWidthFactors(CarvingContext context, RiftCarverConfiguration config, RandomSource random) {
        int i = context.getGenDepth();
        float[] afloat = new float[i];
        float f = 1.0F;

        for (int j = 0; j < i; j++) {
            if (j == 0 || random.nextInt(config.shape.widthSmoothness) == 0) {
                f = 1.0F + random.nextFloat() * random.nextFloat();
            }

            afloat[j] = f * f;
        }

        return afloat;
    }

    private double updateVerticalRadius(RiftCarverConfiguration config, RandomSource random, double verticalRadius, float branchCount, float currentBranch) {
        float f = 1.0F - Mth.abs(0.5F - currentBranch / branchCount) * 2.0F;
        float f1 = config.shape.verticalRadiusDefaultFactor + config.shape.verticalRadiusCenterFactor * f;
        return f1 * verticalRadius * Mth.randomBetween(random, 0.75F, 1.0F);
    }

    private boolean shouldSkip(CarvingContext context, float[] widthFactors, double relativeX, double relativeY, double relativeZ, int y) {
        int i = y - context.getMinGenY();
        return (relativeX * relativeX + relativeZ * relativeZ) * widthFactors[i - 1] + relativeY * relativeY / 6.0 >= 1.0;
    }
}
