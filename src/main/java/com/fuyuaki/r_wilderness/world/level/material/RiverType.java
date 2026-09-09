package com.fuyuaki.r_wilderness.world.level.material;

import com.fuyuaki.r_wilderness.util.HorizontalDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

public class RiverType extends FluidType {

    public RiverType(Properties properties) {
        super(properties);
    }


    @Override
    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
        if (reader instanceof ServerLevel level) {
            return level.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
        }

        return super.canConvertToSource(state, reader, pos);
    }

    @Override
    public @Nullable PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
        return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        HorizontalDirection direction = state.getValue(RiverFluid.FLOW_DIRECTION);
        float strength = state.getValue(RiverFluid.FLOW_STRENGTH) * 0.5f;
        Vec3 movement = direction.getNormal().scale(strength + (entity.level().isRainingAt(entity.blockPosition()) ? 1.0f : 0.0f));
        movementVector.add(movement);
        return true;
    }
}
