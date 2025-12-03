package com.fuyuaki.r_wilderness.data.generation.loot;

import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LootBlocks extends BlockLootSubProvider {
    List<Block> excludedBlocks = List.of();
    List<Block> knownBlocks = RBlocks.BLOCKS.getEntries().stream().map(Holder::value).collect(Collectors.toList());
    protected static final float[] PALM_LEAVES_SAPLING_CHANCES = new float[]{0.1F, 0.2F, 0.4F, 0.6F};

    protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
    public LootBlocks(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(),provider);
    }

    @Override
    protected void generate() {

        add(RBlocks.CHALK.get(), block -> createSingleItemTableWithSilkTouch(block, RItems.CHALK_DUST,ConstantValue.exactly(4.0F)));
        dropSelf(RBlocks.LIMESTONE.get());
        dropSelf(RBlocks.MUD_STONE.get());

        dropSelf(RBlocks.CHALKY_SOIL.get());
        dropOther(RBlocks.CHALKY_FARMLAND.get(), RBlocks.CHALKY_SOIL);
        dropSelf(RBlocks.CLAY_SOIL.get());
        dropOther(RBlocks.CLAY_FARMLAND.get(), RBlocks.CLAY_SOIL);
        add(RBlocks.PEAT.get(), block -> createSingleItemTableWithSilkTouch(block, RItems.PEAT_BALL, ConstantValue.exactly(4.0F)));
        add(RBlocks.PEAT_FARMLAND.get(), createSingleItemTable(RItems.PEAT_BALL, ConstantValue.exactly(4.0F)));
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


    protected LootTable.Builder createLeavesDrops(Block leavesBlock, Item saplingBlock, float... chances) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchOrShearsDispatchTable(
                        leavesBlock,
                        ((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(leavesBlock, LootItem.lootTableItem(saplingBlock)))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), chances))
                )
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .when(this.doesNotHaveShearsOrSilkTouch())
                                .add(
                                        ((LootPoolSingletonContainer.Builder)this.applyExplosionDecay(
                                                leavesBlock, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                        ))
                                                .when(BonusLevelTableCondition.bonusLevelFlatChance(registrylookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))
                                )
                );
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return this.hasShears().or(this.hasSilkTouch());
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }

}
