package com.fuyuaki.r_wilderness.world.generation.river;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public interface WaterBody {

    BodyType bodyType();

    int depth();

    int width();

    double deformation();

    Codec<? extends WaterBody> codec();



    enum BodyType implements StringRepresentable {
        RIVER("river"),
        LAKE("lake"),
        SINKHOLE("sinkhole"),
        WATERFALL("waterfall"),
        UNDERGROUND_RIVER("underground_river"),

        ;

        private final String name;

        BodyType(String serializedName){
            this.name = serializedName;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
