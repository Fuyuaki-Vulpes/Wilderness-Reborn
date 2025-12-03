package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;

import javax.annotation.Nullable;

public interface SurfaceRulesContextExtension {

    void setNoiseChunk(@Nullable WRNoiseChunk noiseChunk);
    @Nullable
    WRNoiseChunk getNoiseChunk();

}
