package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.init.RAttributes;
import com.fuyuaki.r_wilderness.init.RDataComponents;
import com.fuyuaki.r_wilderness.network.DrinkWaterInWorldPacket;
import com.fuyuaki.r_wilderness.server.commands.ModCommands;
import com.fuyuaki.r_wilderness.world.environment.PlayerEnvironment;
import com.fuyuaki.r_wilderness.world.item.components.Drinkables;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomePlacement;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID)
public class RCommonEvents {


    @SubscribeEvent
    public static void onDatapackRegistry(DataPackRegistryEvent.NewRegistry event){
        event.dataPackRegistry(
                WildRegistries.REBORN_BIOME_PLACEMENT_KEY,
                RebornBiomePlacement.CODEC,
                RebornBiomePlacement.CODEC

        );
    }

    @SubscribeEvent
    public static void commandRegistry(RegisterCommandsEvent event){
        ModCommands.init(event.getCommandSelection(),event.getBuildContext(),event.getDispatcher());
    }

    @SubscribeEvent
    public static void modifyEntitiesAttributes(EntityAttributeModificationEvent event){
        event.add(EntityType.PLAYER, RAttributes.BODY_INSULATION);
    }

  @SubscribeEvent
    public static void interactWithBlock(PlayerInteractEvent.RightClickBlock event){
      ((PlayerEnvironment)event.getEntity()).getHydrationData().tryDrinkingWaterSource(event.getEntity(),event.getHand());
    }

  @SubscribeEvent
    public static void modifyComponentsEvent(ModifyDefaultComponentsEvent event){
        event.modify(Items.APPLE, builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.APPLE));
        event.modify(Items.POTION, builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.WATER_BOTTLE));
        event.modify(Items.SUSPICIOUS_STEW,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.MUSHROOM_STEW,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.RABBIT_STEW,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.HONEY_BOTTLE,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.HONEY));
        event.modify(Items.MELON_SLICE,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.WATERMELON));
        event.modify(Items.GLISTERING_MELON_SLICE,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.GLISTERING_WATERMELON));
        event.modify(Items.MILK_BUCKET,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.MILK));
        event.modify(Items.BEETROOT,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BEETROOT));
        event.modify(Items.BEETROOT_SOUP,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BEETROOT_SOUP));
        event.modify(Items.SWEET_BERRIES,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BERRY));
        event.modify(Items.GLOW_BERRIES,builder -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BERRY));

    }

    @SubscribeEvent
    public static void registerPayloadPackets(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(DrinkWaterInWorldPacket.TYPE,
                DrinkWaterInWorldPacket.STREAM_CODEC,
                DrinkWaterInWorldPacket::handle);
    }




}
