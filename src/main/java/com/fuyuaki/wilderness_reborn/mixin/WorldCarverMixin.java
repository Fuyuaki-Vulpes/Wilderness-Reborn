package com.fuyuaki.wilderness_reborn.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCarver.class)
public abstract class WorldCarverMixin<C extends CarverConfiguration> {

    @Shadow
    protected static boolean isDebugEnabled(CarverConfiguration config) {
        return config.debugSettings.isDebugMode();

    }

    @Shadow
    protected static BlockState getDebugState(CarverConfiguration config, BlockState state) {
        if (state.is(Blocks.AIR)) {
            return config.debugSettings.getAirState();
        } else if (state.is(Blocks.WATER)) {
            BlockState blockstate = config.debugSettings.getWaterState();
            return blockstate.hasProperty(BlockStateProperties.WATERLOGGED) ? blockstate.setValue(BlockStateProperties.WATERLOGGED, true) : blockstate;
        } else {
            return state.is(Blocks.LAVA) ? config.debugSettings.getLavaState() : state;
        }    }

    @Inject(method = "getCarveState", at = @At("HEAD"), cancellable = true)
    private void getCarverStateMixin(CarvingContext context, C config, BlockPos pos, Aquifer aquifer, CallbackInfoReturnable<BlockState> cir){
        BlockState blockstate = aquifer.computeSubstance(
                new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()), 0.0
        );
        if (blockstate == null) {
            cir.setReturnValue( isDebugEnabled(config) ? config.debugSettings.getBarrierState() : null);
        } else {
            cir.setReturnValue(isDebugEnabled(config) ? getDebugState(config, blockstate) : blockstate);
        }
    }
}
