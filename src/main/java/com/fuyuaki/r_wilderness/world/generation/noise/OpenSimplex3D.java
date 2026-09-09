package com.fuyuaki.r_wilderness.world.generation.noise;

public class OpenSimplex3D implements Noise3D
{
    final FastNoiseLite fnl;
    private float frequency;
    private float midpoint, amplitude;

    private final int offsetX,offsetY,offsetZ;
    public OpenSimplex3D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public OpenSimplex3D(int seed)
    {

        long xBits = seed * 0xbf58476d1ce4e5b9L;
        long yBits = (seed ^ xBits) * 0x94d049bb133111ebL;
        long zBits = (seed ^ yBits) * 0x9e3779b97f4a7c15L;

        this.offsetX = Math.floorMod(xBits, 2000000) - 1000000;
        this.offsetY = Math.floorMod(yBits, 2000000) - 1000000;
        this.offsetZ = Math.floorMod(zBits, 2000000) - 1000000;

        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        fnl.SetFractalOctaves(1);
        fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);

        frequency = 1;
        midpoint = 0;
        amplitude = 1;
    }

    @Override
    public double noise(double x, double y, double z)
    {
        return midpoint + fnl.GetNoise(x + offsetX, y + offsetY,z + offsetZ) * amplitude;
    }

    @Override
    public OpenSimplex3D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public OpenSimplex3D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    @Override
    public OpenSimplex3D scaled(float min, float max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public OpenSimplex3D scaled(float oldMin, float oldMax, float min, float max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }


    public OpenSimplex3D fractalType(FastNoiseLite.FractalType fractalType){
        fnl.SetFractalType(fractalType);
        return this;
    }

    public OpenSimplex3D fractalOctaves(int octaves){
        fnl.SetFractalOctaves(octaves);
        return this;
    }


    public OpenSimplex3D fractalLacunarity(float lacunarity){
        fnl.SetFractalLacunarity(lacunarity);
        return this;
    }

    public OpenSimplex3D fractalGain(float gain){
        fnl.SetFractalGain(gain);
        return this;
    }

    public OpenSimplex3D fractalWeightedStrength(float strength){
        fnl.SetFractalWeightedStrength(strength);
        return this;
    }

    public OpenSimplex3D pingPongStrength(float strength){
        fnl.SetFractalPingPongStrength(strength);
        return this;
    }


    double getAmplitude()
    {
        return amplitude;
    }
}
