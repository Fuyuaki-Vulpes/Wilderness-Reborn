package com.fuyuaki.r_wilderness.world.level.levelgen.terrain;

import com.fuyuaki.r_wilderness.world.level.levelgen.chunk.WRNoiseChunk;

import javax.annotation.Nullable;

public interface SurfaceRulesContextExtension {

    void setNoiseChunk(@Nullable WRNoiseChunk noiseChunk);
    @Nullable
    WRNoiseChunk getNoiseChunk();

}
