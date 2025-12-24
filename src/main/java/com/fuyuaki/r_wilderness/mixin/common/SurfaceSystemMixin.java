package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.mixin.accessor.BiomeAccessAccessor;
import com.fuyuaki.r_wilderness.world.level.biome.BiomeDitherer;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SurfaceSystem.class)
public class SurfaceSystemMixin {

    @ModifyVariable(
            method = {"buildSurface(Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;ZLnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/NoiseChunk;Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;)V"},
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private BiomeManager injectDitheringBiomeAccess(BiomeManager biomeAccess) {
        BiomeAccessAccessor biomeAccessAccessor = (BiomeAccessAccessor)biomeAccess;
        return new BiomeDitherer(biomeAccessAccessor.getBiomeSource(), biomeAccessAccessor.getSeed());
    }
}
