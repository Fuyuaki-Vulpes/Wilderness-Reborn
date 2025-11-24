package com.fuyuaki.r_wilderness.mixin;


import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.util.Mth;
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
            TerrainParameters t = extension.terrainParameters();

            ChunkPos chunkpos = new ChunkPos(TerrainParameters.findSpawnPosition(TerrainParameters.SPAWN_TARGET,t,level));
//            extension.prepare(chunkpos);

            listener.start(LevelLoadListener.Stage.PREPARE_GLOBAL_SPAWN, 0);
            listener.updateFocus(level.dimension(), chunkpos);

            int spawnY = serverChunkCache.getGenerator().getSpawnHeight(level);

            if (spawnY < level.getMinY()) {
                BlockPos blockpos = chunkpos.getWorldPosition();
                spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos.getX() + 8, blockpos.getZ() + 8);
            }

            levelData.setSpawn(LevelData.RespawnData.of(level.dimension(), chunkpos.getWorldPosition().offset(8, spawnY, 8), 0.0F, 0.0F));
            int xOffset = 0;
            int zOffset = 0;
            int xOff = 0;
            int zOff = -1;

            for (int i1 = 0; i1 < Mth.square(11); i1++) {
                if (xOffset >= -5 && xOffset <= 5 && zOffset >= -5 && zOffset <= 5) {
                    ChunkPos newChunk = new ChunkPos(chunkpos.x + xOffset, chunkpos.z + zOffset);
                    extension.prepare(newChunk);

                    BlockPos blockpos1 = PlayerSpawnFinder.getSpawnPosInChunk(level, newChunk);
                    if (blockpos1 != null) {
                        levelData.setSpawn(LevelData.RespawnData.of(level.dimension(), blockpos1, 0.0F, 0.0F));
                        break;
                    }
                }

                if (xOffset == zOffset || xOffset < 0 && xOffset == -zOffset || xOffset > 0 && xOffset == 1 - zOffset) {
                    int offset = xOff;
                    xOff = -zOff;
                    zOff = offset;
                }

                xOffset += xOff;
                zOffset += zOff;
            }

            if (generateBonusChest) {
                level.registryAccess()
                        .lookup(Registries.CONFIGURED_FEATURE)
                        .flatMap(features -> features.get(MiscOverworldFeatures.BONUS_CHEST))
                        .ifPresent(
                                reference -> reference.value().place(level, serverChunkCache.getGenerator(), level.random, levelData.getRespawnData().pos())
                        );
            }

            listener.finish(LevelLoadListener.Stage.PREPARE_GLOBAL_SPAWN);
            ci.cancel();
        }
    }


}
