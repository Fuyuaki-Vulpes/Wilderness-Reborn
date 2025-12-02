package com.fuyuaki.r_wilderness.world.generation.hydrology;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.saveddata.SavedDataType;

public interface WaterBody {
     <T extends WaterBody> Codec<T> codec();


}
