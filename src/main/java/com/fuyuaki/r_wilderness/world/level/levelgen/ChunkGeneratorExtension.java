package com.fuyuaki.r_wilderness.world.level.levelgen;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface ChunkGeneratorExtension {
    /**
     * Retrieves the {@link ChunkGeneratorExtension} from a structure generator state, if the underlying generator is present and
     * is a TFC compatible chunk generator. This is set in {@link #initRandomState(ChunkMap, ServerLevel)} in the individual generator,
     * by caching it through the {@link net.minecraft.world.level.levelgen.RandomState}.
     *
     * @param state The chunk generator structure state.
     * @return The underlying chunk generator.
     */

    void prepare(ChunkPos pos);

    /**
     * Called from the initialization of {@link ChunkMap}, to initialize seed-based properties on any chunk generator implementing {@link ChunkGeneratorExtension}.
     */
    void initRandomState(ChunkMap chunkMap, ServerLevel level);
    void initBiomeSource(ServerLevel level);

    default ChunkGenerator self() {
        return (ChunkGenerator) this;
    }
}
