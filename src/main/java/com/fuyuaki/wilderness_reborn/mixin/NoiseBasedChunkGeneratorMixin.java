package com.fuyuaki.wilderness_reborn.mixin;


import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin extends ChunkGenerator {
    public NoiseBasedChunkGeneratorMixin(BiomeSource biomeSource) {
        super(biomeSource);
    }



    @Inject(method = "createFluidPicker", at = @At("HEAD"), cancellable = true)
    private static void createFluidPickerMixin(NoiseGeneratorSettings settings, CallbackInfoReturnable<Aquifer.FluidPicker> cir){
        Aquifer.FluidStatus bottomLava = new Aquifer.FluidStatus(-460, Blocks.LAVA.defaultBlockState());
        int seaLevel = settings.seaLevel();

        Aquifer.FluidStatus worldFluid = new Aquifer.FluidStatus(seaLevel, settings.defaultFluid());
        cir.setReturnValue((x, y, z) -> y < Math.min(-460, seaLevel) ? bottomLava : worldFluid);
    }
}
