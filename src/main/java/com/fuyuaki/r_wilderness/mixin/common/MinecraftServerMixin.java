package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.world.level.levelgen.ChunkGeneratorExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {


    @Inject(method = "setInitialSpawn", at = @At(value = "HEAD"), cancellable = true)
    private static void setInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, LevelLoadListener listener, CallbackInfo ci){
        ServerChunkCache serverChunkCache = level.getChunkSource();
        ChunkGenerator generator =  serverChunkCache.getGenerator();
        if (generator instanceof ChunkGeneratorExtension extension){

            ChunkPos chunkpos = ChunkPos.containing(BlockPos.ZERO);

            listener.start(LevelLoadListener.Stage.PREPARE_GLOBAL_SPAWN, 0);
            listener.updateFocus(level.dimension(), chunkpos);

            int spawnY = serverChunkCache.getGenerator().getSpawnHeight(level);

            if (spawnY < level.getMinY()) {
                BlockPos blockpos = chunkpos.getWorldPosition();
                spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos.getX() + 8, blockpos.getZ() + 8);
            }

            levelData.setSpawn(LevelData.RespawnData.of(level.dimension(), chunkpos.getWorldPosition().offset(8, spawnY, 8), 0.0F, 0.0F));

            if (generateBonusChest) {
                level.registryAccess()
                        .lookup(Registries.CONFIGURED_FEATURE)
                        .flatMap(features -> features.get(MiscOverworldFeatures.BONUS_CHEST))
                        .ifPresent(
                                reference -> reference.value().place(level, serverChunkCache.getGenerator(), level.getRandom(), levelData.getRespawnData().pos())
                        );
            }

            listener.finish(LevelLoadListener.Stage.PREPARE_GLOBAL_SPAWN);
            ci.cancel();
        }
    }


}
