package com.fuyuaki.r_wilderness.mixin.client;


import com.fuyuaki.r_wilderness.client.sky.SkyUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.environment.AirBasedFogEnvironment;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AirBasedFogEnvironment.class)
public class AirBasedFogEnvironmentMixin {
/*
    @Inject(method = "getBaseColor", at = @At("HEAD"), cancellable = true)
    private void getBaseColorOverride(ClientLevel level, Camera camera, int renderDistance, float partialTicks, CallbackInfoReturnable<Integer> cir){
        float timeOfDayScalar = Mth.clamp(Mth.cos((float) (SkyUtils.apparentTimeOfDay(level, Minecraft.getInstance().getCameraEntity().position().z(),partialTicks) * 6.2831855F)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomemanager = level.getBiomeManager();
        Vec3 vec3 = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
        Vec3 biomeFogColor = level.effects().getBrightnessDependentFogColor(CubicSampler.gaussianSampleVec3(vec3, (x, y, z) -> {
            return Vec3.fromRGB24(((Biome)biomemanager.getNoiseBiomeAtQuart(x, y, z).value()).getFogColor());
        }), timeOfDayScalar);
        float red = (float)biomeFogColor.x();
        float green = (float)biomeFogColor.y();
        float blue = (float)biomeFogColor.z();
        float cameraRelativeSunVector;
        if (renderDistance >= 4) {
            Vector3f sunVector = SkyUtils.sunVector(level, Minecraft.getInstance().getCameraEntity().position().z(),partialTicks);
            cameraRelativeSunVector = camera.getLookVector().dot(sunVector);
            if (cameraRelativeSunVector > 0.0F && level.effects().isSunriseOrSunset((float) SkyUtils.apparentTimeOfDay(level, Minecraft.getInstance().getCameraEntity().position().z(),partialTicks))) {
                int i = level.effects().getSunriseOrSunsetColor((float) SkyUtils.apparentTimeOfDay(level, Minecraft.getInstance().getCameraEntity().position().z(),partialTicks));
                cameraRelativeSunVector *= ARGB.alphaFloat(i);
                red = Mth.lerp(cameraRelativeSunVector, red, ARGB.redFloat(i));
                green = Mth.lerp(cameraRelativeSunVector, green, ARGB.greenFloat(i));
                blue = Mth.lerp(cameraRelativeSunVector, blue, ARGB.blueFloat(i));
            }
        }

        int skyColor = level.getSkyColor(camera.getPosition(), partialTicks);
        float skyRed = ARGB.redFloat(skyColor);
        cameraRelativeSunVector = ARGB.greenFloat(skyColor);
        float skyBlue = ARGB.blueFloat(skyColor);
        float renderDistanceScalar = 0.25F + 0.75F * (float)renderDistance / 32.0F;
        renderDistanceScalar = 1.0F - (float)Math.pow((double)renderDistanceScalar, 0.25);
        red += (skyRed - red) * renderDistanceScalar;
        green += (cameraRelativeSunVector - green) * renderDistanceScalar;
        blue += (skyBlue - blue) * renderDistanceScalar;
        float rainLevel = level.getRainLevel(partialTicks);
        float rainDarkener;
        float rainDarkenerB;
        if (rainLevel > 0.0F) {
            rainDarkener = 1.0F - rainLevel * 0.5F;
            rainDarkenerB = 1.0F - rainLevel * 0.4F;
            red *= rainDarkener;
            green *= rainDarkener;
            blue *= rainDarkenerB;
        }

        rainDarkener = level.getThunderLevel(partialTicks);
        if (rainDarkener > 0.0F) {
            rainDarkenerB = 1.0F - rainDarkener * 0.5F;
            red *= rainDarkenerB;
            green *= rainDarkenerB;
            blue *= rainDarkenerB;
        }

        cir.setReturnValue(ARGB.colorFromFloat(1.0F, red, green, blue));
    }

*/
}
