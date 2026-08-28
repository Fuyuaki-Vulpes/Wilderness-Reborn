package com.fuyuaki.r_wilderness.mixin.client;

import com.fuyuaki.r_wilderness.client.gui.screens.WorldGenLoadProgressScreen;
import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique
    private LevelLoadingScreen chunkScreen;
    @Unique
    private WorldGenLoadProgressScreen regionCreationScreen;
    @Inject(method = "doWorldLoad",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void createRegionScreen(
            LevelStorageSource.LevelStorageAccess levelSourceAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Optional<GameRules> gameRules,
            boolean newWorld, CallbackInfo ci) {
        this.regionCreationScreen  =
                new WorldGenLoadProgressScreen(
                        null);
    }

    @Redirect(method = "doWorldLoad",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
    private void redirectScreen(
            Gui instance, Screen screen){
        this.chunkScreen = (LevelLoadingScreen)screen;
        instance.setScreen(this.regionCreationScreen);
    }
    @Inject(method = "doWorldLoad",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;tick()V"))
    private void doLoadTick(
            LevelStorageSource.LevelStorageAccess levelSourceAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Optional<GameRules> gameRules,
            boolean newWorld, CallbackInfo ci){
        if (this.regionCreationScreen.getTracker() == null) {
            ServerLevel overworld = ((Minecraft) (Object) this).getSingleplayerServer().overworld();
            if (overworld == null) return;
            this.regionCreationScreen.setTracker(RegionMemory.getInstance(
                    overworld).getTracker());
        }
        boolean regionCreationIsDone = this.regionCreationScreen.isComplete();
        if (regionCreationIsDone) {
            ((Minecraft) (Object) this).gui.setScreen(this.chunkScreen);
            return;
        }
        this.regionCreationScreen.tick();

    }
}
