package com.fuyuaki.r_wilderness.world.generation.biome;

import com.fuyuaki.r_wilderness.util.math.FloatRange;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record BiomeTargetProfile(
        FloatRange temperature,
        FloatRange erodability,
        FloatRange vegetation,
        FloatRange humidity,
        FloatRange rockiness,
        FloatRange continentalness,
        Optional<FloatRange> volcanicness,
        Optional<FloatRange> magicalness,
        Optional<List<BiomePlacementTags>> tags,
        Optional<List<BiomePlacementTags>> disallowedTags

) {

    public static final Codec<BiomeTargetProfile> CODEC = RecordCodecBuilder.create(
            p_223591_ -> p_223591_.group(
                            FloatRange.CODEC.fieldOf("temperature").forGetter(BiomeTargetProfile::temperature),
                            FloatRange.CODEC.fieldOf("erodability").forGetter(BiomeTargetProfile::erodability),
                            FloatRange.CODEC.fieldOf("vegetation").forGetter(BiomeTargetProfile::vegetation),
                            FloatRange.CODEC.fieldOf("humidity").forGetter(BiomeTargetProfile::humidity),
                            FloatRange.CODEC.fieldOf("rockiness").forGetter(BiomeTargetProfile::rockiness),
                            FloatRange.CODEC.fieldOf("continentalness").forGetter(BiomeTargetProfile::continentalness),
                            FloatRange.CODEC.optionalFieldOf("volcanicness").forGetter(BiomeTargetProfile::volcanicness),
                            FloatRange.CODEC.optionalFieldOf("magicalness").forGetter(BiomeTargetProfile::magicalness),
                    BiomePlacementTags.CODEC.listOf().optionalFieldOf("tags").forGetter(BiomeTargetProfile::tags),
                    BiomePlacementTags.CODEC.listOf().optionalFieldOf("disallowed_tags").forGetter(BiomeTargetProfile::disallowedTags)
                    )
                    .apply(p_223591_, BiomeTargetProfile::new)
    );

    public BiomeTargetProfile(
            FloatRange temperature,
            FloatRange erodability,
            FloatRange vegetation,
            FloatRange humidity,
            FloatRange rockiness,
            FloatRange continentalness,
            List<BiomePlacementTags> tags,
            List<BiomePlacementTags> disallowedTags

    ){
        this(temperature,erodability,vegetation,humidity,rockiness,continentalness,Optional.empty(),Optional.empty(),Optional.of(tags),Optional.of(disallowedTags));
    }
    public BiomeTargetProfile(
            FloatRange temperature,
            FloatRange erodability,
            FloatRange vegetation,
            FloatRange humidity,
            FloatRange rockiness,
            FloatRange continentalness

    ){
        this(temperature,erodability,vegetation,humidity,rockiness,continentalness,Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty());
    }

    public static Builder builder() {
        return new Builder();
    }

    public float computeAffinity(BiomeSampleProfile profile){

        float affinity = 0.0f;

        affinity += temperature.affinity(profile.temperature());
        affinity += erodability.affinity(profile.erodability());
        affinity += vegetation.affinity(profile.vegetation());
        affinity += humidity.affinity(profile.humidity());
        affinity += rockiness.affinity(profile.rockiness());
        affinity += continentalness.affinityHarsh(profile.continentalness());
        affinity += volcanicness.orElse(FloatRange.MILD).affinity(profile.volcanicness());
        affinity += magicalness.orElse(FloatRange.of(-1.5f,-0.5f,0.0f)).affinity(profile.magicalness());

        if (this.tags.isPresent() && !this.tags.get().isEmpty()){
            float matchingTagTotal = 0;
            float missingTags = 0;
            List<BiomePlacementTags> t = tags.orElse(List.of());
            for (BiomePlacementTags tag: profile.tags()){
                if (t.contains(tag)) {
                    matchingTagTotal ++;
                   }
            }for (BiomePlacementTags tag: t){
                if (!profile.tags().contains(tag)) {
                    missingTags ++;
                   }
            }
            if (t.contains(BiomePlacementTags.CAVE) && !profile.tags().contains(BiomePlacementTags.CAVE)){
                affinity -= 999;
            }

            affinity += 5 * (matchingTagTotal / t.size());
            affinity -= 5 * (missingTags / t.size());
        }else{
            int l = profile.tags().size();
            affinity -= l * 0.25f;
        }
        if (this.disallowedTags.isPresent() && !this.disallowedTags.get().isEmpty()){
            float negativeTagTotal = 0;
            for (BiomePlacementTags tag : profile.tags()){
                if (disallowedTags.orElse(List.of()).contains(tag)) {
                    negativeTagTotal++;
                }
            }
            affinity -= 20 * (negativeTagTotal / this.disallowedTags.get().size());
        }

        return affinity;
    }

    public static class Builder{
        private FloatRange temperature;
        private FloatRange erodability;
        private FloatRange vegetation;
        private FloatRange humidity;
        private FloatRange rockiness;
        private FloatRange continentalness;
        private Optional<FloatRange> volcanicness;
        private Optional<FloatRange> magicalness;

        private Optional<List<BiomePlacementTags>> tags;
        private Optional<List<BiomePlacementTags>> disallowedTags;

        private static final FloatRange IMPOSSIBLE = new FloatRange(-99999,-90000,-80000);
        private static final float AROUND_VALUE_RANGE = 0.25f;
        private Builder(FloatRange temperature, FloatRange erodability, FloatRange vegetation, FloatRange humidity, FloatRange rockiness, FloatRange continentalness,Optional<FloatRange> volcanicness, Optional<FloatRange> magicalness,Optional<List<BiomePlacementTags>> tags, Optional<List<BiomePlacementTags>> disallowedTags) {
            this.temperature = temperature;
            this.erodability = erodability;
            this.vegetation = vegetation;
            this.humidity = humidity;
            this.rockiness = rockiness;
            this.continentalness = continentalness;
            this.volcanicness = volcanicness;
            this.magicalness = magicalness;
            this.tags = tags;
            this.disallowedTags = disallowedTags;
        }
        private Builder(FloatRange temperature, FloatRange erodability, FloatRange vegetation, FloatRange humidity, FloatRange rockiness, FloatRange continentalness) {
            this.temperature = temperature;
            this.erodability = erodability;
            this.vegetation = vegetation;
            this.humidity = humidity;
            this.rockiness = rockiness;
            this.continentalness = continentalness;
            this.volcanicness = Optional.empty();
            this.magicalness = Optional.empty();
            this.tags = Optional.empty();
            this.disallowedTags = Optional.empty();
        }

        public Builder() {
            this.temperature = FloatRange.ANY;
            this.erodability = FloatRange.ANY;
            this.vegetation = FloatRange.ANY;
            this.humidity = FloatRange.ANY;
            this.rockiness = FloatRange.ANY;
            this.continentalness = FloatRange.ANY;
            this.volcanicness = Optional.empty();
            this.magicalness = Optional.empty();
            this.tags = Optional.empty();
            this.disallowedTags = Optional.empty();
        }

        public Builder impossible() {
            return new Builder(IMPOSSIBLE,IMPOSSIBLE,IMPOSSIBLE,IMPOSSIBLE,IMPOSSIBLE,IMPOSSIBLE);
        }
        public static Builder averageTemplate() {
            return new Builder(FloatRange.MILD,FloatRange.MILD,FloatRange.MILD,FloatRange.MILD,FloatRange.MILD,FloatRange.MILD);
        }
        public static Builder caveTemplate() {
            return new Builder().sparse().dry().temperate().inland().tagged(BiomePlacementTags.CAVE);
        }

        public static Builder hotGrasslandTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .sparse()
                    .withHumidity(-0.5f, 0.5f)
                    .soil()
                    .inland();
        }

        public static Builder grasslandTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .sparse()
                    .withHumidity(-0.5f, 0.5f)
                    .soil()
                    .inland();
        }

        public static Builder coldGrasslandTemplate(){
            return new Builder()
                    .cold()
                    .anyErosion()
                    .withHumidity(-0.5f, 0.5f)
                    .inland();
        }

        public static Builder warmOceanTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .ocean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }

        public static Builder oceanTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .ocean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }

        public static Builder coldOceanTemplate(){
            return new Builder()
                    .cold()
                    .anyErosion()
                    .ocean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }


        public static Builder deepWarmOceanTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .deepOcean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }

        public static Builder deepOceanTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .deepOcean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }

        public static Builder deepColdOceanTemplate(){
            return new Builder()
                    .cold()
                    .anyErosion()
                    .deepOcean()
                    .tagged(BiomePlacementTags.BELOW_SEA_LEVEL);
        }

        public static Builder hotGroveTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .grove()
                    .withHumidity(-0.5f, 0.25f)
                    .soil()
                    .inland();
        }

        public static Builder groveTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .grove()
                    .withHumidity(-0.5f, 0.25f)
                    .soil()
                    .inland();
        }

        public static Builder coldGroveTemplate(){
            return new Builder()
                    .cold()
                    .anyErosion()
                    .grove()
                    .withHumidity(-0.5f, 0.25f)
                    .soil()
                    .inland();
        }

        public static Builder hotForestTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .lush()
                    .soil()
                    .inland();
        }

        public static Builder forestTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .lush()
                    .soil()
                    .inland();
        }

        public static Builder coldForestTemplate(){
            return new Builder()
                    .cold()
                    .anyErosion()
                    .lush()
                    .soil()
                    .inland();
        }

        public static Builder hotWetlandTemplate(){
            return new Builder()
                    .hot()
                    .anyErosion()
                    .withVegetation(0.0f, 0.5f)
                    .wet()
                    .soil()
                    .inland();
        }

        public static Builder wetlandTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .withVegetation(0.0f, 0.5f)
                    .wet()
                    .soil()
                    .inland();
        }

        public static Builder warmShoreTemplate(){
            return new Builder()
                    .warm()
                    .anyErosion()
                    .barren()
                    .soil()
                    .shore();
        }

        public static Builder shoreTemplate(){
            return new Builder()
                    .temperate()
                    .anyErosion()
                    .barren()
                    .soil()
                    .shore();
        }

        public static Builder rockyShoreTemplate(){
            return new Builder()
                    .temperate()
                    .withErodability(-1.0f, 0.1f)
                    .barren()
                    .rugged()
                    .shore();
        }

        public static Builder tropicalIslandTemplate(){
            return new Builder()
                    .warm()
                    .withErodability(-1.0f, -0.25f)
                    .withVegetation(0.0f, 0.75f)
                    .island();
        }

        public static Builder islandTemplate(){
            return new Builder()
                    .temperate()
                    .withErodability(-1.0f, -0.25f)
                    .island();
        }

        public static Builder coldIslandTemplate(){
            return new Builder()
                    .cold()
                    .withErodability(-1.0f, -0.25f)
                    .island();
        }


        public Builder withTemperature(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withTemperature(min,mean,max);
        }
        public Builder withTemperature(float min, float mean, float max){
            this.temperature = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withErodability(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withErodability(min,mean,max);
        }
        public Builder withErodability(float min, float mean, float max){
            this.erodability = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withVegetation(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withVegetation(min,mean,max);
        }
        public Builder withVegetation(float min, float mean, float max){
            this.vegetation = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withHumidity(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withHumidity(min,mean,max);
        }
        public Builder withHumidity(float min, float mean, float max){
            this.humidity = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withRockiness(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withRockiness(min,mean,max);
        }
        public Builder withRockiness(float min, float mean, float max){
            this.rockiness = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withContinentalness(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withContinentalness(min,mean,max);
        }
        public Builder withContinentalness(float min, float mean, float max){
            this.continentalness = new FloatRange(min,mean,max);
            return this;
        }

        public Builder withVolcanicness(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withVolcanicness(min,mean,max);
        }
        public Builder withVolcanicness(float min, float mean, float max){
            this.volcanicness = Optional.of(new FloatRange(min,mean,max));
            return this;
        }

        public Builder withMagicalness(float min, float max){
            float mean = min + (max-min) / 2.0f;
            return withMagicalness(min,mean,max);
        }
        public Builder withMagicalness(float min, float mean, float max){
            this.magicalness = Optional.of(new FloatRange(min,mean,max));
            return this;
        }

        public Builder temperatureAround(float mean) {
            return temperatureAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder temperatureAround(float mean, float tolerance) {
            return withTemperature(mean - tolerance, mean, mean + tolerance);
        }

        public Builder erodabilityAround(float mean) {
            return erodabilityAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder erodabilityAround(float mean, float tolerance) {
            return withErodability(mean - tolerance, mean, mean + tolerance);
        }

        public Builder vegetationAround(float mean) {
            return vegetationAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder vegetationAround(float mean, float tolerance) {
            return withVegetation(mean - tolerance, mean, mean + tolerance);
        }

        public Builder humidityAround(float mean) {
            return humidityAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder humidityAround(float mean, float tolerance) {
            return withHumidity(mean - tolerance, mean, mean + tolerance);
        }

        public Builder rockinessAround(float mean) {
            return rockinessAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder rockinessAround(float mean, float tolerance) {
            return withRockiness(mean - tolerance, mean, mean + tolerance);
        }

        public Builder continentalnessAround(float mean) {
            return continentalnessAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder continentalnessAround(float mean, float tolerance) {
            return withContinentalness(mean - tolerance, mean, mean + tolerance);
        }

        public Builder volcanicnessAround(float mean) {
            return volcanicnessAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder volcanicnessAround(float mean, float tolerance) {
            return withVolcanicness(mean - tolerance, mean, mean + tolerance);
        }

        public Builder magicalnessAround(float mean) {
            return magicalnessAround(mean, AROUND_VALUE_RANGE);
        }

        public Builder magicalnessAround(float mean, float tolerance) {
            return withMagicalness(mean - tolerance, mean, mean + tolerance);
        }


        public Builder tagged(BiomePlacementTags... tags){
            List<BiomePlacementTags> list = this.tags.orElse(new ArrayList<>());
            list.addAll(List.of(tags));
            this.tags = Optional.of(list);
            return this;
        }

        public Builder disallowTags(BiomePlacementTags... tags){
            List<BiomePlacementTags> list = this.disallowedTags.orElse(new ArrayList<>());
            list.addAll(List.of(tags));
            this.disallowedTags = Optional.of(list);
            return this;
        }


        //TEMPERATURES

        public Builder freezing() {
            return withTemperature(-1.5f,-1.0f, -0.75f);
        }
        public Builder cold() {
            return withTemperature(-0.8f, -0.35f);
        }
        public Builder cool() {
            return withTemperature(-0.8f, 0.15f);
        }
        public Builder temperate() {
            return withTemperature(-0.25f, 0.25f);
        }
        public Builder warm() {
            return withTemperature(0.15f, 0.5f);
        }
        public Builder hot() {
            return withTemperature(0.35f, 0.8f);
        }
        public Builder scorching() {
            return withTemperature(0.75f, 1.0f, 1.5f);
        }

        //CONTINENTALNESS

        public Builder island() {
            return withContinentalness(-1.5f, -0.25f).tagged(BiomePlacementTags.ISLAND);
        }

        public Builder deepOcean() {
            return withContinentalness(-2.0f,-1.5f, -0.5f);
        }
        public Builder ocean() {
            return withContinentalness(-0.75f, -0.15f);
        }
        public Builder shore() {
            return withContinentalness(-0.20f, 0.15f);
        }
        public Builder inland() {
            return withContinentalness(0.0f, 1.5f);
        }
        public Builder farInland() {
            return withContinentalness(0.5f, 1.5f,2.0f);
        }

        //EROSION

        public Builder mountainous() {
            return withErodability(-1.5f,-1.0f, -0.3f);
        }
        public Builder averageErosion() {
            return withErodability(-0.5f, -0.5f);
        }
        public Builder flat() {
            return withErodability(0.3f, 1.0f);
        }
        public Builder anyErosion() {
            this.erodability = new FloatRange(-99.9f,0.0f,99.9f,true);
            return this;
        }

        //VEGETATION

        public Builder barren() {
            return withVegetation(-1.5f,-1.0f, -0.5f);
        }
        public Builder sparse() {
            return withVegetation(-0.5f, 0.25f);
        }
        public Builder grove() {
            return withVegetation(0.25f, 0.75f);
        }
        public Builder lush() {
            return withVegetation(0.5f, 0.85f);
        }
        public Builder dense() {
            return withVegetation(0.75f, 1.0f,1.5f);
        }

        //HUMIDITY

        public Builder arid() {
            return withHumidity(-1.5f,-1.0f, -0.5f);
        }
        public Builder dry() {
            return withHumidity(-0.5f, 0.0f);
        }
        public Builder mildHumidity() {
            return withHumidity(-0.35f, 0.35f);
        }
        public Builder damp() {
            return withHumidity(0.25f, 0.75f);
        }
        public Builder wet() {
            return withHumidity(0.5f, 1.0f,1.5f);
        }

        //ROCKINESS

        public Builder soil() {
            return withRockiness(-1.5f,-1.0f, 0.0f);
        }
        public Builder rocky() {
            return withRockiness(0.0f, 0.5f);
        }
        public Builder rugged() {
            return withRockiness(0.5f, 1.0f,1.5f);
        }

        //VOLCANO

        public Builder zeroVolcanicActivity() {
            return withVolcanicness(-1.0f,0.0f,0.015f);
        }
        public Builder lowVolcanicActivity() {
            return withVolcanicness(-0.1f, 0.25f);
        }
        public Builder dormantVolcano() {
            return withVolcanicness(0.25f, 0.75f);
        }
        public Builder activeVolcano() {
            return withVolcanicness(0.6f, 1.0f,1.5f);
        }

        //MAGICALNESS

        public Builder whimsy() {
            return withMagicalness(-0.1f,0.15f, 0.75f);
        }
        public Builder mystical() {
            return withMagicalness(0.5f, 1.0f,1.5f);
        }

        public Builder positiveMagicalness() {
            return withMagicalness(0.0f, 1.0f);
        }


        public BiomeTargetProfile build(){
            return new BiomeTargetProfile(
                    this.temperature,
                    this.erodability,
                    this.vegetation,
                    this.humidity,
                    this.rockiness,
                    this.continentalness,
                    this.volcanicness,
                    this.magicalness,
                    this.tags,
                    this.disallowedTags
            );
        }

    }
}
