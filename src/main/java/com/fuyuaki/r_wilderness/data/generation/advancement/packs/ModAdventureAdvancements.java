package com.fuyuaki.r_wilderness.data.generation.advancement.packs;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class ModAdventureAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> writer) {

        HolderGetter<EntityType<?>> entityRegistry = registries.lookupOrThrow(Registries.ENTITY_TYPE);
        HolderGetter<Item> itemRegistry = registries.lookupOrThrow(Registries.ITEM);
        HolderGetter<Block> blockRegistry = registries.lookupOrThrow(Registries.BLOCK);
        HolderGetter<Biome> biomeRegistry = registries.lookupOrThrow(Registries.BIOME);
        AdvancementRequirements.Strategy needAny = AdvancementRequirements.Strategy.OR;
        AdvancementRequirements.Strategy needAll = AdvancementRequirements.Strategy.AND;

    }

    private String nameKey(String name){
        return "advancements.mta.adventure." + name + ".title";
    }


    private String descKey(String name){
        return "advancements.mta.adventure." + name + ".description";
    }
    private String id(String name){
        return "mta_adventure/" + name ;
    }


    protected static Advancement.Builder addBiomes(Advancement.Builder builder, HolderLookup.Provider levelRegistry, List<ResourceKey<Biome>> biomes) {
        HolderGetter<Biome> holdergetter = levelRegistry.lookupOrThrow(Registries.BIOME);

        for (ResourceKey<Biome> resourcekey : biomes) {
            builder.addCriterion(
                    resourcekey.identifier().toString(),
                    PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(holdergetter.getOrThrow(resourcekey)))
            );
        }

        return builder;
    }


}
