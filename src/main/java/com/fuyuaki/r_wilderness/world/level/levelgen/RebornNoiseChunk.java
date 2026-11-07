package com.fuyuaki.r_wilderness.world.level.levelgen;

import com.fuyuaki.r_wilderness.world.generation.region.RegionMemory;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.DensityBufferPool;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.jspecify.annotations.Nullable;

public class RebornNoiseChunk implements AutoCloseable {
    private final RegionMemory memory;
    private final RandomState randomState;
    private final DensityVolume volume;
    private final Aquifer aquifer;
    private final DensityBufferPool bufferPool;
    private final DensitySamplerSet cachingSamplers;

    public RebornNoiseChunk(
            RegionMemory memory,
            RandomState randomState,
            @Nullable Beardifier beardifier,
            RebornWorldSettings settings,
            Aquifer.FluidPicker globalFluidPicker,
            Blender blender,
            DensityVolume volume
    ) {
        this.memory = memory;
        this.randomState = randomState;
        this.volume = volume;
        this.bufferPool = randomState.acquireDensityBufferPool();
        ContextMap.Builder samplerUserFields = ContextMap.builder().set(Beardifier.CONTEXT_KEY, beardifier);
        if (!blender.isEmpty()) {
            Blender.OutputBuffer blendBuffer = blender.blendOffsetAndFactor(volume);
            samplerUserFields.set(Blender.CONTEXT_KEY, blender)
                    .set(Blender.ALPHA_KEY, blendBuffer.createAlphaSampler())
                    .set(Blender.OFFSET_KEY, blendBuffer.createOffsetSampler());
        }

        this.cachingSamplers = randomState.samplersWithContext(
                SamplerContext.builder().setUserFields(samplerUserFields.build()).useBufferArena(this.bufferPool).enableCaches().build()
        );
        if (settings.aquifers().isPresent()) {
            this.aquifer = RebornAquifer.createFrom(memory,settings.aquifers()
                            .get(),this.cachingSamplers, randomState.getOrCreateRandomFactory(Identifier.withDefaultNamespace("aquifer")), volume, globalFluidPicker);
        } else {
            this.aquifer = Aquifer.createDisabled(globalFluidPicker);
        }
    }

    public DensityVolume volume() {
        return this.volume;
    }

    public Aquifer aquifer() {
        return this.aquifer;
    }

    public DensitySamplerSet cachingSamplers() {
        return this.cachingSamplers;
    }

    @Override
    public void close() {
        this.randomState.releaseDensityBufferPool(this.bufferPool);
    }
}