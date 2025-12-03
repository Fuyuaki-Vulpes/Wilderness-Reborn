package com.fuyuaki.r_wilderness.data.generation.lang;

import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static com.fuyuaki.r_wilderness.api.RWildernessMod.MODID;

public class EN_US_LangProvider extends LanguageProvider {
    public EN_US_LangProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Blocks

        block(RBlocks.CHALK.get(), "Chalk");
        block(RBlocks.LIMESTONE.get(), "Limestone");
        block(RBlocks.MUD_STONE.get(), "Mud Stone");
        block(RBlocks.SCHINITE.get(), "Schinite");
        block(RBlocks.MAGNEISS.get(), "Magneiss");
        block(RBlocks.MALATITE.get(), "Malatite");
        block(RBlocks.COBBLED_SCHINITE.get(), "Cobbled Schinite");
        block(RBlocks.COBBLED_MAGNEISS.get(), "Cobbled Magneiss");
        block(RBlocks.COBBLED_MALATITE.get(), "Cobbled Malatite");

        //Soils

        block(RBlocks.CHALKY_SOIL.get(),"Chalky Soil");
        block(RBlocks.CHALKY_FARMLAND.get(),"Chalky Farmland");
        block(RBlocks.CLAY_SOIL.get(),"Clay Soil");
        block(RBlocks.CLAY_FARMLAND.get(),"Clay Farmland");
        block(RBlocks.PEAT.get(),"Peat");
        block(RBlocks.PEAT_FARMLAND.get(),"Peat Farmland");
        block(RBlocks.SANDY_SOIL.get(),"Sandy Soil");
        block(RBlocks.SANDY_FARMLAND.get(),"Sandy Farmland");
        block(RBlocks.SILT.get(),"Silt");
        block(RBlocks.SILT_FARMLAND.get(),"Silt Farmland");

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
        add("itemGroup.r_wilderness_main", "Reborn: Wilderness");
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


    private void block(Block block, String name){
        add(block,name);
        add(block.asItem(),name);
    }

}
