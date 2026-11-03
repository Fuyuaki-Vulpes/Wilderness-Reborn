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
        //block(RBlocks.WEATHERED_BEDROCK.get(), "Weathered Bedrock");
        //block(RBlocks.WEATHERED_BEDROCK_WALL.get(), "Weathered Bedrock Wall");
        //block(RBlocks.WEATHERED_BEDROCK_SLAB.get(), "Weathered Bedrock Slab");
        //block(RBlocks.WEATHERED_BEDROCK_STAIRS.get(), "Weathered Bedrock Stairs");
        block(RBlocks.SCHINITE.get(), "Schinite");
        block(RBlocks.SCHINITE_WALL.get(), "Schinite Wall");
        block(RBlocks.SCHINITE_SLAB.get(), "Schinite Slab");
        block(RBlocks.SCHINITE_STAIRS.get(), "Schinite Stairs");
        block(RBlocks.MAGNEISS.get(), "Magneiss");
        block(RBlocks.MAGNEISS_WALL.get(), "Magneiss Wall");
        block(RBlocks.MAGNEISS_SLAB.get(), "Magneiss Slab");
        block(RBlocks.MAGNEISS_STAIRS.get(), "Magneiss Stairs");
        block(RBlocks.MALATITE.get(), "Malatite");
        block(RBlocks.MALATITE_WALL.get(), "Malatite Wall");
        block(RBlocks.MALATITE_SLAB.get(), "Malatite Slab");
        block(RBlocks.MALATITE_STAIRS.get(), "Malatite Stairs");
        block(RBlocks.COBBLED_SCHINITE.get(), "Cobbled Schinite");
        block(RBlocks.COBBLED_SCHINITE_WALL.get(), "Cobbled Schinite Wall");
        block(RBlocks.COBBLED_SCHINITE_SLAB.get(), "Cobbled Schinite Slab");
        block(RBlocks.COBBLED_SCHINITE_STAIRS.get(), "Cobbled Schinite Stairs");
        block(RBlocks.COBBLED_MAGNEISS.get(), "Cobbled Magneiss");
        block(RBlocks.COBBLED_MAGNEISS_WALL.get(), "Cobbled Magneiss Wall");
        block(RBlocks.COBBLED_MAGNEISS_SLAB.get(), "Cobbled Magneiss Slab");
        block(RBlocks.COBBLED_MAGNEISS_STAIRS.get(), "Cobbled Magneiss Stairs");
        block(RBlocks.COBBLED_MALATITE.get(), "Cobbled Malatite");
        block(RBlocks.COBBLED_MALATITE_WALL.get(), "Cobbled Malatite Wall");
        block(RBlocks.COBBLED_MALATITE_SLAB.get(), "Cobbled Malatite Slab");
        block(RBlocks.COBBLED_MALATITE_STAIRS.get(), "Cobbled Malatite Stairs");


        block(RBlocks.SCHINITE_GOLD_ORE.get(),"Schinite Gold Ore");
        block(RBlocks.MAGNEISS_GOLD_ORE.get(), "Magneiss Gold Ore");
        block(RBlocks.MALATITE_GOLD_ORE.get(), "Malatite Gold Ore");

        block(RBlocks.SCHINITE_IRON_ORE.get(), "Schinite Iron Ore");
        block(RBlocks.MAGNEISS_IRON_ORE.get(), "Magneiss Iron Ore");
        block(RBlocks.MALATITE_IRON_ORE.get(), "Malatite Iron Ore");

        //block(RBlocks.SCHINITE_COAL_ORE.get(), "Schinite Coal Ore");
        //block(RBlocks.MAGNEISS_COAL_ORE.get(), "Magneiss Coal Ore");
        //block(RBlocks.MALATITE_COAL_ORE.get(), "Malatite Coal Ore");

        block(RBlocks.SCHINITE_LAPIS_ORE.get(), "Schinite Lapis Ore");
        block(RBlocks.MAGNEISS_LAPIS_ORE.get(), "Magneiss Lapis Ore");
        block(RBlocks.MALATITE_LAPIS_ORE.get(), "Malatite Lapis Ore");

        block(RBlocks.SCHINITE_DIAMOND_ORE.get(), "Schinite Diamond Ore");
        block(RBlocks.MAGNEISS_DIAMOND_ORE.get(), "Magneiss Diamond Ore");
        block(RBlocks.MALATITE_DIAMOND_ORE.get(), "Malatite Diamond Ore");

        block(RBlocks.SCHINITE_REDSTONE_ORE.get(), "Schinite Redstone Ore");
        block(RBlocks.MAGNEISS_REDSTONE_ORE.get(), "Magneiss Redstone Ore");
        block(RBlocks.MALATITE_REDSTONE_ORE.get(), "Malatite Redstone Ore");

        block(RBlocks.SCHINITE_EMERALD_ORE.get(), "Schinite Emerald Ore");
        block(RBlocks.MAGNEISS_EMERALD_ORE.get(), "Magneiss Emerald Ore");
        block(RBlocks.MALATITE_EMERALD_ORE.get(), "Malatite Emerald Ore");

        block(RBlocks.SCHINITE_COPPER_ORE.get(), "Schinite Copper Ore");
        block(RBlocks.MAGNEISS_COPPER_ORE.get(), "Magneiss Copper Ore");
        block(RBlocks.MALATITE_COPPER_ORE.get(), "Malatite Copper Ore");

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

        //Flowers

        block(RBlocks.BELLFLOWER.get(), "Bellflower");
        add(RBlocks.POTTED_BELLFLOWER.get(), "Potted Bellflower");
        block(RBlocks.MOUNTAIN_HEATHER.get(), "Mountain Heather");
        add(RBlocks.POTTED_MOUNTAIN_HEATHER.get(), "PottedMountain Heather");

        //Items

        add(RItems.CHALK_DUST.get(), "Chalk Dust");
        add(RItems.PEAT_BALL.get(), "Peat Ball");

        add(RItems.ALPINE_SIGN.get(),"Alpine Sign");
        add(RItems.ALPINE_HANGING_SIGN.get(),"Alpine Hanging Sign");

        //Others

        add("system.r_wilderness.asleepMessage","%1$s out of %2$s players asleep, speeding up time to %3$s");
        add("system.r_wilderness.cancel","No players are asleep. Returning time speed to normal");

        this.advancements();
        this.sounds();
        this.commands();
        add("death.attack.hypothermia","%1$s's body got Too Cold!");
        add("death.attack.hyperthermia","%1$s died from Overheating!");

        add("generator.r_wilderness.reborn", "Reborn");
        add("itemGroup.r_wilderness_main", "Reborn: Wilderness");
        add("r_wilderness.generation.regions","Generating Spawn Regions");
        add("r_wilderness.generation.warn","The game may Slow Down, Lag, or Freeze while loading!");
        add("r_wilderness.generation.warn_second_line","16384 to 32768 blocks are generated in every direction! Please wait at least 10 minutes before closing.");


    }

    private void sounds() {



    }

    private void advancements() {


    }


    private void commands() {
        add("commands.r_wilderness.arguments.region_layer.invalid", "Region Layer must be one of %1$s, found invalid Key %2$s");
        add("commands.r_wilderness.arguments.region_layer.success", "Best Match for value %s, for Region Layer %s, at value %s, is at %s (%s blocks away)");
        add("commands.r_wilderness.arguments.region_layer.fail", "Could not find match for value %s within %s");


    }


    private void block(Block block, String name){
        add(block,name);
        add(block.asItem(),name);
    }

}
