package com.fuyuaki.r_wilderness.world.generation;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.noise.OpenSimplex2D;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
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
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,1);
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
        return  PerlinNoise.create(noiseRandom(name, randomSource), -12,7,8);
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
       double tectonicSample = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize,  0,zPos / baseTectonicSize);
       double mountainLayer = sampleMountains(x,z,tectonicSample,continentalness,terrainType, erosionShaper);

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
    private double sampleMountains(float x, float z, double tectonicActivity, double continentalness, TerrainType terrainType, double erosion){

        double tA = 1 - Math.abs(tectonicActivity);
        if (tA <= 0) return 0;



        float powerCurve = 1.75F;

        double mountainsCore = Math.pow(Math.clamp(((this.shapers.mountains.core.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize) + 1) / 2),0,1), 1.25);
        double mountains = Math.pow(Math.clamp(((this.shapers.mountains.shape.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize) + 1) / 2),0,1), powerCurve);

        double detail = Math.abs(
                (1 - Math.abs(this.shapers.mountains.detail.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize)))
                - 0.5 ) * 2;

        double mountainDetail = detail * ((mountainsCore + mountains) / 2);



        double ridges = 0.75D + (0.25D * detail);

        double alphaCore = mountainsCore * tA * ridges;
        double alpha = (mountains) * tA * ridges;
        double alphaEroded = mountains * tA;
        double alphaDetail = mountainDetail * tA;



        double erodedMountainMap = NaNCheck(mountainEroded(alphaEroded, alphaCore, continentalness));
        double mountainMap = NaNCheck(mountain(alpha, alphaDetail,alphaCore, continentalness));
        double divergentMap = NaNCheck(divergent((mountains), mountainsCore, tA));
        double transform = NaNCheck(transform(mountainsCore, detail, mountains, continentalness,tA));
        double terrainMap = terrainType.mapValues(divergentMap, transform, mountainMap);
        return erosionMapped(erosion,terrainMap,erodedMountainMap,0.2);


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

    private double transform(double mountains, double detail, double modifier, double continentalness, double tA) {
        double t = Math.clamp((5 * tA) - (2 + (modifier * 0.5)),0,1);
        double terrain = (NaNCheck(detail) * 50) + (NaNCheck( mountains) * 80);
        if (continentalness <= 0){
            return t * (terrain + (continentalness * 30));
        }
        return t * terrain;
    }

    private double divergent(double mountains, double core, double tA) {
        double ta2 = Math.clamp(tA * 4,0,1);
        double tM = tA > 0.5 ? 1.5 - (2 * tA): tA;
        double amp = 50 + (mountains * 70) + (core * 80);
        return amp * ta2 * tM;
    }

    private double mountainEroded(double alpha, double alphaCore, double continentalness) {
        double c = Math.clamp(continentalness * 1.1F - 0.1F, 0.0F, 1.0F);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 100 * c;
        double amplitude = 100 * c;

        return (alpha * amplitude) + (alphaCore * amplitudeCore);
    }
    private double mountain(double alpha, double alphaDetail, double alphaCore, double continentalness) {
        double c = Math.clamp(Math.abs(continentalness) + 0.2F, 0.2F, 1.0F);
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

        return lerp(true,1.2F, continentalness,seaBottom,median,continentalTop);
    }


    private double lerp(boolean curved, float curve, double alpha, int bottom, int median, int top){
        if (alpha > 0){
            return Mth.lerp( curved ? Math.pow(alpha,curve) : alpha, median,top);
        }
        return Mth.lerp(curved ? Math.pow(alpha * -1 ,curve) : alpha * -1, median, bottom);
    }


    public Sampled samplerAt(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        return new Sampled(
                this.shapers.continentalness.getValue(xPos * continentScale * baseContinentSize, 0, zPos * continentScale * baseContinentSize),
                this.shapers.erosion.getValue(x,0,z),
                this.shapers.tectonicActivity.getValue(xPos * baseTectonicSize, 0,zPos * baseTectonicSize),
                this.shapers.mountains.core.getValue(xPos * continentScale * mountainCoordsSize, 0, zPos * continentScale * mountainCoordsSize),
                this.shapers.mountains.shape.getValue(xPos * continentScale * mountainCoordsSize, 0, zPos * continentScale * mountainCoordsSize),
                this.shapers.mountains.detail.getValue(x * mountainCoordsSize, 0, z * mountainCoordsSize),
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
        PerlinNoise continentalness,
        PerlinNoise erosion,
        PerlinNoise tectonicActivity,
        Mountain mountains,
        PerlinNoise plateauMap,
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
