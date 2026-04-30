package com.fuyuaki.r_wilderness.mixin.client;


import net.minecraft.client.renderer.*;
import net.neoforged.neoforge.client.IRenderableSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow private SkyRenderer skyRenderer;

    @Shadow @Final private LevelTargetBundle targets;

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
