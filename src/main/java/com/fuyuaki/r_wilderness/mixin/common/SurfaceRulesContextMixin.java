package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.generation.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.generation.terrain.SurfaceRulesContextExtension;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRules.Context.class)
public abstract class SurfaceRulesContextMixin implements SurfaceRulesContextExtension {

    @Shadow @Final public NoiseChunk noiseChunk;
    @Shadow public int blockX;
    @Shadow public int blockZ;


    @Shadow private long lastMinSurfaceLevelUpdate;
    @Shadow private long lastUpdateXZ;
    @Shadow private int minSurfaceLevel;
    @Shadow private int surfaceDepth;
    @Shadow @Final public WorldGenerationContext context;
    @Shadow @Final public RandomState randomState;
    @Shadow public int blockY;
    @Shadow @Final public ChunkAccess chunk;
    @Shadow @Final private int[] preliminarySurfaceCache;


    @Shadow private long lastPreliminarySurfaceCellOrigin;
    @Unique
    @Nullable
    private WRNoiseChunk wildNoiseChunk = null;

    private static int blockCoordToSurfaceCell(int blockCoord) {
        return blockCoord >> 4;
    }

    private static int surfaceCellToBlockCoord(int surfaceCell) {
        return surfaceCell << 4;
    }

    @Override
    public void setNoiseChunk(@Nullable WRNoiseChunk noiseChunk) {
        wildNoiseChunk = noiseChunk;
    }

    @Nullable
    @Override
    public WRNoiseChunk getNoiseChunk() {
        return wildNoiseChunk;
    }



    @Inject(method = "getMinSurfaceLevel", at = @At("HEAD"), cancellable = true)
    private void getMinSurfaceLevel(CallbackInfoReturnable<Integer> cir){
        if (this.noiseChunk == null && this.wildNoiseChunk != null){
            if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
                this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
                int i = blockCoordToSurfaceCell(this.blockX);
                int j = blockCoordToSurfaceCell(this.blockZ);
                long k = ChunkPos.asLong(i, j);
                if (this.lastPreliminarySurfaceCellOrigin != k) {
                    this.lastPreliminarySurfaceCellOrigin = k;
                    this.preliminarySurfaceCache[0] = this.wildNoiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j));
                    this.preliminarySurfaceCache[1] = this.wildNoiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j));
                    this.preliminarySurfaceCache[2] = this.wildNoiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j + 1));
                    this.preliminarySurfaceCache[3] = this.wildNoiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j + 1));
                }

                int l = Mth.floor(
                        Mth.lerp2(
                                (this.blockX & 15) / 16.0F,
                                (this.blockZ & 15) / 16.0F,
                                this.preliminarySurfaceCache[0],
                                this.preliminarySurfaceCache[1],
                                this.preliminarySurfaceCache[2],
                                this.preliminarySurfaceCache[3]
                        )
                );
                this.minSurfaceLevel = l + this.surfaceDepth - 8;
            }

            cir.setReturnValue(this.minSurfaceLevel);
        }
    }
}
