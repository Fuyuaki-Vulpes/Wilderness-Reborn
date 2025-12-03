package com.fuyuaki.r_wilderness.mixin.client;


import com.fuyuaki.r_wilderness.client.sky.SkyUtils;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.IRenderableSection;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow @Final private SkyRenderer skyRenderer;

    @Shadow @Final private LevelRenderState levelRenderState;

    @Shadow @Final private LevelTargetBundle targets;

    @Shadow protected abstract boolean doesMobEffectBlockSky(Camera camera);

    @Shadow public abstract Iterable<? extends IRenderableSection> getRenderableSections();
/*
    @Inject(method = "addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V", at = @At("HEAD"))
    private void skyPass(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice shaderFog, Matrix4f modelViewMatrix, CallbackInfo ci) {
        FogType fogtype = camera.getFluidInCamera();
        if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(camera)) {
            SkyRenderState skyrenderstate = this.levelRenderState.skyRenderState;
            if (skyrenderstate.skyType != DimensionSpecialEffects.SkyType.NONE) {
                FramePass framepass = frameGraphBuilder.addPass("sky");
                this.targets.main = framepass.readsAndWrites(this.targets.main);
                framepass.executes(
                        () -> {
                            if (!this.levelRenderState.dimensionSpecialEffects.renderSky(levelRenderState, skyrenderstate, modelViewMatrix, () -> RenderSystem.setShaderFog(shaderFog))) {
                                RenderSystem.setShaderFog(shaderFog);
                                if (skyrenderstate.skyType == DimensionSpecialEffects.SkyType.END) {
                                    this.skyRenderer.renderEndSky();
                                    if (skyrenderstate.endFlashIntensity > 1.0E-5F) {
                                        PoseStack posestack1 = new PoseStack();
                                        this.skyRenderer
                                                .renderEndFlash(posestack1, skyrenderstate.endFlashIntensity, skyrenderstate.endFlashXAngle, skyrenderstate.endFlashYAngle);
                                    }
                                } else {
                                    PoseStack posestack = new PoseStack();
                                    float f = ARGB.redFloat(skyrenderstate.skyColor);
                                    float f1 = ARGB.greenFloat(skyrenderstate.skyColor);
                                    float f2 = ARGB.blueFloat(skyrenderstate.skyColor);
                                    this.skyRenderer.renderSkyDisc(f, f1, f2);
                                    if (skyrenderstate.isSunriseOrSunset) {
                                        this.skyRenderer.renderSunriseAndSunset(posestack, skyrenderstate.sunAngle, skyrenderstate.sunriseAndSunsetColor);
                                    }

                                    this.skyRenderer
                                            .renderSunMoonAndStars(
                                                    posestack, skyrenderstate.timeOfDay, skyrenderstate.moonPhase, skyrenderstate.rainBrightness, skyrenderstate.starBrightness
                                            );
                                    if (skyrenderstate.shouldRenderDarkDisc) {
                                        this.skyRenderer.renderDarkDisc();
                                    }
                                }
                            }
                            Profiler.get().push("neoforge_render_after_sky");
                            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.client.event.RenderLevelStageEvent.AfterSky((LevelRenderer)(Object)this, this.levelRenderState, null, modelViewMatrix, this.getRenderableSections()));
                            Profiler.get().pop();
                        }
                );
            }
        }
    }
*/


}
