package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.mixin.accessor.BiomeAccessAccessor;
import com.fuyuaki.r_wilderness.world.level.biome.BiomeDitherer;
import net.minecraft.world.level.biome.BiomeManager;
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
