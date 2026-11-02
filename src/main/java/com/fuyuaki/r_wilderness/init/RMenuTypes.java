package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Supplier;

public class RMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(Registries.MENU, RWildernessMod.MODID);


    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> menu(String name, Supplier<MenuType<T>> type) {
        return MENU.register(name, type);
    }

    public static void init(IEventBus bus){
        MENU.register(bus);
    }


}
