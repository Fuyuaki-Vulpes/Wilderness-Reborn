package com.fuyuaki.r_wilderness.data.generation.lang;

import com.fuyuaki.r_wilderness.world.item.ModItems;
import com.fuyuaki.r_wilderness.world.level.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

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

        //Items

        add(ModItems.CHALK_DUST.get(), "Chalk Dust");
        add(ModItems.PEAT_BALL.get(), "Peat Ball");

        //Others

        add("system.r_wilderness.asleepMessage","%1$s out of %2$s players asleep, speeding up time to %3$s");
        add("system.r_wilderness.cancel","No players are asleep. Returning time speed to normal");

        this.advancements();
        this.sounds();
        this.commands();

        add("generator.r_wilderness.reborn", "Reborn");
    }

    private void sounds() {



    }

    private void advancements() {


    }


    private void commands() {




    }


}
