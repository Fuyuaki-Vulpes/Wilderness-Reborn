package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.generation.ChunkGeneratorExtension;
import com.fuyuaki.r_wilderness.world.generation.RandomStateExtension;
import com.fuyuaki.r_wilderness.world.generation.terrain.SurfaceSystemExtension;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public class RandomStateMixin implements RandomStateExtension {


    @Shadow @Final private PositionalRandomFactory random;
    @Unique
    @Nullable
    private ChunkGeneratorExtension wildernessReborn$chunkGeneratorExtension = null;
    @Unique
    @Nullable
    private SurfaceSystemExtension surfaceSystemExtension = null;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void initMethod(NoiseGeneratorSettings settings, HolderGetter noiseParametersGetter, long levelSeed, CallbackInfo ci){
        this.surfaceSystemExtension = new SurfaceSystemExtension((RandomState)(Object)this, settings.defaultBlock(), settings.seaLevel(), this.random);
    }
    @Override
    public void wildernessReborn$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex) {
        wildernessReborn$chunkGeneratorExtension = ex;
    }

    @Nullable
    @Override
    public ChunkGeneratorExtension wildernessReborn$getChunkGeneratorExtension() {
        return wildernessReborn$chunkGeneratorExtension;
    }

    @Override
    public SurfaceSystemExtension getSurfaceSystemExtension() {
        return this.surfaceSystemExtension;
    }
}
