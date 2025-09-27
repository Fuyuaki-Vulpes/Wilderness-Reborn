package com.fuyuaki.r_wilderness.api;

import com.fuyuaki.r_wilderness.data.worldgen.ModSurfaceRuleData;
import com.fuyuaki.r_wilderness.init.ModAttachments;
import com.fuyuaki.r_wilderness.init.ModChunkGenerators;
import com.fuyuaki.r_wilderness.init.ModFeatures;
import com.fuyuaki.r_wilderness.init.ModSoundEvents;
import com.fuyuaki.r_wilderness.world.item.ModCreativeModeTabs;
import com.fuyuaki.r_wilderness.world.item.ModItems;
import com.fuyuaki.r_wilderness.world.level.biome.BiomeRanges;
import com.fuyuaki.r_wilderness.world.level.block.ModBlocks;
import com.fuyuaki.r_wilderness.world.level.levelgen.carver.ModWorldCarvers;
import com.fuyuaki.r_wilderness.world.level.levelgen.placement.ModPlacementModifierTypes;
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
@Mod(RWildernessMod.MODID)
public class RWildernessMod {
    public static final String MODID = "r_wilderness";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public RWildernessMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModSurfaceRuleData.ConditionSource.init(modEventBus);
        ModAttachments.init(modEventBus);
        ModBlocks.init(modEventBus);
        ModItems.init(modEventBus);
        ModCreativeModeTabs.init(modEventBus);
        ModSoundEvents.init(modEventBus);
        ModFeatures.init(modEventBus);
        ModChunkGenerators.init(modEventBus);
        ModWorldCarvers.init(modEventBus);
        ModPlacementModifierTypes.init(modEventBus);

        NeoForge.EVENT_BUS.register(this);



        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BiomeRanges.bootstrap();

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

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static ResourceLocation modLocation(String id){
        return ResourceLocation.fromNamespaceAndPath(MODID,id);
    }
}
