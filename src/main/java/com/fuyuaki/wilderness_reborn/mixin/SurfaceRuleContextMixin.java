package com.fuyuaki.wilderness_reborn.mixin;


import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SurfaceRules.Context.class)
public abstract class SurfaceRuleContextMixin {


    @Shadow private int minSurfaceLevel;

    @Shadow private int surfaceDepth;

    @Shadow private long lastMinSurfaceLevelUpdate;

    @Shadow private long lastUpdateXZ;


    @Unique
    private static int wildernessReborn$blockCoordToSurfaceCell(int blockCoord) {
        return blockCoord >> 4;
    }
    @Shadow public int blockX;

    @Shadow public int blockZ;

    @Shadow private long lastPreliminarySurfaceCellOrigin;

    @Shadow @Final private int[] preliminarySurfaceCache;

    @Shadow @Final public NoiseChunk noiseChunk;

    @Unique
    private static int wildernessReborn$surfaceCellToBlockCoord(int surfaceCell) {
        return surfaceCell << 4;
    }

    @Inject(method = "getMinSurfaceLevel", at = @At("RETURN"), cancellable = true)
    private void getMinSurfaceLevelMixin(CallbackInfoReturnable<Integer> cir) {
        if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int i = wildernessReborn$blockCoordToSurfaceCell(this.blockX);
            int j = wildernessReborn$blockCoordToSurfaceCell(this.blockZ);
            long k = ChunkPos.asLong(i, j);
            if (this.lastPreliminarySurfaceCellOrigin != k) {
                this.lastPreliminarySurfaceCellOrigin = k;
                this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(wildernessReborn$surfaceCellToBlockCoord(i), wildernessReborn$surfaceCellToBlockCoord(j));
                this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(wildernessReborn$surfaceCellToBlockCoord(i + 1), wildernessReborn$surfaceCellToBlockCoord(j));
                this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(wildernessReborn$surfaceCellToBlockCoord(i), wildernessReborn$surfaceCellToBlockCoord(j + 1));
                this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(wildernessReborn$surfaceCellToBlockCoord(i + 1), wildernessReborn$surfaceCellToBlockCoord(j + 1));
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
            this.minSurfaceLevel = l + this.surfaceDepth;
        }

        cir.setReturnValue(this.minSurfaceLevel);
    }

}
