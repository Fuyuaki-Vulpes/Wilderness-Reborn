package com.fuyuaki.r_wilderness.mixin.common;


import com.fuyuaki.r_wilderness.world.level.biome.RebornBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Structure.class)
public class StructureMixin {


    @Inject(method = "isValidBiome", at = @At(value = "HEAD"), cancellable = true)
    private static void isValidBiome(Structure.GenerationStub stub, Structure.GenerationContext context, CallbackInfoReturnable<Boolean> cir) {
        if (!(context.chunkGenerator().getBiomeSource() instanceof RebornBiomeSource)) return;
        BlockPos blockpos = stub.position();
        cir.setReturnValue(
                context.validBiome()
                .test(
                        context.chunkGenerator()
                                .getBiomeSource()
                                .getNoiseBiome(
                                        blockpos.getX(),
                                        blockpos.getY(),
                                        blockpos.getZ(),
                                        context.randomState().sampler()
                                )
                ));
    }


}