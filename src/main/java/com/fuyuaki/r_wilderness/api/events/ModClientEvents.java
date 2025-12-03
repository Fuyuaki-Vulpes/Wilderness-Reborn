package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.block.woods.ModWoodTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
    }


    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
//        event.register(ResourceLocation.fromNamespaceAndPath(MODID, "is_attacking"), IsAttacking.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerGuiComponents(RegisterGuiLayersEvent event) {

        event.registerAboveAll(RWildernessMod.modLocation("debug_ui"),((guiGraphics, deltaTracker) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && !minecraft.options.hideGui){
//                debugScreen.render(guiGraphics);
            }
        }));

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

}
