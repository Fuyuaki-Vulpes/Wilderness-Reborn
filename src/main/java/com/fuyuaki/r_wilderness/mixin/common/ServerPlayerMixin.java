package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.api.common.ModTags;
import com.fuyuaki.r_wilderness.init.RAttachments;
import com.fuyuaki.r_wilderness.world.environment.HydrationData;
import com.fuyuaki.r_wilderness.world.environment.HydrationProperties;
import com.fuyuaki.r_wilderness.world.environment.ServerPlayerEnvironment;
import com.fuyuaki.r_wilderness.world.environment.TemperatureData;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements ServerPlayerEnvironment {



    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void doTick(CallbackInfo ci) {
        TemperatureData temperatureData = ((ServerPlayer) (Object) this).getExistingDataOrNull(RAttachments.TEMPERATURE);
        if (temperatureData == null) {
            ((ServerPlayer) (Object) this).setData(RAttachments.TEMPERATURE, new TemperatureData());
        } else {
            temperatureData.tick(((ServerPlayer) (Object) this));
            ((ServerPlayer) (Object) this).setData(RAttachments.TEMPERATURE, temperatureData);
        }


        HydrationData hydrationData = ((ServerPlayer) (Object) this).getExistingDataOrNull(RAttachments.HYDRATION);
        if (hydrationData == null) {
            ((ServerPlayer) (Object) this).setData(RAttachments.HYDRATION, new HydrationData());
        } else {
            hydrationData.tick(((ServerPlayer) (Object) this));
            ((ServerPlayer) (Object) this).setData(RAttachments.HYDRATION, hydrationData);

        }

    }

    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void jump(CallbackInfo ci) {
        this.addEnergy(0.0005F);
    }


    @Inject(method = "checkMovementStatistics", at = @At("RETURN"))
    private void swimInWaterMovement(double dx, double dy, double dz, CallbackInfo ci) {
        if (!this.isPassenger() && !didNotMove(dx, dy, dz)) {
            double movementDist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (this.isSwimming()) {
                int movement = Math.round((float) movementDist * 100.0F);
                if (movement > 0) {
                    this.addEnergy(movement * 0.0015F);

                }
            } else if (this.isEyeInFluid(FluidTags.WATER)) {
                int movement = Math.round((float) movementDist * 100.0F);
                if (movement > 0) {
                    this.addExhaustion(0.01F * movement * 0.0075F);
                    this.addEnergy(movement * 0.001F);

                }
            } else {
                double movementDistHorizontal = Math.sqrt(dx * dx + dz * dz);
                if (this.isInWater()) {
                    int movement = Math.round((float) movementDistHorizontal * 100.0F);
                    if (movement > 0) {
                        this.addExhaustion(0.01F * movement * 0.0075F);
                        this.addEnergy(movement * 0.00075F);
                    }
                } else if (this.onClimbable()) {
                    if (dy > 0.0) {
                        this.addEnergy(Math.round(dy * 100.0) * 0.0005F);
                    }
                } else if (this.onGround()) {
                    int movement = Math.round((float) movementDistHorizontal * 100.0F);
                    if (movement > 0) {
                        if (this.isSprinting()) {
                            this.addExhaustion(0.1F * movement * 0.0075F);
                            this.addEnergy(movement * 0.005F);
                        } else if (this.isCrouching()) {
                            this.addExhaustion(0.0F * movement * 0.0075F);
                            this.addEnergy(movement * 0.0005F);

                        } else {
                            this.addExhaustion(0.0F * movement * 0.0075F);
                            this.addEnergy(movement * 0.0025F);
                        }
                    }
                }
            }
        }


    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void hurtServer(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.is(ModTags.DamageTypes.CAUSES_TEMPERATURE_DECREASE)){
            this.addCooling(amount * 0.01F);

        } else if (damageSource.is(ModTags.DamageTypes.CAUSES_TEMPERATURE_INCREASE)){
            this.addHeat(amount * 0.01F);
        }
    }

    private static boolean didNotMove(double dx, double dy, double dz) {
        return dx == 0.0 && dy == 0.0 && dz == 0.0;
    }


    @Override
    public HydrationData getHydrationData() {
        return ((ServerPlayer) (Object) this).getData(RAttachments.HYDRATION);
    }

    @Override
    public TemperatureData getTemperatureData() {
        return ((ServerPlayer) (Object) this).getData(RAttachments.TEMPERATURE);
    }

    @Override
    public void addExhaustion(float exhaustion) {
        HydrationData data = this.getHydrationData();
        data.addExhaustion(exhaustion);
        ((ServerPlayer) (Object) this).setData(RAttachments.HYDRATION, data);

    }

    @Override
    public void addEnergy(float energy) {
        TemperatureData data = this.getTemperatureData();
        data.addEnergy(energy);
        ((ServerPlayer) (Object) this).setData(RAttachments.TEMPERATURE, data);

    }

    @Override
    public void drink(int hydrationLevelModifier, float saturationLevelModifier) {
        HydrationData data = this.getHydrationData();
        data.drink(hydrationLevelModifier,saturationLevelModifier);
        ((ServerPlayer) (Object) this).setData(RAttachments.HYDRATION, data);

    }
    @Override
    public void drinkInWorld(int hydrationLevelModifier, float saturationLevelModifier) {
        HydrationData data = this.getHydrationData();
        data.drink(hydrationLevelModifier,saturationLevelModifier);
        data.triggerCooldown();
        ((ServerPlayer) (Object) this).setData(RAttachments.HYDRATION, data);

    }

    @Override
    public void drink(HydrationProperties foodProperties) {
        HydrationData data = this.getHydrationData();
        data.drink(foodProperties);
        ((Player) (Object) this).setData(RAttachments.HYDRATION, data);
    }

    @Override
    public void addHeat(float heat) {
        TemperatureData data = this.getTemperatureData();
        data.heatUp(heat);
        ((Player) (Object) this).setData(RAttachments.TEMPERATURE, data);
    }
    @Override
    public void addCooling(float cooling) {
        TemperatureData data = this.getTemperatureData();
        data.heatUp(cooling);
        ((Player) (Object) this).setData(RAttachments.TEMPERATURE, data);
    }
}
