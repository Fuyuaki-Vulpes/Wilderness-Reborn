package com.fuyuaki.wilderness_reborn.data.generation.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class EN_US_LangProvider extends LanguageProvider {
    public EN_US_LangProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        this.advancements();
        this.sounds();
        this.commands();
    }

    private void sounds() {



    }

    private void advancements() {


    }


    private void commands() {




    }


}
