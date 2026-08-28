package com.fuyuaki.r_wilderness.world.generation.region.reference;

import com.fuyuaki.r_wilderness.world.generation.features.RegionNoisesRecord;
import com.fuyuaki.r_wilderness.world.generation.region.data.RegionFloatMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class RegionLayers {

    private final RegionNoisesRecord record;
    private RegionFloatMap plateWeights;
    private RegionFloatMap plateEdges;
    private RegionFloatMap plateElevationOffsets;
    private RegionFloatMap volcanicness;
    private RegionFloatMap tectonicStress;
    private RegionFloatMap buoyancy;
    private RegionFloatMap distToParent;
    private RegionFloatMap age;
    private RegionFloatMap swelling;
    private RegionFloatMap plateaus;
    private RegionFloatMap elevation;
    private RegionFloatMap atmospherePressure;
    private RegionFloatMap temperature;
    private RegionFloatMap erodability;
    private RegionFloatMap vegetation;
    private RegionFloatMap humidity;
    private RegionFloatMap rockiness;
    private RegionFloatMap magicalness;
    private RegionFloatMap heightmapBasic;
    private RegionFloatMap continentalness;
    private RegionFloatMap hilliness;
    private RegionFloatMap rivers;

    public RegionLayers(
            RegionNoisesRecord record,
            RegionFloatMap plateWeights,
            RegionFloatMap plateEdges,
            RegionFloatMap plateElevationOffsets,
            RegionFloatMap volcanicness,
            RegionFloatMap tectonicStress,
            RegionFloatMap buoyancy,
            RegionFloatMap distToParent,
            RegionFloatMap age,
            RegionFloatMap swelling,
            RegionFloatMap plateaus,
            RegionFloatMap elevation,
            RegionFloatMap atmospherePressure,
            RegionFloatMap temperature,
            RegionFloatMap erodability,
            RegionFloatMap vegetation,
            RegionFloatMap humidity,
            RegionFloatMap rockiness,
            RegionFloatMap magicalness,
            RegionFloatMap heightmapBasic,
            RegionFloatMap continentalness,
            RegionFloatMap hilliness,
            RegionFloatMap rivers
    ) {
        this.record = record;
        this.plateWeights = plateWeights;
        this.plateEdges = plateEdges;
        this.plateElevationOffsets = plateElevationOffsets;
        this.volcanicness = volcanicness;
        this.tectonicStress = tectonicStress;
        this.buoyancy = buoyancy;
        this.distToParent = distToParent;
        this.age = age;
        this.swelling = swelling;
        this.plateaus = plateaus;
        this.elevation = elevation;
        this.atmospherePressure = atmospherePressure;
        this.temperature = temperature;
        this.erodability = erodability;
        this.vegetation = vegetation;
        this.humidity = humidity;
        this.rockiness = rockiness;
        this.magicalness = magicalness;
        this.heightmapBasic = heightmapBasic;
        this.continentalness = continentalness;
        this.hilliness = hilliness;
        this.rivers = rivers;
    }


    public static RegionLayers makeEmpty(RegionNoisesRecord record) {
        return new RegionLayers(
                record,
                new RegionFloatMap(RegionLayerTypes.PLATE_WEIGHTS, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_EDGES, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_ELEVATION_OFFSETS, null),
                new RegionFloatMap(RegionLayerTypes.VOLCANICNESS, record.volcanicness().getNoise()),
                new RegionFloatMap(RegionLayerTypes.TECTONIC_STRESS, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_BUOYANCY, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_DIST_TO_PARENT, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_AGE, null),
                new RegionFloatMap(RegionLayerTypes.PLATE_SWELLING, null),
                new RegionFloatMap(RegionLayerTypes.PLATEAUS, null),
                new RegionFloatMap(RegionLayerTypes.ELEVATION, null),
                new RegionFloatMap(RegionLayerTypes.ATMOSPHERE_PRESSURE, null),
                new RegionFloatMap(RegionLayerTypes.TEMPERATURE, null),
                new RegionFloatMap(RegionLayerTypes.ERODABILITY, null),
                new RegionFloatMap(RegionLayerTypes.VEGETATION, null),
                new RegionFloatMap(RegionLayerTypes.HUMIDITY, null),
                new RegionFloatMap(RegionLayerTypes.ROCKINESS, null),
                new RegionFloatMap(RegionLayerTypes.MAGICALNESS, null),
                new RegionFloatMap(RegionLayerTypes.HEIGHTMAP_BASIC, null),
                new RegionFloatMap(RegionLayerTypes.CONTINENTALNESS, null),
                new RegionFloatMap(RegionLayerTypes.HILLS, null),
                new RegionFloatMap(RegionLayerTypes.RIVER_INFLUENCE, null)
        );
    }

    public List<RegionFloatMap> values() {
        return List.of(
                plateWeights, plateEdges, plateElevationOffsets, volcanicness,
                tectonicStress, buoyancy, distToParent, age, swelling,
                plateaus, elevation, atmospherePressure, temperature,
                erodability, vegetation, humidity, rockiness, magicalness,
                heightmapBasic, continentalness, hilliness,rivers
        );
    }

    public RegionFloatMap[] asArray() {
        return new RegionFloatMap[] {
                plateWeights, plateEdges, plateElevationOffsets, volcanicness,
                tectonicStress, buoyancy, distToParent, age, swelling,
                plateaus, elevation, atmospherePressure, temperature,
                erodability, vegetation, humidity, rockiness, magicalness,
                heightmapBasic, continentalness, hilliness,rivers
        };
    }

    public Stream<RegionFloatMap> stream() {
        return values().stream();
    }

    public RegionFloatMap get(int index) {
        return asArray()[index];
    }

    public RegionFloatMap getOfIndex(int index) {

        return switch (index){
            case 0 -> this.plateWeights;
            case 1 -> this.plateEdges;
            case 2 -> this.plateElevationOffsets;
            case 3 -> this.volcanicness;
            case 4 -> this.tectonicStress;
            case 5 -> this.buoyancy;
            case 6 -> this.distToParent;
            case 7 -> this.age;
            case 8 -> this.swelling;
            case 9 -> this.plateaus;
            case 10 -> this.elevation;
            case 11 -> this.atmospherePressure;
            case 12 -> this.temperature;
            case 13 -> this.erodability;
            case 14 -> this.vegetation;
            case 15 -> this.humidity;
            case 16 -> this.rockiness;
            case 17 -> this.magicalness;
            case 18 -> this.heightmapBasic;
            case 19 -> this.continentalness;
            case 20 -> this.hilliness;
            case 21 -> this.rivers;
            default -> throw new IllegalArgumentException("Index " + index + " out of range for Layers!");
        };
    }

    public static RegionLayers read(RegionNoisesRecord record, DataInputStream inputStream) throws IOException{
        return new RegionLayers(
                record,
                RegionFloatMap.read(inputStream, RegionLayerTypes.PLATE_WEIGHTS, null),
                RegionFloatMap.read(inputStream, RegionLayerTypes.PLATE_EDGES, null),
                RegionFloatMap.read(inputStream, RegionLayerTypes.PLATE_ELEVATION_OFFSETS, null),
                RegionFloatMap.read(inputStream, RegionLayerTypes.VOLCANICNESS, null),
                RegionFloatMap.read(inputStream, RegionLayerTypes.TECTONIC_STRESS, null),
                RegionFloatMap.read(inputStream, RegionLayerTypes.PLATE_BUOYANCY, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.PLATE_DIST_TO_PARENT, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.PLATE_AGE, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.PLATE_SWELLING, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.PLATEAUS, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.ELEVATION, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.ATMOSPHERE_PRESSURE, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.TEMPERATURE, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.ERODABILITY, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.VEGETATION, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.HUMIDITY, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.ROCKINESS, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.MAGICALNESS, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.HEIGHTMAP_BASIC, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.CONTINENTALNESS, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.HILLS, null),
                RegionFloatMap.read(inputStream,RegionLayerTypes.RIVER_INFLUENCE, null)
        );
    }

    public void write(DataOutputStream output) throws IOException {
        plateWeights.write(RegionLayerTypes.PLATE_WEIGHTS,output);
        plateEdges.write(RegionLayerTypes.PLATE_EDGES,output);
        plateElevationOffsets.write(RegionLayerTypes.PLATE_ELEVATION_OFFSETS,output);
        volcanicness.write(RegionLayerTypes.VOLCANICNESS,output);
        tectonicStress.write(RegionLayerTypes.TECTONIC_STRESS,output);
        buoyancy.write(RegionLayerTypes.PLATE_BUOYANCY,output);
        distToParent.write(RegionLayerTypes.PLATE_DIST_TO_PARENT,output);
        age.write(RegionLayerTypes.PLATE_AGE,output);
        swelling.write(RegionLayerTypes.PLATE_SWELLING,output);
        plateaus.write(RegionLayerTypes.PLATEAUS,output);
        elevation.write(RegionLayerTypes.ELEVATION,output);
        atmospherePressure.write(RegionLayerTypes.ATMOSPHERE_PRESSURE,output);
        temperature.write(RegionLayerTypes.TEMPERATURE,output);
        erodability.write(RegionLayerTypes.ERODABILITY,output);
        vegetation.write(RegionLayerTypes.VEGETATION,output);
        humidity.write(RegionLayerTypes.HUMIDITY,output);
        rockiness.write(RegionLayerTypes.ROCKINESS,output);
        magicalness.write(RegionLayerTypes.MAGICALNESS,output);
        heightmapBasic.write(RegionLayerTypes.HEIGHTMAP_BASIC,output);
        continentalness.write(RegionLayerTypes.CONTINENTALNESS,output);
        hilliness.write(RegionLayerTypes.HILLS,output);
        rivers.write(RegionLayerTypes.RIVER_INFLUENCE,output);
    }

    public RegionNoisesRecord record() {
        return record;
    }

    public RegionFloatMap getPlateWeights() {
        return plateWeights;
    }

    public void setPlateWeights(RegionFloatMap plateWeights) {
        this.plateWeights = plateWeights;
    }

    public RegionFloatMap getPlateEdges() {
        return plateEdges;
    }

    public void setPlateEdges(RegionFloatMap plateEdges) {
        this.plateEdges = plateEdges;
    }

    public RegionFloatMap getPlateElevationOffsets() {
        return plateElevationOffsets;
    }

    public void setPlateElevationOffsets(RegionFloatMap plateElevationOffsets) {
        this.plateElevationOffsets = plateElevationOffsets;
    }


    public RegionFloatMap getVolcanicness() {
        return volcanicness;
    }

    public void setVolcanicness(RegionFloatMap volcanicness) {
        this.volcanicness = volcanicness;
    }

    public RegionFloatMap getTectonicStress() {
        return tectonicStress;
    }

    public void setTectonicStress(RegionFloatMap tectonicStress) {
        this.tectonicStress = tectonicStress;
    }

    public RegionFloatMap getBuoyancy() {
        return buoyancy;
    }

    public void setBuoyancy(RegionFloatMap buoyancy) {
        this.buoyancy = buoyancy;
    }

    public RegionFloatMap getDistToParent() {
        return distToParent;
    }

    public RegionFloatMap getAge() {
        return age;
    }

    public void setAge(RegionFloatMap age) {
        this.age = age;
    }

    public RegionFloatMap getSwelling() {
        return swelling;
    }

    public void setSwelling(RegionFloatMap swelling) {
        this.swelling = swelling;
    }

    public void setDistToParent(RegionFloatMap distToParent) {
        this.distToParent = distToParent;
    }

    public RegionFloatMap getPlateaus() {
        return plateaus;
    }

    public void setPlateaus(RegionFloatMap plateaus) {
        this.plateaus = plateaus;
    }



    public RegionFloatMap getElevation() {
        return elevation;
    }

    public void setElevation(RegionFloatMap elevation) {
        this.elevation = elevation;
    }

    public RegionFloatMap getHeightmapBasic() {
        return heightmapBasic;
    }

    public void setHeightmapBasic(RegionFloatMap heightmapBasic) {
        this.heightmapBasic = heightmapBasic;
    }

    public RegionFloatMap getAtmospherePressure() {
        return atmospherePressure;
    }

    public void setAtmospherePressure(RegionFloatMap atmospherePressure) {
        this.atmospherePressure = atmospherePressure;
    }

    public RegionFloatMap getTemperature() {
        return temperature;
    }

    public void setTemperature(RegionFloatMap temperature) {
        this.temperature = temperature;
    }

    public RegionFloatMap getErodability() {
        return erodability;
    }

    public void setErodability(RegionFloatMap erodability) {
        this.erodability = erodability;
    }

    public RegionFloatMap getVegetation() {
        return vegetation;
    }

    public void setVegetation(RegionFloatMap vegetation) {
        this.vegetation = vegetation;
    }

    public RegionFloatMap getHumidity() {
        return humidity;
    }

    public void setHumidity(RegionFloatMap humidity) {
        this.humidity = humidity;
    }

    public RegionFloatMap getRockiness() {
        return rockiness;
    }

    public void setRockiness(RegionFloatMap rockiness) {
        this.rockiness = rockiness;
    }

    public RegionFloatMap getMagicalness() {
        return magicalness;
    }

    public void setMagicalness(RegionFloatMap magicalness) {
        this.magicalness = magicalness;
    }


    public RegionFloatMap getContinentalness() {
        return continentalness;
    }

    public void setContinentalness(RegionFloatMap continentalness) {
        this.continentalness = continentalness;
    }

    public RegionFloatMap getHilliness() {
        return hilliness;
    }

    public void setHilliness(RegionFloatMap hilliness) {
        this.hilliness = hilliness;
    }

    public RegionFloatMap getRivers() {
        return rivers;
    }

    public void setRivers(RegionFloatMap rivers) {
        this.rivers = rivers;
    }

}
