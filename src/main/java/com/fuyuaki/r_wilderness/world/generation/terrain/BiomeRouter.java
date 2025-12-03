package com.fuyuaki.r_wilderness.world.generation.terrain;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public class BiomeRouter {


    public BiomeRouter(){

    }


    protected enum TerrainLocation implements StringRepresentable {
        ISLAND("island"),
        DEEP_SEA("deep_sea"),
        MID_SEA("mid_sea"),
        NEAR_SEA("near_sea"),
        SEASHORE("seashore"),
        SHORE("shore"),
        NEAR_SHORE("near_shore"),
        INLAND("inland"),
        FAR_INLAND("far_inland")
        ;
        public static final StringRepresentable.EnumCodec<TerrainLocation> CODEC = StringRepresentable.fromEnum(TerrainLocation::values);

        private final String name;

        TerrainLocation(String name) {
            this.name = name;
        }

        @Nullable
        public static TerrainLocation byName(@Nullable String name) {
            return CODEC.byName(name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    protected enum TectonicActivityType implements StringRepresentable {
        NONE("none"),
        HILL("hill"),
        MOUNTAIN("mountain"),
        PEAK("peak"),
        JAGGED("jagged"),
        TRENCH("trench"),
        VOLCANIC("volcanic"),
        UNSTABLE("unstable")
        ;
        public static final StringRepresentable.EnumCodec<TectonicActivityType> CODEC = StringRepresentable.fromEnum(TectonicActivityType::values);

        private final String name;

        TectonicActivityType(String name) {
            this.name = name;
        }

        @Nullable
        public static TectonicActivityType byName(@Nullable String name) {
            return CODEC.byName(name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
    protected enum Wateriness implements StringRepresentable {
        NONE("none"),
        UNDERGROUND_WATER("underground_water"),
        RIVER("river"),
        LAKE("lake"),
        SEA("sea")
        ;
        public static final StringRepresentable.EnumCodec<Wateriness> CODEC = StringRepresentable.fromEnum(Wateriness::values);

        private final String name;

        Wateriness(String name) {
            this.name = name;
        }

        @Nullable
        public static Wateriness byName(@Nullable String name) {
            return CODEC.byName(name);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
