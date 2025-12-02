package com.fuyuaki.r_wilderness.mixin.common;


import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRules.Context.SteepMaterialCondition.class)
public abstract class SurfaceRulesSteepConditionMixin extends SurfaceRules.LazyXZCondition {


    protected SurfaceRulesSteepConditionMixin(SurfaceRules.Context p_189622_) {
        super(p_189622_);
    }

    @Inject(method = "compute", at = @At("HEAD"), cancellable = true)
    private void computeSteepness(CallbackInfoReturnable<Boolean> cir){
        int x = this.context.blockX & 15;
        int z = this.context.blockZ & 15;
        int z1 = Math.max(z - 1, 0);
        int z2 = Math.min(z + 1, 15);
        int x1 = Math.max(x - 1, 0);
        int x2 = Math.min(x + 1, 15);

        ChunkAccess chunkaccess = this.context.chunk;

        int center = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        int zOffset1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z1);
        int zOffset2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z2);
        int xOffset1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x1, z);
        int xOffset2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x2, z);
        int xDiff = Math.abs(center - xOffset2) + Math.abs(xOffset1 - center);
        int zDiff = Math.abs(center - zOffset2) + Math.abs(zOffset1 - center);
        cir.setReturnValue(xDiff + zDiff > 5 || xDiff > 4 || zDiff > 4);

    }




}
