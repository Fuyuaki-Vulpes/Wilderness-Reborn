package com.fuyuaki.r_wilderness.world.generation.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

public interface ChunkDataGenerator {

    /**
     * Generate the provided chunk data up to {@link ChunkData.Status#PARTIAL}. Mutates {@code data} and returns it. If the chunk data
     * @throws IllegalStateException if called with invalid or client-side chunk data.
     */
    ChunkData generate(ChunkData data);

    /**
     * Access the chunk data for the provided chunk, which may be an impostor chunk. Generate the data - if valid - up to
     * {@link ChunkData.Status#PARTIAL}, and return it.
     * @throws IllegalStateException if called with an empty or client-side chunk.
     */
    default ChunkData generate(ChunkAccess chunk)
    {
        final ChunkData data = ChunkData.get(chunk);
        generate(data);
        return data;
    }

    /**
     * Generates a clone of the chunk data at the given position up to {@link ChunkData.Status#PARTIAL}. This should only be used if the
     * underlying chunk cannot be accessed, and should be cached if possible, as this will <strong>always</strong> trigger generation, even
     * if the chunk data for that chunk has already been generated or is available elsewhere.
     */
    default ChunkData createAndGenerate(ChunkPos pos)
    {
        return generate(new ChunkData(pos));
    }



    default void displayDebugInfo(List<String> tooltip, BlockPos pos, int surfaceY) {}

}
