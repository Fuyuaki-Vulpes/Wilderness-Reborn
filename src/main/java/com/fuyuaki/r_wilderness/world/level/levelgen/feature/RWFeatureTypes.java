package com.fuyuaki.r_wilderness.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RWFeatureTypes {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE_TYPE,MODID);



    public static void init(IEventBus bus){
        FEATURES.register(bus);
    }

}
