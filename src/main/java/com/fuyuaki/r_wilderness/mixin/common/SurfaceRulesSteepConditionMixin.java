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
        int chunkBlockX = this.context.blockX & 15;
        int chunkBlockZ = this.context.blockZ & 15;
        int zNorth = Math.max(chunkBlockZ - 1, 0);
        int zSouth = Math.min(chunkBlockZ + 1, 15);
        ChunkAccess chunk = this.context.chunk;
        int heightNorth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zNorth);
        int heightSouth = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, chunkBlockX, zSouth);
        if (heightSouth >= heightNorth + 4) {
            cir.setReturnValue(true);
        }

        int xWest = Math.max(chunkBlockX - 1, 0);
        int xEast = Math.min(chunkBlockX + 1, 15);
        int heightWest = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xWest, chunkBlockZ);
        int heightEast = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, xEast, chunkBlockZ);
        cir.setReturnValue(heightWest >= heightEast + 4);

    }




}
