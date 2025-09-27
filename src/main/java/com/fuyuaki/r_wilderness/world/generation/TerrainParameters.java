package com.fuyuaki.r_wilderness.world.generation;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class TerrainParameters {
    private static final float baseContinentSize = 1.5F;
    private final float continentScale;
    private final float terrainScale;
    private final Seed seed;
    private final WildGeneratorSettings settings;
    private  final Shapers shapers;
    private  final Climate climate;

    public TerrainParameters(Seed seed, WildGeneratorSettings settings) {
        this.seed = seed;
        this.settings = settings;
        this.continentScale = 1.0F;
        this.terrainScale = 1.0F;
        this.shapers = makeShapers(seed.fork(), settings);
        this.climate = makeClimate(seed.fork(), settings);

    }
    private Shapers makeShapers(RandomSource randomSource, WildGeneratorSettings settings) {
        return new Shapers(
                getErodedTerrainShaper(randomSource),
                getContinentalness(randomSource),
                getErosion(randomSource),
                getTectonicActivity(randomSource),
                getMountains(randomSource),
                getPlateauMap(randomSource),
                getTerrainOffset(randomSource),
                getTerrainOffsetLarge(randomSource),
                getTerrainType(randomSource),
                getTerrainType(randomSource)
        );

    }

    private NormalNoise getTerrainType(RandomSource randomSource) {
//        return simplex(seed,10F / this.continentalScale);
        return NormalNoise.create(randomSource,-9,1);
    }

    private NormalNoise getTerrainOffsetLarge(RandomSource randomSource) {
//        return simplex(seed, 10F / this.landDetailScale).octaves(3);
        return NormalNoise.create(randomSource,-6,1);
    }

    private NormalNoise getTerrainOffset(RandomSource randomSource) {
//        return simplex(seed,30F / this.landDetailScale).octaves(2);
        return NormalNoise.create(randomSource,-5,1);
    }

    private NormalNoise getPlateauMap(RandomSource randomSource) {
//        return simplex(seed,5.0F / this.continentalScale);
        return NormalNoise.create(randomSource,-10,1);
    }

    private NormalNoise getMountains(RandomSource randomSource) {
//        return simplex(seed, 40.0F / this.continentalScale).octaves(1).clamped(0.0F, 1.0F);
        return NormalNoise.create(randomSource,-9,1,1);
    }
/*
    private static @NotNull NormalNoise simplex(RandomSource randomSource, float spread) {
        return new OpenSimplex2D(seed.next()).spread(spread);
    }
    private static @NotNull NormalNoise simplex(RandomSource randomSource, float spread, int iteration) {
        NormalNoise simplex = simplex(seed,spread);
        for (int i = 0; i < iteration; i++){
            simplex.add(simplex(seed, (float) Math.pow(spread,iteration)).scaled(-1.0D / (iteration * 4.0D), 1.0D / (iteration * 4.0D)));
        }
        return simplex;
    }
*/
    private NormalNoise getTectonicActivity(RandomSource randomSource) {
//        return simplex(seed,1.5F / this.continentalScale);
        return NormalNoise.create(randomSource,-10,1,1);
    }

    private NormalNoise getErosion(RandomSource randomSource) {
//        return simplex(seed,5.0F / this.continentalScale);
        return NormalNoise.create(randomSource,-10,1);
    }

    private NormalNoise getContinentalness(RandomSource randomSource) {
//        return simplex(seed,1F / this.continentalScale).add(simplex(seed,5.0F / this.continentalScale).scaled(-0.25F,0.25F));
        return NormalNoise.create(randomSource,-11,1.5,1);
    }

    private NormalNoise getErodedTerrainShaper(RandomSource randomSource) {
//        return simplex(seed,30F / this.continentalScale).octaves(3).map(operand -> Math.cos(operand * Math.PI));
        return NormalNoise.create(randomSource,-10,1,1);
    }

    private Climate makeClimate(RandomSource randomSource, WildGeneratorSettings settings) {
        return new Climate(
//                simplex(seed,0.2F,2),
//                simplex(seed,0.2F,2),
//                simplex(seed,0.2F,2),
//                simplex(seed,0.4F,2)
                NormalNoise.create(randomSource,-10,1),
                NormalNoise.create(randomSource,-10,1),
                NormalNoise.create(randomSource,-10,1),
                NormalNoise.create(randomSource,-10,1)
        );

    }


    public float yLevelAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        double continentalness = this.shapers.continentalness.getValue(xPos * continentScale * baseContinentSize, 0, zPos * continentScale * baseContinentSize);
        float seaSample = sampleOceans(continentalness);
        float erosionShaper = sampleErosionMap(x,z);

        float terrainSample = sampleTerrain(x,z, continentalness) / 2;
       float terrainSamplePost = (erosionShaper * terrainSample) + terrainSample;
       return seaSample + terrainSamplePost;
    }

    private float sampleTerrain(float x, float z, double continentalness) {
        int amplitude = 8;
        double base = this.shapers.terrainOffset.getValue(x,0,z);
        double large = this.shapers.terrainOffsetLarge.getValue(x,0,z);
        double alpha = this.shapers.erosion.getValue(x,0,z);
        return (float) ((Mth.clamp( continentalness * 5F ,-1.2F,0.8F) + 0.2F) * lerp(alpha,base,large) * amplitude);
    }

    private float sampleErosionMap(float x, float z){
        double alpha = this.shapers.erodedTerrainShaper.getValue(x,0,z);
        return (float) alpha;
    }

    private float sampleMountains(int x, int z){
        int amplitude = 80;
        double alpha = Math.pow(this.shapers.mountains.getValue(x,0,z),3);


        return (float) Mth.lerp(alpha,0,amplitude);
    }

    private float sampleOceans(double continentalness){
        int seaBottom = 0;
        int median = 63;
        int continentalTop = 120;

        return lerp(true,1.2F, continentalness,seaBottom,median,continentalTop);
    }


    private float lerp(float alpha, float a, float b){
        return Mth.lerp(alpha,a,b);
    }

    private float lerp(double alpha, int a, int b){
        return (float) Mth.lerp((alpha + 1.0D) / 2,a,b);
    }

    private float lerp(double alpha, double a, double b){
        return (float) Mth.lerp((alpha + 1.0D) / 2,a,b);
    }

    private float lerp(boolean curved, float curve, double alpha, int bottom, int median, int top){
        if (alpha > 0){
            return (float) Mth.lerp( curved ? Math.pow(alpha,curve) : alpha, median,top);
        }
        return (float) Mth.lerp(curved ? Math.pow(alpha * -1 ,curve) : alpha * -1, median, bottom);
    }


    public Sampled samplerAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        return new Sampled(
                this.shapers.erodedTerrainShaper.getValue(x,0,z),
                this.shapers.continentalness.getValue(xPos * continentScale * baseContinentSize, 0, zPos * continentScale * baseContinentSize),                this.shapers.erosion.getValue(x,0,z),
                this.shapers.tectonicActivity.getValue(x,0,z),
                this.shapers.mountains.getValue(x,0,z),
                this.shapers.plateauMap.getValue(x,0,z),
                this.shapers.terrainOffset.getValue(x,0,z),
                this.shapers.terrainOffsetLarge.getValue(x,0,z),
                this.shapers.terrainTypeA.getValue(x,0,z),
                this.shapers.terrainTypeB.getValue(x,0,z),
                this.climate.temperature.getValue(x,0,z),
                this.climate.humidity.getValue(x,0,z),
                this.climate.vegetationDensity.getValue(x,0,z),
                this.climate.rockyness.getValue(x,0,z)
        );
    }

    private record Shapers(
        NormalNoise erodedTerrainShaper,
        NormalNoise continentalness,
        NormalNoise erosion,
        NormalNoise tectonicActivity,
        NormalNoise mountains,
        NormalNoise plateauMap,
        NormalNoise terrainOffset,
        NormalNoise terrainOffsetLarge,
        NormalNoise terrainTypeA,
        NormalNoise terrainTypeB
        ){}


    public record Sampled(
        double erodedTerrainShaper,
        double continentalness,
        double erosion,
        double tectonicActivity,
        double mountains,
        double plateauMap,
        double terrainOffset,
        double terrainOffsetLarge,
        double terrainTypeA,
        double terrainTypeB,
        double temperature,
        double humidity,
        double vegetationDensity,
        double rockyness
        ){}


    private record Climate(
            NormalNoise temperature,
            NormalNoise humidity,
            NormalNoise vegetationDensity,
            NormalNoise rockyness
    ){}

}
