package com.fuyuaki.r_wilderness.mixin.common;

import com.fuyuaki.r_wilderness.world.level.levelgen.MaterialSystemExtension;
import com.fuyuaki.r_wilderness.world.level.levelgen.ChunkGeneratorExtension;
import com.fuyuaki.r_wilderness.world.level.levelgen.RandomStateExtension;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseRouter;
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
    private MaterialSystemExtension materialSystemExtension = null;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void initMethod(HolderGetter noises, long seed, boolean useLegacyRandom, BlockState defaultBlock, int seaLevel, NoiseRouter router, CallbackInfo ci){
        this.materialSystemExtension = new MaterialSystemExtension((RandomState)(Object)this, seaLevel,defaultBlock, this.random);
    }
    @Override
    public void setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex) {
        wildernessReborn$chunkGeneratorExtension = ex;
    }

    @Nullable
    @Override
    public ChunkGeneratorExtension getChunkGeneratorExtension() {
        return wildernessReborn$chunkGeneratorExtension;
    }

    @Override
    public MaterialSystemExtension getMaterialSystemExtension() {
        return this.materialSystemExtension;
    }
}
