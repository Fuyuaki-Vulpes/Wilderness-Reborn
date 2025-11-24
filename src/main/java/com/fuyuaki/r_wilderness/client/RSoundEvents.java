package com.fuyuaki.r_wilderness.client;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final Holder<SoundEvent> MUSIC_BIOME_BARREN_CAVES = musicOverworld("barren_caves");


    private static Holder<SoundEvent> musicOverworld(String biomeName){
        return music("overworld." + biomeName);
    }
    private static Holder<SoundEvent> music(String musicPath){
        return sound("music." + musicPath);
    }

    private static Holder<SoundEvent> sound(String name){
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }

    private static Holder<SoundEvent> fixedRange(String name, float range){
        return SOUND_EVENTS.register(name, registryName -> SoundEvent.createFixedRangeEvent(registryName,range));
    }



    public static void init(IEventBus bus){
        SOUND_EVENTS.register(bus);
    }


}
