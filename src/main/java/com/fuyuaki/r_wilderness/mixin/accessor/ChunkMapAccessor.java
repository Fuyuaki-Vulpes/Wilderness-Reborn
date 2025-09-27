package com.fuyuaki.r_wilderness.mixin.accessor;


import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
    @Accessor("randomState")
    RandomState accessor$getRandomState();
}
