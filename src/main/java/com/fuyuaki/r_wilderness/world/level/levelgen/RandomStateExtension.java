package com.fuyuaki.r_wilderness.world.level.levelgen;


import javax.annotation.Nullable;

public interface RandomStateExtension
{
    void setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex);
    @Nullable
    ChunkGeneratorExtension getChunkGeneratorExtension();

    MaterialSystemExtension getMaterialSystemExtension();
}
