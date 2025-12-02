package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import com.fuyuaki.r_wilderness.world.level.levelgen.WildWorldSettings;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldDimensions.class)
public class WorldDimensionsMixin {

    @Inject(method = "isStableOverworld", at = @At("HEAD"), cancellable = true)
    private static void isStableOverworldMixin(LevelStem levelStem, CallbackInfoReturnable<Boolean> cir){
        Holder<DimensionType> holder = levelStem.type();
        if (holder.is(WildWorldSettings.DimensionTypes.OVERWORLD)){
            if(levelStem.generator().getBiomeSource() instanceof RebornBiomeSource){
                cir.setReturnValue(true);
            }
            cir.setReturnValue(!(
                    levelStem.generator().getBiomeSource() instanceof MultiNoiseBiomeSource multinoisebiomesource
                            && !multinoisebiomesource.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)));
        }
    }
}
