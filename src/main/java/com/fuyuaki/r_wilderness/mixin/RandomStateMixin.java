package com.fuyuaki.r_wilderness.mixin;

import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import com.fuyuaki.r_wilderness.world.generation.RandomStateExtension;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RandomState.class)
public class RandomStateMixin implements RandomStateExtension {
    @Unique
    @Nullable
    private ChunkGeneratorExtension wildernessReborn$chunkGeneratorExtension = null;

    @Override
    public void wildernessReborn$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex) {
        wildernessReborn$chunkGeneratorExtension = ex;
    }

    @Nullable
    @Override
    public ChunkGeneratorExtension wildernessReborn$getChunkGeneratorExtension() {
        return wildernessReborn$chunkGeneratorExtension;
    }
}
