package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.client.gui.RWildernessLayers;
import com.fuyuaki.r_wilderness.client.gui.components.debug.DebugEntryRegionStatus;
import com.fuyuaki.r_wilderness.client.gui.components.debug.RDebugScreenEntries;
import com.fuyuaki.r_wilderness.client.menu.hud.RWildernessGui;
import com.fuyuaki.r_wilderness.client.renderer.block.RFluidStateModels;
import com.fuyuaki.r_wilderness.network.DrinkWaterInWorldPacket;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.fuyuaki.r_wilderness.world.level.material.RFluids;
import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

import java.util.List;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RClientEvents {

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
    }
    @SubscribeEvent
    public static void blockColorsEvent(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.waterParticles()), RBlocks.RIVER_WATER.get());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                DrinkWaterInWorldPacket.TYPE,
                DrinkWaterInWorldPacket::handle
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

    }


    @SubscribeEvent
    public static void registerClientExtensions(RegisterFluidModelsEvent event) {
        event.register(RFluidStateModels.RIVER_WATER, RFluids.RIVER.get());
        event.register(RFluidStateModels.RIVER_WATER, RFluids.FLOWING_RIVER.get());
    }



    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
//        event.register(Identifier.fromNamespaceAndPath(MODID, "is_attacking"), IsAttacking.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerGuiComponents(RegisterGuiLayersEvent event) {

        event.registerBelow(VanillaGuiLayers.FOOD_LEVEL, RWildernessLayers.HYDRATION_LEVEL, RWildernessGui::hydrationLayer);
        event.registerBelow(VanillaGuiLayers.AIR_LEVEL, RWildernessLayers.ENV_TEMPERATURE, RWildernessGui::environmentTemperatureLayer);
        event.registerBelow(RWildernessLayers.ENV_TEMPERATURE, RWildernessLayers.BODY_TEMPERATURE, RWildernessGui::bodyTemperatureLayer);


    }

//
//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event) {
//
//    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        //event.registerEntityRenderer(ModEntityTypes.ALPINE_BOAT.get(), context -> new BoatRenderer(context, ModModelLayers.ALPINE_BOAT));
        //event.registerEntityRenderer(ModEntityTypes.ALPINE_CHEST_BOAT.get(), context -> new BoatRenderer(context, ModModelLayers.ALPINE_CHEST_BOAT));

        //event.registerBlockEntityRenderer(com.fuyuaki.r_wilderness.world.block.entity.ModBlockEntities.SIGN.get(), SignRenderer::new);
        //event.registerBlockEntityRenderer(com.fuyuaki.r_wilderness.world.block.entity.ModBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //event.registerLayerDefinition(ModModelLayers.ALPINE_BOAT, BoatModel::createBoatModel);
        //event.registerLayerDefinition(ModModelLayers.ALPINE_CHEST_BOAT,BoatModel::createChestBoatModel);
    }
    @SubscribeEvent
    public static void registerLayerDefinitions(RegisterDebugEntriesEvent event) {
        event.register(RDebugScreenEntries.LOADED_REGIONS_LIST, new DebugEntryRegionStatus());
    }

}
