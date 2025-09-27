package com.fuyuaki.r_wilderness.mixin;


import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SoundEvent.class)
public class SoundEventMixin {


    @ModifyConstant (method = "getRange", constant = @Constant(floatValue = 16F), expect = 2)
    private float biggerSoundRange(float value) {

        return value * 6.0F;
    }


}
