package com.fuyuaki.r_wilderness.api.common;

import com.fuyuaki.r_wilderness.api.RWildernessMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> ROOT_BLOCK_GROWS_INTO = tag("root_block_grows_into");

        public static final TagKey<Block> SCHINITE_ORE_REPLACEABLES = tag("schinite_ore_replaceables");
        public static final TagKey<Block> MAGNEISS_ORE_REPLACEABLES = tag("magneiss_ore_replaceables");
        public static final TagKey<Block> MALATITE_ORE_REPLACEABLES = tag("malatite_ore_replaceables");

        public static final TagKey<Block> SLIGHTLY_INCREASES_TEMPERATURE = tag("slightly_increases_temperature");
        public static final TagKey<Block> MODERATELY_INCREASES_TEMPERATURE = tag("moderately_increases_temperature");
        public static final TagKey<Block> GREATLY_INCREASES_TEMPERATURE = tag("greatly_increases_temperature");
        public static final TagKey<Block> INCREASES_TEMPERATURE = tag("increases_temperature");
        public static final TagKey<Block> SLIGHTLY_DECREASES_TEMPERATURE = tag("slightly_decreases_temperature");
        public static final TagKey<Block> MODERATELY_DECREASES_TEMPERATURE = tag("moderately_decreases_temperature");
        public static final TagKey<Block> GREATLY_DECREASES_TEMPERATURE = tag("greatly_decreases_temperature");
        public static final TagKey<Block> DECREASES_TEMPERATURE = tag("decreases_temperature");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, RWildernessMod.modLocation(name));
        }
    }
    public static class EntityTypes {
        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, RWildernessMod.modLocation(name));
        }
    }
    public static class Items {
        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, RWildernessMod.modLocation(name));
        }
    }
    public static class Fluids {
        public static final TagKey<Fluid> COOLING = tag("cooling_fluids");
        public static final TagKey<Fluid> HEATING = tag("heating_fluids");

        private static TagKey<Fluid> tag(String name) {
            return FluidTags.create(RWildernessMod.modLocation(name));
        }
    }
    public static class Enchantments {
        private static TagKey<Enchantment> tag(String name) {
            return TagKey.create(Registries.ENCHANTMENT, RWildernessMod.modLocation(name));
        }
    }
    public static class Biomes {
        public static final TagKey<Biome> HAS_WATER_DELTAS = tag("has_water_deltas");
        public static final TagKey<Biome> HAS_WATER_DELTAS_COMMON = tag("has_water_deltas_common");
        public static final TagKey<Biome> HAS_WATER_DELTAS_VERY_COMMON = tag("has_water_deltas_very_common");
        public static final TagKey<Biome> HAS_WATER_DELTAS_LARGE = tag("has_water_deltas_large");
        public static final TagKey<Biome> HAS_WATER_DELTAS_REGULAR = tag("has_water_deltas_regular");
        public static final TagKey<Biome> HAS_SAND_DUNES = tag("has_sand_dunes");
        public static final TagKey<Biome> HAS_BIG_SAND_DUNES = tag("has_sand_dunes");
        public static final TagKey<Biome> LAVA_AQUIFERS_ONLY = tag("lava_aquifers_only");
        public static final TagKey<Biome> HAS_SALT_WATER = tag("has_salt_water");


        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, RWildernessMod.modLocation(name));
        }
    }
    public static class Structures {
        private static TagKey<Structure> tag(String name) {
            return TagKey.create(Registries.STRUCTURE, RWildernessMod.modLocation(name));
        }
    }
    public static class DamageTypes {

        public static final TagKey<DamageType> CAUSES_TEMPERATURE_INCREASE = tag("causes_temperature_increase");
        public static final TagKey<DamageType> CAUSES_TEMPERATURE_DECREASE = tag("causes_temperature_decrease");

        private static TagKey<DamageType> tag(String name) {

            return TagKey.create(Registries.DAMAGE_TYPE, RWildernessMod.modLocation(name));
        }
    }
}
