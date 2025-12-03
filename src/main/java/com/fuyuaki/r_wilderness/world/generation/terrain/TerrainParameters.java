package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.Seed;
import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.DistortionSpline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.neoforged.api.distmarker.Dist;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

public class TerrainParameters {
    public static final Range POSITIVE = range(0,1.5);
    public static final Range NEGATIVE = range(-1.5,0);
    public static final Range MAGICALNESS_LOW = range(-1.5,0.15);
    public static final Range NEUTRAL = range(-0.65,0.65);
    public static final Range FULL = range(-1.5,1.5);


    public static final Target SPAWN_TARGET =
            new Target(
                    NEUTRAL, //Temperature
                    NEUTRAL, //Humidity
                    NEUTRAL, //Vegetation Density
                    NEUTRAL, //Rockiness
                    range(0.1,0.5,0.75), //Continentalness
                    range(-8,-3.5,0.0), //Tectonic Activity
                    range(-200,0,0.02), //Highlands
                    NEUTRAL, //Erosion
                    range(64,160) // Terrain Height
            );


    private static final float baseContinentSize = 0.5F;
    private static final float baseTectonicSize = 1.5F;
    private static final float highlandsOffset = 1.5F;
    private static final float highlandsScale = 1 / 0.75F;
    private static final float terrainTypeScale = 1 / 0.5F;
    private static final float mountainCoordsSize = 0.6F;
    private static final float temperatureScale = 1.5F;
    private static final float humidityScale = 1.25F;
    private static final float rockinessScale = 1.25F;
    private static final float weirdnessScale = 0.65F;
    public static final double EROSION_BADLANDS_THRESHOLD = 1;
    public static final double HUMIDITY_BADLANDS_THRESHOLD = -0.25;
    public static final double BADLANDS_THRESHOLD = 0.65;
    private final float continentScale;
    private final float terrainScale;
    private final Seed seed;
    private final WildGeneratorSettings settings;
    public final Shapers shapers;
    public final Climate climate;

