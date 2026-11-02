package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.mixin.accessor.BiomeAccessAccessor;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomeDitherer;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SurfaceSystem.class)
public class SurfaceSystemMixin {

    @ModifyVariable(
            method = {"buildSurface"},
            at = @At("HEAD"),
            argsOnly = true, name = "biomeManager")
    private BiomeManager injectDitheringBiomeAccess(BiomeManager biomeAccess) {
        BiomeAccessAccessor biomeAccessAccessor = (BiomeAccessAccessor)biomeAccess;
        return new BiomeDitherer(biomeAccessAccessor.getBiomeSource(), biomeAccessAccessor.getSeed());
    }
}
