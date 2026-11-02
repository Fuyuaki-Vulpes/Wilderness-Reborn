package com.fuyuaki.r_wilderness.references;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

public class RItemIds {

    private static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, RWildernessMod.identifier(name));
    }

    private static ResourceKey<Item> createPotterySherd(ResourceKey<DecoratedPotPattern> sherd) {
        return sherd.dependent(Registries.ITEM, "_pottery_sherd");
    }

    private static ResourceKey<Item> createArmorTrimSmithingTemplate(ResourceKey<TrimPattern> template) {
        return template.dependent(Registries.ITEM, "_armor_trim_smithing_template");
    }

    private static ResourceKey<Item> createMusicDisc(ResourceKey<JukeboxSong> music) {
        return music.dependent(Registries.ITEM, path -> "music_disc_" + path);
    }

    private static ResourceKey<Item> createSpawnEgg(ResourceKey<EntityType<?>> entity) {
        return entity.dependent(Registries.ITEM, "_spawn_egg");
    }

    private static ColorCollection<ResourceKey<Item>> createSimpleColored(String baseName) {
        return ColorCollection.prefixWithColor(ColorCollection.create(baseName)).map(RItemIds::create);
    }

}
