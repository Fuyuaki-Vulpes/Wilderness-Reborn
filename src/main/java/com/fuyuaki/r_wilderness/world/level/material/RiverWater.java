package com.fuyuaki.r_wilderness.world.level.material;

import com.fuyuaki.r_wilderness.init.ModSoundEvents;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RiverWater extends WaterFluid {
    private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();




    private static boolean hasSameAbove(FluidState fluidState, BlockGetter level, BlockPos pos) {
        return fluidState.getType().isSame(level.getFluidState(pos.above()).getType());
    }

    public VoxelShape getShape(FluidState fluidState, BlockGetter getter, BlockPos blockPos) {
        return fluidState.getAmount() == 9 && hasSameAbove(fluidState, getter, blockPos)
                ? Shapes.block()
                : this.shapes.computeIfAbsent(
                        fluidState, fluidState1 ->
                        Shapes.box(0.0, 0.0, 0.0,
                                1.0,
                                fluidState1.getHeight(getter, blockPos) > 0.5 ? 1.0F : fluidState1.getHeight(getter, blockPos) * 2.0F,
                                1.0));
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return super.getSlopeFindDistance(level);
    }

    public static class Flowing extends RiverWater {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends RiverWater {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
