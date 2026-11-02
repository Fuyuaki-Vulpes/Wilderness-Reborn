package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.init.RDataComponents;
import com.fuyuaki.r_wilderness.world.item.components.Drinkable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;",ordinal = 0), cancellable = true)
    private void useMixin(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
        Drinkable drinkable = itemstack.get(RDataComponents.DRINKABLE);
        if (drinkable != null){
            cir.setReturnValue(drinkable.startConsuming(player,itemstack,hand));
        }

    }

    @Inject(method = "finishUsingItem",at = @At(value = "HEAD"), cancellable = true)
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir){
        Drinkable drinkable = stack.get(RDataComponents.DRINKABLE);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (drinkable != null && consumable == null){
            cir.setReturnValue(drinkable.onConsume(level,livingEntity,stack));
        }
    }
    @Inject(method = "getUseAnimation",at = @At(value = "HEAD"), cancellable = true)
    private void getUseAnimation(ItemStack stack, CallbackInfoReturnable<ItemUseAnimation> cir){
        Drinkable drinkable = stack.get(RDataComponents.DRINKABLE);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (drinkable != null && consumable == null){
            cir.setReturnValue(drinkable.animation());
        }
    }



    @Inject(method = "getUseDuration",at = @At(value = "HEAD"), cancellable = true)
    private void duration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir){
        Drinkable drinkable = stack.get(RDataComponents.DRINKABLE);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (drinkable != null){
            if (consumable != null){
                cir.setReturnValue(Math.max(consumable.consumeTicks(), drinkable.drinkTicks()));
            }else{
                cir.setReturnValue(drinkable.drinkTicks());
            }
        }
    }

}
