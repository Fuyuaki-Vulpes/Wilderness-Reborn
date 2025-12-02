package com.fuyuaki.wilderness_reborn.world.level.block.entity;

import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BL_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModSignBlockEntity>> SIGN =
            BL_ENTITY.register("sign", () -> new BlockEntityType<>(ModSignBlockEntity::new,
                    ModBlocks.ALPINE_SIGN.get(),
                    ModBlocks.ALPINE_WALL_SIGN.get()
            ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN =
            BL_ENTITY.register("hanging_sign", () -> new BlockEntityType<>(ModHangingSignBlockEntity::new,
                    ModBlocks.ALPINE_HANGING_SIGN.get(),
                    ModBlocks.ALPINE_HANGING_WALL_SIGN.get()
            ));

    public static void init(IEventBus bus){
        BL_ENTITY.register(bus);
    }
}
