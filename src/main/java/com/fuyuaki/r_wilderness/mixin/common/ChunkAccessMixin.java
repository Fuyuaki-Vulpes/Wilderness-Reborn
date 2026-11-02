package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.world.level.levelgen.chunk.WRNoiseChunk;
import com.fuyuaki.r_wilderness.world.level.levelgen.util.ChunkAccessModifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.StructureAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements ChunkAccessModifier, BiomeManager.NoiseBiomeSource, LightChunk, StructureAccess, net.neoforged.neoforge.attachment.IAttachmentHolder {

    @Unique
    @Nullable
    private WRNoiseChunk wildernessChunk = null;


    @Shadow
    public abstract ChunkPos getPos();

    @Shadow
    public abstract LevelHeightAccessor getHeightAccessorForGeneration();

    @Shadow
    public abstract LevelChunkSection getSection(int index);


    @Nullable
    @Override
    public WRNoiseChunk getWildernessChunk() {
        return this.wildernessChunk;
    }

    @Override
    public void setWildernessChunk(@Nullable WRNoiseChunk ex) {
        this.wildernessChunk = ex;
    }

    @Override
    public WRNoiseChunk getOrCreateWildernessChunk(Function<ChunkAccess, WRNoiseChunk> chunkCreator) {
        if (this.wildernessChunk == null) {
            this.wildernessChunk = chunkCreator.apply((ChunkAccess)(Object) this);
        }

        return this.wildernessChunk;
    }
}
