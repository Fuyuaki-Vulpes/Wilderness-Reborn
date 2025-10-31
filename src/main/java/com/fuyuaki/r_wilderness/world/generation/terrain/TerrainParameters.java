package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.Seed;
import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.DistortionSpline;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.List;

public class TerrainParameters {
    public static final Range NEUTRAL = range(-0.5,0.5);
    public static final Range FULL = range(-1.0,1.0);

    public static final Target SPAWN_TARGET =
            new Target(
                    NEUTRAL, //Temperature
                    NEUTRAL, //Humidity
                    NEUTRAL, //Vegetation Density
                    NEUTRAL, //Rockiness
                    range(0.1,0.75), //Continentalness
                    range(-9,0.15), //Tectonic Activity
                    range(0,0,1), //Plateau
                    NEUTRAL, //Erosion
                    range(64,160) // Terrain Height
            );


    private static final float baseContinentSize = 1.25F;
    private static final float baseTectonicSize = 1.25F;
    private static final float plateauScale = 1 / 0.75F;
    private static final float terrainTypeScale = 1 / 0.5F;
    private static final float mountainCoordsSize = 1.0F;
    private final float continentScale;
    private final float terrainScale;
    private final Seed seed;
    private final WildGeneratorSettings settings;
    public final Shapers shapers;
    public final Climate climate;

    public TerrainParameters(Seed seed, WildGeneratorSettings settings) {
        this.seed = seed;
        this.settings = settings;
        this.continentScale = 1.0F;
        this.terrainScale = 1.0F;
        this.shapers = makeShapers(seed.fork().forkPositional(), settings);
        this.climate = makeClimate(seed.fork().forkPositional(), settings);

    }

    private Shapers makeShapers(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Shapers(
                getContinentalness(randomSource, "continentalness"),
                getErosion(randomSource, "erosion"),
                getTectonicActivity(randomSource, "tectonic_activity"),
                new Shapers.Mountain(
                        getMountainsCore(randomSource, "mountain_core"),
                        getMountains(randomSource, "mountain_shape"),
                        getMountainDetails(randomSource, "mountain_detail"),
                        getMountainNoise(randomSource, "mountain_noise")
                ),
                getPlateauMap(randomSource, "plateaus"),
                getHills(randomSource, "hills"),
                getTerrainOffset(randomSource, "terrain_offset"),
                getTerrainOffsetLarge(randomSource, "terrain_offset_large"),
                getTerrainNoise(randomSource, "terrain_offset_noise"),
                getTerrainType(randomSource, "terrain_type_a"),
                getTerrainType(randomSource, "terrain_type_b")
        );

    }



