package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.common.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenBiomeTags extends KeyTagProvider<Biome> {

    public GenBiomeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.BIOME, provider,MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ModTags.Biomes.HAS_WATER_DELTAS_LARGE)
                .add(Biomes.STONY_SHORE)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP);
        this.tag(ModTags.Biomes.HAS_WATER_DELTAS_COMMON)
                .add(
                        Biomes.STONY_SHORE
        );
        this.tag(ModTags.Biomes.HAS_WATER_DELTAS_VERY_COMMON)
                .add(Biomes.SWAMP)
                .add(Biomes.MANGROVE_SWAMP
        );
        this.tag(ModTags.Biomes.HAS_WATER_DELTAS_REGULAR)
                .add(Biomes.BEACH);


        this.tag(ModTags.Biomes.HAS_WATER_DELTAS)
                .addTag(ModTags.Biomes.HAS_WATER_DELTAS_LARGE)
                .addTag(ModTags.Biomes.HAS_WATER_DELTAS_VERY_COMMON)
                .addTag(ModTags.Biomes.HAS_WATER_DELTAS_COMMON)
                .addTag(ModTags.Biomes.HAS_WATER_DELTAS_REGULAR)
        ;

        this.tag(ModTags.Biomes.HAS_SALT_WATER)
                .addTag(BiomeTags.IS_OCEAN)
                .addTag(BiomeTags.IS_BEACH)
        ;

        this.tag(ModTags.Biomes.HAS_SAND_DUNES)
                .add(Biomes.DESERT)
                .add(Biomes.SNOWY_BEACH)
                .add(Biomes.BEACH);
        this.tag(ModTags.Biomes.HAS_BIG_SAND_DUNES)
                .add(Biomes.DESERT);

        addWolfTags();
    }

    public void addWolfTags() {
  /*      //ashen
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/ashen")))
                .addTag(MtaTags.Biomes.HAS_ASHEN_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/ashen")))
                .addTag(MtaTags.Biomes.HAS_ASHEN_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/ashen")))
                .addTag(MtaTags.Biomes.HAS_ASHEN_WOLF)
        ;

        //black
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/black")))
                .addTag(MtaTags.Biomes.HAS_BLACK_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/black")))
                .addTag(MtaTags.Biomes.HAS_BLACK_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/black")))
                .addTag(MtaTags.Biomes.HAS_BLACK_WOLF)
        ;

        //chestnut
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/chestnut")))
                .addTag(MtaTags.Biomes.HAS_CHESTNUT_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/chestnut")))
                .addTag(MtaTags.Biomes.HAS_CHESTNUT_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/chestnut")))
                .addTag(MtaTags.Biomes.HAS_CHESTNUT_WOLF)
        ;
*/
        //pale
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/pale")))
//                .addTag(MtaTags.Biomes.HAS_PALE_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/pale")))
//                .addTag(MtaTags.Biomes.HAS_PALE_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/pale")))
//                .addTag(MtaTags.Biomes.HAS_PALE_WOLF)
//        ;
//
        //rusty
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/rusty")))
//                .addTag(MtaTags.Biomes.HAS_RUSTY_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/rusty")))
//                .addTag(MtaTags.Biomes.HAS_RUSTY_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/rusty")))
//                .addTag(MtaTags.Biomes.HAS_RUSTY_WOLF)
//        ;
//
//
        //snowy
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/snowy")))
//                .addTag(MtaTags.Biomes.HAS_SNOWY_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/snowy")))
//                .addTag(MtaTags.Biomes.HAS_SNOWY_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/snowy")))
//                .addTag(MtaTags.Biomes.HAS_SNOWY_WOLF)
//        ;
//
//        //spotted
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/spotted")))
//                .addTag(MtaTags.Biomes.HAS_SPOTTED_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/spotted")))
//                .addTag(MtaTags.Biomes.HAS_SPOTTED_WOLF)
//        ;
//        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/spotted")))
//                .addTag(MtaTags.Biomes.HAS_SPOTTED_WOLF)
//        ;

        /*
        //striped
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/striped")))
                .addTag(MtaTags.Biomes.HAS_STRIPED_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/striped")))
                .addTag(MtaTags.Biomes.HAS_STRIPED_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/striped")))
                .addTag(MtaTags.Biomes.HAS_STRIPED_WOLF)
        ;

        //woods
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("forge", "has_wolf_variant/woods")))
                .addTag(MtaTags.Biomes.HAS_WOODS_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "has_wolf_variant/woods")))
                .addTag(MtaTags.Biomes.HAS_WOODS_WOLF)
        ;
        this.tag(TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "has_wolf_variant/woods")))
                .addTag(MtaTags.Biomes.HAS_WOODS_WOLF)
        ;
        */
    }
}
