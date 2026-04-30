package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.init.RDataComponents;
import com.fuyuaki.r_wilderness.world.item.components.Drinkable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public abstract class ConsumableMixin {


    @Shadow public abstract boolean equals(Object par1);

    @Inject(method = "onConsume",at = @At(value = "HEAD"))
    private void onConsume(Level level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<ItemStack> cir){
        Drinkable drinkable = stack.get(RDataComponents.DRINKABLE);
        if (drinkable != null){
            drinkable.onConsumeAdditive(level,entity,stack);
        }
    }
}
