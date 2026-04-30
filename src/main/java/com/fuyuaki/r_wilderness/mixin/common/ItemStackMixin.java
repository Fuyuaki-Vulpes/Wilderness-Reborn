package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.init.RDataComponents;
import com.fuyuaki.r_wilderness.world.item.components.Drinkable;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder, net.neoforged.neoforge.common.MutableDataComponentHolder, net.neoforged.neoforge.common.extensions.IItemStackExtension {


    @Inject(method = "onUseTick",at = @At(value = "HEAD"))
    private void onUseTick(Level level, LivingEntity livingEntity, int remainingUseDuration, CallbackInfo ci){
        Consumable consumable = this.get(DataComponents.CONSUMABLE);
        Drinkable drinkable = this.get(RDataComponents.DRINKABLE);
        if (drinkable != null && consumable == null && drinkable.shouldEmitParticlesAndSounds(remainingUseDuration)){
            drinkable.emitParticlesAndSounds(livingEntity.getRandom(), livingEntity, ((ItemStack)(Object)this), 5);

        }
    }

}
