package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.client.gui.RWildernessLayers;
import com.fuyuaki.r_wilderness.client.menu.hud.RWildernessGui;
import com.fuyuaki.r_wilderness.client.model.block.TreeBlockStateModel;
import com.fuyuaki.r_wilderness.network.DrinkWaterInWorldPacket;
import com.fuyuaki.r_wilderness.world.block.woods.ModWoodTypes;
import net.minecraft.client.renderer.Sheets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(
                DrinkWaterInWorldPacket.TYPE,
                DrinkWaterInWorldPacket::handle
        );
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


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Sheets.addWoodType(ModWoodTypes.ALPINE);

    }

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
    public static void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(TreeBlockStateModel.Unbaked.ID, TreeBlockStateModel.Unbaked.CODEC);
    }
}
