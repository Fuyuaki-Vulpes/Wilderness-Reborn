package com.fuyuaki.wilderness_reborn.data.pack;

import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseGeneratorSettings;
import com.fuyuaki.wilderness_reborn.data.pack.levelgen.PackNoiseRouterData;
import com.fuyuaki.wilderness_reborn.data.pack.worldgen.PackCarvers;
import com.fuyuaki.wilderness_reborn.data.pack.worldgen.PackDimensionTypes;
import com.fuyuaki.wilderness_reborn.data.pack.worldgen.PackNoiseData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.DimensionTypes;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PackBuiltInEntries extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, PackDimensionTypes::bootstrap)
            .add(Registries.DENSITY_FUNCTION, PackNoiseRouterData::bootstrap)
            .add(Registries.CONFIGURED_CARVER, PackCarvers::bootstrap)
            .add(Registries.NOISE, PackNoiseData::bootstrap)
            .add(Registries.NOISE_SETTINGS, PackNoiseGeneratorSettings::bootstrap);

    public PackBuiltInEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of("minecraft"));
    }
}
