package com.fuyuaki.r_wilderness.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT,MODID);

    public static final Holder<SoundEvent> MUSIC_BIOME_BARREN_CAVES = musicOverworld("barren_caves");


    private static Holder<SoundEvent> musicOverworld(String biomeName){
        return music("overworld." + biomeName);
    }
    private static Holder<SoundEvent> music(String musicPath){
        return sound("music." + musicPath);
    }



    public static final DeferredHolder<SoundEvent,SoundEvent> RIVER_FLOW = SOUNDS.register(
            "river_flow",
            registryName -> SoundEvent.createFixedRangeEvent(registryName,32f)
    );
    public static final DeferredHolder<SoundEvent,SoundEvent> RIVER_CRASH = SOUNDS.register(
            "river_crash",
            registryName -> SoundEvent.createFixedRangeEvent(registryName,32f)
    );

    private static Holder<SoundEvent> sound(String name){
        return SOUNDS.register(name, SoundEvent::createVariableRangeEvent);
    }

    private static Holder<SoundEvent> fixedRange(String name, float range){
        return SOUNDS.register(name, registryName -> SoundEvent.createFixedRangeEvent(registryName,range));
    }
    public static void init(IEventBus bus){
        SOUNDS.register(bus);
    }



}
