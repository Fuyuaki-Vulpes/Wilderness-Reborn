package com.fuyuaki.r_wilderness.world.level.material;

import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.WaterFlowData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec2;

public class RiverFluidBlock extends LiquidBlock {

    public static final EnumProperty<HorizontalDirection> FLOW_DIRECTION = RBlockStateProperties.HORIZONTAL_DIRECTION_16;
    public static final IntegerProperty FLOW_STRENGTH = RBlockStateProperties.FLOW_STRENGTH;

    public RiverFluidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
        this.registerDefaultState(
                this.getStateDefinition().any().setValue(LEVEL, 0)
                        .setValue(FLOW_DIRECTION,HorizontalDirection.WEST)
                        .setValue(FLOW_STRENGTH, 10));
    }
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        WaterFlowData data = WaterFlowData.quicklyMakeFlow(pos.getX(), pos.getZ(), level);
        Vec2 waterFlow = data.flow();

        state.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
        state.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        if (state.getFluidState().is(RTags.Fluids.RIVER_WATER)) {
            state.getFluidState().setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
            state.getFluidState().setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        }

    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState s = super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        if (level instanceof ServerLevel l) {
            WaterFlowData data = WaterFlowData.quicklyMakeFlow(pos.getX(), pos.getZ(), l);
            Vec2 waterFlow = data.flow();

            s.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
            s.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        }else{
            ServerLevel l = ((WorldGenRegion)level).getLevel();

            WaterFlowData data = WaterFlowData.quicklyMakeFlow(pos.getX(), pos.getZ(), l);
            Vec2 waterFlow = data.flow();

            s.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
            s.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
            if (s.getFluidState().is(RTags.Fluids.RIVER_WATER)) {
                s.getFluidState().setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
                s.getFluidState().setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
            }
        }
        return s;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide()) return;
        WaterFlowData data = WaterFlowData.quicklyMakeFlow(pos.getX(),pos.getZ(), (ServerLevel) level);
        Vec2 waterFlow = data.flow();

        state.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
        state.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        if (state.getFluidState().is(RTags.Fluids.RIVER_WATER)) {
            state.getFluidState().setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
            state.getFluidState().setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOW_DIRECTION, FLOW_STRENGTH);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        FluidState fluidState = super.getFluidState(state);
        int st = state.getValue(FLOW_STRENGTH);
        HorizontalDirection dir = state.getValue(FLOW_DIRECTION);
        fluidState.setValue(FLOW_STRENGTH,st);
        fluidState.setValue(FLOW_DIRECTION,dir);
        return fluidState;
    }
}
