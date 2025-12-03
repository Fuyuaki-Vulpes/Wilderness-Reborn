package com.fuyuaki.r_wilderness.data.generation.other;

import com.fuyuaki.r_wilderness.client.RSoundEvents;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class GenSoundDefinition extends SoundDefinitionsProvider {
    public GenSoundDefinition(PackOutput output) {
        super(output, MODID);
    }

    @Override
    public void registerSounds() {

        add(RSoundEvents.MUSIC_BIOME_BARREN_CAVES, SoundDefinition.definition()
                .with(
                        Music.AN_ORDINARY_DAY,
                        Music.DANNY,
                        Music.DEEPER,
                        Music.INFINITE_AMETHYST,
                        Music.OXYGENE,
                        Music.POKOPOKO,
                        Music.ONE_MORE_DAY,
                        Music.CLARK,
                        Music.SUBWOOFER_LULLABY,
                        Music.SWEDEN,
                        Music.KEY,
                        Music.ECHO_IN_THE_WIND

                )
        );
    }

    protected static SoundDefinition.Sound entitySound(final String name) {
        return sound(ResourceLocation.fromNamespaceAndPath(MODID,"entity/name"));
    }


    public static class Music{
        public static final SoundDefinition.Sound AN_ORDINARY_DAY = sound("music/game/an_ordinary_day").stream();
        public static final SoundDefinition.Sound DANNY = sound("music/game/danny").stream();
        public static final SoundDefinition.Sound DEEPER = sound("music/game/deeper").stream().volume(0.4);
        public static final SoundDefinition.Sound ELD_UNKNOWN = sound("music/game/eld_unknown").stream().volume(0.4);
        public static final SoundDefinition.Sound ENDLESS = sound("music/game/endless").stream().volume(0.4);
        public static final SoundDefinition.Sound INFINITE_AMETHYST = sound("music/game/infinite_amethyst").stream().volume(0.4);
        public static final SoundDefinition.Sound KEY = sound("music/game/key").stream();
        public static final SoundDefinition.Sound OXYGENE = sound("music/game/oxygene").stream();
        public static final SoundDefinition.Sound POKOPOKO = sound("music/game/pokopoko").stream().volume(0.8);
        public static final SoundDefinition.Sound SUBWOOFER_LULLABY = sound("music/game/subwoofer_lullaby").stream();
        public static final SoundDefinition.Sound WENDING = sound("music/game/wending").stream().volume(0.4);
        public static final SoundDefinition.Sound CLARK = sound("music/game/clark").stream();
        public static final SoundDefinition.Sound ECHO_IN_THE_WIND = sound("music/game/echo_in_the_wind").stream().volume(0.4);
        public static final SoundDefinition.Sound FEATHERFALL = sound("music/game/featherfall").stream().volume(0.4);
        public static final SoundDefinition.Sound FLOATING_DREAM = sound("music/game/floating_dream").stream().volume(0.4);
        public static final SoundDefinition.Sound LEFT_TO_BLOOM = sound("music/game/left_to_bloom").stream().volume(0.4);
        public static final SoundDefinition.Sound MICE_ON_VENUS = sound("music/game/mice_on_venus").stream();
        public static final SoundDefinition.Sound ONE_MORE_DAY = sound("music/game/one_more_day").stream().volume(0.4);
        public static final SoundDefinition.Sound SWEDEN = sound("music/game/sweden").stream();
        public static final SoundDefinition.Sound SWAMP_AERIE = sound("music/game/swamp/aerie").stream().volume(0.4);
        public static final SoundDefinition.Sound SWAMP_FIREBUGS = sound("music/game/swamp/firebugs").stream().volume(0.4);
        public static final SoundDefinition.Sound SWAMP_LABYRINTHINE = sound("music/game/swamp/labyrinthine").stream().volume(0.4);

    }

}
