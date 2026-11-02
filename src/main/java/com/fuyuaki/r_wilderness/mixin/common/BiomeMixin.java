package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.material.RFluids;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public class BiomeMixin {
    @WrapOperation(
            method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z",  at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/material/FluidState;is(Ljava/lang/Object;)Z"))
    private boolean freeze(FluidState state, Object fluid, Operation<Boolean> original) {
        if (state == null) {
            return false;
        } else {
            Fluid type = state.getType();
            return type == RFluids.RIVER.get() || type == RFluids.FLOWING_RIVER.get() || original.call(state, fluid);
        }
    }
}
