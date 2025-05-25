package com.fuyuaki.wilderness_reborn.data.generation.model;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GenBlockModels extends BlockModelGenerators {


    // CLEAR QUARTZ CLUSTER NEEDED

    public final Consumer<BlockModelDefinitionGenerator> blockStateOutput;
    final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

    public GenBlockModels(ModModelProvider.BlockStateGeneratorCollector blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        super(blockStateOutput, itemModelOutput, modelOutput);
        this.blockStateOutput = blockStateOutput;
        this.modelOutput = modelOutput;
    }

    @Override
    public void run() {
//        ModFamilies.getAllFamilies()
//                .filter(BlockFamily::shouldGenerateModel)
//                .forEach(p_386718_ -> this.family(p_386718_.getBaseBlock()).generateFor(p_386718_));

        }

    public void createVerticalBlock(Block block, Block side, Block bottom, Block top){
        TextureMapping texturemapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(bottom))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(top))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(side));
        this.blockStateOutput.accept(createSimpleBlock(block, plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(block, texturemapping, this.modelOutput))));
    }
    public void createVerticalBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top){
        TextureMapping texturemapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.TOP, side)
                .put(TextureSlot.SIDE, top);
        this.blockStateOutput.accept(createSimpleBlock(block, plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(block, texturemapping, this.modelOutput))));
    }
    public void createGrassBlock(Block block, Block dirt, Block top, boolean snow, ItemTintSource colorSource) {
        ResourceLocation resourcelocation = TextureMapping.getBlockTexture(dirt);

        TextureMapping texturemapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, resourcelocation)
                .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_snow"));

        MultiVariant multivariant = plainVariant(
                ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(
                        Blocks.GRASS_BLOCK,
                        "_snow",
                        texturemapping,
                        this.modelOutput)
        );
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block);

        if (snow) {
            this.createGrassLikeBlock(block, createRotatedVariants(plainModel(resourcelocation1)), multivariant);
        } else {
            this.blockStateOutput
                    .accept(
                           createSimpleBlock(block,createRotatedVariants(plainModel(resourcelocation1)))
                    );
        }
        this.registerSimpleTintedItemModel(block, resourcelocation1, colorSource);

    }

    public void createGrassBlock(Block block, Block dirt,boolean snow, ItemTintSource colorSource) {
    this.createGrassBlock(block,dirt,block,snow,colorSource);
    }




}
