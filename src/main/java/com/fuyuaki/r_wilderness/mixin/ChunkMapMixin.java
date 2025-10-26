package com.fuyuaki.r_wilderness.mixin;


import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import com.fuyuaki.r_wilderness.world.generation.ChunkMapBridge;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements ChunkMapBridge {
    @Mutable
    @Shadow
    @Final
    private WorldGenContext worldGenContext;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void createRandomStateExtension(ServerLevel level, LevelStorageSource.LevelStorageAccess storageSource, DataFixer fixerUpper, StructureTemplateManager structureManager, Executor dispatcher, BlockableEventLoop mainThreadExecutor, LightChunkGetter lightChunk, ChunkGenerator generator, ChunkStatusUpdateListener chunkStatusListener, Supplier overworldDataStorage, TicketStorage ticketStorage, int serverViewDistance, boolean sync, CallbackInfo ci)
    {
        if (generator instanceof ChunkGeneratorExtension ex)
        {
            ex.initRandomState((ChunkMap) (Object) this, level);
        }
    }

    @Override
    public void updateGenerator(ChunkGenerator generator) {

        worldGenContext = new WorldGenContext(
                worldGenContext.level(),
                generator,
                worldGenContext.structureManager(),
                worldGenContext.lightEngine(),
                worldGenContext.mainThreadExecutor(),
                worldGenContext.unsavedListener()
        );
    }
}
