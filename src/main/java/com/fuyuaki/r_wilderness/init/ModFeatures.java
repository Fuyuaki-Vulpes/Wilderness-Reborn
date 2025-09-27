package com.fuyuaki.r_wilderness.init;

import com.fuyuaki.r_wilderness.world.level.levelgen.feature.SharpIceSpikesFeature;
import com.fuyuaki.r_wilderness.world.level.levelgen.feature.WaterDeltaFeature;
import com.fuyuaki.r_wilderness.world.level.levelgen.feature.configurations.WaterDeltaFeatureConfiguration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class ModFeatures<FC extends FeatureConfiguration> {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE,MODID);

    public static final DeferredHolder<Feature<?> ,Feature<NoneFeatureConfiguration>> SHARP_ICE_SPIKES = FEATURES.register(
            "sharp_ice_spikes", () -> new SharpIceSpikesFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?> ,Feature<WaterDeltaFeatureConfiguration>> WATER_DELTA = FEATURES.register(
            "water_delta", () -> new WaterDeltaFeature(WaterDeltaFeatureConfiguration.CODEC));



    public static void init(IEventBus bus){
        FEATURES.register(bus);
    }

}
