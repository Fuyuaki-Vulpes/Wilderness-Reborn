package com.fuyuaki.r_wilderness.world.generation.features;

import com.fuyuaki.r_wilderness.world.generation.features.plates.RegionPlates;
import com.fuyuaki.r_wilderness.world.generation.noise.*;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;

public record RegionNoisesRecord(
        NoiseRecord debug,
        NoiseRecord volcanicness,
        NoiseRecord buoyancyBias,
        NoiseRecord plateauFilter,
        NoiseRecord atmospherePressure,
        NoiseRecord temperature,
        NoiseRecord erodability,
        NoiseRecord vegetation,
        NoiseRecord humidity,
        NoiseRecord rockiness,
        NoiseRecord magicalness,
        NoiseRecord plateauCanyonCarve,
        NoiseRecord plateauBadlandCarve,
        NoiseRecord hills,
        NoiseRecord riverSway,
        NoiseRecord riverRandom,
        NoiseRecord riverRandom2,
        NoiseRecord tectonicIntensity,
        NoiseRecord mountainPeaks,
        NoiseRecord peakFilter,
        NoiseRecord mountains,
        NoiseRecord artifacts,
        NoiseRecord pilarArtifacts,
        NoiseRecord islandModulator,
        NoiseRecord stressModulator,
        NoiseRecord mountainChunk
) {

    private static final int CONTINENTAL = RegionPlates.MAJOR_PLATE_SIZE;

    public static RegionNoisesRecord createFor(long seed) {
        return new RegionNoisesRecord(
                NoiseRecord.of(
                        simplex(seed, "debug", 100.0f, 1)
                ), NoiseRecord.of(
                        simplex(seed, "volcanicness", 512.0f, 1).octaves(2)
        ), NoiseRecord.of(
                        simplex(seed, "buoyancy_bias", 800.0f, 1).fractalType(FastNoiseLite.FractalType.FBm)
        ), NoiseRecord.of(
                        simplex(seed, "plateau", 1400.0f, 1).fractalType(FastNoiseLite.FractalType.FBm).fractalLacunarity(3.5f)
        ), NoiseRecord.of(
                        simplex(seed, "atmosphere_pressure", CONTINENTAL * 1.75f, 1)
        ), NoiseRecord.of(
                        simplex(seed, "temperature", CONTINENTAL * 1.75f, 1).octaves(2)
        ), NoiseRecord.of(
                        simplex(seed, "erodability", CONTINENTAL * 0.75f, 1).octaves(2)
        ), NoiseRecord.of(
                        simplex(seed, "vegetation", CONTINENTAL * 0.25f, 1).octaves(2)
        ), NoiseRecord.of(
                        simplex(seed, "humidity", CONTINENTAL * 0.5f, 1)
        ), NoiseRecord.of(
                        simplex(seed, "rockiness", 512.0f, 1).octaves(2)
        ), NoiseRecord.of(
                        simplex(seed, "magic", 512.0f, 1).octaves(2)
        ), NoiseRecord.of(
                        cellular(seed, "canyon_carver", 256.0f, 1).distanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq)
        ), NoiseRecord.of(
                        simplex(seed, "badlands_carver", 256.0f, 1).octaves(2).fractalOctaves(2).fractalGain(0.14f).ridged()
        ), NoiseRecord.of(
                        cellular(seed, "hills", 512.0f, 1).distanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq)
        ),NoiseRecord.of(
                simplex(seed, "river_sway",128.0f,1).octaves(2)
        ),NoiseRecord.of(
                simplex(seed, "river_random",32.0f,1).octaves(2)
        ),NoiseRecord.of(
                simplex(seed, "river_random2",16.0f,1).octaves(2)
        ),NoiseRecord.of(
                simplex(seed, "tectonic_intensity",1280.0f,1).octaves(2)
        ), NoiseRecord.of(
                cellular(seed, "peaks", 32.0f, 1).distanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq).scaled(0,1)
        ),NoiseRecord.of(
                simplex(seed, "peak_filter",64.0f,1).octaves(2).ridged().scaled(0,1)
        ),NoiseRecord.of(
                simplex(seed, "mountains",128.0f,1).octaves(4).ridged().scaled(0,1)
        ), NoiseRecord.of(
                cellular(seed, "artifacts", 64.0f, 1).jitter(1.7f).distanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq).scaled(0,1)
        ),NoiseRecord.of(
                simplex(seed, "pillar_artifacts",64.0f,1).octaves(2).fractalOctaves(2).fractalGain(0.1f)
        ),NoiseRecord.of(
                simplex(seed, "island_modulator",520f,1).octaves(3)
        ),NoiseRecord.of(
                simplex(seed, "stress_modulator",612f,1).octaves(3).scaled(0,1)
        ),NoiseRecord.of(
                simplex(seed, "mountain_chunk",160.0f,1).scaled(0.0f,1.0f).fractalGain(0.5f).fractalWeightedStrength(1.0f).fractalType(FastNoiseLite.FractalType.Ridged).fractalOctaves(3).octaves(3).scaled(0.0f,1.0f)
        )
        );
    }



    private static OpenSimplex2D baseSimplex(long seed, float wavelength, float amplitude){
        return new OpenSimplex2D(seed)
                .spread(1/wavelength)
                .scaled(-amplitude,amplitude);
    }

    private static OpenSimplex2D simplex(long seed, long hash, float wavelength, float amplitude){
        return baseSimplex(seed ^ hash, wavelength, amplitude);
    }
    private static OpenSimplex2D simplex(long seed, String hash, float wavelength, float amplitude){
        long hashedString = 7L;
        int length = hash.length();
        for (int i = 0; i < length; i++) {
            hashedString = 31L * hashedString + hash.charAt(i);
        }

        return baseSimplex(seed ^ hashedString, wavelength, amplitude);
    }
    private static Cellular2D cellular(long seed, String hash, float wavelength, float amplitude){
        long hashedString = 7L;
        int length = hash.length();
        for (int i = 0; i < length; i++) {
            hashedString = 31L * hashedString + hash.charAt(i);
        }

        return baseCellular(seed ^ hashedString, wavelength, amplitude);
    }

    private static Cellular2D baseCellular(long seed, float wavelength, float amplitude){
        return new Cellular2D(seed)
                .spread(1/wavelength)
                .scaled(-amplitude,amplitude);
    }

    //3D


    private static OpenSimplex3D baseSimplex3d(long seed, float wavelength, float amplitude){
        return new OpenSimplex3D(seed)
                .spread(1/wavelength)
                .scaled(-amplitude,amplitude);
    }

    private static OpenSimplex3D simplex3d(long seed, long hash, float wavelength, float amplitude){
        return baseSimplex3d(seed ^ hash, wavelength, amplitude);
    }
    private static OpenSimplex3D simplex3d(long seed, String hash, float wavelength, float amplitude){
        long hashedString = 7L;
        int length = hash.length();
        for (int i = 0; i < length; i++) {
            hashedString = 31L * hashedString + hash.charAt(i);
        }

        return baseSimplex3d(seed ^ hashedString, wavelength, amplitude);
    }
    private static Cellular3D cellular3d(long seed, String hash, float wavelength, float amplitude){
        long hashedString = 7L;
        int length = hash.length();
        for (int i = 0; i < length; i++) {
            hashedString = 31L * hashedString + hash.charAt(i);
        }

        return baseCellular3d(seed ^ hashedString, wavelength, amplitude);
    }

    private static Cellular3D baseCellular3d(long seed, float wavelength, float amplitude){
        return new Cellular3D(seed)
                .spread(1/wavelength)
                .scaled(-amplitude,amplitude);
    }

    public static class NoiseRecord {
        private final Noise2D noise;

        private NoiseRecord(Noise2D noise) {
            this.noise = noise;

        }
        public static NoiseRecord of(Noise2D noise) {
            return new NoiseRecord(noise);
        }


        public Noise2D getNoise() {
            return noise;
        }

        public RegionFloatMap generate(RegionFloatMap map, RegionPos position){
            return RegionFloatMap.createMap(this.getNoise(),position,new RegionFloatMap(map.getIndex(),map.getResolution(),this.getNoise()));
        }
    }
}
