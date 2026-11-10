package com.fuyuaki.r_wilderness.mixin.common;

import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGeneratorStructureState.class)
public abstract class ChunkGeneratorStructureStateMixin {

    @Inject(method = "ensureStructuresGenerated",at = @At(value = "HEAD"), cancellable = true)
    private void init(CallbackInfo ci){
        ci.cancel();
    }
}
