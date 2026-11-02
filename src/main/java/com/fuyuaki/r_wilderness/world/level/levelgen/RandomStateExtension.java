package com.fuyuaki.r_wilderness.world.level.levelgen;


import com.fuyuaki.r_wilderness.world.level.levelgen.terrain.SurfaceSystemExtension;

import javax.annotation.Nullable;

public interface RandomStateExtension
{
    void wildernessReborn$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex);
    @Nullable
    ChunkGeneratorExtension wildernessReborn$getChunkGeneratorExtension();

    SurfaceSystemExtension getSurfaceSystemExtension();
}
