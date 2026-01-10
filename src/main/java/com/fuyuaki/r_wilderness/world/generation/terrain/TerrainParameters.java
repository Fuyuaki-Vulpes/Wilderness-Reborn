package com.fuyuaki.r_wilderness.world.generation.terrain;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.environment.TemperatureData;
import com.fuyuaki.r_wilderness.world.generation.Seed;
import com.fuyuaki.r_wilderness.world.generation.WildGeneratorSettings;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.DistortionSpline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

public class TerrainParameters {
    public static final Range POSITIVE = range(0,1.5);
    public static final Range NEGATIVE = range(-1.5,0);
    public static final Range MAGICALNESS_LOW = range(-5.5,0,0.15);
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


    private static final float baseContinentSize = 1.0F;
    private static final float baseTectonicSize = 1.5F;
    private static final float highlandsOffset = 1.5F;
    private static final float highlandsSize = 1.5F;
    private static final float terrainTypeScale = 2.0F;
    private static final float mountainCoordsSize = 0.6F;
    private static final float temperatureScale = 2.0F;
    private static final float humidityScale = 0.75F;
    private static final float rockinessScale = 1.0F;
    private static final float vegetationScale = 0.75F;
    private static final float weirdnessScale = 1.25F;
    public static final double HUMIDITY_BADLANDS_THRESHOLD = 0.15;
    public static final double BADLANDS_THRESHOLD = 0.65;
    private final float continentScale;
    private final float terrainScale;
    private final Seed seed;
    private final WildGeneratorSettings settings;
    public final Shapers shapers;
    public final Climate climate;
    public final PostPass postPass;

    private static final int CACHE_CLEAR_POINT = 6000;
    private static final int SURFACE_CACHE_CLEAR_POINT = 12000;
    private final Map<Long, ChunkCache> cache = new ConcurrentHashMap<>();
    private final Map<Long, ChunkCache> surfaceOnlyCache = new ConcurrentHashMap<>();

