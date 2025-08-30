package com.fuyuaki.wilderness_reborn.util.world;

import com.fuyuaki.wilderness_reborn.util.world.settings.Settings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;

public interface ExtendedChunkGenerator {

    Settings settings();


//    ChunkDataGenerator chunkDataGenerator();

    Aquifer getOrCreateAquifer(ChunkAccess chunk);


    void initRandomState(ChunkMap chunkMap, ServerLevel level);

    default ChunkGenerator self()
    {
        return (ChunkGenerator) this;
    }

}
