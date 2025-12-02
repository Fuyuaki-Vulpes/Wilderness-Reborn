package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(PieceGeneratorSupplier.Context.class)
public class PieceGeneratorSupplierContextMixin {
    @Shadow @Final private ChunkGenerator chunkGenerator;

    @Shadow @Final private ChunkPos chunkPos;

    @Shadow @Final private LevelHeightAccessor heightAccessor;

    @Shadow @Final private RandomState randomState;

    @Shadow @Final private Predicate<Holder<Biome>> validBiome;

    @Inject(method = "validBiomeOnTop", at = @At(value = "HEAD"), cancellable = true)
    private void validBiomeOnTop(Heightmap.Types heightmapType, CallbackInfoReturnable<Boolean> cir){
        BiomeSource source = this.chunkGenerator.getBiomeSource();
        if (source instanceof RebornBiomeSource rebornBiomeSource) {
            int x = this.chunkPos.getMiddleBlockX();
            int z = this.chunkPos.getMiddleBlockZ();
            int y = this.chunkGenerator.getFirstOccupiedHeight(x, z, heightmapType, this.heightAccessor, this.randomState);
            Holder<Biome> holder = rebornBiomeSource
                    .getNoiseBiome(x, y, z, this.randomState.sampler());
            cir.setReturnValue(this.validBiome.test(holder));
        }
    }


}
