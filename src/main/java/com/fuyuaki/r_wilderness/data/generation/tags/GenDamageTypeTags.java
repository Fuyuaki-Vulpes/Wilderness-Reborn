package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.api.common.RTags;
import com.fuyuaki.r_wilderness.init.RDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenDamageTypeTags extends DamageTypeTagsProvider {
    public GenDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(RTags.DamageTypes.CAUSES_TEMPERATURE_INCREASE)
                .add(DamageTypes.DRAGON_BREATH)
                .add(DamageTypes.CRAMMING)
                .addTag(DamageTypeTags.IS_FIRE);
        this.tag(RTags.DamageTypes.CAUSES_TEMPERATURE_DECREASE)
                .add(DamageTypes.OUTSIDE_BORDER)
                .add(DamageTypes.WIND_CHARGE)
                .add(DamageTypes.DROWN)
                .addTag(DamageTypeTags.IS_FREEZING);


        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);
        this.tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);
        this.tag(DamageTypeTags.BYPASSES_EFFECTS)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);
        this.tag(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);

        this.tag(DamageTypeTags.NO_KNOCKBACK)
                .add(RDamageTypes.HYPERTHERMIA)
                .add(RDamageTypes.HYPOTHERMIA);

    }
}
