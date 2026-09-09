package com.fuyuaki.r_wilderness.world.level.material;

import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.fuyuaki.r_wilderness.world.block.state.properties.RBlockStateProperties;
import com.fuyuaki.r_wilderness.world.generation.features.hydrology.WaterFlowData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public abstract class RiverFluid extends FlowingFluid {
    public static final EnumProperty<HorizontalDirection> FLOW_DIRECTION = RBlockStateProperties.HORIZONTAL_DIRECTION_16;
    public static final IntegerProperty FLOW_STRENGTH = RBlockStateProperties.FLOW_STRENGTH;

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(FLOW_DIRECTION);
        builder.add(FLOW_STRENGTH);
    }

    @Override
    protected FluidState getNewLiquid(ServerLevel level, BlockPos pos, BlockState state) {

        WaterFlowData data =WaterFlowData.quicklyMakeFlow(pos.getX(),pos.getZ(), level);
        Vec2 waterFlow = data.flow();
        FluidState newLiquid = super.getNewLiquid(level, pos, state);
        if (!newLiquid.hasProperty(FLOW_DIRECTION)) return newLiquid;

        newLiquid.setValue(RiverFluid.FLOW_DIRECTION, HorizontalDirection.fromVector(waterFlow.normalized()));
        newLiquid.setValue(RiverFluid.FLOW_STRENGTH, Mth.floor(waterFlow.length() * 10 + 1));

        return newLiquid;
    }

    @Override
    public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState fluidState) {
        HorizontalDirection direction = fluidState.getValue(RiverFluid.FLOW_DIRECTION);
        float strength = fluidState.getValue(RiverFluid.FLOW_STRENGTH);

        return direction.getNormal().scale(strength);
    }

    @Override
    public Fluid getFlowing() {
        return RFluids.FLOWING_RIVER.get();
    }

    @Override
    public Fluid getSource() {
        return RFluids.RIVER.get();
    }


    @Override
    public Item getBucket() {
        return Items.WATER_BUCKET;
    }

    @Override
    public void animateTick(Level level, BlockPos pos, FluidState fluidState, RandomSource random) {
        if (!fluidState.isSource() && !fluidState.getValue(FALLING)) {
            if (random.nextInt(64) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.WATER_AMBIENT,
                        SoundSource.AMBIENT,
                        random.nextFloat() * 0.25F + 0.75F,
                        random.nextFloat() + 0.5F,
                        false
                );
            }
        } else if (random.nextInt(10) == 0) {
            level.addParticle(
                    ParticleTypes.UNDERWATER, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
            );
        }
    }

    @Override
    public @Nullable ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    public boolean canConvertToSource(FluidState state, ServerLevel level, BlockPos pos) {
        HorizontalDirection direction = state.getValue(RiverFluid.FLOW_DIRECTION);
        Direction closest = direction.getClosestDirection();
        Direction other = direction.getSecondClosestDirection();
        BlockPos pos1;
        if (closest == other){
            pos1 = pos.relative(closest);
        }else{
            pos1 = pos.relative(closest).relative(other);
        }
        FluidState state1 = level.getFluidState(pos1);
        return super.canConvertToSource(state, level, pos) && (state1.isEmpty() || state1.isSource() );
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return level.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        effectApplier.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return RBlocks.RIVER_WATER.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState))
                .setValue(RiverFluidBlock.FLOW_STRENGTH, fluidState.getValue(FLOW_STRENGTH))
                .setValue(RiverFluidBlock.FLOW_DIRECTION, fluidState.getValue(FLOW_DIRECTION));
    }

    @Override
    public boolean isSame(Fluid other) {
        return other == RFluids.RIVER.get() || other == RFluids.FLOWING_RIVER.get() || other == Fluids.WATER || other == Fluids.FLOWING_WATER;
    }

    @Override
    public FluidType getFluidType() {
        return RFluids.RIVER_TYPE.get();
    }


    @Override
    public int getDropOff(LevelReader level) {
        return 2;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) {
        return direction == Direction.DOWN && !other.is(FluidTags.WATER);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends RiverFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Source extends RiverFluid {
        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }

}
