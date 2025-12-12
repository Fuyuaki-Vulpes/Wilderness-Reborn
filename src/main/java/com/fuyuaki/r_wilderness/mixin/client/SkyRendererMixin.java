package com.fuyuaki.r_wilderness.mixin.client;


import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.api.config.ClientConfig;
import com.fuyuaki.r_wilderness.client.sky.star.Gradient;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.Math;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    private static final RenderPipeline R_STARS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                    .withLocation("pipeline/stars")
                    .withVertexShader("core/stars")
                    .withFragmentShader("core/stars")
                    .withBlend(BlendFunction.OVERLAY)
                    .withDepthWrite(false)
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                    .build()
    );


    @Shadow private int starIndexCount;
    @Shadow @Final private GpuBuffer starBuffer;

    @Shadow protected abstract void renderStars(float starBrightness, PoseStack poseStack);


    @Shadow protected abstract void renderSun(float alpha, PoseStack poseStack);

    @Shadow @Final private RenderSystem.AutoStorageIndexBuffer quadIndices;
    private static final Identifier MOON_BRIGHT_LOCATION = RWildernessMod.modLocation("textures/environment/moon.png");
    private static final Identifier MOON_DARK_LOCATION = RWildernessMod.modLocation("textures/environment/moon_shadow.png");


    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {

    }


    private final Gradient starGradient = new Gradient() {{
        add(0.0f, 255, 179, 60);
        add(0.2f, 255, 249, 253);
        add(0.7f, 255, 255, 253);
        add(1.0f, 120, 199, 255);
    }};


/*
    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack poseStack, float timeOfDay, int moonPhase, float rainLevel, float starBrightness, CallbackInfo ci) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-timeOfDay * 360.0F));

        this.renderSun(rainLevel, poseStack);
        this.renderMoon(moonPhase, rainLevel, poseStack);
        if (starBrightness > 0.0F) {
            this.renderStars(starBrightness, poseStack);
        }

        poseStack.popPose();
        ci.cancel();
    }
*/

    @Inject(method = "buildStars", at = @At("HEAD"), cancellable = true)
    private void buildStars(CallbackInfoReturnable<GpuBuffer> cir) {
        RandomSource randomsource = RandomSource.create(10842L);

        GpuBuffer gpubuffer;
        try (ByteBufferBuilder bytebufferbuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR.getVertexSize() * ClientConfig.STAR_COUNT.get() * 4)) {
            BufferBuilder bufferbuilder = new BufferBuilder(bytebufferbuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            for (int i = 0; i < ClientConfig.STAR_COUNT.get(); i++) {
                float x = randomsource.nextFloat() * 2.0F - 1.0F;
                float y = randomsource.nextFloat() * 2.0F - 1.0F;
                float z = randomsource.nextFloat() * 2.0F - 1.0F;
                float radius = 0.05F + randomsource.nextFloat() * 0.1F;
                float length = Mth.lengthSquared(x, y, z);
                float gradient = randomsource.nextFloat();
                int[] c = starGradient.getAt(gradient);
                int color = ARGB.color(c[0],c[1],c[2]);
                if (!(length <= 0.010000001F) && !(length >= 1.0F)) {
                    Vector3f pos = new Vector3f(x, y, z).normalize(100.0F);
                    float randomRot = (float) (randomsource.nextDouble() * (float) Math.PI * 2.0);
                    Matrix3f matrix3f = new Matrix3f().rotateTowards(new Vector3f(pos).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-randomRot);
                    bufferbuilder.addVertex(new Vector3f(radius, -radius, 0.0F).mul(matrix3f).add(pos)).setColor(color);
                    bufferbuilder.addVertex(new Vector3f(radius, radius, 0.0F).mul(matrix3f).add(pos)).setColor(color);
                    bufferbuilder.addVertex(new Vector3f(-radius, radius, 0.0F).mul(matrix3f).add(pos)).setColor(color);
                    bufferbuilder.addVertex(new Vector3f(-radius, -radius, 0.0F).mul(matrix3f).add(pos)).setColor(color);
                }
            }
            try (MeshData meshdata = bufferbuilder.buildOrThrow()) {
                this.starIndexCount = meshdata.drawState().indexCount();
                gpubuffer = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", 40, meshdata.vertexBuffer());
            }
        }
        cir.setReturnValue(gpubuffer);
    }

    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
    private void renderStars(float starBrightness, PoseStack poseStack, CallbackInfo ci) {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        matrix4fstack.mul(poseStack.last().pose());
        RenderPipeline renderpipeline = R_STARS;
        GpuTextureView gputextureview = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView gputextureview1 = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBuffer gpubuffer = this.quadIndices.getBuffer(this.starIndexCount);
        GpuBufferSlice gpubufferslice = RenderSystem.getDynamicUniforms()
                .writeTransform(matrix4fstack, new Vector4f(starBrightness, starBrightness, starBrightness, starBrightness), new Vector3f(), new Matrix4f());

        try (RenderPass renderpass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Stars", gputextureview, OptionalInt.empty(), gputextureview1, OptionalDouble.empty())) {
            renderpass.setPipeline(renderpipeline);
            RenderSystem.bindDefaultUniforms(renderpass);
            renderpass.setUniform("DynamicTransforms", gpubufferslice);
            renderpass.setVertexBuffer(0, this.starBuffer);
            renderpass.setIndexBuffer(gpubuffer, this.quadIndices.type());
            renderpass.drawIndexed(0, 0, this.starIndexCount, 1);
        }

        matrix4fstack.popMatrix();

        ci.cancel();
    }
}
