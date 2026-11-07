package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.RRegistries;
import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.data.worldgen.biome.RWBiomePlacements;
import com.fuyuaki.r_wilderness.init.RDamageTypes;
import com.fuyuaki.r_wilderness.world.generation.biome.BiomePlacementData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class RWBiomePlacementDataTags extends TagsProvider<BiomePlacementData> {
    public RWBiomePlacementDataTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, RRegistries.REBORN_BIOME_PLACEMENT_KEY,lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(RTags.BiomePlacementDatas.ENABLED)
                .add(RWBiomePlacements.OCEAN)
                .add(RWBiomePlacements.DEEP_OCEAN)
                .add(RWBiomePlacements.WARM_OCEAN)
                .add(RWBiomePlacements.LUKEWARM_OCEAN)
                .add(RWBiomePlacements.DEEP_LUKEWARM_OCEAN)
                .add(RWBiomePlacements.COLD_OCEAN)
                .add(RWBiomePlacements.DEEP_COLD_OCEAN)
                .add(RWBiomePlacements.FROZEN_OCEAN)
                .add(RWBiomePlacements.DEEP_FROZEN_OCEAN)
                .add(RWBiomePlacements.MUSHROOM_ISLAND)
                .add(RWBiomePlacements.PLAINS)
                .add(RWBiomePlacements.SUNFLOWER_PLAINS)
                .add(RWBiomePlacements.SNOWY_PLAINS)
                .add(RWBiomePlacements.ICE_SPIKES)
                .add(RWBiomePlacements.DESERT)
                .add(RWBiomePlacements.FOREST)
                .add(RWBiomePlacements.FLOWER_FOREST)
                .add(RWBiomePlacements.BIRCH_FOREST)
                .add(RWBiomePlacements.OLD_GROWTH_BIRCH_FOREST)
                .add(RWBiomePlacements.TAIGA)
                .add(RWBiomePlacements.OLD_GROWTH_SPRUCE_TAIGA)
                .add(RWBiomePlacements.OLD_GROWTH_PINE_TAIGA)
                .add(RWBiomePlacements.SNOWY_TAIGA)
                .add(RWBiomePlacements.DARK_FOREST)
                .add(RWBiomePlacements.PALE_GARDEN)
                .add(RWBiomePlacements.JUNGLE)
                .add(RWBiomePlacements.BAMBOO_JUNGLE)
                .add(RWBiomePlacements.SPARSE_JUNGLE)
                .add(RWBiomePlacements.SAVANNA)
                .add(RWBiomePlacements.SAVANNA_PLATEAU)
                .add(RWBiomePlacements.WINDSWEPT_SAVANNA)
                .add(RWBiomePlacements.SWAMP)
                .add(RWBiomePlacements.MANGROVE_SWAMP)
                .add(RWBiomePlacements.MEADOW)
                .add(RWBiomePlacements.GROVE)
                .add(RWBiomePlacements.CHERRY_GROVE)
                .add(RWBiomePlacements.BADLANDS)
                .add(RWBiomePlacements.ERODED_BADLANDS)
                .add(RWBiomePlacements.WOODED_BADLANDS)
                .add(RWBiomePlacements.MEADOW_MOUNTAIN)
                .add(RWBiomePlacements.SNOWY_SLOPES)
                .add(RWBiomePlacements.WINDSWEPT_HILLS)
                .add(RWBiomePlacements.WINDSWEPT_GRAVELLY_HILLS)
                .add(RWBiomePlacements.WINDSWEPT_FOREST)
                .add(RWBiomePlacements.BEACH)
                .add(RWBiomePlacements.SNOWY_BEACH)
                .add(RWBiomePlacements.STONY_SHORE)
                .add(RWBiomePlacements.RIVER)
                .add(RWBiomePlacements.FROZEN_RIVER)
                .add(RWBiomePlacements.JAGGED_PEAKS)
                .add(RWBiomePlacements.FROZEN_PEAKS)
                .add(RWBiomePlacements.STONY_PEAKS)
                .add(RWBiomePlacements.DRIPSTONE_CAVES)
                .add(RWBiomePlacements.BARREN_CAVES)
                .add(RWBiomePlacements.DEEP_DARK)
                .add(RWBiomePlacements.LUSH_CAVES)
                .add(RWBiomePlacements.SULFUR_CAVES)
        ;

    }
}
