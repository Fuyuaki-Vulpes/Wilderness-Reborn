package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.common.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenDamageTypeTags extends DamageTypeTagsProvider {
    public GenDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.DamageTypes.CAUSES_TEMPERATURE_INCREASE)
                .add(DamageTypes.DRAGON_BREATH)
                .add(DamageTypes.CRAMMING)
                .addTag(DamageTypeTags.IS_FIRE);
        this.tag(ModTags.DamageTypes.CAUSES_TEMPERATURE_DECREASE)
                .add(DamageTypes.OUTSIDE_BORDER)
                .add(DamageTypes.WIND_CHARGE)
                .add(DamageTypes.DROWN)
                .addTag(DamageTypeTags.IS_FREEZING);

    }
}
