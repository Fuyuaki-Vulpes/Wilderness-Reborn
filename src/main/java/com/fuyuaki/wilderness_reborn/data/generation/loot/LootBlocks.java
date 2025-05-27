package com.fuyuaki.wilderness_reborn.data.generation.loot;

import com.fuyuaki.wilderness_reborn.world.item.ModItems;
import com.fuyuaki.wilderness_reborn.world.level.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public class LootBlocks extends BlockLootSubProvider {
    List<Block> excludedBlocks = List.of();
    List<Block> knownBlocks = List.of();
//            MtaBlocks.BLOCKS.getEntries().stream().map(Holder::value).collect(Collectors.toList());
    protected static final float[] PALM_LEAVES_SAPLING_CHANCES = new float[]{0.1F, 0.2F, 0.4F, 0.6F};

    protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
    public LootBlocks(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(),provider);
    }

    @Override
    protected void generate() {

        add(ModBlocks.CHALK.get(), block -> createSingleItemTableWithSilkTouch(block, ModItems.CHALK_DUST,ConstantValue.exactly(4.0F)));
        dropSelf(ModBlocks.LIMESTONE.get());
        dropSelf(ModBlocks.MUD_STONE.get());

        dropSelf(ModBlocks.CHALKY_SOIL.get());
        dropOther(ModBlocks.CHALKY_FARMLAND.get(), ModBlocks.CHALKY_SOIL);
        dropSelf(ModBlocks.CLAY_SOIL.get());
        dropOther(ModBlocks.CLAY_FARMLAND.get(), ModBlocks.CLAY_SOIL);
        add(ModBlocks.PEAT.get(), block -> createSingleItemTableWithSilkTouch(block, ModItems.PEAT_BALL, ConstantValue.exactly(4.0F)));
        add(ModBlocks.PEAT_FARMLAND.get(), createSingleItemTable(ModItems.PEAT_BALL, ConstantValue.exactly(4.0F)));
        dropSelf(ModBlocks.SANDY_SOIL.get());
        dropOther(ModBlocks.SANDY_FARMLAND.get(), ModBlocks.SANDY_SOIL);
        dropSelf(ModBlocks.SILT.get());
        dropOther(ModBlocks.SILT_FARMLAND.get(), ModBlocks.SILT);

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
