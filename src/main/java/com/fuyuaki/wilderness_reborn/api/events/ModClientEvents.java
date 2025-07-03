package com.fuyuaki.wilderness_reborn.api.events;

import com.fuyuaki.wilderness_reborn.api.WildernessRebornMod;
import com.fuyuaki.wilderness_reborn.client.gui.ModDebugScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

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

        event.registerAboveAll(WildernessRebornMod.mod("debug_ui"),((guiGraphics, deltaTracker) -> {
            Minecraft minecraft = Minecraft.getInstance();
            ModDebugScreen debugScreen = new ModDebugScreen(minecraft);
            if (minecraft.player != null && !minecraft.options.hideGui){
                debugScreen.render(guiGraphics);
            }
        }));


    }
}
