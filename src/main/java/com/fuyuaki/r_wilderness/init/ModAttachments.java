package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.chunk.ChunkData;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RWildernessMod.MODID);

    public static final DeferredHolder<AttachmentType<?>,AttachmentType<ChunkData>> CHUNK_DATA = registerAtt("chunk_data", () -> AttachmentType.builder(holder -> {
                final ChunkAccess chunk = (ChunkAccess) holder;

                return new ChunkData(chunk.getPos());
            }).serialize(new IAttachmentSerializer<ChunkData>() {

                @Override
                public ChunkData read(IAttachmentHolder holder, ValueInput input) {
                    ChunkData data =  holder.getData(CHUNK_DATA);
                    data.deserializeNBT(input);
                    return data;
                }

                @Override
                public boolean write(ChunkData attachment, ValueOutput output) {
                    attachment.serializeNBT(output);
                    return true;
                }
            })
            .build());

    private static <T> DeferredHolder<AttachmentType<?>,AttachmentType<T>> registerAtt(String name, Supplier<AttachmentType<T>> type)
    {
        return TYPES.register(name, type);
    }


    public static void init(IEventBus bus){
        TYPES.register(bus);
    }

}
