package com.fuyuaki.wilderness_reborn.mixin;


import com.fuyuaki.wilderness_reborn.data.worldgen.ModSurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.TBSurfaceRuleData;

@Mixin(TBSurfaceRuleData.class)
public abstract class MixinTerrablenderSurfaceRuleData {

    @Inject(method = "overworld",at = @At("HEAD"), cancellable = true)
    private static void overworldSurfaceRulesBase(CallbackInfoReturnable<SurfaceRules.RuleSource> cir){
        cir.setReturnValue(ModSurfaceRuleData.overworld());
    }
}
