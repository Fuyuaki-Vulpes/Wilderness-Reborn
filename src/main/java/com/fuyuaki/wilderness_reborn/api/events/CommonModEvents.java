package com.fuyuaki.wilderness_reborn.api.events;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.nio.file.Path;
import java.util.Optional;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

@EventBusSubscriber(modid = MODID,bus = EventBusSubscriber.Bus.MOD)
public class CommonModEvents {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            Path resourcePath = ModList.get().getModFileById("wilderness_reborn").getFile().findResource("resourcepacks/wilderness_reborn");

            Pack dataPack = Pack.readMetaAndCreate(
                    new PackLocationInfo(
                            resourcePath.getFileName().toString(),
                            Component.literal("Wilderness Reborn"),
                            PackSource.BUILT_IN,
                            Optional.of(new KnownPack(MODID, "data", SharedConstants.getCurrentVersion().getId()))
                    ),
                    new PathPackResources.PathResourcesSupplier(resourcePath),
                    PackType.SERVER_DATA,
                    new PackSelectionConfig(
                            true,
                            Pack.Position.TOP,
                            false
                    )
            );

            event.addRepositorySource((packConsumer) -> packConsumer.accept(dataPack));
        }
    }


    @SubscribeEvent
    public static void onDatapackRegistry(DataPackRegistryEvent.NewRegistry event){
    }
}
