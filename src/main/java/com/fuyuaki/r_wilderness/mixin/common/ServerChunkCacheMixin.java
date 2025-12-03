package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ServerChunkCache.class)
public abstract class ServerChunkCacheMixin {


    @Shadow public abstract ChunkGenerator getGenerator();

    @Shadow @Final public ChunkMap chunkMap;

    @Inject(method = "getChunk", at = @At(value = "HEAD"))
    private void scheduleGenerationTask(int x, int z, ChunkStatus chunkStatus, boolean requireChunk, CallbackInfoReturnable<CompletableFuture<ChunkResult<ChunkAccess>>> cir) {
        if (this.getGenerator() instanceof ChunkGeneratorExtension ex) {
            ex.prepare(new ChunkPos(x,z));
        }

    }
}
