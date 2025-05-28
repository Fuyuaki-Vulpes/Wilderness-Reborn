package com.fuyuaki.wilderness_reborn.world.item;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    private static final ResourceLocation SCROLLBAR = WildernessRebornMod.mod("textures/gui/sprites/container/creative_inventory/scroller.png");;
    private static final ResourceLocation DEFAULT_TEXTURE = WildernessRebornMod.mod("textures/gui/container/creative_inventory/tab/generic.png");
    private static final ResourceLocation MAIN_TAB_TEXTURE = WildernessRebornMod.mod("textures/gui/container/creative_inventory/tab/main.png");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = registerTab("wilderness_reborn_main",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.wilderness_reborn_main").withStyle(ChatFormatting.ITALIC,ChatFormatting.GREEN,ChatFormatting.BOLD))
            .withTabsBefore(CreativeModeTabs.OP_BLOCKS)
            .withScrollBarSpriteLocation(SCROLLBAR)
            .withSearchBar()
            .hideTitle()
            .backgroundTexture(MAIN_TAB_TEXTURE)
            .icon(Items.COMPASS.asItem()::getDefaultInstance)
            .displayItems(((parameters, output) -> {
                output.accept(Items.CHERRY_SAPLING);
                output.accept(ModItems.PEAT_BALL);
                output.accept(ModItems.CHALK_DUST);
                output.accept(ModBlocks.LIMESTONE);
                output.accept(ModBlocks.MUD_STONE);
                output.accept(ModBlocks.CHALK);
                output.accept(ModBlocks.CHALKY_SOIL);
                output.accept(ModBlocks.CHALKY_FARMLAND);
                output.accept(ModBlocks.CLAY_SOIL);
                output.accept(ModBlocks.CLAY_FARMLAND);
                output.accept(ModBlocks.PEAT);
                output.accept(ModBlocks.PEAT_FARMLAND);
                output.accept(ModBlocks.SANDY_SOIL);
                output.accept(ModBlocks.SANDY_FARMLAND);
                output.accept(ModBlocks.SILT);
                output.accept(ModBlocks.SILT_FARMLAND);
            }))
            .build());




    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(
            String name,
            DeferredHolder<CreativeModeTab, CreativeModeTab> previousTab,
            ItemStack icon,
            CreativeModeTab.DisplayItemsGenerator generator){
        return registerTab(name, Component.translatable("itemGroup." + name),previousTab,icon,DEFAULT_TEXTURE,0xb0fbf0,generator);

    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(
            String name,
            DeferredHolder<CreativeModeTab, CreativeModeTab> previousTab,
            ItemStack icon,
            ResourceLocation texture,
            int color,
            CreativeModeTab.DisplayItemsGenerator generator){
        return registerTab(name, Component.translatable("itemGroup." + name),previousTab,icon,texture,color,generator);

    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(
            String name,
            Component title,
            DeferredHolder<CreativeModeTab, CreativeModeTab> previousTab,
            ItemStack icon, ResourceLocation texture, int color, CreativeModeTab.DisplayItemsGenerator generator){
        return registerTab(name, () -> CreativeModeTab.builder()
                .title(title)
                .build());

    }




    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> function){
        return TABS.register(name,function);
    }

    public static void init(IEventBus bus){
        TABS.register(bus);
    }



}
