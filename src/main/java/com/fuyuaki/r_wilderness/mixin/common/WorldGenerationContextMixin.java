package com.fuyuaki.r_wilderness.mixin.common;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldGenerationContext.class)
public class WorldGenerationContextMixin {

    @Redirect(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getMinY()I"))
    private int initMixinMinY(ChunkGenerator instance){
        if (instance == null) return 0;
        return  instance.getMinY();
    }

    @Redirect(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getGenDepth()I"))
    private int initMixinDepth(ChunkGenerator instance){
        if (instance == null) return 0;
        return  instance.getGenDepth();
    }
}
