package com.fuyuaki.wilderness_reborn.api.events;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import com.fuyuaki.wilderness_reborn.world.level.block.entity.ModBlockEntities;
import com.fuyuaki.wilderness_reborn.world.level.block.woods.ModWoodTypes;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.level.FoliageColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(WildernessRebornMod::setupRenderTypes);
        Sheets.addWoodType(ModWoodTypes.ALPINE);

    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        //event.registerEntityRenderer(ModEntityTypes.ALPINE_BOAT.get(), context -> new BoatRenderer(context, ModModelLayers.ALPINE_BOAT));
        //event.registerEntityRenderer(ModEntityTypes.ALPINE_CHEST_BOAT.get(), context -> new BoatRenderer(context, ModModelLayers.ALPINE_CHEST_BOAT));

        event.registerBlockEntityRenderer(ModBlockEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //event.registerLayerDefinition(ModModelLayers.ALPINE_BOAT, BoatModel::createBoatModel);
        //event.registerLayerDefinition(ModModelLayers.ALPINE_CHEST_BOAT,BoatModel::createChestBoatModel);
    }

    @SubscribeEvent
    public static void registerColoredBlocks(RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> pLevel != null && pPos != null ? BiomeColors.getAverageFoliageColor(pLevel, pPos) : FoliageColor.FOLIAGE_EVERGREEN, ModBlocks.ALPINE_LEAVES.get());

    }
}