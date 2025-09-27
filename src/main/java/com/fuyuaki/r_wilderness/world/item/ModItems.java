package com.fuyuaki.r_wilderness.world.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    //Items

    public static final DeferredItem<Item> PEAT_BALL = registerItem("peat_ball", new Item.Properties());
    public static final DeferredItem<Item> CHALK_DUST = registerItem("chalk_dust", new Item.Properties());



    //Methods

    public static DeferredItem<Item> registerItem(String name, Item.Properties properties) {
        return registerItem(name, Item::new, properties);
    }

    public static DeferredItem<Item> registerItem(String name) {
        return registerItem(name, Item::new, new Item.Properties());
    }

    public static <I extends Item> DeferredItem<I> registerItem(String key, Function<Item.Properties, I> factory) {
        return registerItem(key, factory, new Item.Properties());
    }

    public static <I extends Item> DeferredItem<I> registerItem(String key, Function<Item.Properties, I> factory, Item.Properties properties) {
        return ITEMS.registerItem(key,factory,properties);
    }

    public static void init(IEventBus bus){
        ITEMS.register(bus);
    }


}
