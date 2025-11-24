package com.fuyuaki.r_wilderness.mixin;

import com.fuyuaki.r_wilderness.world.level.levelgen.ModWorldPresets;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @ModifyArg(method = "openFresh(Lnet/minecraft/client/Minecraft;Ljava/lang/Runnable;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldCallback;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;openCreateWorldScreen(Lnet/minecraft/client/Minecraft;Ljava/lang/Runnable;Ljava/util/function/Function;Lnet/minecraft/client/gui/screens/worldselection/WorldCreationContextMapper;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/gui/screens/worldselection/CreateWorldCallback;)V"),
    index = 4)
    private static ResourceKey<WorldPreset> openFreshMixin(ResourceKey<WorldPreset> preset){

        return ModWorldPresets.REBORN_WORLD;
    }
}
