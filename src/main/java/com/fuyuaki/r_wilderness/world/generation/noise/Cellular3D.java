package com.fuyuaki.r_wilderness.world.generation.noise;

public class Cellular3D implements Noise3D
{
    final FastNoiseLite fnl;
    private float frequency;
    private float midpoint, amplitude;

    private final int offsetX,offsetY,offsetZ;
    public Cellular3D(long seed)
    {
        this((int) (seed ^ (seed >> 32)));
    }

    public Cellular3D(int seed)
    {

        long xBits = seed * 0xbf58476d1ce4e5b9L;
        long yBits = (seed ^ xBits) * 0x94d049bb133111ebL;
        long zBits = (seed ^ yBits) * 0x9e3779b97f4a7c15L;

        this.offsetX = Math.floorMod(xBits, 2000000) - 1000000;
        this.offsetY = Math.floorMod(yBits, 2000000) - 1000000;
        this.offsetZ = Math.floorMod(zBits, 2000000) - 1000000;

        fnl = new FastNoiseLite(seed);
        fnl.SetFrequency(1f);
        fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
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
    public Cellular3D octaves(int octaves)
    {
        fnl.SetFractalOctaves(octaves);
        fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
        return spread(1f / (1 << (octaves - 1))); // Due to legacy reasons, most callers expect this scale factor
    }

    @Override
    public Cellular3D spread(float scaleFactor)
    {
        frequency *= scaleFactor;
        fnl.SetFrequency(frequency);
        return this;
    }

    @Override
    public Cellular3D scaled(float min, float max)
    {
        return scaled(-1, 1, min, max);
    }

    @Override
    public Cellular3D scaled(float oldMin, float oldMax, float min, float max)
    {
        assert oldMin == -1 && oldMax == 1;
        midpoint = (max + min) / 2;
        amplitude = (max - min) / 2;
        return this;
    }


    public Cellular3D fractalType(FastNoiseLite.FractalType fractalType){
        fnl.SetFractalType(fractalType);
        return this;
    }

    public Cellular3D returnType(FastNoiseLite.CellularReturnType type){
        fnl.SetCellularReturnType(type);
        return this;
    }

    public Cellular3D distanceFunction(FastNoiseLite.CellularDistanceFunction function){
        fnl.SetCellularDistanceFunction(function);
        return this;
    }

    public Cellular3D jitter(float jitter) {
        fnl.SetCellularJitter(jitter);
        return this;
    }

    public Cellular3D fractalOctaves(int octaves){
        fnl.SetFractalOctaves(octaves);
        return this;
    }


    public Cellular3D fractalLacunarity(float lacunarity){
        fnl.SetFractalLacunarity(lacunarity);
        return this;
    }

    public Cellular3D fractalGain(float gain){
        fnl.SetFractalGain(gain);
        return this;
    }

    public Cellular3D fractalWeightedStrength(float strength){
        fnl.SetFractalWeightedStrength(strength);
        return this;
    }

    public Cellular3D pingPongStrength(float strength){
        fnl.SetFractalPingPongStrength(strength);
        return this;
    }

    double getAmplitude()
    {
        return amplitude;
    }
}
