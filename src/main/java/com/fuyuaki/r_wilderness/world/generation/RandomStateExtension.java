package com.fuyuaki.r_wilderness.world.generation;


import com.fuyuaki.r_wilderness.world.generation.terrain.SurfaceSystemExtension;

import javax.annotation.Nullable;

public interface RandomStateExtension
{
    void wildernessReborn$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex);
    @Nullable
    ChunkGeneratorExtension wildernessReborn$getChunkGeneratorExtension();

    SurfaceSystemExtension getSurfaceSystemExtension();
}
