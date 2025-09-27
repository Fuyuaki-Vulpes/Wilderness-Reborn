package com.fuyuaki.r_wilderness.data.generation.tags;

import com.fuyuaki.r_wilderness.world.level.levelgen.ModWorldPresets;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.concurrent.CompletableFuture;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;


public class GenWorldPresetTags extends KeyTagProvider<WorldPreset> {

    public GenWorldPresetTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.WORLD_PRESET, provider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_255734_) {
        this.tag(WorldPresetTags.NORMAL)
                .add(ModWorldPresets.REBORN_WORLD);

    }
}