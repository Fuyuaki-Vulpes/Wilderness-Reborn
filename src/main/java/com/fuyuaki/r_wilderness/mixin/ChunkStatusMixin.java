package com.fuyuaki.r_wilderness.mixin;

import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

/*
    @Inject(method = "register",at = @At("HEAD"))
    private static void registerMixin(String name, ChunkStatus parent, EnumSet<Heightmap.Types> heightmapsAfter, ChunkType chunkType, CallbackInfoReturnable<ChunkStatus> cir){

    }

    */
}
