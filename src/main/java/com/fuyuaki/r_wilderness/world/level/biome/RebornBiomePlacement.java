package com.fuyuaki.r_wilderness.world.level.biome;

import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.api.WildernessConstants;
import com.fuyuaki.r_wilderness.world.generation.terrain.TerrainParameters;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RebornBiomePlacement(
        Identifier name,
        Holder<Biome> biome,
        TerrainParameters.Target target,
        List<Type> validTerrainTypes,
        Optional<List<TerrainStates>> tectonicStates,
        Optional<List<TerrainStates>> dislikedTectonicStates
) {


    public static final Codec<RebornBiomePlacement> CODEC = ExtraCodecs.catchDecoderException(
            RecordCodecBuilder.create(
                    p_415523_ -> p_415523_.group(
                                    Identifier.CODEC.fieldOf("name").forGetter(RebornBiomePlacement::name),
                                    Biome.CODEC.fieldOf("biome").forGetter(RebornBiomePlacement::biome),
                                    TerrainParameters.Target.CODEC.fieldOf("target").forGetter(RebornBiomePlacement::target),
                                    Type.CODEC.listOf().fieldOf("valid_terrain_types").forGetter(RebornBiomePlacement::validTerrainTypes),
                                    TerrainStates.CODEC.listOf().optionalFieldOf("liked_tectonic_states").forGetter(RebornBiomePlacement::tectonicStates),
                                    TerrainStates.CODEC.listOf().optionalFieldOf("disliked_tectonic_states").forGetter(RebornBiomePlacement::dislikedTectonicStates)
                            )
                            .apply(p_415523_, RebornBiomePlacement::new)
            )
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<RebornBiomePlacement>> STREAM_CODEC = ByteBufCodecs.holderRegistry(WildRegistries.REBORN_BIOME_PLACEMENT_KEY);
    public static final Codec<Holder<RebornBiomePlacement>> HOLDER_CODEC = RegistryFileCodec.create(WildRegistries.REBORN_BIOME_PLACEMENT_KEY, CODEC);


    public RebornBiomePlacement(
            Identifier name,
            Holder<Biome> biome,
            TerrainParameters.Target target,
            List<Type> validTerrainTypes){
        this(name,biome,target,validTerrainTypes,Optional.empty(),Optional.empty());
    }

    public double tryAt(double yLevel, TerrainParameters.Sampled sampled,int x, int y, int z){
        List<TerrainStates> states = TerrainStates.statesAt(sampled,y);
        Type type = Type.find(yLevel,sampled, x, y, z,states);
        double affinity = target.affinity(sampled,Math.min(y,yLevel),false);
        if (!this.validTerrainTypes.contains(type)) affinity -= 60;
        if (this.tectonicStates.isPresent()){
            boolean v = false;
            for (TerrainStates state : states){
                if (this.tectonicStates.get().contains(state)) {
                    v = true;
                    if (state.strongerRelevancy){
                        affinity += 7;
                    } else {
                        affinity += 3;
                    }

                }
            }
            if (!v) affinity -= 12;
        }if (this.dislikedTectonicStates.isPresent()){
            for (TerrainStates state : states){
                if (this.dislikedTectonicStates.get().contains(state)) {
                    if (state.strongerRelevancy){
                        affinity -= 5;
                    } else {
                        affinity -= 2;
                    }

                }
            }
        }

        return affinity;
    }

    public enum Type implements StringRepresentable {
        NEUTRAL("neutral"),

        RIVER("river"),
        CAVE("cave"),

        PEAK("mountain_peak"),
        MOUNTAIN("mountain"),
        HILLY("mountain"),

        HIGHLANDS("highlands"),

        SHORE("shore"),
        NEAR_SHORE("sea_near_shore"),
        SEA("sea"),
        FAR_SEA("far_sea"),

        ISLAND("island"),

        ;

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }


        public static Type find(double yLevel, TerrainParameters.Sampled sampled,int x, int y, int z, List<TerrainStates> states){
            if (sampled.highlandsMap() > 0.05 || sampled.badlands() > 0.15 || sampled.tectonicActivity() > 0.25){
                if(yLevel > y + 24 && sampled.erosion() > 0){
                    return CAVE;
                }if(yLevel > y + 48 && (sampled.erosion() < 0)){
                    return CAVE;
                }
            }else{
                if(yLevel > y + 16 && sampled.erosion() > 0){
                    return CAVE;
                }if(yLevel > y + 32 && (sampled.erosion() < 0)){
                    return CAVE;
                }
            }
            double tectonicActivity = (1 - Math.abs(sampled.tectonicActivity()));
            if (!states.contains(TerrainStates.NORMAL)){
                boolean canBeMountainFlag = yLevel >= 80;
                boolean defaultMountainFlag = states.contains(TerrainStates.REGULAR_MOUNTAINS);
                boolean mountainousFlag = (states.contains(TerrainStates.ERODED_MOUNTAINS) || defaultMountainFlag) && canBeMountainFlag;
                boolean cliffMountain = states.contains( TerrainStates.CLIFF_MOUNTAINS) && canBeMountainFlag;
                if (states.contains(TerrainStates.MOUNTAINOUS)
                        && (sampled.continentalness() > 0.05 && cliffMountain || mountainousFlag)){
                    if (sampled.mountains() > 0.2 || sampled.mountainsCore() > 0.2) {
                        if (((sampled.mountains() > 0.65 && sampled.mountainsCore() > 0.65) || (sampled.mountainsCore() >= 1.0 && sampled.mountains() > 0.25)) && (sampled.continentalness() > 0.15 && cliffMountain || defaultMountainFlag)) {
                            return PEAK;
                        }
                        return MOUNTAIN;
                    }
                    else if((sampled.mountains() > 0.1 || sampled.mountainsCore() > 0.1)) return HIGHLANDS;
                }
                if (sampled.erosion() < -0.25){
                    return HILLY;
                }
            }
            double continentalness = sampled.continentalness();

            if (continentalness > -0.05) {
                double r = Math.pow(Math.abs(Math.clamp(sampled.waterBasins() * 3,-1,1)),2);

                if (r < 0.5 && 1- Math.abs(tectonicActivity) < 0.15){
                    return RIVER;
                }
                if (continentalness > 0.05) {

                    if (tectonicActivity > -5.0 && sampled.highlandsMap() > 0.5)
                        return HIGHLANDS;
                    if ((sampled.erosion() < -0.5 && sampled.hills() > 0.35) || states.contains(TerrainStates.BADLANDS)) {
                        return HILLY;
                    }
                    return NEUTRAL;
                }

                if (yLevel > 160) {
                    return MOUNTAIN;
                }
                if (yLevel > 92) {
                    return HIGHLANDS;
                }
                if (states.contains(TerrainStates.BADLANDS)) {
                    return HILLY;
                }
                return SHORE;
            }


            if (continentalness > -0.15){
                if(yLevel > WildernessConstants.SEA_LEVEL - 1){
                    return SHORE;
                }
                return NEAR_SHORE;
            }

            if (continentalness > -0.5){
                if(yLevel > WildernessConstants.SEA_LEVEL - 2){
                    return SHORE;
                }
                return SEA;
            }

            if(yLevel > WildernessConstants.SEA_LEVEL -6){
                return ISLAND;
            }

            return FAR_SEA;


        }
    }
    public enum TerrainStates implements StringRepresentable {
        NORMAL("normal",false),
        UNSTABLE("unstable",false),
        BADLANDS("badlands",true),
        DIVERGING_PLATES("diverging_plates",false),
        TRENCH("trench",true),
        FAULT_SURROUNDINGS("fault_surroundings",false),
        FAULT("fault",true),
        CLIFF_MOUNTAINS("cliff_mountains",false),
        MOUNTAINOUS("mountainous",true),
        REGULAR_MOUNTAINS("regular_mountains",false),
        ERODED_MOUNTAINS("eroded_mountains",false),
        VOLCANIC("volcanic",true),
        DEEP_UNDERGROUND ("deep_underground",true),
        VERY_DEEP_UNDERGROUND ("very_deep_underground",true),
        DEEPEST_UNDERGROUND ("deepest_underground",true),

        ;

        public static final Codec<TerrainStates> CODEC = StringRepresentable.fromEnum(TerrainStates::values);

        private final String name;
        private final boolean strongerRelevancy;

        TerrainStates(String name, boolean strongerRelevancy) {
            this.name = name;
            this.strongerRelevancy = strongerRelevancy;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }


        public static List<TerrainStates> statesAt(TerrainParameters.Sampled sampled, int y){
            double tTypeA = sampled.terrainType().a();
            double tTypeB = sampled.terrainType().b();
            double continentalness = sampled.continentalness();
            double tectonicActivity = (1 - Math.abs(sampled.tectonicActivity()));
            double erosion = sampled.erosion();


            List<TerrainStates> states = new ArrayList<>();

            TerrainFilter filter = TerrainFilter.pick(tTypeA,tTypeB);

            if (y < 0.0){
                states.add(DEEP_UNDERGROUND);
                if (y < -64){
                    states.add(VERY_DEEP_UNDERGROUND);
                    if (y < -128){
                        states.add(DEEPEST_UNDERGROUND);
                    }
                }
            }
            if (       sampled.badlands() > TerrainParameters.BADLANDS_THRESHOLD
                    && sampled.humidity() < TerrainParameters.HUMIDITY_BADLANDS_THRESHOLD
                    && sampled.continentalness() > 0.0) {
                states.add(BADLANDS);
            }
            if (tectonicActivity > 0) {
                switch (filter) {
                    case TerrainFilter.DIVERGENT -> {
                        if (tectonicActivity > 0.950) {
                            states.add(TRENCH);
                        }
                        states.add(DIVERGING_PLATES);
                        states.add(VOLCANIC);
                    }
                    case CONVERGENT_CLIFF -> {
                        if (erosion < 0.0) {
                            if (continentalness > 0.05) {
                                states.add(CLIFF_MOUNTAINS);
                            } else {
                                states.add(UNSTABLE);
                            }
                        } else {
                            states.add(ERODED_MOUNTAINS);
                        }
                        states.add(MOUNTAINOUS);
                        states.add(UNSTABLE);
                        states.add(VOLCANIC);
                    }
                    case CONVERGENT -> {
                        if (erosion < 0.0) states.add(REGULAR_MOUNTAINS);
                        else states.add(ERODED_MOUNTAINS);
                        states.add(MOUNTAINOUS);
                        states.add(UNSTABLE);
                        states.add(VOLCANIC);
                    }
                    case TRANSFORM -> {
                        states.add(FAULT_SURROUNDINGS);
                        if (tectonicActivity > 0.990) states.add(FAULT);
                        states.add(UNSTABLE);
                    }
                    case TRANSITIONAL_C_D -> states.add(UNSTABLE);
                    case TRANSITIONAL_MOUNTAIN -> {
                        states.add(MOUNTAINOUS);
                        states.add(UNSTABLE);
                        states.add(VOLCANIC);
                    }
                    default -> states.add(NORMAL);
                }
            }
            else {
                states.add(NORMAL);
            }




            return states;
        }
        enum TerrainFilter{
            DIVERGENT,
            CONVERGENT_CLIFF,
            CONVERGENT,
            TRANSFORM,
            TRANSITIONAL_MOUNTAIN,
            TRANSITIONAL_C_D,
            TRANSITIONAL_CENTRAL;

            static TerrainFilter pick(double tA, double tB){
                if (tB < 0.35){
                    if (tA < 0.35){
                        return DIVERGENT;
                    }
                    if (tA > 0.65){
                        return TRANSFORM;
                    }
                    return TRANSITIONAL_C_D;
                }
                if (tB > 0.65){
                    if (tA < 0.35){
                        return CONVERGENT_CLIFF;
                    }
                    if (tA > 0.65){
                        return CONVERGENT;
                    }
                    return TRANSITIONAL_MOUNTAIN;
                }
                return TRANSITIONAL_CENTRAL;
            }
        }

    }
}
