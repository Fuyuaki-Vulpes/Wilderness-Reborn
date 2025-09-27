package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;

public class ChunkHeightFiller {

    protected final int seaLevel;
    protected final Noise2D tideHeightNoise;
    public ChunkHeightFiller(int seaLevel, Noise2D tideHeightNoise)
    {
        this.seaLevel = seaLevel;
        this.tideHeightNoise = tideHeightNoise;
    }

    protected int blockX, blockZ; // Absolute x/z positions
    protected int localX, localZ; // Chunk-local x/z
    protected void setupColumn(int x, int z)
    {
        this.blockX = x;
        this.blockZ = z;
        this.localX = x & 15;
        this.localZ = z & 15;
    }
}
