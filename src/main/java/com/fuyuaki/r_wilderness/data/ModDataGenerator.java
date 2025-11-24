package com.fuyuaki.r_wilderness.data;


import com.fuyuaki.r_wilderness.data.generation.lang.EN_US_LangProvider;
import com.fuyuaki.r_wilderness.data.generation.model.ModModelProvider;
import com.fuyuaki.r_wilderness.data.generation.other.GenLoot;
import com.fuyuaki.r_wilderness.data.generation.other.GenRecipes;
import com.fuyuaki.r_wilderness.data.generation.other.GenSoundDefinition;
import com.fuyuaki.r_wilderness.data.generation.other.ModBuiltInEntries;
import com.fuyuaki.r_wilderness.data.generation.tags.GenBiomeTags;
import com.fuyuaki.r_wilderness.data.generation.tags.GenWorldPresetTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

@EventBusSubscriber(modid = MODID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        var datapackRegistries = new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), ModBuiltInEntries.BUILDER, Set.of(MODID));


        event.createProvider(GenRecipes.Runner::new);

        generator.addProvider(true, GenLoot.create(packOutput,lookupProvider));


        generator.addProvider(true,new ModModelProvider(packOutput));
/*
        event.createBlockAndItemTags(GenBlockTags::new, GenItemTags::new);

        generator.addProvider(true,
                new GenEnchantmentTags(packOutput, datapackRegistries.getRegistryProvider()));
*/
        generator.addProvider(true,
                new GenBiomeTags(packOutput, datapackRegistries.getRegistryProvider()));

        generator.addProvider(true,
                new GenWorldPresetTags(packOutput, datapackRegistries.getRegistryProvider()));
/*
        generator.addProvider(true,
                new GenEntityTags(packOutput, datapackRegistries.getRegistryProvider()));

*/
        generator.addProvider(true,
                new ModBuiltInEntries(packOutput,  datapackRegistries.getRegistryProvider()));

        generator.addProvider(true,
                new EN_US_LangProvider(packOutput));

      generator.addProvider(true,
                new GenSoundDefinition(packOutput));
/*
        generator.addProvider(true,
                new GlobalLootModifiers(packOutput,datapackRegistries.getRegistryProvider()));

        generator.addProvider(true,new GenAdvancements(packOutput,datapackRegistries.getRegistryProvider()
                )
        );*/



    }

}