    private static final DistortionSpline BADLANDS_LARGE_HILLS = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-1.5,0)
                    .addPoint(0.0,0)
                    .addPoint(0.25,12)
                    .addPoint(0.4,16)
                    .addPoint(0.6,24)
                    .addPoint(0.85,32)
                    .addPoint(1.0,48)
                    .addPoint(2.0,48)
            ,0,48
    );
    private static final DistortionSpline BADLANDS_HILLS = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-1.5,0)
                    .addPoint(0.0,0)
                    .addPoint(0.25,8)
                    .addPoint(0.4,8)
                    .addPoint(0.6,16)
                    .addPoint(0.85,16)
                    .addPoint(1.0,24)
                    .addPoint(2.0,24)
            ,0,24
    );

    private static final DistortionSpline BADLANDS_STEP = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-2.0,4)
                    .addPoint(-1.5,4)
                    .addPoint(0.0,8)
                    .addPoint(1.5,12)
                    .addPoint(2.0,12)
            ,0,12
    );

    private static final DistortionSpline HIGHLANDS_CURVE_CONTIENT_FILTER = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-5,0)
                    .addPoint(0.0,0)
                    .addPoint(0.05F, 0)
                    .addPoint(0.25F, 1)
                    .addPoint(5.0F, 1),
            0,1
    );

    private static final DistortionSpline EROSION_DETAILER_SIZE = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-1.5,32)
                    .addPoint(-1.0,32)
                    .addPoint(-0.5F, 24)
                    .addPoint(0.0F, 16)
                    .addPoint(0.5F,12)
                    .addPoint(1.5F,4),
            52,6
    );
    private static final DistortionSpline TECTONIC_ACTIVITY_DETAILER = new DistortionSpline(
            new DistortionSpline.Spline()
                    .addPoint(-20.0F,0)
                    .addPoint(-7.0F,0)
                    .addPoint(-5.0F, 0.5)
                    .addPoint(-3.0,0.75)
                    .addPoint(-1.5,1.0)
                    .addPoint(1.5F,1.0),
            0,1
    );


    public TerrainParameters(Seed seed, WildGeneratorSettings settings) {
        this.seed = seed;
        this.settings = settings;
        this.continentScale = 1.0F;
        this.terrainScale = 1.0F;
        this.shapers = makeShapers(seed.fork().forkPositional(), settings);
        this.climate = makeClimate(seed.fork().forkPositional(), settings);
        this.postPass = postPass(seed.fork().forkPositional(),settings);

    }

    private PostPass postPass(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new PostPass(
                getShiftPass(randomSource, "x"),
                getShiftPass(randomSource, "z")
        );
    }

    private Shapers makeShapers(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Shapers(
                getShift(randomSource, "x"),
                getShift(randomSource, "z"),
                getContinentalness(randomSource, "continentalness"),
                getErosion(randomSource, "erosion"),
                getTectonicActivity(randomSource, "tectonic_activity"),
                new Shapers.Mountain(
                        getMountainsCore(randomSource, "mountain_core"),
                        getMountains(randomSource, "mountain_shape"),
                        getMountainDetails(randomSource, "mountain_detail"),
                        getMountainNoise(randomSource, "mountain_noise"),
                        getMountainCliffLike(randomSource, "mountain_cliff_like"),
                        getMountainCliffSharpness(randomSource, "mountain_cliff_sharpness")
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
                new Shapers.TerrainBeyond(
                        getMountainsCoreBeyond(randomSource, "beyond_mountain_core"),
                        getMountainsBeyond(randomSource, "beyond_mountain_shape"),
                        getMountainNoiseBeyond(randomSource, "beyond_mountain_noise"),
                        getMountainFilterBeyond(randomSource,"beyond_filter")
                ),
                new Shapers.Cave(
                        getCaveFilter(randomSource,"base",-7),
                        getCaveFilter(randomSource,"ball_filter",-8),
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
                        getBadlandsHills(randomSource,"badlands_hills"),
                        getBadlandsLargeHills(randomSource,"badlands_large_hills")
                ),
                new Shapers.River(
                        getRiverDirection(randomSource,"river_direction"),
                        getRiverType(randomSource,"river_type_a"),
                        getRiverType(randomSource,"river_type_b")
                ),
                new Shapers.Aquifers(
                        getAquiferBarrier(randomSource,"aquifer_barrier"),
                        getAquiferFloodedness(randomSource,"aquifer_floodedness"),
                        getAquiferSpread(randomSource,"aquifer_spread"),
                        getAquiferLava(randomSource,"aquifer_lava")

                )
        );
    }

    private PerlinNoise getAquiferBarrier(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,1);
    }
    private PerlinNoise getAquiferFloodedness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1);
    }
    private PerlinNoise getAquiferSpread(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,1);
    }
    private PerlinNoise getAquiferLava(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-1,1);
    }
    private PerlinNoise getRiverDirection(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,2);
    }
    private PerlinNoise getRiverType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,2);
    }

    private PerlinNoise getBadlandsHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1.25,1,2);
    }
    private PerlinNoise getBadlandsLargeHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.25,1,2);
    }
    private PerlinNoise getBadlandsFilter(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,1.25,1,2);
    }
    private PerlinNoise getBadlandsStep(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.25,1,2);
    }
    private PerlinNoise getBadlandsValleys(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.25,3);
    }

    private PerlinNoise getCaveFilter(PositionalRandomFactory randomSource, String name,int firstOctave) {
        return PerlinNoise.create(noiseRandom(name, randomSource),firstOctave,2,3);
    }
    private PerlinNoise getCavePillar(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-2,1);
    }
    private PerlinNoise getCaveBall(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,2,2);
    }
    private PerlinNoise getCaveSquiggle(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1,1);
    }
    private PerlinNoise getCaveRidge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-10,4,3);
    }
    private PerlinNoise getCaveVeins(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,4,8);
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


    private static double get(PerlinNoise noise, int x, int z){
        return noise.getValue(x,0,z) * 2;
    }
    private static double get(PerlinNoise noise, float x, float z){
        return noise.getValue(x,0,z) * 2;
    }
    private static double get(PerlinNoise noise, double x, double z){
        return noise.getValue(x,0,z) * 2;
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
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1.5);
    }
    private static double getP(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1.5);
    }
    private static double getP(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1.5);
    }


    private static double getN(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,0);
    }
    private static double getN(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,0);
    }
    private static double getN(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1.5,0);
    }



    private static double getC2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,1);
    }
    private static double getC2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,1);
    }
    private static double getC2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,1);
    }

    private static double getP2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1);
    }
    private static double getP2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1);
    }
    private static double getP2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,0,1);
    }


    private static double getN2(PerlinNoise noise, int x, int z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,0);
    }
    private static double getN2(PerlinNoise noise, float x, float z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,0);
    }
    private static double getN2(PerlinNoise noise, double x, double z){
        return Mth.clamp(noise.getValue(x,0,z) * 2,-1,0);
    }



    private PerlinNoise getTerrainType(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-11,10);
    }

    private PerlinNoise getTerrainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-4,1);
    }

    private PerlinNoise getTerrainOffsetLarge(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,1);
    }

    private PerlinNoise getTerrainOffset(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.5,1);
    }

    private PerlinNoise geHighlandsMap(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,4,5,5);
    }

    private PerlinNoise getHills(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,1.5);
    }

    private PerlinNoise getDetailer(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,5,5,5);
    }
    private PerlinNoise getDetailerDelta(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,10,10);
    }
    private PerlinNoise getDetailerMask(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,2,2,2);
    }

    private PerlinNoise getMountainsCore(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,1.5,1,2);
    }
    private PerlinNoise getMountains(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1.5,1,2);
    }
    private PerlinNoise getMountainNoise(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-3,1.5,1);
    }
    private PerlinNoise getMountainsCoreBeyond(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-8,1.5,1);
    }
    private PerlinNoise getMountainsBeyond(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-7,1.5,1);
    }
    private PerlinNoise getMountainNoiseBeyond(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,1.5);
    }
    private PerlinNoise getMountainFilterBeyond(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-9,2,1);
    }
    private PerlinNoise getMountainCliffLike(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-6,2,1,1);
    }
    private PerlinNoise getMountainCliffSharpness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,3);
    }
    private PerlinNoise getMountainDetails(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-5,1.5);
    }

    private PerlinNoise getTectonicActivity(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource), -13,10,6);
    }

    private PerlinNoise getErosion(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-12,2,1.5);
    }

    private PerlinNoise getContinentalness(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom(name, randomSource),-12,1.5,2);
    }
    private PerlinNoise getShift(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom("shift_" + name, randomSource),-4,1,1,1);
    }
    private PerlinNoise getShiftPass(PositionalRandomFactory randomSource, String name) {
        return PerlinNoise.create(noiseRandom("shift_" + name, randomSource),-5,3,1.5,1.5);
    }


    private Climate makeClimate(PositionalRandomFactory randomSource, WildGeneratorSettings settings) {
        return new Climate(
                PerlinNoise.create(noiseRandom("temperature", randomSource),-11,1,1),
                PerlinNoise.create(noiseRandom("humidity", randomSource),-10,1,1),
                PerlinNoise.create(noiseRandom("vegetation_density", randomSource),-10,1,1,1),
                PerlinNoise.create(noiseRandom("rockiness", randomSource),-9,1,1),
                PerlinNoise.create(noiseRandom("weirdness", randomSource),-9,1,1),
                PerlinNoise.create(noiseRandom("magicalness", randomSource),-9,1,1)
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
    public double yLevelAt(int xPos, int zPos, boolean surfaceOnly) {
        return yLevelAt(xPos,zPos,surfaceOnly,true);
    }
    public double yLevelAt(int xPos, int zPos, boolean surfaceOnly,boolean cache) {
        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        TerrainType terrainType = new TerrainType(
                terrainType(this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale)),
                terrainType(this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale))
        );
        double continentalness = get(this.shapers.continentalness, xContinental * baseContinentSize, zContinental * baseContinentSize) + 0.2;
        double seaSample = sampleOceans(continentalness);

        double erosionShaper = sampleErosionMap(x, z);

        double terrainSample = sampleTerrain(x, z, continentalness, erosionShaper);

        double randomElevation = sampleRandomElevation(x, z, continentalness, erosionShaper);

        double highlands = sampleHighlands(xPos, zPos, continentalness);

        double tectonicSample = get(this.shapers.tectonicActivity, xPos / baseTectonicSize, zPos / baseTectonicSize);
        double mountainLayer = sampleMountains(x, z, highlands, tectonicSample, continentalness, terrainType, erosionShaper, surfaceOnly);
        double detailerLayer = sampleDetailer(x, z, continentalness, tectonicSample, erosionShaper);
        double terrainBeyond = sampleBeyond(continentalness,erosionShaper,x,z);

        // Adds the different terrain Layers together.
        double yOff = terrainSample + mountainLayer  + terrainBeyond;

        yOff = postWithBadlands(x, z, erosionShaper, continentalness, yOff) + detailerLayer + randomElevation;

        double y = samplePRivers(x, z, continentalness, tectonicSample, yOff + seaSample);
        if (cache) {
            if (surfaceOnly) {
                updateChunkYSurface(y, xPos, zPos);
            } else {
                updateChunkY(y, xPos, zPos);
            }
        }
        return y;
    }

    private double sampleBeyond(double continentalness, double erosionShaper, float x, float z) {
        double height = 0;
        double mountainFilter = Math.clamp((get(this.shapers.terrainBeyond.filter,x,z) - 0.5) * 1.5,-1,1);
        double mountainHeight = 0;
        if (mountainFilter > 0){

            double shiftX = getC2(this.shapers.shiftX, x, z) * 8;
            double shiftZ = getC2(this.shapers.shiftZ, x, z) * 8;

            double mountainCore = Math.abs(getC2(this.shapers.terrainBeyond.core,x + shiftX,z + shiftZ)) * mountainFilter;
            double mountainShape = Math.abs(getC2(this.shapers.terrainBeyond.shape,x + shiftX,z + shiftZ)) * mountainFilter;
            double mountainNoise = Math.abs(getC2(this.shapers.terrainBeyond.noise,x + shiftX,z + shiftZ)) * mountainFilter;

            mountainHeight = mountainBeyond(mountainCore,mountainShape,mountainNoise,erosionShaper,continentalness);
        }

        height = mountainHeight;

        return height;
    }

    private double mountainBeyond(double core, double mountain, double noise, double erosion, double continentalness) {
        double e = Math.clamp((erosion + 1) / 2,0,1);
        // Controls the height of the mountains (peaks / max height, not average)
        double coreAmp = 128 * (e * 0.25 + 0.75);
        double mountainAmp = 32 * (e * 0.75 + 0.25);
        double noiseAmp = 8 * (e * 0.9 + 0.1);
        //TOTAL = 176



        double mountains = Math.clamp(mountain,0,1);
        double noiseMountain = Math.clamp(noise,0,1.5);



        double seaDelta = Math.clamp(Math.abs((continentalness + 0.3) * 3),0.25,1);

        double seaFilter = Math.clamp(Math.abs(continentalness + 0.35) * 5,0,1);

        double height = ((mountains * mountainAmp * seaDelta)+ (core * coreAmp * seaDelta) + (noiseMountain * noiseAmp));

        return height * seaFilter;
    }

    private double sampleHighlands(int xPos, int zPos, double continentalness) {


        double x = xPos / baseTectonicSize / highlandsSize;
        double z = zPos / baseTectonicSize / highlandsSize;
        double highlands = get(this.shapers.highlandsMap, x, z);
        return Math.clamp(
                Math.pow(
                        (highlands - highlandsOffset) * 2,
                        3)
                * HIGHLANDS_CURVE_CONTIENT_FILTER.at(continentalness)
                ,0,1);
    }
    private double unclampedHighlands(int xPos, int zPos, double continentalness) {

        double x = xPos / baseTectonicSize / highlandsSize;
        double z = zPos / baseTectonicSize / highlandsSize;
        double highlands = get(this.shapers.highlandsMap, x, z);
        return Math.pow(
                (highlands - highlandsOffset) * 2,
                3)
                * HIGHLANDS_CURVE_CONTIENT_FILTER.at(continentalness);
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
        double continentalness = get(this.shapers.continentalness, xContinental * baseContinentSize, zContinental * baseContinentSize) + 0.2;
        double seaSample = sampleOceans(continentalness);
        double terrainSample = sampleTerrain(x,z, continentalness,1);

        double randomElevation = sampleRandomElevation(x,z,continentalness,0);
        double tectonicSample = get(this.shapers.tectonicActivity, xPos / baseTectonicSize, zPos / baseTectonicSize);
        double mountainLayer = sampleMountains(x, z, 0, tectonicSample, continentalness, terrainType, 1, false);

       return terrainSample + mountainLayer + randomElevation + seaSample;
    }

    private double samplePRivers(float x, float z, double continentalness, double tectonicSample, double y) {
        if (continentalness < -0.2) return y;

        double r = Math.pow(Math.abs(Math.clamp(getC(this.shapers.waterBasins,x,z) * 3,-1,1)),1.15);
        if (r >= 2) return y;

        double tFilter = Math.clamp(2 - (Math.abs(tectonicSample) * 2),0,1);
        DistortionSpline spline =
                new DistortionSpline(
                        new DistortionSpline.Spline()
                                .addPoint(-1,Math.min(52,y))
                                .addPoint(0,Math.min(52,y))
                                .addPoint(0.4,Math.min(64,y))
                                .addPoint(1.5,y)
                                .addPoint(5,y)
                        ,Math.min(52,y),y
                );

        return Math.min(y,Mth.lerp(tFilter,spline.at(r),y));
    }

    private double postWithBadlands(float x, float z, double erosionShaper, double continentalness, double yOffset) {

        double humidity = getC(this.climate.humidity, x, z);

        if (humidity > HUMIDITY_BADLANDS_THRESHOLD) return yOffset;
        double filterH = Math.clamp(-(humidity - HUMIDITY_BADLANDS_THRESHOLD) * 4,0,1);

        double badlands = getC(this.shapers.badlands.filter,x,z);
        if (badlands < BADLANDS_THRESHOLD) return yOffset;
        double continentFilter = Math.clamp(continentalness * 8,0,1);
        double filter = Math.clamp((badlands - BADLANDS_THRESHOLD) * 10,0,1) * filterH * continentFilter ;
        double step = getC(this.shapers.badlands.step,x,z) * filter;





        double e = Math.clamp((erosionShaper + 1) / 2 * 5,0,1);
        double stepPost = Math.floor(BADLANDS_STEP.at(step)) * e;

        double hills = BADLANDS_HILLS.at(Math.abs(getC(this.shapers.badlands.hills,x,z)));
        double hillsLarge = BADLANDS_LARGE_HILLS.at(Math.abs(getC(this.shapers.badlands.largeHills,x,z)));

        double yPost = yOffset + hillsLarge;
        double heightOffset = yPost;
        if (stepPost >= 1){
            int yMod = Math.floorMod(Mth.floor(yPost), (int) stepPost);
            heightOffset = yPost - yMod;

        }
        if (heightOffset > 0){
            double valleysNoise = Math.abs(getC2(this.shapers.badlands.valleys,x,z));
            double valleys = Math.clamp((1 - Math.pow((1-valleysNoise),3)) * 3,0,1) / 2 + 0.5;
            heightOffset *= valleys;

        }
        return Mth.lerp(filter,yOffset,heightOffset + hills);
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
        double ridge = Math.abs(this.shapers.cave.ridge.getValue(x, y * 1.5, z)) * 3;
        double squiggle = Math.abs(this.shapers.cave.squiggle.getValue(x, y * 1.25, z)) * 3;
        double vein = Math.abs(this.shapers.cave.veins.getValue(x, y * 1.25, z)) * 1.5;

        double ballCave = ball - ballFilter - baseFilter - 0.15 - filter - ballCaveFilter;
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



        double size = EROSION_DETAILER_SIZE.at(erosion);

        DistortionSpline continentalSizeBias = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-2.5F,size)
                        .addPoint(-1.5F,size)
                        .addPoint(-0.5,size * 0.5)
                        .addPoint(0,size * 0.25)
                        .addPoint(0.05,size)
                        .addPoint(2.5F,size),
                size,size
        );

        double dScale = continentalSizeBias.at(continentalness);
        double dScaleT = TECTONIC_ACTIVITY_DETAILER.at(tectonicActivity);

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
        double c = Math.pow(Math.clamp((continentalness + 0.05) * 20,0,1),5);
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
        return Math.clamp((continentalness + 0.1) * extremity,0,1) * terrain;
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
    private double sampleMountains(float xPos,
                                   float zPos,
                                   double highlands,
                                   double tectonicActivity,
                                   double continentalness,
                                   TerrainType terrainType,
                                   double erosion,
                                   boolean surfaceOnly){

        double tA = 1 - (Math.pow(tectonicActivity,2) * 0.75);
        double hillHighlandsStart = 5.0;
        if (tectonicActivity <= -hillHighlandsStart) return 0;


        double continentalFilter = Math.clamp((continentalness * 20),0,1);

        float x = xPos * mountainCoordsSize;
        float z = zPos * mountainCoordsSize;
        double hills = 1-Math.abs(getC2(this.shapers.hills, x, z));

        DistortionSpline hillErosionBasedSize = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1.5,42)
                        .addPoint(-0.75,42)
                        .addPoint(-0.5F, 32)
                        .addPoint(0.0F, 24)
                        .addPoint(0.5F,12)
                        .addPoint(0.75F,6)
                        .addPoint(1.5F,6),
                42,0
        );
        double hillSize = hillErosionBasedSize.at(erosion);
        double tA2 = Math.clamp((tectonicActivity + hillHighlandsStart), 0, 1);
        double hillShape = tA2 * hills * hillSize * continentalFilter;
        double highlandHeight = terrainType.mapValues(80,40,60,70);
        double highlandShape = highlands * highlandHeight * tA2;

        double cliff = 0;

        if (highlands > 0.15 && tA <=0){
            cliff = calculateCliffs(xPos,zPos,erosion) * highlands;
        }

        if (tA <= 0) return NaNCheck(highlandShape + hillShape + cliff);

        cliff = calculateCliffs(xPos,zPos,erosion) * Math.max(tA,highlands);

        double shiftX = getC2(this.shapers.shiftX, x, z) * 24;
        double shiftZ = getC2(this.shapers.shiftZ, x, z) * 24;

        double tectonicFilter = Math.clamp(tA * 1.5, 0,1);

        double mountainsCore = Math.abs(getC2(this.shapers.mountains.core, x + shiftX,z + shiftZ));
        double mountains = Math.abs(getC2(this.shapers.mountains.shape, x + shiftX,z + shiftZ));
        double mountainNoise = getC(this.shapers.mountains.noise, x + shiftX,z + shiftZ);

        double detail = 1 - Math.abs(getC2(this.shapers.mountains.detail, x + shiftX,z + shiftZ));

        double mountainDetail = detail * ((mountainsCore + mountains) / 2);

        double offset = 12;

        double mc1 = Math.abs(getC2(this.shapers.mountains.core, x + shiftX + offset, z + shiftZ));
        double m1 = Math.abs(getC2(this.shapers.mountains.shape, x + shiftX + offset, z + shiftZ));

        double mc2 = Math.abs(getC2(this.shapers.mountains.core, x + shiftX - offset, z + shiftZ));
        double m2 = Math.abs(getC2(this.shapers.mountains.shape, x + shiftX - offset, z + shiftZ));

        double mc3 = Math.abs(getC2(this.shapers.mountains.core, x + shiftX,z + shiftZ + offset));
        double m3 = Math.abs(getC2(this.shapers.mountains.shape, x + shiftX,z + shiftZ + offset));

        double mc4 = Math.abs(getC2(this.shapers.mountains.core, x + shiftX,z + shiftZ - offset));
        double m4 = Math.abs(getC2(this.shapers.mountains.shape, x + shiftX,z + shiftZ - offset));

        double[] mc = new double[4];
        mc[0] = mc1 + m1;
        mc[1] = mc2 + m2;
        mc[2] = mc3 + m3;
        mc[3] = mc4 + m4;



        double ridges = 0.8D + (0.2D * detail);

        double alphaCore = mountainsCore * tectonicFilter * ridges;
        double alphaCoreEroded = mountainsCore * tectonicFilter;
        double alpha = mountains * tA * ridges;
        double alphaEroded = mountains * tA;
        double alphaDetail = mountainDetail * tectonicFilter;
        double alphaDetailSharp = detail * tectonicFilter;
        double alphaNoise = mountainNoise * tectonicFilter;


        double erodedMountainMap = NaNCheck(mountainEroded(alphaEroded, alphaCoreEroded, continentalness));
        double mountainMap = NaNCheck(mountain(alpha, alphaDetail,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double mountainCliffMap = NaNCheck(mountainSeaCliff(alpha, alphaDetailSharp,alphaCore,alphaNoise, continentalness,mountains + mountainsCore,mc));
        double divergentMap = NaNCheck(divergent(mountains, mountainsCore, Math.pow(tA,5),surfaceOnly,continentalness, tectonicActivity));
        double transform = NaNCheck(transform(mountainsCore, detail, mountains, Math.pow(tA,5),continentalness));
        double terrainMap = terrainType.mapValuesWithInBetween(
                divergentMap + cliff,
                transform,

                erosionMapped(erosion,mountainMap,erodedMountainMap,0.3) + cliff,
                erosionMapped(erosion,mountainCliffMap,erodedMountainMap,0.3) + cliff,

                0

        ) * tectonicFilter;

        return terrainMap + NaNCheck(highlandShape + hillShape);


    }

    private double calculateCliffs(float xPos, float zPos, double erosion) {
        int cliffHeights = 24;

        double map = getP2(this.shapers.mountains.cliffLike,xPos,zPos);
        double eFilter = Math.clamp((-erosion + 1) / 2,0,1);

        double sharpness = getC2(this.shapers.mountains.cliffSharpness,xPos,zPos);
        double sharpCliff = Math.pow(Math.min(map * 10,1),2);
        double sharpnessFilter = (sharpness + 1) / 2;

        double cliffs = Mth.lerp(sharpnessFilter,map,sharpCliff) * eFilter;

        return cliffs * cliffHeights;
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

    private double transform(double mountain, double detail, double hill, double tA, double continentalness) {
        double c = 1- Math.clamp(-continentalness * 3,0,1);
        double faultPoint = 0.982;
        double tMultiplyFault = 1 / (1 - faultPoint);
        double fault = Math.pow(
                Math.max(
                        (tA - faultPoint) * tMultiplyFault,
                        0),
                2);
        double t = Math.clamp((1.5 * tA), 0, 1);
        double terrain = Math.max((detail * 8) + (mountain * 24) + (hill * 12) + 42 - (c*64),0);
        double faultTerrain = (-terrain - 32) * fault;
        return terrain * t + faultTerrain;
    }

    private double divergent(double mountains, double core, double tA, boolean surfaceOnly,double continentalness, double tectonicActivity) {
        double bias = tectonicActivity > 0 ? 1 : -1;
        double amp = (32 + (mountains * 12) + (core * 12) + (24 * bias)) * (1- Math.clamp(-continentalness * 10,0,1));
        if (surfaceOnly) {
            DistortionSpline spline = new DistortionSpline(
                    new DistortionSpline.Spline()
                            .addPoint(0, 0)
                            .addPoint(0.75F, amp)
                            .addPoint(0.95F, (amp * 2) - 24)
                            .addPoint(0.985F, -64)
                            .addPoint(1.0F, -128)
                            .addPoint(1.25F, -128),
                    0, -128
            );
            return spline.at(tA);
        }
        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(0, 0)
                        .addPoint(0.75F, amp)
                        .addPoint(0.95F, (amp  * 2) - 24)
                        .addPoint(0.985F, -128)
                        .addPoint(1.0F, -192)
                        .addPoint(1.25F, -192),
                0, -192
        );
        return spline.at(tA);
    }

    private double mountainEroded(double alpha, double alphaCore, double continentalness) {
        double c = Math.clamp(Math.abs(continentalness) * 7, 0.15, 1.0);

        double amplitudeCore = 192 * c;
        double amplitude = 16 * c;

        return (alpha * amplitude) + (alphaCore * amplitudeCore);
    }

    private double mountain(double alpha, double alphaDetail, double alphaCore, double alphaNoise, double continentalness, double m, double[] mc) {
        // Controls the height of the mountains (peaks / max height, not average)
        double coreAmp = 160;
        double mountainAmp = 140;
        double detailAmp = 20;
        double noiseAmp = 16;
        //TOTAL = 352
        double cHeight = Math.max(-continentalness,0) * 80;

        double xMp = Math.abs(m - mc[0]);
        double xMn = Math.abs(m - mc[1]);
        double zMp = Math.abs(m - mc[2]);
        double zMn = Math.abs(m - mc[3]);
        double slopeX = Math.max(xMp,xMn);
        double slopeZ = Math.max(zMp,zMn);
        double slope = Math.sqrt(slopeX * slopeX + slopeZ * slopeZ) * 0.5;

        double ridgeErosionFilter = Math.clamp(1 - alphaDetail,0,1);

        double detail = Math.clamp(alphaDetail - (slope * ridgeErosionFilter),0,1.5);
        double mountains = Math.clamp(alpha - (slope * 0.3),0,1);
        double noise = Math.clamp(alphaNoise * (0.5 + slope),0,1.5);



        double seaDelta = Math.clamp(Math.abs(continentalness * 5),0.25,1);
        double seaDelta2 = Math.clamp(Math.abs(continentalness * 5),0.5,1);
        double seaFilter = Math.clamp(continentalness * 10,0,1);

        double height = ((mountains * mountainAmp * seaDelta) + (detail * detailAmp * seaFilter) + (alphaCore * coreAmp * seaDelta2) + (noise * noiseAmp));



        return height - cHeight;
    }
    private double mountainSeaCliff(double alpha, double alphaDetail, double alphaCore, double alphaNoise, double continentalness, double m, double[] mc) {
        double c;
        double cM;
        double cD;
        if (continentalness > 0.0){
            c = Math.clamp(continentalness * 5,0,1);
            cM = Math.pow(Math.clamp(continentalness * 10,0.25,1),2);
            cD = Math.pow(Math.clamp(continentalness * 30,0,1),3);
        }else{
            c = Math.clamp(-continentalness * 2,0,1);
            cM = Math.clamp(-continentalness * 5,0.25,1);
            cD = 0;
        }

        // Controls the height of the mountains (peaks / max height, not average)
        double coreAmp = 160 * c;
        double mountainAmp = 140 * cM;
        double detailAmp = 20 * (cD * 0.5 + 0.5);
        double noiseAmp = 16;
        //TOTAL = 352

        double xMp = Math.abs(m - mc[0]);
        double xMn = Math.abs(m - mc[1]);
        double zMp = Math.abs(m - mc[2]);
        double zMn = Math.abs(m - mc[3]);
        double slopeX = Math.max(xMp,xMn);
        double slopeZ = Math.max(zMp,zMn);
        double slope = Math.sqrt(slopeX * slopeX + slopeZ * slopeZ) * 0.75;
        double cHeight = Math.max(-continentalness,0) * 20;

        if (continentalness < 0.0){
            slope = Math.pow(Math.clamp(slope,0,1), 1.25);
        }
        double ridgeErosionFilter = Math.clamp(1-alphaDetail,0,1);

        double detail = Math.clamp(alphaDetail - (slope * ridgeErosionFilter),0,1.5);
        double mountains = Math.clamp(alpha - (slope * 0.3),0,1);
        double noise = Math.clamp(alphaNoise * (0.5 + slope),0,1.5);

        return (mountains * mountainAmp) + (detail * detailAmp) + (alphaCore * coreAmp) + (noise * noiseAmp) - cHeight;
    }

    private double sampleOceans(double continentalness){
        int seaBottom = 12;
        int median = 64;
        int continentalTop = 120;

        DistortionSpline spline = new DistortionSpline(
                new DistortionSpline.Spline()
                        .addPoint(-1.5F,seaBottom)
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



    public Environment environment(double x, double z){
        double xContinental = x * continentScale;
        double zContinental = z * continentScale;
        double continentalness = get(this.shapers.continentalness, xContinental * baseContinentSize, zContinental * baseContinentSize) + 0.2;
        double humidity = getC(this.climate.humidity, x / humidityScale, z / humidityScale);

        return new Environment(
                (float) getC(this.climate.vegetationDensity, x / vegetationScale, z / vegetationScale),
                (float) humidity,
                (float) getC(this.climate.temperature, x / temperatureScale, z / temperatureScale),
                (float) continentalness
        );
    }

    public Sampled samplerAt(int xPos, int zPos) {

        float x = xPos * terrainScale;
        float z = zPos * terrainScale;
        float xContinental = xPos * continentScale;
        float zContinental = zPos * continentScale;
        double terrainA = this.shapers.terrainTypeA.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);
        double terrainB = this.shapers.terrainTypeB.getValue((xContinental / baseTectonicSize) / terrainTypeScale, 0, (zContinental / baseTectonicSize) / terrainTypeScale);

        double tectonicActivity = get(this.shapers.tectonicActivity, xPos / baseTectonicSize, zPos / baseTectonicSize);

        double tA = 1 - Math.pow(tectonicActivity,2);
        double tA2 = Math.clamp((tectonicActivity + 5), 0, 1);
        double continentalness = get(this.shapers.continentalness, xContinental * baseContinentSize, zContinental * baseContinentSize) + 0.2;

        double highlandsMap = unclampedHighlands(xPos, zPos, continentalness);
        double erosion = sampleErosionMap(x,z);


        double tectonicFilter = Math.clamp(tA * 4, 0,1);
        double tectonicFilter1 = Math.clamp(tA * 1.5, 0,1);

        float xM = xPos * mountainCoordsSize;
        float zM = zPos * mountainCoordsSize;
        double mountainsCore = Math.abs(getC2(this.shapers.mountains.core, xM, zM));
        double mountains = Math.abs(getC2(this.shapers.mountains.shape, xM, zM));
        double mountainNoise = getC2(this.shapers.mountains.noise, xM,  zM);

        double detail = 1 - Math.abs(getC2(this.shapers.mountains.detail, xM, zM));

        double alphaCore = mountainsCore * tectonicFilter;
        double alpha = (mountains) * tectonicFilter1;
        double alphaDetail = detail * tectonicFilter;
        double alphaNoise = mountainNoise * tectonicFilter;
        double badlandsFilter = getC(this.shapers.badlands.filter,xPos,zPos);
        double humidity = getC(this.climate.humidity, xPos / humidityScale, zPos / humidityScale);
        double filterH = Math.clamp(-(humidity - HUMIDITY_BADLANDS_THRESHOLD) * 4,0,1);
        double continentFilter = Math.clamp(continentalness * 8,0,1);
        double badlands = Math.clamp((badlandsFilter - BADLANDS_THRESHOLD) * 10,0,1) * filterH * continentFilter ;

        Sampled sampled = new Sampled(
                continentalness,
                erosion,
                tectonicActivity,
                alphaCore,
                alpha,
                alphaDetail,
                alphaNoise,
                highlandsMap,
                Math.pow(Math.abs(getC(this.shapers.hills, x, z)),1.25) * tA2,
                Math.max(Math.abs(this.shapers.terrainOffset.getValue(x, 0, z))
                        , Math.abs(this.shapers.terrainOffsetLarge.getValue(x, 0, z)))
                        + (Math.abs(this.shapers.terrainNoise.getValue(x, 0, z)) * 0.125),
                terrainA, terrainB,
                new TerrainType(terrainType(terrainA), terrainType(terrainB)),
                badlands,
                Math.abs(getC(this.shapers.waterBasins,xPos,zPos)),
                getC(this.climate.temperature, xPos / temperatureScale, zPos / temperatureScale),
                humidity,
                getC(this.climate.vegetationDensity, xPos / vegetationScale, zPos / vegetationScale),
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
        PerlinNoise shiftX,
        PerlinNoise shiftZ,
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
        TerrainBeyond terrainBeyond,
        Cave cave,
        Badlands badlands,
        River rivers,
        Aquifers aquifers
        ){

        public record Mountain(
                PerlinNoise core,
                PerlinNoise shape,
                PerlinNoise detail,
                PerlinNoise noise,
                PerlinNoise cliffLike,
                PerlinNoise cliffSharpness
        ){}

        public record TerrainBeyond(
                PerlinNoise core,
                PerlinNoise shape,
                PerlinNoise noise,
                PerlinNoise filter
        ){}
        public record Cave(
                PerlinNoise base,
                PerlinNoise ballFilter,
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
                PerlinNoise hills,
                PerlinNoise largeHills
        ){}
        public record Aquifers(
                PerlinNoise barrier,
                PerlinNoise fluidLevelFloodednessNoise,
                PerlinNoise fluidLevelSpreadNoise,
                PerlinNoise lavaNoise
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

    public record Environment(
            float vegetationDensity,
            float humidity,
            float temperature,
            float continentalness
    ){
        public float temperatureInDegrees(){
            DistortionSpline spline = new DistortionSpline(
                    new DistortionSpline.Spline()
                    .addPoint(-1.5,TemperatureData.ENVIRONMENT_COLDEST)
                            .addPoint(0,TemperatureData.ENVIRONMENT_MEDIAN)
                            .addPoint(1.5,TemperatureData.ENVIRONMENT_HOTTEST),
                    -1.5F,1.5F
            );
            return (float) spline.at(temperature);
        }

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
    public record PostPass(
            PerlinNoise x,
            PerlinNoise z
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

        public static final double temperatureInfluence = 3.0;
        public static final double humidityInfluence = 1.75;
        public static final double vegetationDensityInfluence = 1.75;
        public static final double rockinessInfluence = 1.0;
        public static final double continentalnessInfluence = 2.75;
        public static final double tectonicActivityInfluence = 1.0;
        public static final double highlandsInfluence = 1;
        public static final double erosionInfluence = 2.0;
        public static final double terrainHeightInfluence = 0.5;
        public static final double weirdnessInfluence = 1.0;
        public static final double magicalnessInfluence = 1.0;
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