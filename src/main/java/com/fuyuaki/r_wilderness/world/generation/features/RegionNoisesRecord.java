package com.fuyuaki.r_wilderness.world.generation.features;

import com.fuyuaki.r_wilderness.world.generation.noise.Cellular2D;
import com.fuyuaki.r_wilderness.world.generation.noise.FastNoiseLite;
import com.fuyuaki.r_wilderness.world.generation.noise.Noise2D;
import com.fuyuaki.r_wilderness.world.generation.noise.OpenSimplex2D;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionPos;
import net.minecraft.server.level.ServerLevel;

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
        NoiseRecord plateauTableCarve,
        NoiseRecord plateauRiverCarve,
        NoiseRecord mountains,
        NoiseRecord riverSway,
        NoiseRecord riverRandom,
        NoiseRecord tectonicIntensity
) {

    public static RegionNoisesRecord createFor(ServerLevel level, long seed) {
        return new RegionNoisesRecord(
                NoiseRecord.of(
                        simplex(seed, "debug", 100.0f, 1), RegionFloatMap.Resolution.R_1024
                ), NoiseRecord.of(
                        simplex(seed, "volcanicness", 500.0f, 1).scaled(0, 1), RegionFloatMap.Resolution.R_1024
                ), NoiseRecord.of(
                        simplex(seed, "buoyancy_bias", 1024.0f, 1).fractalType(FastNoiseLite.FractalType.FBm), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "plateau", 1400.0f, 1).fractalType(FastNoiseLite.FractalType.FBm).fractalLacunarity(3.5f), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "atmosphere_pressure", 3200.0f, 1), RegionFloatMap.Resolution.R_128
                ), NoiseRecord.of(
                        simplex(seed, "temperature", 5000.0f, 1).octaves(2), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "erodability", 3000.0f, 1).octaves(3), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "vegetation", 1600.0f, 1).octaves(2), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "humidity", 1300.0f, 1), RegionFloatMap.Resolution.R_256
                ), NoiseRecord.of(
                        simplex(seed, "rockiness", 900.0f, 1).octaves(3), RegionFloatMap.Resolution.R_512
                ), NoiseRecord.of(
                        simplex(seed, "magic", 1100.0f, 1).octaves(3), RegionFloatMap.Resolution.R_512
                ), NoiseRecord.of(
                        cellular(seed, "plateau_table_carve", 200.0f, 1).distanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean)
                                .warped(simplex(seed, "plateau_table_warp", 100.0f, 80)), RegionFloatMap.Resolution.R_512
                ), NoiseRecord.of(
                        simplex(seed, "plateau_river_carve", 300.0f, 1).fractalOctaves(2).fractalGain(0.14f).ridged(), RegionFloatMap.Resolution.R_512
                ), NoiseRecord.of(
                        cellular(seed, "mountains", 1900.0f, 1).distanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq)
                                .warped(simplex(seed, "mountains_warp", 500.0f, 80)), RegionFloatMap.Resolution.R_512
                ),NoiseRecord.of(
                simplex(seed, "river_sway",128.0f,1).octaves(2), RegionFloatMap.Resolution.R_512
                ),NoiseRecord.of(
                simplex(seed, "river_random",32.0f,1).octaves(2), RegionFloatMap.Resolution.R_512
                ),NoiseRecord.of(
                simplex(seed, "tectonic_intensity",2048.0f,1).octaves(2), RegionFloatMap.Resolution.R_512
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


    public static class NoiseRecord {
        private final Noise2D noise;
        private final int resolution;

        private NoiseRecord(Noise2D noise, int resolution) {
            this.noise = noise;
            this.resolution = resolution;

        }
        public static NoiseRecord of(Noise2D noise, int resolution) {
            return new NoiseRecord(noise, resolution);
        }

        public int getResolution() {
            return resolution;
        }

        public Noise2D getNoise() {
            return noise;
        }

        public RegionFloatMap generate(int index, RegionPos position){
            return RegionFloatMap.createMap(this.getNoise(),position,new RegionFloatMap(index,this.getResolution(),this.getNoise()));
        }
    }
}
