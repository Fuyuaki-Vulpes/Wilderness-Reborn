package com.fuyuaki.r_wilderness.mixin.common;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @ModifyVariable(
            method = {"createExperience"},
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private static float experienceBuff(float value) {
        return value * 10;
    }
}
