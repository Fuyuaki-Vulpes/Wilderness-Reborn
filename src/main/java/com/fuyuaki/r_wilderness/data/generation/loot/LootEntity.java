package com.fuyuaki.r_wilderness.data.generation.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

public class LootEntity extends EntityLootSubProvider {
    List<Block> excludedEntities = List.of();
    List<EntityType<?>> knownEntities = List.of();
//            MtaEntityTypes.ENTITY.getEntries().stream().map(Holder::value).collect(Collectors.toList());

    public LootEntity(LootTableSubProvider.Context output) {
        super(FeatureFlags.REGISTRY.allFlags(), output);
    }

    @Override
    public void generate() {
        HolderGetter<EntityType<?>> holdergetter = this.output.lookup(Registries.ENTITY_TYPE);


    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {

        List<EntityType<?>> list = knownEntities;
        list.remove(excludedEntities);
        return list.stream();
    }
}
