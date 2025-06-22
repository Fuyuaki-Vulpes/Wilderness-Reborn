package com.fuyuaki.wilderness_reborn.data;


import com.fuyuaki.wilderness_reborn.data.generation.lang.EN_US_LangProvider;
import com.fuyuaki.wilderness_reborn.data.generation.other.GenLoot;
import com.fuyuaki.wilderness_reborn.data.generation.other.GenRecipes;
import com.fuyuaki.wilderness_reborn.data.generation.other.ModBuiltInEntries;
import com.fuyuaki.wilderness_reborn.data.generation.tags.GenBiomeTags;
import com.fuyuaki.wilderness_reborn.data.pack.PackBuiltInEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        var datapackRegistries = new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), ModBuiltInEntries.BUILDER, Set.of(MODID));


        event.createProvider(GenRecipes.Runner::new);

        generator.addProvider(true, GenLoot.create(packOutput,lookupProvider));

/*
        generator.addProvider(true,new ModModelProvider(packOutput));

        event.createBlockAndItemTags(GenBlockTags::new, GenItemTags::new);

        generator.addProvider(true,
                new GenEnchantmentTags(packOutput, datapackRegistries.getRegistryProvider()));
*/
        generator.addProvider(true,
                new GenBiomeTags(packOutput, datapackRegistries.getRegistryProvider()));
/*
        generator.addProvider(true,
                new GenEntityTags(packOutput, datapackRegistries.getRegistryProvider()));

*/
        generator.addProvider(true,
                new ModBuiltInEntries(packOutput, lookupProvider));

        generator.addProvider(true,
                new EN_US_LangProvider(packOutput));

 /*       generator.addProvider(true,
                new GenSoundDefinition(packOutput));

        generator.addProvider(true,
                new GlobalLootModifiers(packOutput,datapackRegistries.getRegistryProvider()));

        generator.addProvider(true,new GenAdvancements(packOutput,datapackRegistries.getRegistryProvider()
                )
        );*/
        PackOutput datapackOutput = new PackOutput(packOutput.getOutputFolder().resolve("resourcepacks/wilderness_reborn"));
        DataGenerator.PackGenerator packGenerator = event.getGenerator().getBuiltinDatapack(true, MODID,"wilderness_reborn");

        packGenerator.addProvider(output ->
                new PackBuiltInEntries(datapackOutput, lookupProvider));


    }

}
