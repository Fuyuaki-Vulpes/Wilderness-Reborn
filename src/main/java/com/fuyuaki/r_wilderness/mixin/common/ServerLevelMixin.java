package com.fuyuaki.r_wilderness.mixin.common;


import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntityGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.WritableLevelData;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ServerEntityGetter, WorldGenLevel {
    @Shadow @Final private SleepStatus sleepStatus;

    @Shadow public abstract List<ServerPlayer> players();

    @Shadow @Final private List<ServerPlayer> players;

    @Shadow public abstract GameRules getGameRules();

    @Shadow public abstract void setDayTime(long time);

    @Shadow protected abstract void wakeUpAllPlayers();

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }


    @Redirect(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z"))
    private boolean skipThroughNight(SleepStatus instance, int requiredSleepPercentage){
        return false;
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;updateSkyBrightness()V"))
    private void tickWorld(BooleanSupplier hasTimeLeft, CallbackInfo ci){
        int i = this.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE);


        if (this.sleepStatus.areEnoughSleeping(i) && this.sleepStatus.areEnoughDeepSleeping(i, this.players)) {
            if (this.getGameRules().get(GameRules.ADVANCE_TIME)) {
                long k = this.levelData.getDayTime() + 600L;
                this.setDayTime(EventHooks.onSleepFinished((ServerLevel) (Object) this, k - k % 600L, this.getDayTime()));
            }
        }
        if (this.sleepStatus.amountSleeping() > 0 && this.levelData.getDayTime() > 23400) {
            this.wakeUpAllPlayers();
        }

    }


    @Redirect(method = "updateSleepingPlayerList",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;announceSleepStatus()V"))
    private void mixinUpdateSleepingPlayerAnnounceSleep(ServerLevel instance){

        int playerCount = instance.players().size();
        int playerSleepMinCount = Math.max(1,(instance.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE) * playerCount) / 100);
        int asleepPlayers = this.sleepStatus.amountSleeping();

        float speedMultiplier = 1 + (((float) asleepPlayers / (float) playerSleepMinCount) * 3);
        int speedMultiplierPercent = (int) (speedMultiplier * 100);

        MutableComponent message = Component.translatable("system.r_wilderness.asleepMessage",asleepPlayers,playerSleepMinCount, speedMultiplierPercent);
        if (speedMultiplier  > 2.0F) {
            if (speedMultiplier > 3.0F) {
                if(speedMultiplier >= 4.0F){
                    message.withStyle(ChatFormatting.GOLD,ChatFormatting.BOLD,ChatFormatting.ITALIC);
                }else{
                    message.withStyle(ChatFormatting.GREEN,ChatFormatting.BOLD);
                }
            } else{
                message.withStyle(ChatFormatting.AQUA);
            }
        }else{
            message = Component.translatable("system.r_wilderness.asleepMessage.cancel").withStyle(ChatFormatting.GRAY);
        }

        if (asleepPlayers >= 1) {
            for (ServerPlayer serverplayer : this.players) {
                serverplayer.displayClientMessage(message, true);
            }        }

    }
}
