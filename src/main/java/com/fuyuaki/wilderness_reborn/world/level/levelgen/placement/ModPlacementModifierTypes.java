package com.fuyuaki.wilderness_reborn.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class ModPlacementModifierTypes {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENTS = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,MODID);

    public static final DeferredHolder<PlacementModifierType<?>,PlacementModifierType<DensityFunctionIncrementalPlacement>> DENSITY_FUNCTION_INCREMENTAL_PLACEMENT =
            register("density_function_incremental_placement", DensityFunctionIncrementalPlacement.CODEC);

    private static <P extends PlacementModifier> DeferredHolder<PlacementModifierType<?>,PlacementModifierType<P>> register(String name, MapCodec<P> codec) {
        return PLACEMENTS.register(name, () -> () -> codec);
    }
    public static void init(IEventBus bus){
        PLACEMENTS.register(bus);
    }
}
