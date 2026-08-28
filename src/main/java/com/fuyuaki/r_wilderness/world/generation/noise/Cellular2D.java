package com.fuyuaki.r_wilderness.world.generation.noise;

public class Cellular2D implements Noise2D{
    final FastNoiseLite fnl;
    private double frequency;
    private double midpoint, amplitude;
    private final int offsetX,offsetZ;

    public Cellular2D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public Cellular2D(int seed)
    {
        long xBits = seed * 0xbf58476d1ce4e5b9L;
        long zBits = (seed ^ xBits) * 0x94d049bb133111ebL;

        this.offsetX = Math.floorMod(xBits, 2000000) - 1000000;
        this.offsetZ = Math.floorMod(zBits, 2000000) - 1000000;

        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
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
    public Cellular2D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public Cellular2D spread(double scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency((float) frequency);
        return this;
    }

    @Override
    public Cellular2D scaled(double min, double max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public Cellular2D scaled(double oldMin, double oldMax, double min, double max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }

    public Cellular2D fractalType(FastNoiseLite.FractalType fractalType){
        fnl.SetFractalType(fractalType);
        return this;
    }

    public Cellular2D returnType(FastNoiseLite.CellularReturnType type){
        fnl.SetCellularReturnType(type);
        return this;
    }

    public Cellular2D distanceFunction(FastNoiseLite.CellularDistanceFunction function){
        fnl.SetCellularDistanceFunction(function);
        return this;
    }

    public Cellular2D jitter(float jitter) {
        fnl.SetCellularJitter(jitter);
        return this;
    }

    public Cellular2D fractalOctaves(int octaves){
        fnl.SetFractalOctaves(octaves);
        return this;
    }


    public Cellular2D fractalLacunarity(float lacunarity){
        fnl.SetFractalLacunarity(lacunarity);
        return this;
    }

    public Cellular2D fractalGain(float gain){
        fnl.SetFractalGain(gain);
        return this;
    }

    public Cellular2D fractalWeightedStrength(float strength){
        fnl.SetFractalWeightedStrength(strength);
        return this;
    }

    public Cellular2D pingPongStrength(float strength){
        fnl.SetFractalPingPongStrength(strength);
        return this;
    }

    double getAmplitude()
    {
        return amplitude;
    }

}
