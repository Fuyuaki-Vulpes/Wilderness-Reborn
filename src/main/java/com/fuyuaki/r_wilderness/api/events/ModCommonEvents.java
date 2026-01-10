package com.fuyuaki.r_wilderness.api.events;

import com.fuyuaki.r_wilderness.api.WildRegistries;
import com.fuyuaki.r_wilderness.init.RAttributes;
import com.fuyuaki.r_wilderness.server.commands.ModCommands;
import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomePlacement;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID)
public class ModCommonEvents {


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


}
