package com.fuyuaki.wilderness_reborn.api;

import com.fuyuaki.wilderness_reborn.data.worldgen.ModSurfaceRuleData;
import com.fuyuaki.wilderness_reborn.init.ModFeatures;
import com.fuyuaki.wilderness_reborn.init.ModSoundEvents;
import com.fuyuaki.wilderness_reborn.world.item.ModCreativeModeTabs;
import com.fuyuaki.wilderness_reborn.world.item.ModItems;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(WildernessRebornMod.MODID)
public class WildernessRebornMod {
    public static final String MODID = "wilderness_reborn";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public WildernessRebornMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModSurfaceRuleData.ConditionSource.init(modEventBus);
        ModBlocks.init(modEventBus);
        ModItems.init(modEventBus);
        ModCreativeModeTabs.init(modEventBus);
        ModSoundEvents.init(modEventBus);
        ModFeatures.init(modEventBus);

        NeoForge.EVENT_BUS.register(this);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
//        LOGGER.info("HELLO FROM COMMON SETUP");
//
//        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static ResourceLocation mod(String id){
        return ResourceLocation.fromNamespaceAndPath(MODID,id);
    }
}
