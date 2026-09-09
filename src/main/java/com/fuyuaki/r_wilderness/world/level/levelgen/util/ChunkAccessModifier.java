package com.fuyuaki.r_wilderness.world.level.levelgen.util;

import com.fuyuaki.r_wilderness.world.level.levelgen.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.biome.RebornBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ChunkAccessModifier {

    void setWildernessChunk(@Nullable WRNoiseChunk ex);
    @Nullable
    WRNoiseChunk getWildernessChunk();
    WRNoiseChunk getOrCreateWildernessChunk(Function<ChunkAccess, WRNoiseChunk> chunkCreator);
}
