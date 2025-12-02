package com.fuyuaki.r_wilderness.mixin.client;

import com.fuyuaki.r_wilderness.client.sky.SkyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow @Final private Minecraft minecraft;
/*
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"), method = "getSkyColor")
    private float getSkyColor$getTimeOfDay(ClientLevel instance, float v) {
        return (float) SkyUtils.apparentTimeOfDay(instance, minecraft.getCameraEntity().position().z(),v);
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"), method = "getStarBrightness(F)F")
    private float getStarBrightness$getTimeOfDay(ClientLevel instance, float v) {
        return (float) SkyUtils.apparentTimeOfDay(instance, minecraft.getCameraEntity().position().z(),v);
    }
 */

}
