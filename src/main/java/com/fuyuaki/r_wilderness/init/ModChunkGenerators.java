package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR,MODID);


    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<WildChunkGenerator>> REBORN = GENERATORS.register(
            "reborn", () -> WildChunkGenerator.CODEC);


    public static void init(IEventBus bus){
        GENERATORS.register(bus);
    }

}
