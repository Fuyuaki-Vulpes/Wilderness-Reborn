package com.fuyuaki.wilderness_reborn.world.item;

import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
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

    //Items

    public static final DeferredItem<Item> PEAT_BALL = registerItem("peat_ball", new Item.Properties());
    public static final DeferredItem<Item> CHALK_DUST = registerItem("chalk_dust", new Item.Properties());

    public static final DeferredItem<Item> ALPINE_SIGN = ITEMS.registerItem("alpine_sign",
            properties -> new SignItem(ModBlocks.ALPINE_SIGN.get(), ModBlocks.ALPINE_WALL_SIGN.get(), properties), (new Item.Properties()).stacksTo(16));
    public static final DeferredItem<Item> ALPINE_HANGING_SIGN = ITEMS.registerItem("alpine_hanging_sign",
            properties -> new HangingSignItem(ModBlocks.ALPINE_HANGING_SIGN.get(), ModBlocks.ALPINE_HANGING_WALL_SIGN.get(), properties), new Item.Properties().stacksTo(16));




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
