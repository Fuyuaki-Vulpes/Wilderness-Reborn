package com.fuyuaki.r_wilderness.world.generation.noise;

// Original Code is from TerraFirmaCraft
// https://github.com/TerraFirmaCraft/TerraFirmaCraft/blob/1.21.x/src/main/java/net/dries007/tfc/world/noise/OpenSimplex2D.java

public class OpenSimplex2D implements Noise2D
{
    final FastNoiseLite fnl;
    private double frequency;
    private double midpoint, amplitude;
    private final int offsetX,offsetZ;

    public OpenSimplex2D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public OpenSimplex2D(int seed)
    {
        long xBits = seed * 0xbf58476d1ce4e5b9L;
        long zBits = (seed ^ xBits) * 0x94d049bb133111ebL;

        this.offsetX = Math.floorMod(xBits, 2000000) - 1000000;
        this.offsetZ = Math.floorMod(zBits, 2000000) - 1000000;

        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        fnl.SetFractalOctaves(1);

        frequency = 1;
        midpoint = 0;
        amplitude = 1;
    }

    @Override
    public double noise(double x, double z)
    {
        return midpoint + fnl.GetNoise(x + offsetX, z + offsetZ) * amplitude;
    }

    @Override
    public OpenSimplex2D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public OpenSimplex2D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency((float) frequency);
        return this;
    }

    @Override
    public OpenSimplex2D scaled(double min, double max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public OpenSimplex2D scaled(double oldMin, double oldMax, double min, double max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }

    public OpenSimplex2D fractalType(FastNoiseLite.FractalType fractalType){
        fnl.SetFractalType(fractalType);
        return this;
    }

    public OpenSimplex2D fractalOctaves(int octaves){
        fnl.SetFractalOctaves(octaves);
        return this;
    }


    public OpenSimplex2D fractalLacunarity(float lacunarity){
        fnl.SetFractalLacunarity(lacunarity);
        return this;
    }

    public OpenSimplex2D fractalGain(float gain){
        fnl.SetFractalGain(gain);
        return this;
    }

    public OpenSimplex2D fractalWeightedStrength(float strength){
        fnl.SetFractalWeightedStrength(strength);
        return this;
    }

    public OpenSimplex2D pingPongStrength(float strength){
        fnl.SetFractalPingPongStrength(strength);
        return this;
    }

    double getAmplitude()
    {
        return amplitude;
    }
}