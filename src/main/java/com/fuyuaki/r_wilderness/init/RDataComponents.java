package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import com.fuyuaki.r_wilderness.world.item.components.Drinkable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.Consumable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, RWildernessMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, @NotNull DataComponentType<Drinkable>> DRINKABLE = DATA.register("drinkable", () ->
            DataComponentType.<Drinkable>builder().persistent(Drinkable.CODEC).networkSynchronized(Drinkable.STREAM_CODEC).cacheEncoding().build());



    public static void init(IEventBus bus){
        DATA.register(bus);
    }
}
