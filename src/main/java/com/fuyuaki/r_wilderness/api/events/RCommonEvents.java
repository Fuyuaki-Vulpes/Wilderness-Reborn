package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.init.RAttributes;
import com.fuyuaki.r_wilderness.init.RDataComponents;
import com.fuyuaki.r_wilderness.network.DrinkWaterInWorldPacket;
import com.fuyuaki.r_wilderness.server.commands.RCommands;
import com.fuyuaki.r_wilderness.util.PreemptiveGenerationCollector;
import com.fuyuaki.r_wilderness.world.environment.PlayerEnvironment;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import com.fuyuaki.r_wilderness.world.item.components.Drinkables;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewDatapackRegistryEvent;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID)
public class RCommonEvents {


    @SubscribeEvent
    public static void onDatapackRegistry(NewDatapackRegistryEvent event){
        event.worldRegistry(
                RRegistries.REBORN_BIOME_PLACEMENT_KEY,
                BiomePlacementData.CODEC,
                BiomePlacementData.CODEC

        );
        event.worldRegistry(
                RRegistries.WRGEN_SETTINGS,
                RebornWorldSettings.DIRECT_CODEC,
                RebornWorldSettings.DIRECT_CODEC
        );
    }

    @SubscribeEvent
    public static void commandRegistry(RegisterCommandsEvent event){
        RCommands.init(event.getCommandSelection(),event.getBuildContext(),event.getDispatcher());
    }

    @SubscribeEvent
    public static void modifyEntitiesAttributes(EntityAttributeModificationEvent event){
        event.add(EntityTypes.PLAYER, RAttributes.BODY_INSULATION);
    }

  @SubscribeEvent
    public static void interactWithBlock(PlayerInteractEvent.RightClickBlock event){
      ((PlayerEnvironment)event.getEntity()).getHydrationData().tryDrinkingWaterSource(event.getEntity(),event.getHand());
    }

  @SubscribeEvent
    public static void modifyComponentsEvent(ModifyDefaultComponentsEvent event){
        event.modify(Items.APPLE, (builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.APPLE));
        event.modify(Items.POTION, (builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.WATER_BOTTLE));
        event.modify(Items.SUSPICIOUS_STEW,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.MUSHROOM_STEW,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.RABBIT_STEW,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.STEW));
        event.modify(Items.HONEY_BOTTLE,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.HONEY));
        event.modify(Items.MELON_SLICE,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.WATERMELON));
        event.modify(Items.GLISTERING_MELON_SLICE,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.GLISTERING_WATERMELON));
        event.modify(Items.MILK_BUCKET,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.MILK));
        event.modify(Items.BEETROOT,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BEETROOT));
        event.modify(Items.BEETROOT_SOUP,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BEETROOT_SOUP));
        event.modify(Items.SWEET_BERRIES,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BERRY));
        event.modify(Items.GLOW_BERRIES,(builder,context,key) -> builder.set(RDataComponents.DRINKABLE.get(), Drinkables.BERRY));

    }

    @SubscribeEvent
    public static void registerPayloadPackets(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(DrinkWaterInWorldPacket.TYPE,
                DrinkWaterInWorldPacket.STREAM_CODEC,
                DrinkWaterInWorldPacket::handle);
    }
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level
                && level.dimension() == Level.OVERWORLD) {

//            RegionGenerationService.getInstance().stop(); // background threads first
            RegionMemory.getInstance(level).shutdown();        // then clear the cache
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level
                && level.dimension() == Level.OVERWORLD) {
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (event.getServer().getTickCount() % 20 == 0) { // Every 20 Ticks
            RegionMemory.getInstance(event.getServer().overworld()).refresh(event.getServer().overworld());
        }
        if (event.getServer().getTickCount() % 5 == 0) { //Every 5 Ticks
            RegionMemory.getInstance(event.getServer().overworld()).updateManager();
        }
    }



    @SubscribeEvent
    public static void serverStoppedEvent(ServerStoppedEvent event){
        PreemptiveGenerationCollector.instance().flush();
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event){
        PreemptiveGenerationCollector.instance().backupInit(event.getServer());
    }



}
