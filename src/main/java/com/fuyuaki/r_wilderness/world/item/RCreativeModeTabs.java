package com.fuyuaki.r_wilderness.world.item;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    private static final ResourceLocation SCROLLBAR = RWildernessMod.modLocation("container/creative_inventory/scroller");
    private static final ResourceLocation TAB = RWildernessMod.modLocation("container/creative_inventory/tab");
    private static final ResourceLocation DEFAULT_TEXTURE = RWildernessMod.modLocation("textures/gui/container/creative_inventory/tab/generic.png");
    private static final ResourceLocation MAIN_TAB_TEXTURE = RWildernessMod.modLocation("textures/gui/container/creative_inventory/tab/main.png");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = registerTab("r_wilderness_main",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.r_wilderness_main").withStyle(ChatFormatting.ITALIC,ChatFormatting.GREEN,ChatFormatting.BOLD))
            .withTabsBefore(CreativeModeTabs.OP_BLOCKS)
            .hideTitle()
            .withScrollBarSpriteLocation(SCROLLBAR)
            .withSearchBar()
            .withTabsImage(TAB)
            .backgroundTexture(MAIN_TAB_TEXTURE)
            .icon(Blocks.BIRCH_LEAVES.asItem()::getDefaultInstance)
            .displayItems(((parameters, output) -> {
                output.accept(RItems.PEAT_BALL);
                output.accept(RItems.CHALK_DUST);
                output.accept(RBlocks.LIMESTONE);
                output.accept(RBlocks.MUD_STONE);
                output.accept(RBlocks.CHALK);
                output.accept(RBlocks.CHALKY_SOIL);
                output.accept(RBlocks.CHALKY_FARMLAND);
                output.accept(RBlocks.CLAY_SOIL);
                output.accept(RBlocks.CLAY_FARMLAND);
                output.accept(RBlocks.PEAT);
                output.accept(RBlocks.PEAT_FARMLAND);
                output.accept(RBlocks.SANDY_SOIL);
                output.accept(RBlocks.SANDY_FARMLAND);
                output.accept(RBlocks.SILT);
                output.accept(RBlocks.SILT_FARMLAND);
                output.accept(RBlocks.BELLFLOWER);
                output.accept(RBlocks.MOSS_COMPANION);
                output.accept(RBlocks.MOUNTAIN_HEATHER);
                output.accept(RBlocks.SNOWBELL);

                output.accept(RItems.PEAT_BALL);
                output.accept(RItems.CHALK_DUST);
                output.accept(RBlocks.LIMESTONE);
                output.accept(RBlocks.MUD_STONE);
                output.accept(RBlocks.CHALK);

                output.accept(RBlocks.SCHINITE);
                output.accept(RBlocks.COBBLED_SCHINITE);
                output.accept(RBlocks.MAGNEISS);
                output.accept(RBlocks.COBBLED_MAGNEISS);
                output.accept(RBlocks.MALATITE);
                output.accept(RBlocks.COBBLED_MALATITE);



                output.accept(RBlocks.CHALKY_SOIL);
                output.accept(RBlocks.CHALKY_FARMLAND);
                output.accept(RBlocks.CLAY_SOIL);
                output.accept(RBlocks.CLAY_FARMLAND);
                output.accept(RBlocks.PEAT);
                output.accept(RBlocks.PEAT_FARMLAND);
                output.accept(RBlocks.SANDY_SOIL);
                output.accept(RBlocks.SANDY_FARMLAND);
                output.accept(RBlocks.SILT);
                output.accept(RBlocks.SILT_FARMLAND);

                output.accept(RBlocks.ALPINE_LOG);
                output.accept(RBlocks.ALPINE_WOOD);
                output.accept(RBlocks.STRIPPED_ALPINE_LOG);
                output.accept(RBlocks.STRIPPED_ALPINE_WOOD);
                output.accept(RBlocks.ALPINE_PLANKS);
                output.accept(RBlocks.ALPINE_LEAVES);
                output.accept(RBlocks.ALPINE_SAPLING);
                output.accept(RBlocks.ALPINE_SLAB);
                output.accept(RBlocks.ALPINE_STAIRS);
                output.accept(RBlocks.ALPINE_BUTTON);
                output.accept(RBlocks.ALPINE_PRESSURE_PLATE);
                output.accept(RBlocks.ALPINE_FENCE);
                output.accept(RBlocks.ALPINE_FENCE_GATE);
                output.accept(RBlocks.ALPINE_DOOR);
                output.accept(RBlocks.ALPINE_TRAPDOOR);
                output.accept(RBlocks.ALPINE_SIGN);
                output.accept(RBlocks.ALPINE_WALL_SIGN);
                output.accept(RBlocks.ALPINE_HANGING_SIGN);
                output.accept(RBlocks.ALPINE_HANGING_WALL_SIGN);
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
