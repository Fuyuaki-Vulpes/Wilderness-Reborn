package com.fuyuaki.r_wilderness.api;

import com.fuyuaki.r_wilderness.data.worldgen.RSurfaceRuleData;
import com.fuyuaki.r_wilderness.init.RAttachments;
import com.fuyuaki.r_wilderness.init.RChunkGenerators;
import com.fuyuaki.r_wilderness.init.RFeatures;
import com.fuyuaki.r_wilderness.init.RSoundEvents;
import com.fuyuaki.r_wilderness.world.generation.distant_horizons.DHApiEventHandler;
import com.fuyuaki.r_wilderness.world.item.RCreativeModeTabs;
import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.fuyuaki.r_wilderness.world.level.levelgen.carver.RWorldCarvers;
import com.fuyuaki.r_wilderness.world.level.levelgen.placement.RPlacementModifierTypes;
import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.methods.events.DhApiEventRegister;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
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
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(RWildernessMod.MODID)
public class RWildernessMod {
    public static final String MODID = "r_wilderness";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public RWildernessMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        RSurfaceRuleData.ConditionSource.init(modEventBus);
        RebornBiomeSource.Registerer.init(modEventBus);
        RAttachments.init(modEventBus);
        RBlocks.init(modEventBus);
        RItems.init(modEventBus);
        RCreativeModeTabs.init(modEventBus);
        RSoundEvents.init(modEventBus);
        RFeatures.init(modEventBus);
        RChunkGenerators.init(modEventBus);
        RWorldCarvers.init(modEventBus);
        RPlacementModifierTypes.init(modEventBus);
        com.fuyuaki.r_wilderness.client.RSoundEvents.init(modEventBus);

        NeoForge.EVENT_BUS.register(this);



        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        DhApiEventRegister.on(DhApiLevelLoadEvent.class, new DHApiEventHandler());


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

    public static RegistryAccess getRegistryAccess(){
        if (EffectiveSide.get().isClient())
            return Minecraft.getInstance().level.registryAccess();
        if (ServerLifecycleHooks.getCurrentServer() != null)
            return ServerLifecycleHooks.getCurrentServer().registryAccess();
        return RegistryAccess.EMPTY;
    }
}
