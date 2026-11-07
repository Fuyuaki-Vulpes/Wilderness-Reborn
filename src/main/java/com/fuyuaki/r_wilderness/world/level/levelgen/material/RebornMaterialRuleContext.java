package com.fuyuaki.r_wilderness.world.level.levelgen.material;

import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.MaterialSystem;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public class RebornMaterialRuleContext extends MaterialRuleContext {
    private final RegionMemory memory;
    public RebornMaterialRuleContext(RegionMemory memory, MaterialSystem system, RandomState randomState, DensityVolume expectedVolume, DensitySamplerSet densitySamplers, Function<BlockPos, Holder<Biome>> biomeGetter, WorldGenerationContext context, @Nullable Set<Holder<Biome>> possibleBiomes) {
        super(system, randomState, expectedVolume, densitySamplers, biomeGetter, context, possibleBiomes);
        this.memory = memory;
    }

    @Override
    public int getMinSurfaceLevel() {
        if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int index = this.preliminarySurfaceVolume.indexOfBlock(this.blockX, 0, this.blockZ);
            if (index != -1) {
                if (this.preliminarySurfaceBuffer == null) {
                    this.preliminarySurfaceBuffer = DensityBuffer.createUnpooled(this.preliminarySurfaceVolume.size());
                    this.densitySamplers
                            .get(this.system.preliminarySurfaceFunction())
                            .sampleVolume(this.preliminarySurfaceBuffer, this.preliminarySurfaceVolume);
                }

            }
            this.minSurfaceLevel = Mth.floor(this.memory.getRegionAt(this.blockX, this.blockZ).layers().getElevation().getUpsampled(this.blockX, this.blockZ, this.memory)) + this.surfaceDepth - 8;
        }
        return this.minSurfaceLevel;
    }
}

