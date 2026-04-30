package com.fuyuaki.r_wilderness.world.item;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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


    private static final Identifier SCROLLBAR = RWildernessMod.modLocation("container/creative_inventory/scroller");
    private static final Identifier TAB = RWildernessMod.modLocation("container/creative_inventory/tab");
    private static final Identifier DEFAULT_TEXTURE = RWildernessMod.modLocation("textures/gui/container/creative_inventory/tab/generic.png");
    private static final Identifier MAIN_TAB_TEXTURE = RWildernessMod.modLocation("textures/gui/container/creative_inventory/tab/main.png");

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
                output.accept(RBlocks.MOUNTAIN_HEATHER);

                output.accept(RItems.PEAT_BALL);
                output.accept(RItems.CHALK_DUST);
                output.accept(RBlocks.LIMESTONE);
                output.accept(RBlocks.MUD_STONE);
                output.accept(RBlocks.CHALK);

                output.accept(RBlocks.SCHINITE);
                output.accept(RBlocks.SCHINITE_STAIRS);
                output.accept(RBlocks.SCHINITE_SLAB);
                output.accept(RBlocks.SCHINITE_WALL);

                output.accept(RBlocks.COBBLED_SCHINITE);
                output.accept(RBlocks.COBBLED_SCHINITE_STAIRS);
                output.accept(RBlocks.COBBLED_SCHINITE_SLAB);
                output.accept(RBlocks.COBBLED_SCHINITE_WALL);

                output.accept(RBlocks.MAGNEISS);
                output.accept(RBlocks.MAGNEISS_STAIRS);
                output.accept(RBlocks.MAGNEISS_SLAB);
                output.accept(RBlocks.MAGNEISS_WALL);

                output.accept(RBlocks.COBBLED_MAGNEISS);
                output.accept(RBlocks.COBBLED_MAGNEISS_STAIRS);
                output.accept(RBlocks.COBBLED_MAGNEISS_SLAB);
                output.accept(RBlocks.COBBLED_MAGNEISS_WALL);

                output.accept(RBlocks.MALATITE);
                output.accept(RBlocks.MALATITE_STAIRS);
                output.accept(RBlocks.MALATITE_SLAB);
                output.accept(RBlocks.MALATITE_WALL);

                output.accept(RBlocks.COBBLED_MALATITE);
                output.accept(RBlocks.COBBLED_MALATITE_STAIRS);
                output.accept(RBlocks.COBBLED_MALATITE_SLAB);
                output.accept(RBlocks.COBBLED_MALATITE_WALL);


                output.accept(RBlocks.SCHINITE_GOLD_ORE);
                output.accept(RBlocks.MAGNEISS_GOLD_ORE);
                output.accept(RBlocks.MALATITE_GOLD_ORE);

                output.accept(RBlocks.SCHINITE_IRON_ORE);
                output.accept(RBlocks.MAGNEISS_IRON_ORE);
                output.accept(RBlocks.MALATITE_IRON_ORE);

                output.accept(RBlocks.SCHINITE_COAL_ORE);
                output.accept(RBlocks.MAGNEISS_COAL_ORE);
                output.accept(RBlocks.MALATITE_COAL_ORE);

                output.accept(RBlocks.SCHINITE_LAPIS_ORE);
                output.accept(RBlocks.MAGNEISS_LAPIS_ORE);
                output.accept(RBlocks.MALATITE_LAPIS_ORE);

                output.accept(RBlocks.SCHINITE_DIAMOND_ORE);
                output.accept(RBlocks.MAGNEISS_DIAMOND_ORE);
                output.accept(RBlocks.MALATITE_DIAMOND_ORE);

                output.accept(RBlocks.SCHINITE_REDSTONE_ORE);
                output.accept(RBlocks.MAGNEISS_REDSTONE_ORE);
                output.accept(RBlocks.MALATITE_REDSTONE_ORE);

                output.accept(RBlocks.SCHINITE_EMERALD_ORE);
                output.accept(RBlocks.MAGNEISS_EMERALD_ORE);
                output.accept(RBlocks.MALATITE_EMERALD_ORE);

                output.accept(RBlocks.SCHINITE_COPPER_ORE);
                output.accept(RBlocks.MAGNEISS_COPPER_ORE);
                output.accept(RBlocks.MALATITE_COPPER_ORE);

                output.accept(RBlocks.WEATHERED_BEDROCK);
                output.accept(RBlocks.WEATHERED_BEDROCK_STAIRS);
                output.accept(RBlocks.WEATHERED_BEDROCK_SLAB);
                output.accept(RBlocks.WEATHERED_BEDROCK_WALL);

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
            Identifier texture,
            int color,
            CreativeModeTab.DisplayItemsGenerator generator){
        return registerTab(name, Component.translatable("itemGroup." + name),previousTab,icon,texture,color,generator);

    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTab(
            String name,
            Component title,
            DeferredHolder<CreativeModeTab, CreativeModeTab> previousTab,
            ItemStack icon, Identifier texture, int color, CreativeModeTab.DisplayItemsGenerator generator){
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
