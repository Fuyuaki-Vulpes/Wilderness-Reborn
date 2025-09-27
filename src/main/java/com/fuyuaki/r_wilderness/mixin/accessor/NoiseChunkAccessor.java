package com.fuyuaki.r_wilderness.mixin.accessor;

import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(NoiseChunk.class)
public interface NoiseChunkAccessor {

    @Accessor("cellWidth")
    int getCellWidth();

    @Accessor("cellHeight")
    int getCellHeight();


}