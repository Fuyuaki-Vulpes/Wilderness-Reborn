package com.fuyuaki.wilderness_reborn.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {

    @Shadow @Final private Biome.ClimateSettings climateSettings;

    @Shadow public abstract float getBaseTemperature();

    @Shadow @Final private static PerlinSimplexNoise TEMPERATURE_NOISE;

    @Inject(method = "getHeightAdjustedTemperature", at = @At("HEAD"), cancellable = true)
    private void modifyHeightTempVariable(BlockPos pos, int seaLevel, CallbackInfoReturnable<Float> cir){
        float f = this.climateSettings.temperatureModifier().modifyTemperature(pos, this.getBaseTemperature());
        int i = seaLevel + 17;
        if (pos.getY() > i) {
            float f1 = (float)(TEMPERATURE_NOISE.getValue(pos.getX() / 8.0F, pos.getZ() / 8.0F, false) * 8.0);
            cir.setReturnValue(f - (f1 + pos.getY() - i) * 0.05F / 300.0F);
        } else {
            cir.setReturnValue(f);
        }
    }
}
