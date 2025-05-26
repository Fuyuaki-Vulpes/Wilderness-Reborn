package com.fuyuaki.wilderness_reborn.world.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);





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
