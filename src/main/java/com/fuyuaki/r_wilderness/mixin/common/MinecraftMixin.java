package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.client.gui.screens.WorldGenLoadProgressScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique
    private LevelLoadingScreen chunkScreen;
    @Unique
    private WorldGenLoadProgressScreen regionCreationScreen;


    @ModifyArg(method = "doWorldLoad",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private @Nullable Screen redirectScreen(
            @Nullable Screen screen){
        this.chunkScreen = (LevelLoadingScreen)screen;
        WorldGenLoadProgressScreen worldGenLoadProgressScreen = new WorldGenLoadProgressScreen();
        this.regionCreationScreen = worldGenLoadProgressScreen;
        return worldGenLoadProgressScreen;
    }
    @Redirect(method = "doWorldLoad",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;tick()V"))
    private void doLoadTick(
            LevelLoadingScreen instance){
        this.regionCreationScreen.tick();

    }
}
