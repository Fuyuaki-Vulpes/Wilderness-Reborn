package com.fuyuaki.r_wilderness.world.generation.hydrology.river.old_backup;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public interface WaterBodyOld {

    BodyType bodyType();

    int depth();

    int width();

    double deformation();

    Codec<? extends WaterBodyOld> codec();



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
