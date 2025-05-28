package com.fuyuaki.wilderness_reborn.mixin;


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
        int thisX = this.context.blockX & 15;
        int thisZ = this.context.blockZ & 15;
        ChunkAccess chunkaccess = this.context.chunk;
        int thisHeight = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, thisX, thisZ);
        int xWest = thisHeight - chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, thisX - 1, thisZ);
        int xEast = thisHeight - chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, thisX + 1, thisZ);
        int zSouth = thisHeight - chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, thisX, thisZ - 1);
        int zNorth = thisHeight - chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, thisX, thisZ + 1);
        int north = Math.abs(zNorth);
        int south = Math.abs(zSouth);
        int east = Math.abs(xEast);
        int west = Math.abs(xWest);


        if (west > 5 && east > 5 && ((south + north) / 2) >= 2 ){
            cir.setReturnValue(true);
        }
        else cir.setReturnValue(south > 5 && north > 5 && ((east + west) / 2) >= 2);
    }



}
