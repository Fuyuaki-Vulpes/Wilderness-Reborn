package com.fuyuaki.r_wilderness.data;


import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.data.generation.advancement.RWAdvancements;
import com.fuyuaki.r_wilderness.data.generation.lang.EN_US_LangProvider;
import com.fuyuaki.r_wilderness.data.generation.model.ModModelProvider;
import com.fuyuaki.r_wilderness.data.generation.other.*;
import com.fuyuaki.r_wilderness.data.generation.tags.*;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeData;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomeModifiers;
import com.fuyuaki.r_wilderness.data.worldgen.biome.ModBiomePlacements;
import com.fuyuaki.r_wilderness.data.worldgen.features.ModFeatureUtils;
import com.fuyuaki.r_wilderness.data.worldgen.placement.ModPlacementUtils;
import com.fuyuaki.r_wilderness.init.RDamageTypes;
import com.fuyuaki.r_wilderness.world.item.enchantment.ModEnchantments;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldConfiguration;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldPresets;
import com.fuyuaki.r_wilderness.world.level.levelgen.RebornWorldSettings;
import com.fuyuaki.r_wilderness.world.level.levelgen.feature.RWFeatureTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getReloadableLookupProvider();


        generator.addProvider(true,new ModModelProvider(packOutput));

        event.createBlockAndItemTags(RWBlockTags::new, RWItemTags::new);

        generator.addProvider(true, new RWBiomeTags(packOutput, lookupProvider));

        generator.addProvider(true, new RWFluidTags(packOutput, lookupProvider));

        generator.addProvider(true, new RWWorldPresetTags(packOutput, lookupProvider));

        generator.addProvider(true, new EN_US_LangProvider(packOutput));

      generator.addProvider(true, new RWSoundDefinition(packOutput));



    }

    @SubscribeEvent
    public static void gatherData(GatherDataRegistryEntriesEvent event){
        event.gatherFor(MODID, Identifier.DEFAULT_NAMESPACE)
                .add(Registries.ADVANCEMENT, RWAdvancements.create())
                .add(Registries.FEATURE, ModFeatureUtils::bootstrap)
                .add(Registries.LOOT_TABLE, RWLoot.create())
                .add(Registries.CARVER, ModCarvers::bootstrap)
                .add(Registries.PLACED_FEATURE, ModPlacementUtils::bootstrap)
                .add(Registries.BIOME, ModBiomeData::bootstrap)
                .add(RRegistries.REBORN_BIOME_PLACEMENT_KEY, ModBiomePlacements::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
                .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)
                .add(Registries.WORLD_PRESET, RebornWorldPresets::bootstrap)
                .add(Registries.DIMENSION_TYPE, RebornWorldConfiguration.DimensionTypes::bootstrap)
                .add(Registries.DAMAGE_TYPE, RDamageTypes::bootstrap)
                .add(RRegistries.WRGEN_SETTINGS, RebornWorldSettings::bootstrap);
        event.recipe(RWRecipes::new);
    }



}
