package com.fuyuaki.r_wilderness.world.generation;

public interface Sampler<T>
{
    T get(int x, int z);
}