    private static final int CACHE_CLEAR_POINT = 6000;
    private static final int SURFACE_CACHE_CLEAR_POINT = 12000;
    private final Map<Long, ChunkCache> cache = new ConcurrentHashMap<>();
    private final Map<Long, ChunkCache> surfaceOnlyCache = new ConcurrentHashMap<>();


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
                new Shapers.Detailer(
                        getDetailer(randomSource, "detailer_first"),
                        getDetailer(randomSource, "detailer_second"),
                        getDetailerDelta(randomSource, "detailer_delta"),
                        getDetailerMask(randomSource, "detailer_mask")
                ),
                geHighlandsMap(randomSource, "highlands"),
                getHills(randomSource, "hills"),
                getTerrainOffset(randomSource, "terrain_offset"),
                getTerrainOffsetLarge(randomSource, "terrain_offset_large"),
                getTerrainNoise(randomSource, "terrain_offset_noise"),
                getTerrainType(randomSource, "terrain_type_a"),
                getTerrainType(randomSource, "terrain_type_b"),
                getWaterBasins(randomSource, "basins"),
                getWaterBasinFilters(randomSource,"basin_filter"),
                getRandomElevation(randomSource,"random_elevation"),
                new Shapers.Cave(
                        getCaveFilter(randomSource,"base",-7),
                        getCaveFilter(randomSource,"ball_filter",-8),
                        getCavePillar(randomSource,"pillar"),
                        getCaveBall(randomSource,"ball"),
                        getCaveFilter(randomSource,"squiggle_filter",-7),
                        getCaveSquiggle(randomSource,"squiggle"),
                        getCaveFilter(randomSource,"ridge_filter",-8),
                        getCaveRidge(randomSource,"ridge"),
                        getCaveFilter(randomSource,"vein_filter",-7),
                        getCaveVeins(randomSource,"veins")
                ),
                new Shapers.Badlands(
                        getBadlandsFilter(randomSource,"badlands_filter"),
                        getBadlandsStep(randomSource,"badlands_step"),
                        getBadlandsValleys(randomSource,"badlands_valleys"),
                        getBadlandsHills(randomSource,"badlands_hills")
                ),
                new Shapers.River(
                        getRiverDirection(randomSource,"river_direction"),
                        getRiverType(randomSource,"river_type_a"),
                        getRiverType(randomSource,"river_type_b")
                )
        );
    }

    private PerlinNoise getRiverDirection(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,2);
    }
    private PerlinNoise getRiverType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,2);
    }

    private PerlinNoise getBadlandsHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1.25,1,2,2);
    }
    private PerlinNoise getBadlandsFilter(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,1.25,1,1,1);
    }
    private PerlinNoise getBadlandsStep(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.25,1,1,2,2,2);
    }
    private PerlinNoise getBadlandsValleys(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,1.25,2,2,3);
    }

    private PerlinNoise getCaveFilter(PositionalRandomFactory randomSource, String name,int firstOctave) {
        return PerlinNoise.create(noiseRandom(name, randomSource),firstOctave,2,3);
    }
    private PerlinNoise getCavePillar(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-2,1);
    }
    private PerlinNoise getCaveBall(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,2,2,2);
    }
    private PerlinNoise getCaveSquiggle(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1,1);
    }
    private PerlinNoise getCaveRidge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,5,4);
    }
    private PerlinNoise getCaveVeins(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,6,11);
    }

    private PerlinNoise getWaterBasins(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,10);
    }
    private PerlinNoise getWaterBasinFilters(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,10);
    }
    private PerlinNoise getRandomElevation(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,1,1,1);
    }

    private static int posIndex(int xPos, int zPos){
        int x = Math.floorMod(xPos,16);
        int z = Math.floorMod(zPos,16);
        return x * 16 + z;
    }


    private static double getC(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,1.5);
    }
    private static double getC(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,1.5);
    }
    private static double getC(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,1.5);
    }

    private static double getP(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z),0,1.5);
    }
    private static double getP(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z),0,1.5);
    }
    private static double getP(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z),0,1.5);
    }


    private static double getN(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z),-1.5,0);
    }
    private static double getN(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z),-1.5,0);
    }
    private static double getN(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z),-1.5,0);
    }



    private static double getC2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z),-1,1);
    }
    private static double getC2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,1);
    }
    private static double getC2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z),-1,1);
    }

    private static double getP2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z),0,1);
    }
    private static double getP2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1);
    }
    private static double getP2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z),0,1);
    }


    private static double getN2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z),-1,0);
    }
    private static double getN2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z),-1,0);
    }
    private static double getN2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z),-1,0);
    }



    private PerlinNoise getTerrainType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,10);
    }

    private PerlinNoise getTerrainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-4,1);
    }

    private PerlinNoise getTerrainOffsetLarge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,0,1);
    }

    private PerlinNoise getTerrainOffset(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.5,1,0,1);
    }

    private PerlinNoise geHighlandsMap(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,4,5,5,6,8);
    }

    private PerlinNoise getHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,1.5,0.5,0.5,1,1);
    }

    private PerlinNoise getDetailer(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,5,5,5);
    }
    private PerlinNoise getDetailerDelta(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,10,10,10,10);
    }
    private PerlinNoise getDetailerMask(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,2,2,2,2);
    }

    private PerlinNoise getMountainsCore(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,1,2,1);
    }
    private PerlinNoise getMountains(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.5,1,2,1);
    }
    private PerlinNoise getMountainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,1.5,1,1);
    }
    private PerlinNoise getMountainDetails(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,1.5);
    }

    private PerlinNoise getTectonicActivity(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource), -13,12,8,8,8);
    }

    private PerlinNoise getErosion(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-12,2,1.5);
    }

    private PerlinNoise getContinentalness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-12,1.5,1,1,1);
    }


    private Climate makeClimate(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Climate(
                PerlinNoise.create(noiseRandom("temperature", randomSource),-11,2,1),
                PerlinNoise.create(noiseRandom("humidity", randomSource),-11,2,1,1),
                PerlinNoise.create(noiseRandom("vegetation_density", randomSource),-10,2,2,1.5),
                PerlinNoise.create(noiseRandom("rockiness", randomSource),-9,2,1,1.5),
                PerlinNoise.create(noiseRandom("weirdness", randomSource),-9,2,1,1),
                PerlinNoise.create(noiseRandom("magicalness", randomSource),-9,1.5,1)
        );
    }

    private RandomSource noiseRandom(String name, PositionalRandomFactory randomSource) {
        return randomSource.fromHashOf((RWildernessMod.modLocation("terrain_parameters/noises/" + name)));
    }

    public Sampled samplerAtCached(int x, int z){
        ChunkPos pos = new ChunkPos(x,z);
        long longPos = new ColumnPos(pos.x,pos.z).toLong();
        final ChunkCache chunkCache = this.cache.get(longPos);
        if (chunkCache == null || chunkCache.caches[posIndex(x,z)] == null || chunkCache.caches[posIndex(x, z)].sampled.isEmpty()) {
            return samplerAt(x,z);
        }
            return chunkCache.caches[posIndex(x, z)].sampled.get();
    }
    public double yLevelAtWithCache(int x, int z) {
        return this.yLevelAt(x, z, false);
    }
    public double sampleTectonicPlates(int xPos, int zPos){
        return this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize, 0, zPos / baseTectonicSize);
    }


    public double yLevelAt(int xPos, int zPos, boolean surfaceOnly) {
        return yLevelAt(xPos,zPos,surfaceOnly,true);
    }
    public double yLevelAt(int xPos, int zPos, boolean surfaceOnly,boolean cache){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        TerrainType terrainType = new TerrainType(
                terrainType(this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale)),
                terrainType(this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale))
        );
        double continentalness = (this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zContinental * baseContinentSize) * 2) + 0.2;
        double seaSample = sampleOceans(continentalness);

        double erosionShaper = sampleErosionMap(x,z);

        double terrainSample = sampleTerrain(x,z, continentalness,erosionShaper);

        double randomElevation = sampleRandomElevation(x,z,continentalness,erosionShaper);

        double highlands = sampleHighlands(xPos, zPos, continentalness);

       double tectonicSample = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize,  0,zPos / baseTectonicSize) * 2.5;
       double mountainLayer = sampleMountains( x, z, highlands, tectonicSample, continentalness, terrainType, erosionShaper, surfaceOnly);
       double detailerLayer = sampleDetailer(x,z,continentalness,tectonicSample,erosionShaper);

       // Adds the different terrain Layers together.
       double yOff = terrainSample + mountainLayer + detailerLayer + randomElevation;

       yOff = postWithBadlands(x,z,erosionShaper, continentalness, yOff);

        double y = samplePRivers(x,z,continentalness,tectonicSample,yOff + seaSample);
        if(cache){
            if (surfaceOnly) {
                updateChunkYSurface(y, xPos, zPos);
            } else {
                updateChunkY(y, xPos, zPos);
            }
        }
       return y;
    }

    private double sampleHighlands(int xPos, int zPos, double continentalness) {
        return (Math.sin(((
                Math.pow(Math.clamp((getC(this.shapers.highlandsMap, xPos / baseTectonicSize / highlandsScale, zPos / baseTectonicSize / highlandsScale) - highlandsOffset) * 2, 0, 1), 3)
                        *
                        Math.clamp(continentalness * 7, -0.2, 1)) - 0.5) * Mth.PI) + 1) / 2;
    }

    /**
     * Super-duper simplified version of Y calculatiom
     */
    public double riverRawY(int xPos, int zPos){
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        TerrainType terrainType = new TerrainType(
                terrainType(this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale)),
                terrainType(this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale))
        );
        double continentalness = (this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zContinental * baseContinentSize) * 2) + 0.2;
        double seaSample = sampleOceans(continentalness);
        double terrainSample = sampleTerrain(x,z, continentalness,1);

        double randomElevation = sampleRandomElevation(x,z,continentalness,0);

       double tectonicSample = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize,  0,zPos / baseTectonicSize) * 2.5;
       double mountainLayer = sampleMountains( x, z, 0, tectonicSample, continentalness, terrainType, 1, false);

       return terrainSample + mountainLayer + randomElevation + seaSample;
    }

    private double samplePRivers(float x, float z, double continentalness, double tectonicSample, double y) {
        if (continentalness < -0.2) return y;

        double tFilter = Math.clamp(2 - (Math.abs(tectonicSample) * 2),0,1);
        double r = Math.pow(Math.abs(Math.clamp(getC(this.shapers.waterBasins,x,z) * 3,-1,1)),1.15);
        if (r >= 1) return y;
        DistortionSpline spline =
                new DistortionSpline(
                        new DistortionSpline.Spline()
                                .addPoint(-1,52)
                                .addPoint(0,52)
                                .addPoint(0.4,64)
                                .addPoint(1,y)
                                .addPoint(5,y)
                        ,52,y
                );
        return Math.min(y,Mth.lerp(tFilter,spline.at(r),y));
    }

    private double postWithBadlands(float x, float z, double erosionShaper, double continentalness, double yOff) {
        if (erosionShaper > EROSION_BADLANDS_THRESHOLD) return yOff;
        double filterE = Math.clamp(-(erosionShaper - EROSION_BADLANDS_THRESHOLD) * 8,0,1);

        double humidity = getC(this.climate.humidity, x, z);

        if (humidity > HUMIDITY_BADLANDS_THRESHOLD) return yOff;
        double filterH = Math.clamp(-(humidity - HUMIDITY_BADLANDS_THRESHOLD) * 8,0,1);

        double f = getC(this.shapers.badlands.filter,x,z);
        if (f < BADLANDS_THRESHOLD) return yOff;
        double continentFilter = Math.clamp(continentalness * 8,0,1);
        double filter = Math.clamp((f - BADLANDS_THRESHOLD) * 8,0,1) * filterH * filterE * continentFilter ;
        double step = getC(this.shapers.badlands.step,x,z) * filter;

        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-2.0,4)
                        .addPoint(-1.5,4)
                        .addPoint(0.0,8)
                        .addPoint(1.5,12)
                        .addPoint(2.0,12)
                ,0,12
        );

        double e = Math.min(Math.abs(erosionShaper - EROSION_BADLANDS_THRESHOLD) * 5,1);
        double stepPost = Math.floor(spline.at(step)) * e;

        double hillAmp = 12;
        double hills = Math.abs(getC(this.shapers.badlands.hills,x,z)) * hillAmp;

        double heightOffset = yOff;
        if (stepPost >= 1){
            int yMod = Math.floorMod(Mth.floor(yOff), (int) stepPost);
            heightOffset = yOff - yMod;

        }
        if (heightOffset > 0){
            double valleysNoise = Math.abs(getC2(this.shapers.badlands.valleys,x,z));
            double valleys = Math.clamp((1 - Math.pow((1-valleysNoise),3)) * 3,0,1) * filter;
            heightOffset *= valleys;

        }
        return Mth.lerp(filter,yOff,heightOffset + hills);
    }

    public boolean cavesAt(int x,int y, int z, double yLevel){
        double filter = 0;
        if (y > 0){
            filter = Math.min(((double) y / 200) + ((y / yLevel) * 0.15),1);
        }else if (y < -200){
            filter =  Math.abs(y + 200D) / 40;
        }

        double ballCaveFilter = Math.max(y + 128D,0) / 64;
        double baseFilter = Math.max(this.shapers.cave.base.getValue(x, y * 5, z),0) * 2;
        double ballFilter = Math.max(this.shapers.cave.ballFilter.getValue(x, y * 1.5, z),0) * 2;
        double ridgeFilter = Math.abs(this.shapers.cave.ridgeFilter.getValue(x, y, z)) * 5;
        double squiggleFilter = Math.abs(this.shapers.cave.squiggleFilter.getValue(x, y * 2.5, z)) * 5;
        double veinFilter = Math.abs(this.shapers.cave.veinFilter.getValue(x, y * 1.5, z)) * 5;

        double ball = this.shapers.cave.ball.getValue(x, y * 5, z) * 3;
        double pillars = Math.pow(Math.min(Math.abs(this.shapers.cave.pillars.getValue(x, y * 0.15, z)) * 2,1),2) * 0.15;
        double ridge = Math.abs(this.shapers.cave.ridge.getValue(x, y * 1.5, z)) * 5;
        double squiggle = Math.abs(this.shapers.cave.squiggle.getValue(x, y * 1.25, z)) * 5;
        double vein = Math.abs(this.shapers.cave.veins.getValue(x, y * 1.25, z)) * 3;

        double ballCave = ball - pillars - ballFilter - baseFilter - 0.15 - filter - ballCaveFilter;
        double ridgeCave = ridge + ridgeFilter + baseFilter + 0.15 + filter;
        double squiggleCave = (squiggle + squiggleFilter + baseFilter  - 0.15 + (filter * 0.3)) * 3;
        double veinCave = (vein + veinFilter + baseFilter - 0.15 + Math.min(filter * 0.3,0.15)) * 3;

        double veinThreshold = 0.15;
        double squiggleThreshold = 0.3;
        double ridgeThreshold = 0.45;
        double ballThreshold = 1.0;
        return veinCave < veinThreshold
                || squiggleCave < squiggleThreshold
                || ridgeCave < ridgeThreshold
                || ballCave > ballThreshold
                ;
    }


    private void updateChunkY(double y, int x, int z){
        ChunkPos pos = new ChunkPos(x,z);
        long longPos = new ColumnPos(pos.x,pos.z).toLong();
        if (this.cache.containsKey(longPos)){
            final ChunkCache chunkCache = this.cache.get(longPos);
            if (chunkCache == null){
                checkCacheSize();
                this.cache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromY(y)));
            }
            else if (chunkCache.validAt(x,z)){
                InfoCache infoCache = chunkCache.caches[posIndex(x, z)];
                if (!infoCache.checkYLevel()){
                    this.cache.put(longPos,chunkCache.with(posIndex(x,z),infoCache.withYLevel(y)));
                }
            }else{
                this.cache.put(longPos,chunkCache.with(posIndex(x,z),InfoCache.fromY(y)));
            }
        }else {
            checkCacheSize();
            this.cache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromY(y)));
        }
    }

    private void updateChunkYSurface(double y, int x, int z){
        ChunkPos pos = new ChunkPos(x,z);
        long longPos = new ColumnPos(pos.x,pos.z).toLong();
        if (this.surfaceOnlyCache.containsKey(longPos)){
            final ChunkCache chunkCache = this.surfaceOnlyCache.get(longPos);
            if (chunkCache == null){
                checkSurfaceCacheSize();
                this.surfaceOnlyCache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromY(y)));
            }
            else if (chunkCache.validAt(x,z)){
                InfoCache infoCache = chunkCache.caches[posIndex(x, z)];
                if (!infoCache.checkYLevel()){
                    this.surfaceOnlyCache.put(longPos,chunkCache.with(posIndex(x,z),infoCache.withYLevel(y)));
                }
            }else{
                this.surfaceOnlyCache.put(longPos,chunkCache.with(posIndex(x,z),InfoCache.fromY(y)));
            }
        }else {
            checkSurfaceCacheSize();
            this.surfaceOnlyCache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromY(y)));
        }
    }


    private void updateChunkSampler(Sampled sampled, int x, int z){
        ChunkPos pos = new ChunkPos(x,z);
        long longPos = new ColumnPos(pos.x,pos.z).toLong();
        if (this.cache.containsKey(longPos)){
            final ChunkCache chunkCache = this.cache.get(longPos);
            if (chunkCache == null){
                checkCacheSize();
                this.cache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromSampled(sampled)));
            }
            else if (chunkCache.validAt(x,z)){
                InfoCache infoCache = chunkCache.caches[posIndex(x, z)];
                if (!infoCache.checkSampled()){
                    this.cache.put(longPos,chunkCache.with(posIndex(x,z),infoCache.withSampled(sampled)));
                }
            }else{
                this.cache.put(longPos,chunkCache.with(posIndex(x,z),InfoCache.fromSampled(sampled)));
            }
        }else {
            checkCacheSize();
            this.cache.put(longPos,ChunkCache.fromInfoCache(posIndex(x,z),InfoCache.fromSampled(sampled)));
        }
    }

    private synchronized void checkCacheSize() {
        if (this.cache.size() >= CACHE_CLEAR_POINT){
            this.cache.clear();
        }
    }
    private synchronized void checkSurfaceCacheSize() {
        if (this.surfaceOnlyCache.size() >= SURFACE_CACHE_CLEAR_POINT){
            this.surfaceOnlyCache.clear();
        }
    }

    private double sampleDetailer(float x, float z, double continentalness, double tectonicActivity,double erosion) {
        double detail = Math.clamp(this.shapers.detailer.get(x,z) - 0.5,0,3) / 3;


        DistortionSpline erosionSpline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1.5,32)
                        .addPoint(-0.5F, 24)
                        .addPoint(0.0F, 16)
                        .addPoint(0.5F,12)
                        .addPoint(1.5F,4),
                52,6
        );
        double size = erosionSpline.at(erosion);

        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-2.5F,size * 0.75)
                        .addPoint(-0.5,size * 0.25)
                        .addPoint(0,0)
                        .addPoint(0.05,size)
                        .addPoint(2.5F,size),
                size,size
        );
        double dScale = spline.at(continentalness);




        DistortionSpline splineT = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-20.0F,0)
                        .addPoint(-5.0F,0)
                        .addPoint(-4.0F, 0.1)
                        .addPoint(-3.0,0.5)
                        .addPoint(-1.5,1.0)
                        .addPoint(1.5F,1.0),
                0,1
        );
        double dScaleT = splineT.at(tectonicActivity);

        return dScale * detail * dScaleT;
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
    private double sampleRandomElevation(float x, float z, double continentalness, double erosion) {
        double e = Math.clamp(1 - ((erosion + 1) / 2),0,1);
        double c = Math.pow(Math.clamp((continentalness + 0.05) / 0.1,0,1),5);
        int amplitude = 32;

        double scale = Math.abs(getC2(this.shapers.randomElevation,x,z));
        return e * c * scale * amplitude;

    }
    private double sampleTerrain(float x, float z, double continentalness, double erosion) {
        int amplitude = 32;
        int eAmp = 2;
        double base = Math.abs(this.shapers.terrainOffset.getValue(x,0,z) * 2);
        double large = Math.abs(this.shapers.terrainOffsetLarge.getValue(x,0,z) * 2);
        double noise = Math.abs(this.shapers.terrainNoise.getValue(x,0,z) * 2) * 0.04;
        double e = Math.clamp(1 - ((erosion + 1) / 2),0,1);
        double terrainAmp = Math.max(base, large);
        double terrain = (terrainAmp * e * amplitude) + (terrainAmp * eAmp * (1 - e)) + (terrainAmp * noise);
        // Imagine extremity as 1/x, the higher the offset is, the more the terrain will be affected by terrain offset
        // the value is like this so that beaches and shores remain mostly flat.
        // The value here leads to a 0.2 continentalness becoming the point where the offset becomes normal.
        // offset makes it so that the terrain "point zero" is offset towards the sea, so shores look more natural, otherwise
        // you'll see completely flat beaches and shores and the terrain won't have any variation there.
        float extremity = 1 / 0.15F;
        return Math.min(Math.abs(continentalness + 0.02) * extremity,1) * terrain;
    }

    private double sampleErosionMap(float x, float z){
        return getC2(this.shapers.erosion, x,z);
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
    private double sampleMountains(float xPos, float zPos, double highlands, double tectonicActivity, double continentalness, TerrainType terrainType, double erosion, boolean surfaceOnly){

        double tA = 1 - Math.pow(tectonicActivity,2);
        double hillHighlandsStart = 3.0;
        if (tA <= -hillHighlandsStart) return 0;


        double continentalFilter = Math.clamp((continentalness * (1/0.05F)),0,1);

        float x = xPos * mountainCoordsSize;
        float z = zPos * mountainCoordsSize;
        double hills = Math.pow(Math.abs(getC(this.shapers.hills, x, z)),1.25);

        DistortionSpline hillErosionBasedSize = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1.5,42)
                        .addPoint(-0.75,42)
                        .addPoint(0.0F, 32)
                        .addPoint(0.5F,24)
                        .addPoint(0.75F,6)
                        .addPoint(1.5F,6),
                42,0
        );
        double hillSize = hillErosionBasedSize.at(erosion);
        double hillShape = (Math.min((tA + hillHighlandsStart), 1)) * hills * hillSize * continentalFilter;

        double highlandFilter = (1 - highlands) * 0.5 + 0.5;


        double highlandHeight = terrainType.mapValues(80,40,60,70);
        double highlandShape = highlands * highlandHeight * Math.min((tA + hillHighlandsStart) * 1.5, 1);

        if (tA <= 0) return NaNCheck(highlandShape + hillShape);

        double tectonicFilter = Math.clamp(tA * 2.5, 0,1);
        double tectonicFilter1 = Math.clamp(tA * 1.2, 0,1);
        float powerCurve = 1.75F;

        double mountainsCore = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, x, z) + 1) / 2),0,1), 1.25);
        double mountains = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, x, z) + 1) / 2),0,1), powerCurve);
        double mountainNoise = Math.clamp(Math.pow(Math.abs(getC(this.shapers.mountains.noise, x,  z)), powerCurve),0,1);

        double detail = Math.abs(
                (1 - Math.abs(getC(this.shapers.mountains.detail, x, z)))
                - 0.5);

        double mountainDetail = detail * ((mountainsCore + mountains) / 2);

        double offset = 12;

        double mc1 = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, x+offset, z) + 1) / 2),0,1), 1.25);
        double m1 = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, x+offset, z) + 1) / 2),0,1), powerCurve);

        double mc2 = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, x-offset, z) + 1) / 2),0,1), 1.25);
        double m2 = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, x-offset, z) + 1) / 2),0,1), powerCurve);

        double mc3 = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, x, z+offset) + 1) / 2),0,1), 1.25);
        double m3 = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, x, z+offset) + 1) / 2),0,1), powerCurve);

        double mc4 = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, x, z-offset) + 1) / 2),0,1), 1.25);
        double m4 = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, x, z-offset) + 1) / 2),0,1), powerCurve);

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

        double rift = Math.clamp(Math.pow(Math.abs(continentalness - 0.2) / 0.1, 1.5), 0,1);

        double erodedMountainMap = NaNCheck(mountainEroded(alphaEroded, alphaCore, continentalness));
        double mountainMap = NaNCheck(mountain(alpha, alphaDetail,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double mountainCliffMap = NaNCheck(mountainSeaCliff(alpha, alphaDetail,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double divergentMap = divergent((mountains), mountainsCore, Math.pow(tA,3),surfaceOnly,rift);
        if (highlands > 0){
            double dA = Math.max(divergentMap, 0);
            double dB = Math.min(divergentMap, 0);
            divergentMap = (dA * highlandFilter) + dB;
        }
        divergentMap = NaNCheck(divergentMap);
        double transform = NaNCheck(transform(mountainsCore, 1 - detail, mountains, continentalness,Math.pow(tA,3),surfaceOnly));
        double terrainMap = terrainType.mapValuesWithInBetween(
                divergentMap,
                transform,

                erosionMapped(erosion,mountainMap,erodedMountainMap,0.3),
                erosionMapped(erosion,mountainCliffMap,erodedMountainMap,0.3),

                0

        ) * tectonicFilter;

        return terrainMap + NaNCheck(highlandShape + hillShape);


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

    private double transform(double mountains, double detail, double modifier, double continentalness, double tA, boolean surfaceOnly) {
        double faultPoint = 0.982;
        double tMultiplyFault = 1 / (1 - faultPoint);
        double fault = Math.pow(
                Math.max(
                        (tA - faultPoint) * tMultiplyFault,
                        0),
                3);
        double t = Math.clamp((1.5 * tA) - (modifier * 0.5), 0, 1);
        double terrain = (detail * 4) + (mountains * 4 + 6);
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
            double r = Math.max(riftFilter,0.3);

            DistortionSpline spline = new DistortionSpline(
                    new DistortionSpline.Spline()
                            .addPoint(0,0)
                            .addPoint(0.75F,  amp)
                            .addPoint(0.95F, (amp / 3) -(24 * r))
                            .addPoint(0.985F,-24 * r)
                            .addPoint(1.0F,-32 * r)
                            .addPoint(1.25F,-48 * r),
                    0,-48 * r
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
        double c = Math.clamp(Math.abs(continentalness) * 7, 0.0, 1.0);

        double seaDelta = continentalnessFade(continentalness,0.2,0.15,false);

        double amplitudeCore = 150 * c;
        amplitudeCore = seaDelta * amplitudeCore;

        double amplitude = 150 * c;
        amplitude = seaDelta * amplitude;

        return (alpha * amplitude) + (alphaCore * amplitudeCore);
    }
    private double mountain(double alpha, double alphaDetail, double alphaCore, double alphaNoise, double continentalness, double m, double[] mc) {
        double c = Math.clamp(Math.abs(continentalness) * 6, 0.0, 1.0);
        // Controls the height of the mountains (peaks / max height, not average)
        double amplitudeCore = 160 * c;
        double amplitude = 140 * c;
        double detailAmp = 20 * c;
        double noiseAmp = 16 * c;
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
        double noiseAmp = 16 * c;
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




    public Sampled samplerAt(int xPos, int zPos) {
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        double terrainA = this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);
        double terrainB = this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);
        double tectonicActivity = this.shapers.tectonicActivity.getValue(xPos / baseTectonicSize, 0, zPos / baseTectonicSize) * 2.5;
        double tA = 1 - Math.pow(tectonicActivity,2);
        double tA2 = 1 - Math.clamp((Math.abs(tectonicActivity) - 3), 0, 1);
        double continentalness = (this.shapers.continentalness.getValue(xContinental * baseContinentSize, 0, zPos * continentScale * baseContinentSize) * 2) + 0.2;
        double highlandsMap = sampleHighlands(xPos, zPos, continentalness);
        double erosion = sampleErosionMap(x,z);


        double tectonicFilter = Math.clamp(tA * 4, 0,1);
        double tectonicFilter1 = Math.clamp(tA * 1.5, 0,1);

        float powerCurve = 1.75F;

        float xM = xPos * mountainCoordsSize;
        float zM = zPos * mountainCoordsSize;
        double mountainsCore = Math.pow(Math.clamp(((getC(this.shapers.mountains.core, xM, zM) + 1) / 2),0,1), 1.25);
        double mountains = Math.pow(Math.clamp(((getC(this.shapers.mountains.shape, xM, zM) + 1) / 2),0,1), powerCurve);
        double mountainNoise = Math.pow(Math.abs(getC(this.shapers.mountains.noise, xM,  zM)), powerCurve);

        double detail = Math.abs(
                (1 - Math.abs(getC(this.shapers.mountains.detail, xM, zM)))
                        - 0.5 ) * 2;

        double alphaCore = mountainsCore * tectonicFilter;
        double alpha = (mountains) * tectonicFilter1;
        double alphaDetail = detail * tectonicFilter;
        double alphaNoise = mountainNoise * tectonicFilter;



        Sampled sampled = new Sampled(
                continentalness,
                erosion,
                tectonicActivity,
                alphaCore,
                alpha,
                alphaDetail,
                alphaNoise,
                highlandsMap * Math.clamp((tA + 3.0) * 1.5, 0,1),
                Math.abs(this.shapers.hills.getValue(xM, 0, zM)) * tA2,
                Math.max(Math.abs(this.shapers.terrainOffset.getValue(x, 0, z))
                        , Math.abs(this.shapers.terrainOffsetLarge.getValue(x, 0, z)))
                        + (Math.abs(this.shapers.terrainNoise.getValue(x, 0, z)) * 0.125),
                terrainA, terrainB,
                new TerrainType(terrainType(terrainA), terrainType(terrainB)),
                getC(this.shapers.badlands.filter,xPos,zPos),
                Math.abs(getC(this.shapers.waterBasins,xPos,zPos)),
                getC(this.climate.temperature, xPos / temperatureScale, zPos / temperatureScale),
                getC(this.climate.humidity, xPos / humidityScale, zPos / humidityScale),
                getC(this.climate.vegetationDensity, xPos, z),
                getC(this.climate.rockiness, xPos / rockinessScale, zPos / rockinessScale),
                getC(this.climate.weirdness, xPos / weirdnessScale, zPos / weirdnessScale),
                Math.clamp((getC(this.climate.magicalness, xPos, zPos) - 0.5) * 2,-1.5,1.5),
                new Sampled.River(
                        getC2(this.shapers.rivers.direction,xPos,zPos),
                        getC2(this.shapers.rivers.typeA,xPos,zPos),
                        getC2(this.shapers.rivers.typeB,xPos,zPos)
                )
        );
        this.updateChunkSampler(sampled, xPos, zPos);
        return sampled;
    }

    public Sampled.River getRiverSampledAt(int x, int z){
        return new Sampled.River(
                getC2(this.shapers.rivers.direction,x,z),
                getC2(this.shapers.rivers.typeA,x,z),
                getC2(this.shapers.rivers.typeB,x,z)
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
        Detailer detailer,
        PerlinNoise highlandsMap,
        PerlinNoise hills,
        PerlinNoise terrainOffset,
        PerlinNoise terrainOffsetLarge,
        PerlinNoise terrainNoise,
        PerlinNoise terrainTypeA,
        PerlinNoise terrainTypeB,
        PerlinNoise waterBasins,
        PerlinNoise basinFilter,
        PerlinNoise randomElevation,
        Cave cave,
        Badlands badlands,
        River rivers
        ){

        public record Mountain(
                PerlinNoise core,
                PerlinNoise shape,
                PerlinNoise detail,
                PerlinNoise noise
        ){}
        public record Cave(
                PerlinNoise base,
                PerlinNoise ballFilter,
                PerlinNoise pillars,
                PerlinNoise ball,
                PerlinNoise squiggleFilter,
                PerlinNoise squiggle,
                PerlinNoise ridgeFilter,
                PerlinNoise ridge,
                PerlinNoise veinFilter,
                PerlinNoise veins
        ){}

        public record Badlands(
                PerlinNoise filter,
                PerlinNoise step,
                PerlinNoise valleys,
                PerlinNoise hills
        ){}

        public record Detailer(
                PerlinNoise first,
                PerlinNoise second,
                PerlinNoise delta,
                PerlinNoise mask
        ){

            public double get(float x, float z){
                double a = first.getValue(x,0,z);
                double b = second.getValue(x,0,z);
                double d = getC2(delta,x,z);
                double m = getP2(delta,x,z);

                return Mth.lerp(d,a,b) * m;
            }

        }


        public record River(
                PerlinNoise direction,
                PerlinNoise typeA,
                PerlinNoise typeB
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
        double highlandsMap,
        double hills,
        double terrainOffset,
        double terrainTypeA,
        double terrainTypeB,
        TerrainType terrainType,
        double badlands,
        double waterBasins,
        double temperature,
        double humidity,
        double vegetationDensity,
        double rockyness,
        double weirdness,
        double magicalness,
        River river
        ){

        public record River(
                double direction,
                double typeA,
                double typeB
        ){}
    }


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
            PerlinNoise rockiness,
            PerlinNoise weirdness,
            PerlinNoise magicalness
    ){}


    public record Range(double min, double mean, double max){



        public static final MapCodec<Range> CODEC = RecordCodecBuilder.mapCodec(
                p_223591_ -> p_223591_.group(
                                Codec.DOUBLE.fieldOf("min").forGetter(Range::min),
                                Codec.DOUBLE.fieldOf("mean").forGetter(Range::mean),
                                Codec.DOUBLE.fieldOf("max").forGetter(Range::max)
                        )
                        .apply(p_223591_,Range::new)
        );

        public boolean accepts(double v){
            return v >= min && v <= max;
        }

        public double affinity(double v, boolean extremeNegative){


            double p;
            double a;

            if (v > mean){
                p = max - mean;
                a = v - mean;

            }else{
                p = mean - min;
                a = mean - v;

            }

            double n;
            if (p == 0){
                n = a;
            }else {
                n = (a / p);
            }

            if (!accepts(v)) {
                return (1-n) - (extremeNegative ? 1000 : 0);
            }

            return 1-n;

        }
    }

    public record Target(
            Range temperature,
            Range humidity,
            Range vegetationDensity,
            Range rockiness,
            Range continentalness,
            Range tectonicActivity,
            Range highlands,
            Range erosion,
            Range terrainHeight,
            Range weirdness,
            Range magicalness
    ){


        public static final MapCodec<Target> CODEC = RecordCodecBuilder.mapCodec(
                p_223591_ -> p_223591_.group(
                                Range.CODEC.fieldOf("temperature").forGetter(Target::temperature),
                                Range.CODEC.fieldOf("humidity").forGetter(Target::humidity),
                                Range.CODEC.fieldOf("vegetation_density").forGetter(Target::vegetationDensity),
                                Range.CODEC.fieldOf("rockiness").forGetter(Target::rockiness),
                                Range.CODEC.fieldOf("continentalness").forGetter(Target::continentalness),
                                Range.CODEC.fieldOf("tectonic_activity").forGetter(Target::tectonicActivity),
                                Range.CODEC.fieldOf("highlands").forGetter(Target::highlands),
                                Range.CODEC.fieldOf("erosion").forGetter(Target::erosion),
                                Range.CODEC.fieldOf("terrain_height").forGetter(Target::terrainHeight),
                                Range.CODEC.fieldOf("weirdness").forGetter(Target::weirdness),
                                Range.CODEC.fieldOf("magicalness").forGetter(Target::magicalness)
                                )
                        .apply(p_223591_, Target::new)
        );

        public static final double temperatureInfluence = 3;
        public static final double humidityInfluence = 1.75;
        public static final double vegetationDensityInfluence = 1.75;
        public static final double rockinessInfluence = 1.0;
        public static final double continentalnessInfluence = 1.75;
        public static final double tectonicActivityInfluence = 1.0;
        public static final double highlandsInfluence = 1;
        public static final double erosionInfluence = 1.25;
        public static final double terrainHeightInfluence = 0.25;
        public static final double weirdnessInfluence = 1.0;
        public static final double magicalnessInfluence = 1;
        public static final double TOTAL_MAX =
                temperatureInfluence + humidityInfluence
                        + vegetationDensityInfluence + rockinessInfluence
                        + continentalnessInfluence + tectonicActivityInfluence
                        + highlandsInfluence + erosionInfluence
                        + terrainHeightInfluence + weirdnessInfluence
                        + magicalnessInfluence;
        public Target(
                Range temperature,
                Range humidity,
                Range vegetationDensity,
                Range rockiness,
                Range continentalness,
                Range tectonicActivity,
                Range highlands,
                Range erosion,
                Range terrainHeight
        ){this(temperature,humidity,vegetationDensity,rockiness,continentalness,tectonicActivity,highlands,erosion,terrainHeight,FULL,MAGICALNESS_LOW);}



        public boolean test(TerrainParameters.Sampled parameters, double yLevel){
            boolean t = temperature.accepts(parameters.temperature);
            boolean h = humidity.accepts(parameters.humidity);
            boolean v = vegetationDensity.accepts(parameters.vegetationDensity);
            boolean r = rockiness.accepts(parameters.rockyness);
            boolean c = continentalness.accepts(parameters.continentalness);
            boolean tA = tectonicActivity.accepts(1 - Math.abs(parameters.tectonicActivity));
            boolean p = highlands.accepts(Math.pow(Math.clamp(parameters.highlandsMap, 0, 1), 5));
            boolean e = erosion.accepts(parameters.erosion);
            boolean height = terrainHeight.accepts(yLevel);
            return t && h && v && r && c && tA && p && e && height;
        }

        public double affinity(TerrainParameters.Sampled parameters, double yLevel, boolean extremeNegative){
            double t = temperature.affinity(parameters.temperature,extremeNegative) * temperatureInfluence;
            double h = humidity.affinity(parameters.humidity,extremeNegative) * humidityInfluence;
            double v = vegetationDensity.affinity(parameters.vegetationDensity,extremeNegative) * vegetationDensityInfluence;
            double r = rockiness.affinity(parameters.rockyness,extremeNegative) * rockinessInfluence;
            double c = continentalness.affinity(parameters.continentalness,extremeNegative) * continentalnessInfluence;
            double tA = tectonicActivity.affinity(1 - Math.abs(parameters.tectonicActivity),extremeNegative) * tectonicActivityInfluence;
            double p = highlands.affinity(Math.pow(Math.clamp(parameters.highlandsMap, 0, 1), 5),extremeNegative) * highlandsInfluence;
            double e = erosion.affinity(parameters.erosion,extremeNegative) * erosionInfluence;
            double height = Math.min(terrainHeight.affinity(yLevel,extremeNegative),1) * terrainHeightInfluence;
            height = height > 0.15 ? 1 : -5;
            double w = weirdness.affinity(parameters.weirdness,extremeNegative) * weirdnessInfluence;
            double m = magicalness.affinity(parameters.magicalness,extremeNegative) * magicalnessInfluence;
            double affinity = t + h + v + r + c + tA + p + e + height + w + m;
            if (!test(parameters,yLevel) && extremeNegative) return affinity -1000;

            return affinity;
        }

        public double affinity(TerrainParameters.Sampled parameters, BlockPos pos, boolean extremeNegative){
            return affinity(parameters,pos.getCenter().y,extremeNegative);
        }

    }

    public static BlockPos findSpawnPosition(Target points, TerrainParameters parameters, Level access) {
        SpawnFinder spawnFinder = new SpawnFinder(points, parameters,BlockPos.ZERO,access);
        return spawnFinder.result.location();
    }


    static class SpawnFinder {
        Result result;

        SpawnFinder(Target points, TerrainParameters parameters, BlockPos pos, Level level) {
            this.result = Result.of(pos,points.affinity(parameters.samplerAtCached(pos.getX(),pos.getZ()),pos.atY(Mth.floor(parameters.yLevelAtWithCache(pos.getX(),pos.getZ())) + 1),true));
            this.radialSearch(points, parameters, 512.0F, 32.0F,level);
            if (this.result.affinity < Target.TOTAL_MAX / 2){
                this.radialSearch(points, parameters, 4096.0F, 512.0F,level);
            }
            if (this.result.affinity < Target.TOTAL_MAX / 2){
                this.radialSearch(points,parameters,16384,2048,level);
            }
        }

        private void radialSearch(Target point, TerrainParameters parameters, float max, float min, Level level) {
            float f = 0.0F;
            float f1 = min;
            BlockPos blockpos = this.result.location();

            while (f1 <= max) {
                int x = blockpos.getX() + (int)(Math.sin(f) * f1);
                int z = blockpos.getZ() + (int)(Math.cos(f) * f1);
                Sampled sampled = parameters.samplerAtCached(x,z);
                int y = Mth.floor(parameters.yLevelAtWithCache(x,z) + 1);
                double affinity = point.affinity(sampled,y,true);

                if (affinity > this.result.affinity) {
                    this.result = Result.of(new BlockPos(x,y,z), affinity);
                    if (affinity > Target.TOTAL_MAX / 2){
                        break;
                    }
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

    public record InfoCache(Optional<Sampled> sampled, OptionalDouble yLevel){
        public boolean checkSampled(){
            return sampled.isPresent();
        }
        synchronized public boolean checkYLevel(){
            return yLevel.isPresent();
        }
        synchronized public InfoCache withYLevel(double y){
            return new InfoCache(this.sampled,OptionalDouble.of(y));
        }
        public InfoCache withSampled(Sampled sampled){
            return new InfoCache(Optional.of(sampled),this.yLevel);
        }

        public static InfoCache fromY(double y){
            return new InfoCache(Optional.empty(),OptionalDouble.of(y));
        }
        public static InfoCache fromSampled(Sampled sampled){
            return new InfoCache(Optional.of(sampled),OptionalDouble.empty());
        }

    }
    public record ChunkCache(InfoCache[] caches){
        public ChunkCache(){
            this(new InfoCache[16*16]);
        }
        synchronized public boolean validAt(int x, int z){
            return this.caches != null && this.caches[posIndex(x,z)] != null;
        }

        synchronized public ChunkCache with(int index, InfoCache cache){
            InfoCache[] caches = this.caches;
            caches[index] = cache;
            return new ChunkCache(caches);
        }

        public static ChunkCache fromInfoCache(int index, InfoCache cache){
            InfoCache[] caches =  new InfoCache[16*16];
            caches[index] = cache;
            return new ChunkCache(caches);
        }


    }
}