package com.fuyuaki.r_wilderness.mixin;

import com.fuyuaki.r_wilderness.world.generation.WildChunkGenerator;
import com.fuyuaki.r_wilderness.world.level.levelgen.ModWorldPresets;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(WorldPresets.class)
public class WorldPresetsMixin {


    @Shadow @Final public static ResourceKey<WorldPreset> FLAT;

    @Shadow @Final public static ResourceKey<WorldPreset> DEBUG;

    @Shadow @Final public static ResourceKey<WorldPreset> NORMAL;

    @Inject(method = "fromSettings",at = @At(value = "HEAD"), cancellable = true)
    private static void freshScreenMixin(WorldDimensions worldDimensions, CallbackInfoReturnable<Optional<ResourceKey<WorldPreset>>> cir){
        cir.setReturnValue( worldDimensions.get(LevelStem.OVERWORLD).flatMap((p_344665_) -> {
            ChunkGenerator var10000 = p_344665_.generator();
            Optional var6;
            switch (var10000) {
                case FlatLevelSource var3 -> var6 = Optional.of(FLAT);
                case DebugLevelSource var4 -> var6 = Optional.of(DEBUG);
                case NoiseBasedChunkGenerator var5 -> var6 = Optional.of(NORMAL);
                case WildChunkGenerator var7 -> var6 = Optional.of(ModWorldPresets.REBORN_WORLD);
                default -> var6 = Optional.empty();
            }

            return var6;
        }));

    }
}
