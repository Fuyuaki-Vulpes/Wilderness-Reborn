package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.init.RAttachments;
import com.fuyuaki.r_wilderness.init.RAttributes;
import com.fuyuaki.r_wilderness.world.environment.HydrationData;
import com.fuyuaki.r_wilderness.world.environment.HydrationProperties;
import com.fuyuaki.r_wilderness.world.environment.PlayerEnvironment;
import com.fuyuaki.r_wilderness.world.environment.TemperatureData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerEnvironment {
    @Shadow public int experienceLevel;

    @Shadow protected abstract void turtleHelmetTick();

    @Shadow protected abstract void damageStatsAndHearts(Entity entity, float healthBeforeAttack);

    @Inject(method = "getXpNeededForNextLevel", at = @At(value = "HEAD"), cancellable = true)
    private void getXpNeededForNextLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(100);
    }
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void attack(CallbackInfo ci) {
        TemperatureData data = this.getTemperatureData();
        data.addEnergy(0.05F);
        ((Player) (Object) this).setData(RAttachments.TEMPERATURE, data);
    }

    @Inject(method = "stabAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void stabAttack(EquipmentSlot p_454961_, Entity p_455450_, float p_454993_, boolean p_455515_, boolean p_455235_, boolean p_454651_, CallbackInfoReturnable<Boolean> cir) {
        TemperatureData data = this.getTemperatureData();
        data.addEnergy(0.05F);
        ((Player) (Object) this).setData(RAttachments.TEMPERATURE, data);
    }


    @Override
    public HydrationData getHydrationData() {
        return ((Player) (Object) this).getData(RAttachments.HYDRATION);
    }

    @Override
    public TemperatureData getTemperatureData() {
        return ((Player) (Object) this).getData(RAttachments.TEMPERATURE);
    }

    @Override
    public double getInsulation() {
        AttributeInstance insulation = ((Player) (Object) this).getAttribute(RAttributes.BODY_INSULATION);
        if (insulation == null) {
            return 0;
        }
        return insulation.getValue();
    }

}
