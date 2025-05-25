package com.fuyuaki.wilderness_reborn.data.generation.other;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class GenSoundDefinition extends SoundDefinitionsProvider {
    public GenSoundDefinition(PackOutput output) {
        super(output, MODID);
    }

    @Override
    public void registerSounds() {


    }

    protected static SoundDefinition.Sound entitySound(final String name) {
        return sound(ResourceLocation.fromNamespaceAndPath(MODID,"entity/name"));
    }

}
