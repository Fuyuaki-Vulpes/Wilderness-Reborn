package com.fuyuaki.r_wilderness.mixin;

import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldPresets.class)
public abstract class WorldPresetsMixin {



//    @Inject(method = "createNormalWorldDimensions",at = @At("HEAD"), cancellable = true)
//    private static void createNormalWorldDimensions(HolderLookup.Provider registries, CallbackInfoReturnable<WorldDimensions> cir) {
//        cir.setReturnValue(registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WRWorldPresets.REBORN_WORLD).value().createWorldDimensions());
//
//    }
}