    private PerlinNoise getTerrainType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,10);
    }

    private PerlinNoise getTerrainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-4,1);
    }

    private PerlinNoise getTerrainOffsetLarge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1,1);
    }

    private PerlinNoise getTerrainOffset(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1,1);
    }

    private PerlinNoise getPlateauMap(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,4,5);
    }

    private PerlinNoise getHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.5,1);
    }

    private PerlinNoise getMountainsCore(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1);
    }
    private PerlinNoise getMountains(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1);
    }
    private PerlinNoise getMountainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,1,1);
    }
    private PerlinNoise getMountainDetails(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,1);
    }

    private PerlinNoise getTectonicActivity(PositionalRandomFactory randomSource, String name) {
        return  PerlinNoise.create(noiseRandom(name, randomSource), -13,18,12,12);
    }

    private PerlinNoise getErosion(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,1);
    }

    private PerlinNoise getContinentalness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-12,1.5,1,1,1,1,1);
    }


    private Climate makeClimate(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Climate(
                PerlinNoise.create(noiseRandom("temperature", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("humidity", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("vegetation_density", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("aquifers", randomSource),-9,1.5,1),
                PerlinNoise.create(noiseRandom("rockiness", randomSource),-10,1)
        );
    }

    private RandomSource noiseRandom(String name, PositionalRandomFactory randomSource) {
        return randomSource.fromHashOf((RWildernessMod.modLocation("terrain_parameters/noises/" + name)));
    }


    public double yLevelAt(int xPos, int zPos, boolean surfaceOnly){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        TerrainType terrainType = new TerrainType(
                terrainType(this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale)),
                terrainType(this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale))
        );
        double continentalness = this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zContinental * baseContinentSize) + 0.2;
        double seaSample = sampleOceans(continentalness);

        double erosionShaper = sampleErosionMap(x,z);

        double terrainSample = sampleTerrain(x,z, continentalness,erosionShaper);

        double plateau = (Math.sin(((
                Math.pow(Math.clamp(this.shapers.plateauMap.getValue(xPos / baseTectonicSize / plateauScale, 0, zPos / baseTectonicSize/ plateauScale),0,1),3)
                        *
                        Math.clamp(continentalness * 7, -0.2,1)) - 0.5) * Mth.PI) + 1) / 2;

       double tectonicSample = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize,  0,zPos / baseTectonicSize);
       double mountainLayer = sampleMountains( x, z, plateau, tectonicSample, continentalness, terrainType, erosionShaper, surfaceOnly);

       // Adds the different terrain Layers together.
       return seaSample + terrainSample + mountainLayer;
    }

    private double terrainType(double value) {
        return (Math.sin(Math.clamp(value,-1,1) * Mth.HALF_PI) + 1) / 2;
    }


    /* Samples Terrain Offset and Terrain Offset large, mixes the two so that the higher erosion terrain (smoother and flatter)
    * has larger and less detailed hills and shapes (Terrain Offset Large).
    * Then, makes it so the Continentalness (how close we are to the shore) makes it inverse near the beaches,
    * that serves two purposes:
    * 1- The beaches will become smoother and have a more realistic and logical shape.
    * 2- It avoids weird islands in seashores
    *
    * Amplitude = The size of the mini hills
     */
    private double sampleTerrain(float x, float z, double continentalness, double erosion) {
        int amplitude = 32;
        int eAmp = 2;
        double base = Math.abs(this.shapers.terrainOffset.getValue(x,0,z));
        double large = Math.abs(this.shapers.terrainOffsetLarge.getValue(x,0,z));
        double noise = Math.abs(this.shapers.terrainNoise.getValue(x,0,z)) * 0.04;
        double e = Math.clamp(1 - ((erosion + 1) / 2),0,1);
        double terrainAmp = Math.max(base, large);
        double terrain = (terrainAmp * e * amplitude) + (terrainAmp * eAmp * (1 - e)) + (terrainAmp * noise);
        // Imagine extremity as 1/x, the higher the offset is, the more the terrain will be affected by terrain offset
        // the value is like this so that beaches and shores remain mostly flat.
        // The value here leads to a 0.2 continentalness becoming the point where the offset becomes normal.
        // offset makes it so that the terrain "point zero" is offset towards the sea, so shores look more natural, otherwise
        // you'll see completely flat beaches and shores and the terrain won't have any variation there.
        float extremity = 1 / 0.1F;
        return Mth.clamp( continentalness * extremity,-1,1) * terrain;
    }

    private double sampleErosionMap(float x, float z){
        return this.shapers.erosion.getValue(x,0,z);
    }


    private double continentalnessFade(double continentalness, double range, double offset, boolean negativePost){
        double c = continentalness + offset;
        double r = 1 / range;
        return negativePost ? (Mth.clamp(c * r,-1,1) + 1)/2: Math.abs(Mth.clamp(c * r,-1,1));
    }


    /* Samples the Tectonic Activity noise, and folds it so it becomes ridged, and inverses it's value so the closer
    * it is to 0, the stronger it is.
    * Then, if it is greater than 0 (so we avoid unnecessary sampling), it multiplies the mountain size by that.
    * Perhaps I might make it so it is a curve instead of a linear value.
    * I'll also add erosion based
    */
    private double sampleMountains(float xPos, float zPos, double plateau,double tectonicActivity, double continentalness, TerrainType terrainType, double erosion, boolean surfaceOnly){

        double tA = 1 - Math.abs(tectonicActivity);
        double hillPlateausStart = 3.0;
        if (tA <= -hillPlateausStart) return 0;


        double continentalFilter = Math.clamp((continentalness * (1/0.05F)),0,1);

        float x = xPos * mountainCoordsSize;
        float z = zPos * mountainCoordsSize;
        double hills = Math.pow(Math.abs(this.shapers.hills.getValue(x,0, z)),1.25);
        double hillSize = 42 * (1-Math.clamp((erosion+1)/2,0,1));
        double hillShape = (Math.min((tA + hillPlateausStart), 1)) * hills * hillSize * Math.clamp(( 1 + erosion) / 2,0,1) * continentalFilter;

        double plateauFilter = (1 - plateau) * 0.5 + 0.5;
        hillShape *= plateauFilter;
        double plateauHeight = terrainType.mapValues(80,40,60,70);
        double plateauShape = plateau * plateauHeight * Math.min((tA + hillPlateausStart) * 1.5, 1);

        if (tA <= 0) return NaNCheck(hillShape + plateauShape);

        double tectonicFilter = Math.clamp(tA * 4, 0,1);
        double tectonicFilter1 = Math.clamp(tA * 1.5, 0,1);
        float powerCurve = 1.75F;

        double mountainsCore = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x, 0, z) + 1) / 2),0,1), 1.25);
        double mountains = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x, 0, z) + 1) / 2),0,1), powerCurve);
        double mountainNoise = Math.pow(Math.abs(this.shapers.mountains.noise.getValue(x, 0, z)), powerCurve);

        double detail = Math.abs(
                (1 - Math.abs(this.shapers.mountains.detail.getValue(x, 0, z)))
                - 0.5 ) * 2;

        double mountainDetail = detail * ((mountainsCore + mountains) / 2);

        double offset = 12;

        double mc1 = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x+offset, 0, z) + 1) / 2),0,1), 1.25);
        double m1 = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x+offset, 0, z) + 1) / 2),0,1), powerCurve);

        double mc2 = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x-offset, 0, z) + 1) / 2),0,1), 1.25);
        double m2 = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x-offset, 0, z) + 1) / 2),0,1), powerCurve);

        double mc3 = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x, 0, z+offset) + 1) / 2),0,1), 1.25);
        double m3 = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x, 0, z+offset) + 1) / 2),0,1), powerCurve);

        double mc4 = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x, 0, z-offset) + 1) / 2),0,1), 1.25);
        double m4 = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x, 0, z-offset) + 1) / 2),0,1), powerCurve);

        double[] mc = new double[4];
        mc[0] = mc1 + m1;
        mc[1] = mc2 + m2;
        mc[2] = mc3 + m3;
        mc[3] = mc4 + m4;



        double ridges = 0.9D + (0.1D * detail);

        double alphaCore = mountainsCore * tectonicFilter * ridges;
        double alpha = (mountains) * tectonicFilter1 * ridges;
        double alphaEroded = mountains * tA;
        double alphaDetail = mountainDetail * tectonicFilter;
        double alphaNoise = mountainNoise * tectonicFilter;

        double filter = Math.clamp(Math.pow(((continentalness + 0.3) * (1/0.1)), 1.5), 0,2);
        double rift = Math.abs(filter-1);


        double erodedMountainMap = NaNCheck(mountainEroded(alphaEroded, alphaCore, continentalness));
        double mountainMap = NaNCheck(mountain(alpha, alphaDetail,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double mountainCliffMap = NaNCheck(mountainSeaCliff(alpha, alphaDetail,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double divergentMap = divergent((mountains), mountainsCore, tA,surfaceOnly,rift);
        if (plateau > 0){
            double dA = Math.max(divergentMap, 0);
            double dB = Math.min(divergentMap, 0);
            divergentMap = (dA * plateauFilter) + dB;
        }
        divergentMap = NaNCheck(divergentMap);
        double transform = NaNCheck(transform(mountainsCore, 1 - detail, mountains, continentalness,tA,surfaceOnly,rift) * plateauFilter);
        double terrainMap = terrainType.mapValuesWithInBetween(
                divergentMap,
                transform,

                erosionMapped(erosion,mountainMap,erodedMountainMap,0.2),
                erosionMapped(erosion,mountainCliffMap,erodedMountainMap,0.2),

                0

        ) * tectonicFilter;

        return terrainMap + NaNCheck(hillShape) + NaNCheck(plateauShape);


    }

    private double erosionMapped(double erosion, double base, double eroded,double range) {
        if (erosion > range){
            return eroded;
        } else if (erosion < -range) {
            return base;
        }
        double e = (Math.clamp(erosion * (1/range),-1,1) + 1)/2;
        return Mth.lerp(e,base,eroded);
    }

    private static double NaNCheck(double v) {
        return Double.isNaN(v) ? 0 : v;
    }

    private double transform(double mountains, double detail, double modifier, double continentalness, double tA, boolean surfaceOnly, double riftFilter) {
        double faultPoint = 0.990;
        double tMultiplyFault = 1 / (1 - faultPoint);
        double fault = Math.pow(
                Math.max(
                        (tA - faultPoint) * tMultiplyFault,
                        0),
                3);
        double t = Math.clamp((1.5 * tA) - (modifier * 0.5), 0, 1);
        double terrain = (detail * 4) + (mountains * 4);
        double faultTerrain = terrain * fault;
        double terrainFaded = terrain * continentalnessFade(continentalness,0.3F,0,true);
        double transformTerrain = terrainFaded + (continentalness * 12);
        if (surfaceOnly) {
            return transformTerrain * t;
        } else {
            return t * transformTerrain - faultTerrain;
        }
    }

    private double divergent(double mountains, double core, double tA, boolean surfaceOnly, double riftFilter) {
        double amp = (mountains * 12) + (core * 12);
        if (surfaceOnly){

            DistortionSpline spline = new DistortionSpline(
                    new DistortionSpline.Spline()
                            .addPoint(0,0)
                            .addPoint(0.75F,  amp)
                            .addPoint(0.95F, (amp / 3) -(24 * riftFilter))
                            .addPoint(0.985F,-24 * riftFilter)
                            .addPoint(1.0F,-24 * riftFilter)
                            .addPoint(1.25F,-24 * riftFilter),
                    0,-24 * riftFilter
            );
            return spline.at(tA);
        }
        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(0,0)
                        .addPoint(0.75F, amp)
                        .addPoint(0.95F, (amp / 3) - (24 * riftFilter))
                        .addPoint(0.985F,-128 * riftFilter)
                        .addPoint(1.0F,-192 * riftFilter)
                        .addPoint(1.25F,-192 * riftFilter),
                0,-125 * riftFilter
        );
        return spline.at(tA);
    }

    private double mountainEroded(double alpha, double alphaCore, double continentalness) {
        double c = Math.clamp(Math.abs(continentalness) * 2, 0.0, 1.0);

        double seaDelta = continentalnessFade(continentalness,0.2,0.2,false);

        double amplitudeCore = 150 * c;
        amplitudeCore = seaDelta * amplitudeCore;

        double amplitude = 150 * c;
        amplitude = seaDelta * amplitude;

        return (alpha * amplitude) + (alphaCore * amplitudeCore);
    }
    private double mountain(double alpha, double alphaDetail, double alphaCore, double alphaNoise, double continentalness, double m, double[] mc) {
        double c = Math.clamp(Math.abs(continentalness) * 4, 0.0, 1.0);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 160 * c;
        double amplitude = 140 * c;
        double detailAmp = 20 * c;
        double noiseAmp = 32 * c;
        //TOTAL = 352
        double maxCurve = 1 / 0.4F;
        double xM = Math.max(Math.abs(m - mc[0]), Math.abs(m - mc[1])) * maxCurve;
        double zM = Math.max(Math.abs(m - mc[2]), Math.abs(m - mc[3])) * maxCurve;
        double mA = Math.min(Math.max(xM,zM), 1);



        double seaDelta = continentalnessFade(continentalness,0.2,0.1,false);
        double seaDeltaB = continentalnessFade(continentalness,0.2,0.1,true);

        amplitudeCore = seaDelta * amplitudeCore;
        amplitude = seaDelta * amplitude;
        detailAmp = seaDelta * detailAmp;
        noiseAmp = seaDelta * noiseAmp;



        return (alpha * amplitude) + (alphaDetail * detailAmp) + (alphaCore * amplitudeCore) + (noiseAmp * mA * alphaNoise) - Mth.lerp(seaDeltaB,120,0);
    }
    private double mountainSeaCliff(double alpha, double alphaDetail, double alphaCore, double alphaNoise, double continentalness, double m, double[] mc) {
        double c = Math.clamp((continentalness * 30) + Math.min(alphaDetail,0.2), -0.35, 1.0);
        if (c < 0){
            c = Math.sin((c * (1/0.35) * Mth.HALF_PI)) * 0.35;
        }else{
            c = Math.sin(c * Mth.HALF_PI);
        }
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 150 * c;
        double amplitude = 150 * c;
        double detailAmp = 20 * c;
        double noiseAmp = 32 * c;
        //TOTAL = 352
        double maxCurve = 1 / 0.4F;
        double xM = Math.max(Math.abs(m - mc[0]), Math.abs(m - mc[1])) * maxCurve;
        double zM = Math.max(Math.abs(m - mc[2]), Math.abs(m - mc[3])) * maxCurve;
        double mA = Math.min(Math.max(xM,zM), 1);



        return (alpha * amplitude) + (alphaDetail * detailAmp) + (alphaCore * amplitudeCore) + (noiseAmp * mA * alphaNoise);
    }

    private double sampleOceans(double continentalness){
        int seaBottom = 12;
        int median = 64;
        int continentalTop = 120;

        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1.5F,median + 16)
                        .addPoint(-1.15F,median)
                        .addPoint(-1.0F,seaBottom)
                        .addPoint(-0.175F,32)
                        .addPoint(-0.075F,48)
                        .addPoint(-0.05F,58)
                        .addPoint(-0.01F,median-1)
                        .addPoint(0.0F,median)
                        .addPoint(0.4F,68)
                        .addPoint(0.65F,90)
                        .addPoint(1.2F,continentalTop),
                median,continentalTop
        );

        return spline.at((float) continentalness);
    }




    public Sampled samplerAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        double terrainA = this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);
        double terrainB = this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);
        return new Sampled(
                this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zPos * continentScale * baseContinentSize)  + 0.2,
                this.shapers.erosion.getValue(x,0,z),
                this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize, 0,zPos / baseTectonicSize),
                foldZeroToOne(this.shapers.mountains.core.getValue(x * mountainCoordsSize, 0, z *  mountainCoordsSize)),
                foldZeroToOne(this.shapers.mountains.shape.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize)),
                Math.abs(
                        (1 - Math.abs(this.shapers.mountains.detail.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize)))
                                - 0.5 ) * 2,
                Math.abs(this.shapers.mountains.noise.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize)),
                this.shapers.plateauMap.getValue(xPos / baseTectonicSize / plateauScale,0,zPos / baseTectonicSize / plateauScale),
                Math.abs(this.shapers.hills.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize)),
                Math.max(Math.abs(this.shapers.terrainOffset.getValue(x,0,z))
                        , Math.abs(this.shapers.terrainOffsetLarge.getValue(x,0,z)))
                        + (Math.abs(this.shapers.terrainNoise.getValue(x,0,z)) * 0.125),
                terrainA, terrainB,
                new TerrainType(terrainType(terrainA),terrainType(terrainB)),
                this.climate.temperature.getValue(x,0,z),
                this.climate.humidity.getValue(x,0,z),
                this.climate.vegetationDensity.getValue(x,0,z),
                this.climate.rockyness.getValue(x,0,z)
        );
    }

    private double foldZeroToOne(double value) {
        return Math.clamp((value+1)/2,0,1);
    }
    public static Range range(double min, double max){
        double r = max - min;
        return new Range(min, min + r/2,max);
    }
    public static Range range(double min, double mean, double max){
        return new Range(min,mean,max);
    }

    public record Shapers(
        PerlinNoise continentalness,
        PerlinNoise erosion,
        PerlinNoise tectonicActivity,
        Mountain mountains,
        PerlinNoise plateauMap,
        PerlinNoise hills,
        PerlinNoise terrainOffset,
        PerlinNoise terrainOffsetLarge,
        PerlinNoise terrainNoise,
        PerlinNoise terrainTypeA,
        PerlinNoise terrainTypeB
        ){

        public record Mountain(
                PerlinNoise core,
                PerlinNoise shape,
                PerlinNoise detail,
                PerlinNoise noise
        ){}
    }


    public record Sampled(
        double continentalness,
        double erosion,
        double tectonicActivity,
        double mountainsCore,
        double mountains,
        double mountainDetails,
        double mountainNoise,
        double plateauMap,
        double hills,
        double terrainOffset,
        double terrainTypeA,
        double terrainTypeB,
        TerrainType terrainType,
        double temperature,
        double humidity,
        double vegetationDensity,
        double rockyness
        ){}


    public record TerrainType(double a, double b){
        double mapValues(double aa, double ab, double ba, double bb){
            return Mth.lerp2(a,b,aa,ab,ba,bb);
        }
        double mapValuesWithInBetween(double aa, double ab, double ba, double bb, double middle){
            double deltaA1 = Mth.clamp(2 * a,0,1);
            double deltaA2 = Mth.clamp(2 * a,1,2) - 1;
            double deltaB1 = Mth.clamp(2 * b,0,1);
            double deltaB2 = Mth.clamp(2 * b,1,2) - 1;

            return
                    Mth.lerp(
                            b,
                            Mth.lerp(deltaB1,
                                    Mth.lerp(
                                            a,
                                            Mth.lerp(deltaA1,aa,middle),
                                            Mth.lerp(deltaA2,middle,ab)
                                    ),
                                    middle),
                            Mth.lerp(deltaB2,
                                    middle,
                                    Mth.lerp(
                                            a,
                                            Mth.lerp(deltaA1,ba,middle),
                                            Mth.lerp(deltaA2,middle,bb)
                                    )
                            )
                    );
        }
    }
    public record Climate(
            PerlinNoise temperature,
            PerlinNoise humidity,
            PerlinNoise vegetationDensity,
            PerlinNoise aquifers,
            PerlinNoise rockyness
    ){}


    public record Range(double min, double mean, double max){



        public boolean accepts(double v){
            return v < min || v > max;
        }

        public double affinity(double v, boolean extremeNegative){


            double p;
            double a;

            if (v > mean){
                p = max - mean;
                a = v - mean;

            }else{
                p = mean - min;
                a = v - min;

            }
            if (p == 0){
                if (!accepts(v) && extremeNegative) return a -1000;
                return a;
            }

            if (!accepts(v) && extremeNegative) return (a/p) -1000;

            return a / p;

        }
    }

    public record Target(
            Range temperature,
            Range humidity,
            Range vegetationDensity,
            Range rockiness,
            Range continentalness,
            Range tectonicActivity,
            Range plateau,
            Range erosion,
            Range terrainHeight
    ){
        boolean test(TerrainParameters.Sampled parameters, double yLevel){
            boolean t = temperature.accepts(parameters.temperature);
            boolean h = humidity.accepts(parameters.humidity);
            boolean v = vegetationDensity.accepts(parameters.vegetationDensity);
            boolean r = rockiness.accepts(parameters.rockyness);
            boolean c = continentalness.accepts(parameters.continentalness);
            boolean tA = tectonicActivity.accepts(parameters.tectonicActivity);
            boolean p = plateau.accepts(parameters.plateauMap);
            boolean e = erosion.accepts(parameters.erosion);
            boolean height = terrainHeight.accepts(yLevel);
            return t && h && v && r && c && tA && p && e && height;
        }

        double affinity(TerrainParameters.Sampled parameters, double yLevel, boolean extremeNegative){
            double t = temperature.affinity(parameters.temperature,extremeNegative);
            double h = humidity.affinity(parameters.humidity,extremeNegative);
            double v = vegetationDensity.affinity(parameters.vegetationDensity,extremeNegative);
            double r = rockiness.affinity(parameters.rockyness,extremeNegative);
            double c = continentalness.affinity(parameters.continentalness,extremeNegative);
            double tA = tectonicActivity.affinity(parameters.tectonicActivity,extremeNegative);
            double p = plateau.affinity(parameters.plateauMap,extremeNegative);
            double e = erosion.affinity(parameters.erosion,extremeNegative);
            double height = terrainHeight.affinity(yLevel,extremeNegative);
//            System.out.println("T: " + t);
//            System.out.println("H: " + h);
//            System.out.println("V: " + v);
//            System.out.println("R: " + r);
//            System.out.println("C: " + c);
//            System.out.println("TA: " + tA);
//            System.out.println("P: " + p);
//            System.out.println("E: " + e);
//            System.out.println("H: " + h);
            double affinity = (t + h + v + r + c + tA + p + e + height) / 9;
            if (!test(parameters,yLevel) && extremeNegative) return affinity -1000;

            return affinity;
        }

        double affinity(TerrainParameters.Sampled parameters, BlockPos pos, boolean extremeNegative){
            return affinity(parameters,pos.getCenter().y,extremeNegative);
        }

    }

    public static BlockPos findSpawnPosition(Target points, TerrainParameters parameters) {
        SpawnFinder spawnFinder = new SpawnFinder(points, parameters,BlockPos.ZERO);
        return spawnFinder.result.location();
    }


    static class SpawnFinder {
        Result result;

        SpawnFinder(Target points, TerrainParameters parameters, BlockPos pos) {
            this.result = Result.of(pos,points.affinity(parameters.samplerAt(pos.getX(),pos.getZ()),pos.atY(Mth.floor(parameters.yLevelAt(pos.getX(),pos.getZ(),false)) + 1),false));
//            System.out.println("Affinity 0,0: " + result.affinity);
            this.radialSearch(points, parameters, 4096.0F, 512.0F);
            this.radialSearch(points, parameters, 512.0F, 32.0F);
            if (this.result.affinity < 0.5){
                this.radialSearch(points,parameters,16384,2048);
            }
        }

        private void radialSearch(Target point, TerrainParameters parameters, float max, float min) {
            float f = 0.0F;
            float f1 = min;
            BlockPos blockpos = this.result.location();

            while (f1 <= max) {
                int x = blockpos.getX() + (int)(Math.sin(f) * f1);
                int z = blockpos.getZ() + (int)(Math.cos(f) * f1);
                Sampled sampled = parameters.samplerAt(x,z);
                int y = Mth.floor(parameters.yLevelAt(x,z,false) + 1);
                double affinity = point.affinity(sampled,y,false);
//                System.out.println("Affinity "+ x + ","+ z + ": " + affinity);

                if (affinity > this.result.affinity) {
                    this.result = Result.of(new BlockPos(x,y,z), affinity);
                }

                f += min / f1;
                if (f > Math.PI * 2) {
                    f = 0.0F;
                    f1 += min;
                }
            }
        }

        record Result(BlockPos location, double affinity) {
            static Result of (BlockPos location, double affinity){
                return new Result(location,affinity);
            }
        }

    }
}
