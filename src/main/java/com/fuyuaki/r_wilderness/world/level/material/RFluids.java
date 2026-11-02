package com.fuyuaki.r_wilderness.world.level.material;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidIds;
import net.minecraft.world.level.material.WaterFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RFluids {

    public static final DeferredRegister<Fluid> FLUID = DeferredRegister.create(Registries.FLUID, RWildernessMod.MODID);
    public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, RWildernessMod.MODID);


    public static final DeferredHolder<Fluid,FlowingFluid> FLOWING_RIVER = FLUID.register("flowing_river_water", RiverFluid.Flowing::new);
    public static final DeferredHolder<Fluid,FlowingFluid> RIVER = FLUID.register("river_water", RiverFluid.Source::new);

    public static final DeferredHolder<FluidType,FluidType> RIVER_TYPE = TYPES.register("river_water",
            () -> new RiverType(FluidType.Properties.create().descriptionId("block.minecraft.water")
                    .fallDistanceModifier(0.0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canHydrate(true)
                    .isWaterLike(true)
                    .addDripstoneDripping(0.17578125F, ParticleTypes.DRIPPING_DRIPSTONE_WATER, Blocks.WATER_CAULDRON, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON)));

    public static void init(IEventBus bus){
        TYPES.register(bus);
        FLUID.register(bus);
    }


}
