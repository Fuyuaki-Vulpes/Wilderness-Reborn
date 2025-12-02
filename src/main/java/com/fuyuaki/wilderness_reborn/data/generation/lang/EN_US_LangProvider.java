package com.fuyuaki.wilderness_reborn.data.generation.lang;

import com.fuyuaki.wilderness_reborn.world.item.ModItems;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static com.fuyuaki.wilderness_reborn.api.WildernessRebornMod.MODID;

public class EN_US_LangProvider extends LanguageProvider {
    public EN_US_LangProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Blocks

        add(ModBlocks.CHALK.get(), "Chalk");
        add(ModBlocks.LIMESTONE.get(), "Limestone");
        add(ModBlocks.MUD_STONE.get(), "Mud Stone");

        //Soils

        add(ModBlocks.CHALKY_SOIL.get(),"Chalky Soil");
        add(ModBlocks.CHALKY_FARMLAND.get(),"Chalky Farmland");
        add(ModBlocks.CLAY_SOIL.get(),"Clay Soil");
        add(ModBlocks.CLAY_FARMLAND.get(),"Clay Farmland");
        add(ModBlocks.PEAT.get(),"Peat");
        add(ModBlocks.PEAT_FARMLAND.get(),"Peat Farmland");
        add(ModBlocks.SANDY_SOIL.get(),"Sandy Soil");
        add(ModBlocks.SANDY_FARMLAND.get(),"Sandy Farmland");
        add(ModBlocks.SILT.get(),"Silt");
        add(ModBlocks.SILT_FARMLAND.get(),"Silt Farmland");

        //Flowers

        add(ModBlocks.BELLFLOWER.get(), "Bellflower");
        add(ModBlocks.POTTED_BELLFLOWER.get(), "Potted Bellflower");
        add(ModBlocks.MOSS_COMPANION.get(), "Moss Companion");
        add(ModBlocks.POTTED_MOSS_COMPANION.get(), "Potted Moss Companion");
        add(ModBlocks.MOUNTAIN_HEATHER.get(), "Mountain Heather");
        add(ModBlocks.POTTED_MOUNTAIN_HEATHER.get(), "PottedMountain Heather");
        add(ModBlocks.SNOWBELL.get(), "Snowbell");
        add(ModBlocks.POTTED_SNOWBELL.get(), "Potted Snowbell");

        //Items

        add(ModItems.CHALK_DUST.get(), "Chalk Dust");
        add(ModItems.PEAT_BALL.get(), "Peat Ball");

        add(ModItems.ALPINE_SIGN.get(),"Alpine Sign");
        add(ModItems.ALPINE_HANGING_SIGN.get(),"Alpine Hanging Sign");

        //Others

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
