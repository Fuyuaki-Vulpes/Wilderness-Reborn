package com.fuyuaki.r_wilderness.world.level.levelgen.util;

import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ChunkAccessModifier {
    void fillBiomesReborn(RebornBiomeSource biomeSource, RandomState state);

    void setWildernessChunk(@Nullable WRNoiseChunk ex);
    @Nullable
    WRNoiseChunk getWildernessChunk();
    WRNoiseChunk getOrCreateWildernessChunk(Function<ChunkAccess, WRNoiseChunk> chunkCreator);
}
