package com.fuyuaki.r_wilderness.mixin;


import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.terrain.SurfaceRulesContextExtension;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRules.Context.class)
public abstract class SurfaceRulesContextMixin implements SurfaceRulesContextExtension {

    @Shadow @Final public NoiseChunk noiseChunk;
    @Shadow public int blockX;
    @Shadow public int blockZ;


    @Shadow private long lastMinSurfaceLevelUpdate;
    @Shadow private long lastUpdateXZ;
    @Shadow private int minSurfaceLevel;
    @Shadow private int surfaceDepth;
    @Shadow @Final public WorldGenerationContext context;
    @Shadow @Final public RandomState randomState;
    @Shadow public int blockY;
    @Unique
    @Nullable
    private WRNoiseChunk wildNoiseChunk = null;


    @Override
    public void setNoiseChunk(@Nullable WRNoiseChunk noiseChunk) {
        wildNoiseChunk = noiseChunk;
    }

    @Nullable
    @Override
    public WRNoiseChunk getNoiseChunk() {
        return wildNoiseChunk;
    }



    @Inject(method = "getMinSurfaceLevel", at = @At("HEAD"), cancellable = true)
    private void getMinSurfaceLevel(CallbackInfoReturnable<Integer> cir){
        if (this.noiseChunk == null && this.wildNoiseChunk != null){
            if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
                this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
                int l = Math.max(this.wildNoiseChunk.getSurfaceY(this.blockX,this.blockZ,true),
                        this.randomState.getOrCreateRandomFactory(RWildernessMod.modLocation("surface_depth_bottom")).at(this.blockX,this.blockY,this.blockZ).nextIntBetweenInclusive(4,12)

                        );

                this.minSurfaceLevel = l + this.surfaceDepth - 8;
            }

            cir.setReturnValue(this.minSurfaceLevel);
        }
    }
}
