package com.fuyuaki.r_wilderness.world.block.woods;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModWoodTypes {

    public static final WoodType ALPINE = register("alpine", BlockSetType.OAK);

    public static WoodType register(String name, BlockSetType type) {
        return WoodType.register(new WoodType(MODID + ":" + name,type));
    }

    public static WoodType registerFull(String name, BlockSetType setType, SoundType soundType, SoundType hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen){
        return WoodType.register(new WoodType(MODID + name, setType, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen));
    }
}
