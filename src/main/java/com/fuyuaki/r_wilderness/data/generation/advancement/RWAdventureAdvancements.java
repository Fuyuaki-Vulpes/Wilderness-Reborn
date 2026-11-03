package com.fuyuaki.r_wilderness.data.generation.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RWAdventureAdvancements extends AdvancementSubProvider {

    private final HolderGetter<EntityType<?>> entityRegistry;
    private final HolderGetter<Item> itemRegistry;
    private final HolderGetter<Block> blockRegistry;
    private final HolderGetter<Biome> biomeRegistry;
    private static final AdvancementRequirements.Strategy needAny = AdvancementRequirements.Strategy.OR;
    private static final AdvancementRequirements.Strategy needAll = AdvancementRequirements.Strategy.AND;

    public RWAdventureAdvancements(BootstrapContext<Advancement> output) {
        super(output);

        this.entityRegistry = output.lookup(Registries.ENTITY_TYPE);
        this.itemRegistry = output.lookup(Registries.ITEM);
        this.blockRegistry = output.lookup(Registries.BLOCK);
        this.biomeRegistry = output.lookup(Registries.BIOME);
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


    @Override public void generate() {

    }
}
