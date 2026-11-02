package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.material.RFluids;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {

    @Inject(method = "isSame", at = @At("HEAD"), cancellable = true)
    private void isSameMixin(Fluid other, CallbackInfoReturnable<Boolean> cir) {
        if (other == RFluids.RIVER.get() || other == RFluids.FLOWING_RIVER.get()) {
            cir.setReturnValue(true);
        }

    }
}
