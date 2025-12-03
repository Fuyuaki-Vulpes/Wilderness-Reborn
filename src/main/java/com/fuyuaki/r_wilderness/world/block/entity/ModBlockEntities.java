package com.fuyuaki.r_wilderness.world.block.entity;

import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BL_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModSignBlockEntity>> SIGN =
            BL_ENTITY.register("sign", () -> new BlockEntityType<>(ModSignBlockEntity::new,
                    RBlocks.ALPINE_SIGN.get(),
                    RBlocks.ALPINE_WALL_SIGN.get()
            ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN =
            BL_ENTITY.register("hanging_sign", () -> new BlockEntityType<>(ModHangingSignBlockEntity::new,
                    RBlocks.ALPINE_HANGING_SIGN.get(),
                    RBlocks.ALPINE_HANGING_WALL_SIGN.get()
            ));

    public static void init(IEventBus bus){
        BL_ENTITY.register(bus);
    }
}
