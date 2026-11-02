package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.environment.HydrationData;
import com.fuyuaki.r_wilderness.world.environment.TemperatureData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RWildernessMod.MODID);

    public static final Supplier<AttachmentType<TemperatureData>> TEMPERATURE = registerAtt("player_temperature",
            () -> AttachmentType.<TemperatureData>serializable(holder ->
            new TemperatureData()).sync(TemperatureData.STREAM_CODEC).build());

    public static final Supplier<AttachmentType<HydrationData>> HYDRATION = registerAtt("hydration",
            () -> AttachmentType.<HydrationData>serializable(holder ->
            new HydrationData()).sync(HydrationData.STREAM_CODEC).build());


    private static <T> DeferredHolder<AttachmentType<?>,AttachmentType<T>> registerAtt(String name, Supplier<AttachmentType<T>> type)
    {
        return TYPES.register(name, type);
    }


    public static void init(IEventBus bus){
        TYPES.register(bus);
    }

}
