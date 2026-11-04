package com.fuyuaki.r_wilderness.data.generation.loot;

import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LootBlocks extends BlockLootSubProvider {
    List<Block> excludedBlocks = List.of();
    List<Block> knownBlocks = RBlocks.BLOCKS.getEntries().stream().map(Holder::value).collect(Collectors.toList());
    protected static final float[] PALM_LEAVES_SAPLING_CHANCES = new float[]{0.1F, 0.2F, 0.4F, 0.6F};

    protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
    public LootBlocks(LootTableSubProvider.Context context) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(),context);
    }

    @Override
    protected void generate() {

        add(RBlocks.CHALK.get(), block -> createSingleItemTableWithSilkTouch(block, RItems.CHALK_DUST,ContextIntProviders.exactly(4)));
        dropSelf(RBlocks.LIMESTONE.get());
        dropSelf(RBlocks.MUD_STONE.get());
        add(RBlocks.SCHINITE.get(), block -> createSingleItemTableWithSilkTouch(block, RBlocks.COBBLED_SCHINITE));
        add(RBlocks.MAGNEISS.get(), block -> createSilkTouchDispatchTable(block, applyExplosionDecay(block,LootItem.lootTableItem(RBlocks.COBBLED_MAGNEISS))));
        add(RBlocks.MALATITE.get(), block -> createSilkTouchDispatchTable(block, applyExplosionDecay(block,LootItem.lootTableItem(RBlocks.COBBLED_MALATITE))));


        dropSelf(RBlocks.WEATHERED_BEDROCK.get());
        dropSelf(RBlocks.WEATHERED_BEDROCK_STAIRS.get());
        dropSelf(RBlocks.WEATHERED_BEDROCK_SLAB.get());
        dropSelf(RBlocks.WEATHERED_BEDROCK_WALL.get());

        dropSelf(RBlocks.SCHINITE_STAIRS.get());
        dropSelf(RBlocks.SCHINITE_SLAB.get());
        dropSelf(RBlocks.SCHINITE_WALL.get());
        dropSelf(RBlocks.MAGNEISS_STAIRS.get());
        dropSelf(RBlocks.MAGNEISS_SLAB.get());
        dropSelf(RBlocks.MAGNEISS_WALL.get());
        dropSelf(RBlocks.MALATITE_STAIRS.get());
        dropSelf(RBlocks.MALATITE_SLAB.get());
        dropSelf(RBlocks.MALATITE_WALL.get());

        dropSelf(RBlocks.COBBLED_SCHINITE.get());
        dropSelf(RBlocks.COBBLED_SCHINITE_STAIRS.get());
        dropSelf(RBlocks.COBBLED_SCHINITE_SLAB.get());
        dropSelf(RBlocks.COBBLED_SCHINITE_WALL.get());
        dropSelf(RBlocks.COBBLED_MAGNEISS.get());
        dropSelf(RBlocks.COBBLED_MAGNEISS_STAIRS.get());
        dropSelf(RBlocks.COBBLED_MAGNEISS_SLAB.get());
        dropSelf(RBlocks.COBBLED_MAGNEISS_WALL.get());
        dropSelf(RBlocks.COBBLED_MALATITE.get());
        dropSelf(RBlocks.COBBLED_MALATITE_STAIRS.get());
        dropSelf(RBlocks.COBBLED_MALATITE_SLAB.get());
        dropSelf(RBlocks.COBBLED_MALATITE_WALL.get());

        //Ores

        add(RBlocks.SCHINITE_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(RBlocks.SCHINITE_COPPER_ORE.get(), block -> createOreDrop(block, Items.RAW_COPPER));
        add(RBlocks.SCHINITE_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(RBlocks.SCHINITE_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(RBlocks.SCHINITE_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(RBlocks.SCHINITE_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(RBlocks.SCHINITE_REDSTONE_ORE.get(), block -> createOreDrop(block, Items.REDSTONE));
        add(RBlocks.SCHINITE_LAPIS_ORE.get(), block -> createOreDrop(block, Items.LAPIS_LAZULI));

        add(RBlocks.MAGNEISS_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(RBlocks.MAGNEISS_COPPER_ORE.get(), block -> createOreDrop(block, Items.RAW_COPPER));
        add(RBlocks.MAGNEISS_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(RBlocks.MAGNEISS_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(RBlocks.MAGNEISS_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(RBlocks.MAGNEISS_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(RBlocks.MAGNEISS_REDSTONE_ORE.get(), block -> createOreDrop(block, Items.REDSTONE));
        add(RBlocks.MAGNEISS_LAPIS_ORE.get(), block -> createOreDrop(block, Items.LAPIS_LAZULI));

        add(RBlocks.MALATITE_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(RBlocks.MALATITE_COPPER_ORE.get(), block -> createOreDrop(block, Items.RAW_COPPER));
        add(RBlocks.MALATITE_IRON_ORE.get(), block -> createOreDrop(block, Items.RAW_IRON));
        add(RBlocks.MALATITE_GOLD_ORE.get(), block -> createOreDrop(block, Items.RAW_GOLD));
        add(RBlocks.MALATITE_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(RBlocks.MALATITE_EMERALD_ORE.get(), block -> createOreDrop(block, Items.EMERALD));
        add(RBlocks.MALATITE_REDSTONE_ORE.get(), block -> createOreDrop(block, Items.REDSTONE));
        add(RBlocks.MALATITE_LAPIS_ORE.get(), block -> createOreDrop(block, Items.LAPIS_LAZULI));

        //Soils

        dropSelf(RBlocks.CHALKY_SOIL.get());
        dropOther(RBlocks.CHALKY_FARMLAND.get(), RBlocks.CHALKY_SOIL);
        dropSelf(RBlocks.CLAY_SOIL.get());
        dropOther(RBlocks.CLAY_FARMLAND.get(), RBlocks.CLAY_SOIL);
        add(RBlocks.PEAT.get(), block -> createSingleItemTableWithSilkTouch(block, RItems.PEAT_BALL, ContextIntProviders.exactly(4)));
        add(RBlocks.PEAT_FARMLAND.get(), createSingleItemTable(RItems.PEAT_BALL, ContextIntProviders.exactly(4)));
        dropSelf(RBlocks.SANDY_SOIL.get());
        dropOther(RBlocks.SANDY_FARMLAND.get(), RBlocks.SANDY_SOIL);
        dropSelf(RBlocks.SILT.get());
        dropOther(RBlocks.SILT_FARMLAND.get(), RBlocks.SILT);

        //Trees & Wood

        dropSelf(RBlocks.ALPINE_LOG.get());
        dropSelf(RBlocks.STRIPPED_ALPINE_LOG.get());
        dropSelf(RBlocks.ALPINE_WOOD.get());
        dropSelf(RBlocks.STRIPPED_ALPINE_WOOD.get());
        dropSelf(RBlocks.ALPINE_PLANKS.get());
        add(RBlocks.ALPINE_LEAVES.get(), block -> createLeavesDrops(block, RBlocks.ALPINE_SAPLING.get(),NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(RBlocks.ALPINE_SAPLING.get());
        dropSelf(RBlocks.ALPINE_STAIRS.get());
        add(RBlocks.ALPINE_SLAB.get(), block -> createSlabItemTable(RBlocks.ALPINE_SLAB.get()));
        dropSelf(RBlocks.ALPINE_PRESSURE_PLATE.get());
        dropSelf(RBlocks.ALPINE_BUTTON.get());
        dropSelf(RBlocks.ALPINE_FENCE.get());
        dropSelf(RBlocks.ALPINE_FENCE_GATE.get());
        add(RBlocks.ALPINE_DOOR.get(), block -> createDoorTable(RBlocks.ALPINE_DOOR.get()));
        dropSelf(RBlocks.ALPINE_TRAPDOOR.get());
        dropSelf(RBlocks.ALPINE_SIGN.get());
        dropOther(RBlocks.ALPINE_WALL_SIGN.get(), RBlocks.ALPINE_SIGN);
        dropSelf(RBlocks.ALPINE_HANGING_SIGN.get());
        dropOther(RBlocks.ALPINE_HANGING_WALL_SIGN.get(), RBlocks.ALPINE_HANGING_SIGN);
        dropPottedContents(RBlocks.POTTED_ALPINE_SAPLING.get());

        //Flowers
        dropSelf(RBlocks.BELLFLOWER.get());
        add(RBlocks.POTTED_BELLFLOWER.get(), createPotFlowerItemTable(RBlocks.BELLFLOWER));
        dropSelf(RBlocks.MOSS_COMPANION.get());
        add(RBlocks.POTTED_MOSS_COMPANION.get(), createPotFlowerItemTable(RBlocks.MOSS_COMPANION));
        dropSelf(RBlocks.MOUNTAIN_HEATHER.get());
        add(RBlocks.POTTED_MOUNTAIN_HEATHER.get(), createPotFlowerItemTable(RBlocks.MOUNTAIN_HEATHER));
        dropSelf(RBlocks.SNOWBELL.get());
        add(RBlocks.POTTED_SNOWBELL.get(), createPotFlowerItemTable(RBlocks.SNOWBELL));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> list = knownBlocks;
        list.remove(excludedBlocks);
        return list;
    }


    protected LootTable.Builder createLeavesDrops(Block original, Block sapling, float... saplingChances) {
        return this.createSilkTouchOrShearsDispatchTable(original, (LootPoolEntryContainer.Builder)((UniformContainerBase.Builder)this.applyExplosionCondition(original, LootItem.lootTableItem(sapling))).when(BonusLevelTableCondition.bonusLevelFlatChance(this.enchantments.getOrThrow(Enchantments.FORTUNE), saplingChances))).withPool(((LootPool.Builder)LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).when(this.doesNotHaveShearsOrSilkTouch())).add((LootPoolEntryContainer.Builder)((UniformContainerBase.Builder)this.applyExplosionDecay(original, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2))))).when(BonusLevelTableCondition.bonusLevelFlatChance(this.enchantments.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))));
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return (new AnyOfCondition.Builder(new LootItemCondition.Builder[0])).or(this.hasShears()).or(this.hasSilkTouch());
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }

}
