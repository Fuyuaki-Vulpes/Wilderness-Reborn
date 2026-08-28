package com.fuyuaki.r_wilderness.server.commands.arguments;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, RWildernessMod.MODID);

    public static final Holder<ArgumentTypeInfo<?, ?>> REGION_LAYERS = TYPES.register("region_layers", () -> ArgumentTypeInfos.registerByClass(RegionLayerArgument.class, SingletonArgumentInfo.contextFree(RegionLayerArgument::argument)));


    public static void init(IEventBus bus) {
        TYPES.register(bus);
    }

}

