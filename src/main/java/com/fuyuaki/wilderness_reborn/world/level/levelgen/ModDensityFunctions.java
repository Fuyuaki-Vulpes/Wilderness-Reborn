package com.fuyuaki.wilderness_reborn.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModDensityFunctions {
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> FUNCTIONS = DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE,MODID);




    private static  <B extends Block> DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<? extends DensityFunction>> register(String name, KeyDispatchDataCodec<? extends DensityFunction> codec) {
        return FUNCTIONS.register(name, codec::codec);
    }

    public static void init(IEventBus bus){
        FUNCTIONS.register(bus);
    }
}
