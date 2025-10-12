package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.noise.OpenSimplex2D;
import com.mojang.logging.LogUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class TerrainParameters {
    private static final float baseContinentSize = 1.5F;
    private static final float baseTectonicSize = 1.0F;
    private static final float mountainCoordsSize = 1.0F;
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
                        getMountains(randomSource, "mountain_peaks")
                ),
                getPlateauMap(randomSource, "plateaus"),
                getHills(randomSource, "hills"),
                getTerrainOffset(randomSource, "terrain_offset"),
                getTerrainOffsetLarge(randomSource, "terrain_offset_large"),
                getTerrainType(randomSource, "terrain_type_a"),
                getTerrainType(randomSource, "terrain_type_b")
        );

    }

    private PerlinNoise getTerrainType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,40);
    }

    private PerlinNoise getTerrainOffsetLarge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1);
    }

    private PerlinNoise getTerrainOffset(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1);
    }

    private PerlinNoise getPlateauMap(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,2);
    }

    private PerlinNoise getHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1,1);
    }

    private PerlinNoise getMountainsCore(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1);
    }
    private PerlinNoise getMountains(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1);
    }
    private PerlinNoise getMountainDetails(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,1);
    }

    private static Noise2D simplex(Seed seed, float spread) {
        return new OpenSimplex2D(seed.next()).spread(spread);
    }
    private PerlinNoise getTectonicActivity(PositionalRandomFactory randomSource, String name) {
        return  PerlinNoise.create(noiseRandom(name, randomSource), -12,12,12);
    }

    private PerlinNoise getErosion(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,1);
    }

    private PerlinNoise getContinentalness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,1.5,1);
    }


    private Climate makeClimate(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Climate(
                PerlinNoise.create(noiseRandom("temperature", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("humidity", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("vegetation_density", randomSource),-10,1),
                PerlinNoise.create(noiseRandom("rockyness", randomSource),-10,1)
        );
    }

    private RandomSource noiseRandom(String name, PositionalRandomFactory randomSource) {
        return randomSource.fromHashOf((RWildernessMod.modLocation("terrain_parameters/noises/" + name)));
    }


    public double yLevelAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        TerrainType terrainType = new TerrainType(
                terrainType(this.shapers.terrainTypeA.getValue(xContinental / baseTectonicSize, 0, zContinental / baseTectonicSize)),
                terrainType(this.shapers.terrainTypeB.getValue(xContinental / baseTectonicSize, 0, zContinental / baseTectonicSize))
        );
        double continentalness = this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zContinental * baseContinentSize);
        double seaSample = sampleOceans(continentalness);
        double erosionShaper = sampleErosionMap(x,z);
        double terrainSample = sampleTerrain(x,z, continentalness,erosionShaper);
        double plateau =
                Math.pow(Math.clamp(this.shapers.plateauMap.getValue(xPos / baseTectonicSize, 0, zPos / baseTectonicSize),0,1),5)
                *
                Math.clamp((Math.max(0.15,continentalness) - 0.15) * 7, 0,1);
       double tectonicSample = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize,  0,zPos / baseTectonicSize);
       double mountainLayer = sampleMountains(x,z,plateau,tectonicSample,continentalness,terrainType, erosionShaper);

       // Adds the different terrain Layers together.
       return seaSample + terrainSample + mountainLayer;
    }

    private double terrainType(double value) {
        return Math.clamp((value + 1) /2,0,1);
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
        int amplitude = 16;
        int eAmp = 5;
        double base = this.shapers.terrainOffset.getValue(x,0,z) * amplitude;
        double large = this.shapers.terrainOffsetLarge.getValue(x,0,z) * eAmp;
        double terrain = erosionMapped(erosion,base,large,1.0F);
        // Imagine extremity as 1/x, the higher the offset is, the more the terrain will be affected by terrain offset
        // the value is like this so that beaches and shores remain mostly flat.
        // The value here leads to a 0.2 continentalness becoming the point where the offset becomes normal.
        // offset makes it so that the terrain "point zero" is offset towards the sea, so shores look more natural, otherwise
        // you'll see completely flat beaches and shores and the terrain won't have any variation there.
        float extremity = 1 / 0.5F;
        float offset = 0.2F;
        return Math.abs((Mth.clamp( Math.abs(continentalness) * extremity,-1 - offset,1 - offset) + offset) * terrain);
    }

    private double sampleErosionMap(float x, float z){
        return this.shapers.erosion.getValue(x,0,z);
    }

    /* Samples the Tectonic Activity noise, and folds it so it becomes ridged, and inverses it's value so the closer
    * it is to 0, the stronger it is.
    * Then, if it is greater than 0 (so we avoid unnecessary sampling), it multiplies the mountain size by that.
    * Perhaps I might make it so it is a curve instead of a linear value.
    * I'll also add erosion based
    */
    private double sampleMountains(float xPos, float zPos, double plateau,double tectonicActivity, double continentalness, TerrainType terrainType, double erosion){

        double tA = 1 - Math.abs(tectonicActivity);
        if (tA <= -1.5) return 0;


        double continentalFilter = Math.clamp(Math.abs(continentalness) * (1/0.25F),-1,1 );

        float x = xPos * mountainCoordsSize;
        float z = zPos * mountainCoordsSize;
        double hills = Math.pow(Math.abs(this.shapers.hills.getValue(x,0, z)),1.25);
        double hillSize = 40;
        double hillShape = (Math.min((tA + 1.5) * 2, 1)) * hills * hillSize * Math.clamp(( 1 + erosion) / 2,0,1) * continentalFilter;

        if (tA <= 0) return 0;

        double tectonicFilter = Math.clamp(tA * 6, 0,1);
        double plateauFilter = 1 - plateau;
        hillShape *= plateauFilter;
        double plateauHeight = 120;

        float powerCurve = 1.75F;

        double mountainsCore = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x, 0, z) + 1) / 2),0,1), 1.25);
        double mountains = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x, 0, z) + 1) / 2),0,1), powerCurve);

        double detail = Math.abs(
                (1 - Math.abs(this.shapers.mountains.detail.getValue(x, 0, z)))
                - 0.5 ) * 2;

        double mountainDetail = detail * ((mountainsCore + mountains) / 2);



        double ridges = 0.9D + (0.1D * detail);
        double plateauShape = plateau * plateauHeight * tectonicFilter;

        double alphaCore = mountainsCore * tA * ridges;
        double alpha = (mountains) * tA * ridges;
        double alphaEroded = mountains * tA;
        double alphaDetail = mountainDetail * tA;



        double erodedMountainMap = NaNCheck(mountainEroded(alphaEroded, alphaCore, continentalness));
        double mountainMap = NaNCheck(mountain(alpha, alphaDetail,alphaCore, continentalness));
        double mountainCliffMap = NaNCheck(mountainSeaCliff(alpha, alphaDetail,alphaCore, continentalness));
        double divergentMap = divergent((mountains), mountainsCore, tA);
        if (plateauFilter > 0){
            double dA = Math.max(divergentMap, 0);
            double dB = Math.min(divergentMap, 0);
            divergentMap = (dA * plateauFilter) + dB;
        }
        divergentMap = NaNCheck(divergentMap);
        double transform = NaNCheck(transform(mountainsCore, 1 - detail, mountains, continentalness,tA) * plateauFilter);
        double terrainMap = terrainType.mapValues(
                divergentMap, transform,
                erosionMapped(erosion,mountainMap,erodedMountainMap,0.2),erosionMapped(erosion,mountainCliffMap,erodedMountainMap,0.2)
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
        if (Double.isNaN(v)){
            LogUtils.getLogger().warn("World Generation value is NaN");
        }
        return Double.isNaN(v) ? 0 : v;
    }

    private double transform(double mountains, double detail, double modifier, double continentalness, double tA) {
        double fault = Math.pow(
                Math.max((tA - 0.75F) * 4,0)
                , 3);
        double t = Math.clamp((5 * tA) - (2 + (modifier * 0.5)),0,1);
        double terrain = (detail * 20) + (mountains * 30);
        if (continentalness <= 0){
            return t * (terrain + (continentalness * 30)) * (1 - fault);
        }
        return t * terrain * (1-fault);
    }

    private double divergent(double mountains, double core, double tA) {
        double fault = Math.pow(
                Math.max((tA - 0.75F) * 4,0)
                , 4);
        double ta2 = Math.pow(tA, 4);
        double amp = 50 + (mountains * 70) + (core * 80);
        return (amp * ta2 * (1-fault)) + (fault * -120);
    }

    private double mountainEroded(double alpha, double alphaCore, double continentalness) {
        double c = Math.clamp(continentalness * 1.1 - 0.1, 0.0, 1.0);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 100 * c;
        double amplitude = 100 * c;

        return (alpha * amplitude) + (alphaCore * amplitudeCore);
    }
    private double mountain(double alpha, double alphaDetail, double alphaCore, double continentalness) {
        double c = Math.clamp(Math.abs(continentalness) + 0.2, 0.2, 1.0);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 130 * c;
        double amplitude = 130 * c;
        double detailAmp = 20 * c;
        //TOTAL = 280
        return (alpha * amplitude) + (alphaDetail * detailAmp) + (alphaCore * amplitudeCore);
    }
    private double mountainSeaCliff(double alpha, double alphaDetail, double alphaCore, double continentalness) {
        double c = Math.clamp((continentalness * 30) + Math.min(alpha,0.2), -0.05, 1.0);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 130 * c;
        double amplitude = 130 * c;
        double detailAmp = 20 * c;
        //TOTAL = 280
        return (alpha * amplitude) + (alphaDetail * detailAmp) + (alphaCore * amplitudeCore);
    }

    private double sampleOceans(double continentalness){
        int seaBottom = 0;
        int median = 63;
        int continentalTop = 120;

        return lerpOceans(continentalness,seaBottom,median,continentalTop);
    }


    private double lerpOceans(double alpha, int bottom, int median, int top){
        if (alpha > 0){
            return Mth.lerp( Math.pow(Math.sin(alpha * 0.5 * Math.PI), 2), median,top);
        }
        return Mth.lerp(Math.pow(Math.sin(alpha * 0.5 * Math.PI) * -1, 2), median, bottom);
    }


    public Sampled samplerAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        return new Sampled(
                this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zPos * continentScale * baseContinentSize),
                this.shapers.erosion.getValue(x,0,z),
                this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize, 0,zPos / baseTectonicSize),
                this.shapers.mountains.core.getValue(x * mountainCoordsSize, 0, z *  mountainCoordsSize),
                this.shapers.mountains.shape.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize),
                this.shapers.mountains.detail.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize),
                this.shapers.plateauMap.getValue(xPos / baseTectonicSize,0,zPos / baseTectonicSize),
                this.shapers.hills.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize),
                this.shapers.terrainOffset.getValue(x,0,z),
                this.shapers.terrainOffsetLarge.getValue(x,0,z),
                this.shapers.terrainTypeA.getValue(xContinental / baseTectonicSize,0,zContinental / baseTectonicSize),
                this.shapers.terrainTypeB.getValue(xContinental / baseTectonicSize,0,zContinental / baseTectonicSize),
                this.climate.temperature.getValue(x,0,z),
                this.climate.humidity.getValue(x,0,z),
                this.climate.vegetationDensity.getValue(x,0,z),
                this.climate.rockyness.getValue(x,0,z)
        );
    }

    private record Shapers(
        PerlinNoise continentalness,
        PerlinNoise erosion,
        PerlinNoise tectonicActivity,
        Mountain mountains,
        PerlinNoise plateauMap,
        PerlinNoise hills,
        PerlinNoise terrainOffset,
        PerlinNoise terrainOffsetLarge,
        PerlinNoise terrainTypeA,
        PerlinNoise terrainTypeB
        ){

        public record Mountain(
                PerlinNoise core,
                PerlinNoise shape,
                PerlinNoise detail,
                PerlinNoise peak
        ){}
    }


    public record Sampled(
        double continentalness,
        double erosion,
        double tectonicActivity,
        double mountainsCore,
        double mountains,
        double mountainDetails,
        double plateauMap,
        double hills,
        double terrainOffset,
        double terrainOffsetLarge,
        double terrainTypeA,
        double terrainTypeB,
        double temperature,
        double humidity,
        double vegetationDensity,
        double rockyness
        ){}


    record TerrainType(double a, double b){
        double mapValues(double aa, double ab, double ba, double bb){
            double aValue = Mth.lerp(a,aa,ab);
            double bValue = Mth.lerp(a,ba,bb);
            return Mth.lerp(b,aValue,bValue);
        }
        double mapValues(double aa, double ab, double b2){
            double aValue = Mth.lerp(a,aa,ab);
            return Mth.lerp(b,aValue,b2);
        }
    }
    private record Climate(
            PerlinNoise temperature,
            PerlinNoise humidity,
            PerlinNoise vegetationDensity,
            PerlinNoise rockyness
    ){}

}
