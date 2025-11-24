package com.fuyuaki.r_wilderness.data.generation.lang;

import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
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

        add(RBlocks.CHALK.get(), "Chalk");
        add(RBlocks.LIMESTONE.get(), "Limestone");
        add(RBlocks.MUD_STONE.get(), "Mud Stone");

        //Soils

        add(RBlocks.CHALKY_SOIL.get(),"Chalky Soil");
        add(RBlocks.CHALKY_FARMLAND.get(),"Chalky Farmland");
        add(RBlocks.CLAY_SOIL.get(),"Clay Soil");
        add(RBlocks.CLAY_FARMLAND.get(),"Clay Farmland");
        add(RBlocks.PEAT.get(),"Peat");
        add(RBlocks.PEAT_FARMLAND.get(),"Peat Farmland");
        add(RBlocks.SANDY_SOIL.get(),"Sandy Soil");
        add(RBlocks.SANDY_FARMLAND.get(),"Sandy Farmland");
        add(RBlocks.SILT.get(),"Silt");
        add(RBlocks.SILT_FARMLAND.get(),"Silt Farmland");

        //Items

        add(RItems.CHALK_DUST.get(), "Chalk Dust");
        add(RItems.PEAT_BALL.get(), "Peat Ball");

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

/*
        add("r_wilderness.commands.locate.not_found", "Could not find goal within reasonable distance");
        add("r_wilderness.commands.locate.river.success", "The nearest River is at %s (%s blocks away)");
        add("r_wilderness.commands.locate.terrain.success", "The nearest Target Terrain is at %s (%s blocks away)");
        add("r_wilderness.chat.horizontal_coordinates", "%s , %s");
        add("r_wilderness.chat.horizontal_coordinates.tooltip", "Click to teleport");

*/
    }


}
