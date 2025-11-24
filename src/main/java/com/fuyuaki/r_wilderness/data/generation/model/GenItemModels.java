package com.fuyuaki.r_wilderness.data.generation.model;

import com.fuyuaki.r_wilderness.world.item.RItems;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.Charge;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

public class GenItemModels extends ItemModelGenerators {

    private final ItemModelOutput itemModelOutput;
    private final BiConsumer<ResourceLocation, ModelInstance> modelOutput;


    public GenItemModels(ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        super(itemModelOutput, modelOutput);
        this.itemModelOutput = itemModelOutput;
        this.modelOutput = modelOutput;
    }

    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }


    @Override
    public void run() {

        simpleItem(RItems.CHALK_DUST);
        simpleItem(RItems.PEAT_BALL);

    }


    private void handheldItem(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), ModelTemplates.FLAT_HANDHELD_ITEM)));

    }

    private void handheldClaw(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), RebornModels.CLAW)));

    }

    private void handheldBigItem(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), RebornModels.BIG_HANDHELD_LOW_HILT)));

    }
    private void handheldBigItemMediumHilt(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), RebornModels.BIG_HANDHELD_MEDIUM_HILT)));
    }
    private void handheldBigItemMiddleHilt(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), RebornModels.BIG_HANDHELD_MIDDLE_HILT)));
    }


    public void simpleItem(DeferredItem<?> item) {
        this.itemModelOutput.accept(item.get(), ItemModelUtils.plainModel(this.createFlatItemModel(item.get(), ModelTemplates.FLAT_ITEM)));
    }

    public void generateBow(Item bowItem) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(bowItem));
        this.createFlatItemModel(bowItem, RebornModels.BOW);
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_0", RebornModels.BOW));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_1", RebornModels.BOW));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_2", RebornModels.BOW));
        this.itemModelOutput
                .accept(
                        bowItem,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new UseDuration(false),
                                        0.05F,
                                        itemmodel$unbaked1,
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.65F),
                                        ItemModelUtils.override(itemmodel$unbaked3, 0.9F)
                                ),
                                itemmodel$unbaked
                        )
                );
    }
    public void generateBowBig(Item bowItem) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(bowItem));
        this.createFlatItemModel(bowItem, RebornModels.BOW_BIG);
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_0", RebornModels.BOW_BIG));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_1", RebornModels.BOW_BIG));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(this.createFlatItemModel(bowItem, "_pulling_2", RebornModels.BOW_BIG));
        this.itemModelOutput
                .accept(
                        bowItem,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new UseDuration(false),
                                        0.05F,
                                        itemmodel$unbaked1,
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.65F),
                                        ItemModelUtils.override(itemmodel$unbaked3, 0.9F)
                                ),
                                itemmodel$unbaked
                        )
                );
    }

    public void generateCrossbow(Item crossbowItem) {
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(crossbowItem));
        this.createFlatItemModel(crossbowItem, RebornModels.CROSSBOW);
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(this.createFlatItemModel(crossbowItem, "_pulling_0", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(this.createFlatItemModel(crossbowItem, "_pulling_1", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(this.createFlatItemModel(crossbowItem, "_pulling_2", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked4 = ItemModelUtils.plainModel(this.createFlatItemModel(crossbowItem, "_arrow", ModelTemplates.CROSSBOW));
        ItemModel.Unbaked itemmodel$unbaked5 = ItemModelUtils.plainModel(this.createFlatItemModel(crossbowItem, "_firework", ModelTemplates.CROSSBOW));
        this.itemModelOutput
                .accept(
                        crossbowItem,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new CrossbowPull(),
                                        itemmodel$unbaked1,
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.58F),
                                        ItemModelUtils.override(itemmodel$unbaked3, 1.0F)
                                ),
                                ItemModelUtils.select(
                                        new Charge(),
                                        itemmodel$unbaked,
                                        ItemModelUtils.when(CrossbowItem.ChargeType.ARROW, itemmodel$unbaked4),
                                        ItemModelUtils.when(CrossbowItem.ChargeType.ROCKET, itemmodel$unbaked5)
                                )
                        )
                );
    }


    public void generateDrink(Item drink) {
    }
/*
    public void generateTridents() {

        Item netheriteTrident = MtaItems.NETHERITE_TRIDENT.get();
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(netheriteTrident, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(netheriteTrident, "_in_hand"), new NetheriteTridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(netheriteTrident, "_throwing"), new NetheriteTridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), itemmodel$unbaked2, itemmodel$unbaked1);
        this.itemModelOutput.accept(netheriteTrident, createFlatModelDispatch(itemmodel$unbaked, itemmodel$unbaked3));

        MTAModelTemplates.TRIDENT.create(ModelLocationUtils.getModelLocation(netheriteTrident).withSuffix("_in_hand"), TextureMapping.particle(ModelLocationUtils.getModelLocation(netheriteTrident)), this.modelOutput);
        MTAModelTemplates.TRIDENT_THROWING.create(ModelLocationUtils.getModelLocation(netheriteTrident).withSuffix("_throwing"), TextureMapping.particle(ModelLocationUtils.getModelLocation(netheriteTrident)), this.modelOutput);

        Item mermaidTrident = MtaItems.MYSTIC_MERMAIDS_TRIDENT.get();

        ItemModel.Unbaked _itemmodel$unbaked = ItemModelUtils.plainModel(this.createFlatItemModel(mermaidTrident, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked _itemmodel$unbaked1 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(mermaidTrident, "_in_hand"), new MermaidTridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked _itemmodel$unbaked2 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(mermaidTrident, "_throwing"), new MermaidTridentSpecialRenderer.Unbaked()
        );
        MTAModelTemplates.TRIDENT.create(ModelLocationUtils.getModelLocation(mermaidTrident).withSuffix("_in_hand"), TextureMapping.particle(ModelLocationUtils.getModelLocation(mermaidTrident)), this.modelOutput);
        MTAModelTemplates.TRIDENT_THROWING.create(ModelLocationUtils.getModelLocation(mermaidTrident).withSuffix("_throwing"), TextureMapping.particle(ModelLocationUtils.getModelLocation(mermaidTrident)), this.modelOutput);

        ItemModel.Unbaked _itemmodel$unbaked3 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), _itemmodel$unbaked2, _itemmodel$unbaked1);
        this.itemModelOutput.accept(mermaidTrident, createFlatModelDispatch(_itemmodel$unbaked, _itemmodel$unbaked3));
    }*/


}
