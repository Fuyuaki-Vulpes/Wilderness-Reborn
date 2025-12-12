package com.fuyuaki.r_wilderness.client.sky;


import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.api.config.ClientConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.math.BigDecimal;
import java.math.RoundingMode;


/*
* Class borrowed from Caelum
* https://modrinth.com/mod/caelum
*
* Some code is from Nicer Skies
* https://modrinth.com/mod/nicer-skies
* */

public class SkyUtils {

    private static final float[] sunriseCol = new float[4];

    public static double calculateStarLatitudeRotation(Level level, double z) {
        BigDecimal minZ = BigDecimal.valueOf(ClientConfig.MIN_Z.get()).subtract(BigDecimal.valueOf(ClientConfig.Z_MARGIN.get()));
        BigDecimal maxZ = BigDecimal.valueOf(ClientConfig.MAX_Z.get()).add(BigDecimal.valueOf(ClientConfig.Z_MARGIN.get())).subtract(minZ);
        BigDecimal bigZ = BigDecimal.valueOf(z).subtract(minZ);

        return Mth.clamp(bigZ.divide(maxZ, RoundingMode.HALF_DOWN).doubleValue(), 0, 1) - 0.5;
    }

    public static double starLatitudeRotation(Level level, double z) {
        return calculateStarLatitudeRotation(level, z);
    }

    public static double sunLatitudeRotation(Level level, double z) {
        return starLatitudeRotation(level, z);
    }



    public static double sunHeight(Level level, double z, float partialTicks) {
        double latitudeTilt = sunLatitudeRotation(level, z) * Mth.PI;
        double untitledHeight = (Mth.cos(level.getDayTimePerTick() * Mth.TWO_PI));
        return untitledHeight * Math.cos(latitudeTilt);
    }

    public static double smoothTimeOfDay(double timeOfDay) {
        double subtracted = timeOfDay - 0.75d;
        if (subtracted < 0) {
            subtracted++;
        }
        return subtracted;
    }

    public static float[] getSunriseColor(Level level, double z, float partialTicks) {
        float f = 0.4F;
        float f1 = (float) sunHeight(level, z, partialTicks);
        float f2 = -0.0F;
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
            f4 *= f4;
            sunriseCol[0] = f3 * 0.3F + 0.7F;
            sunriseCol[1] = f3 * f3 * 0.7F + 0.2F;
            sunriseCol[2] = f3 * f3 * 0.0F + 0.2F;
            sunriseCol[3] = f4;
            return sunriseCol;
        } else {
            return null;
        }
    }

    private static final Vector3f EAST = new Vector3f(1, 0, 0);
    private static final Vector3f UP = new Vector3f(0, 1, 0);

    public static double getSunriseColorRotation(Level level, double z, float partialTicks) {
        return 180 + (sign(z) * EAST.angleSigned(sunVector(level, z, partialTicks), UP) * Mth.RAD_TO_DEG);
    }

    private static double sign(double val) {
        return val == 0 ? 1 : Math.signum(val);
    }

    public static double apparentTimeOfDay(Level level, double z, float partialTicks) {
        double sunHeight = sunHeight(level, z, partialTicks);
        return 0.75 + sunHeight / 4d;
    }

    public static Vector3f sunVector(Level level, double zPos, float partialTicks) {
        double azimuth = smoothTimeOfDay(level.getDayTimePerTick()) * 360;
        double tiltAngle = sunLatitudeRotation(level, zPos) * 180;
        double zenithAngle = tiltAngle - 90;
        double azimuthRad = azimuth * Mth.DEG_TO_RAD;
        double zenithAngleRad = zenithAngle * Mth.DEG_TO_RAD;

        double x = Math.cos(azimuthRad) * Math.cos(zenithAngleRad);
        double z = Math.sin(azimuthRad) * Math.cos(zenithAngleRad);

        if (tiltAngle < 0) {
            x *= -1;
            z *= -1;
        }

        return new Vector3f((float) x, 0, (float) z);
    }


}
