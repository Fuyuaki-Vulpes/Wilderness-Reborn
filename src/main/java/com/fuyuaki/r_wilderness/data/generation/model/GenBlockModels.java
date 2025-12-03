package com.fuyuaki.r_wilderness.data.generation.model;

import com.fuyuaki.r_wilderness.world.block.RBlocks;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.registries.DeferredBlock;

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

        createTrivialCube(RBlocks.CHALK.get());
        createTrivialCube(RBlocks.LIMESTONE.get());
        createTrivialCube(RBlocks.MUD_STONE.get());
        createTrivialCube(RBlocks.CHALKY_SOIL.get());
        createFarmland(RBlocks.CHALKY_FARMLAND, RBlocks.CHALKY_SOIL);
        createTrivialCube(RBlocks.CLAY_SOIL.get());
        createFarmland(RBlocks.CLAY_FARMLAND, RBlocks.CLAY_SOIL);
        createTrivialCube(RBlocks.PEAT.get());
        createFarmland(RBlocks.PEAT_FARMLAND, RBlocks.PEAT);
        createTrivialCube(RBlocks.SANDY_SOIL.get());
        createFarmland(RBlocks.SANDY_FARMLAND, RBlocks.SANDY_SOIL);
        createTrivialCube(RBlocks.SILT.get());
        createFarmland(RBlocks.SILT_FARMLAND, RBlocks.SILT);

        //Trees & Wood
        woodProvider(RBlocks.ALPINE_LOG.get()).logWithHorizontal(RBlocks.ALPINE_LOG.get()).wood(RBlocks.ALPINE_WOOD.get());
        woodProvider(RBlocks.STRIPPED_ALPINE_LOG.get()).logWithHorizontal(RBlocks.STRIPPED_ALPINE_LOG.get()).wood(RBlocks.STRIPPED_ALPINE_WOOD.get());
        createTintedLeaves(RBlocks.ALPINE_LEAVES.get(), TexturedModel.LEAVES, FoliageColor.FOLIAGE_EVERGREEN);
        createPlantWithDefaultItem(RBlocks.ALPINE_SAPLING.get(),RBlocks.POTTED_ALPINE_SAPLING.get(),PlantType.NOT_TINTED);
        createHangingSign(RBlocks.STRIPPED_ALPINE_LOG.get(), RBlocks.ALPINE_HANGING_SIGN.get(), RBlocks.ALPINE_HANGING_WALL_SIGN.get());

        //Flowers
        createPlantWithDefaultItem(RBlocks.BELLFLOWER.get(), RBlocks.POTTED_BELLFLOWER.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        createPlantWithDefaultItem(RBlocks.MOSS_COMPANION.get(), RBlocks.POTTED_MOSS_COMPANION.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        createPlantWithDefaultItem(RBlocks.MOUNTAIN_HEATHER.get(), RBlocks.POTTED_MOUNTAIN_HEATHER.get(), BlockModelGenerators.PlantType.NOT_TINTED);
        createPlantWithDefaultItem(RBlocks.SNOWBELL.get(), RBlocks.POTTED_SNOWBELL.get(), BlockModelGenerators.PlantType.NOT_TINTED);


        vanillaOverride();
    }

    private void vanillaOverride() {
        this.createGrassBlock(Blocks.GRASS_BLOCK,Blocks.DIRT,new GrassColorSource());

        this.createRTintedPlantBlock(Blocks.SHORT_GRASS, RPlantType.TINTED);
        this.createRTintedPlantBlock(Blocks.BUSH, RPlantType.TINTED);
        this.createRPlantBlock(Blocks.SHORT_DRY_GRASS, RPlantType.NOT_TINTED);
        this.createRPlantBlock(Blocks.TALL_DRY_GRASS, RPlantType.NOT_TINTED);
        this.createTintedRebornPlant(Blocks.FERN, Blocks.POTTED_FERN,RPlantType.FERN_LIKE);
        this.createRDoublePlantWithTintedModel(Blocks.LARGE_FERN, RPlantType.FERN_LIKE, RPlantType.FERN_LIKE_DOUBLE_BOTTOM);
        this.createRDoublePlantWithTintedModel(Blocks.TALL_GRASS, RPlantType.TINTED, RPlantType.TINTED);
        this.createRPlantBlock(Blocks.FIREFLY_BUSH, RPlantType.EMISSIVE_NOT_TINTED);

    }


    //TEXTURE METHODS

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
    public void createGrassBlock(Block block, Block dirt, Block top, ItemTintSource colorSource) {
        ResourceLocation resourcelocation = TextureMapping.getBlockTexture(dirt);

        TextureMapping texturemapping = new TextureMapping()
                .put(RebornSlots.DIRT, resourcelocation)
                .copyForced(RebornSlots.DIRT, TextureSlot.PARTICLE)
                .put(RebornSlots.GRASS, TextureMapping.getBlockTexture(top, "_top"))
                .put(RebornSlots.OVERLAY, TextureMapping.getBlockTexture(block, "_snow_overlay"));

        TextureMapping texturemappingNoSnow = new TextureMapping()
                .put(RebornSlots.DIRT, resourcelocation)
                .copyForced(RebornSlots.DIRT, TextureSlot.PARTICLE)
                .put(RebornSlots.GRASS, TextureMapping.getBlockTexture(top, "_top"))
                .put(RebornSlots.OVERLAY, TextureMapping.getBlockTexture(block, "_overlay"));

        MultiVariant multivariant = plainVariant(
                RebornModels.TEMPLATE_GRASS_BLOCK_NO_TINT.createWithSuffix(
                        Blocks.GRASS_BLOCK,
                        "_snow",
                        texturemapping,
                        this.modelOutput)
        );
        Variant variant = plainModel(
                RebornModels.TEMPLATE_GRASS_BLOCK.create(
                        Blocks.GRASS_BLOCK,
                        texturemappingNoSnow,
                        this.modelOutput)
        );
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block);

        this.createGrassLikeBlock(block, createRotatedVariants(variant), multivariant);

        this.registerSimpleTintedItemModel(block, resourcelocation1, colorSource);

    }

    public void createGrassBlock(Block block, Block dirt, ItemTintSource colorSource) {
    this.createGrassBlock(block,dirt,block, colorSource);
    }

    public void createFarmland(DeferredBlock<Block> farmland, DeferredBlock<Block> soil) {
        TextureMapping texturemapping = new TextureMapping()
                .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(soil.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(farmland.get()));
        TextureMapping texturemapping1 = new TextureMapping()
                .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(soil.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(farmland.get(), "_moist"));
        MultiVariant multivariant = plainVariant(ModelTemplates.FARMLAND.create(farmland.get(), texturemapping, this.modelOutput));
        MultiVariant multivariant1 = plainVariant(
                ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(farmland.get(), "_moist"), texturemapping1, this.modelOutput)
        );
        this.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(farmland.get()).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, multivariant1, multivariant))
                );
    }


    public void createRPlantBlock(Block block, RPlantType plantType) {
        TextureMapping texturemapping = plantType.getTextureMapping(block);
        this.createRPlantBlock(block, plantType, texturemapping);
    }
    public void createRPlantBlock(Block block, RPlantType plantType, TextureMapping textureMapping) {
        ResourceLocation location = plantType.getCross().create(block, textureMapping, this.modelOutput);
        MultiVariant multivariant = plainVariant(location);
        this.registerSimpleItemModel(block, location);
        this.blockStateOutput.accept(createSimpleBlock(block, multivariant));
    }
    public void createRTintedPlantBlock(Block block, RPlantType plantType) {
        TextureMapping texturemapping = plantType.getTextureMapping(block);
        this.createRTintedPlantBlock(block, plantType, texturemapping);
    }
    public void createRTintedPlantBlock(Block block, RPlantType plantType, TextureMapping textureMapping) {
        ResourceLocation location = plantType.getCross().create(block, textureMapping, this.modelOutput);
        MultiVariant multivariant = plainVariant(location);
        this.registerSimpleTintedItemModel(block, location, new GrassColorSource());
        this.blockStateOutput.accept(createSimpleBlock(block, multivariant));
    }
    public void createRebornPlant(Block block, Block pottedBlock, RPlantType plantType) {
        this.createRPlantBlock(block, plantType);
        TextureMapping texturemapping = plantType.getPlantTextureMapping(block);
        MultiVariant multivariant = plainVariant(plantType.getCrossPot().create(pottedBlock, texturemapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(pottedBlock, multivariant));
    }public void createTintedRebornPlant(Block block, Block pottedBlock, RPlantType plantType) {
        this.createRTintedPlantBlock(block, plantType);
        TextureMapping texturemapping = plantType.getPlantTextureMapping(block);
        MultiVariant multivariant = plainVariant(plantType.getCrossPot().create(pottedBlock, texturemapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(pottedBlock, multivariant));
    }

    public void createRDoublePlantWithTintedModel(Block block, RPlantType plantType, RPlantType bottomType) {
        ResourceLocation topLocation = this.createSuffixedVariant(block, "_top", plantType.getCross(), TextureMapping::cross);
        MultiVariant multivariant = plainVariant(topLocation);
        MultiVariant multivariant1 = plainVariant(this.createSuffixedVariant(block, "_bottom", bottomType.getCross(), bottomType == RPlantType.FERN_LIKE_DOUBLE_BOTTOM ? f -> bottomType.getTextureMapping(block) : TextureMapping::cross));
        this.registerSimpleTintedItemModel(block,topLocation, new GrassColorSource());
        this.createDoubleBlock(block, multivariant, multivariant1);
    }


    public void createDoubleBlock(Block block, MultiVariant lower, MultiVariant upper) {
        this.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(
                                        PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF)
                                                .select(DoubleBlockHalf.LOWER, upper)
                                                .select(DoubleBlockHalf.UPPER, lower)
                                )
                );
    }



    //OTHERS

    public static enum RPlantType {
        TINTED(RebornModels.GRASS_FOLIAGE, ModelTemplates.TINTED_FLOWER_POT_CROSS, false),
        FERN_LIKE(RebornModels.FERN_FOLIAGE, ModelTemplates.TINTED_FLOWER_POT_CROSS, false),
        FERN_LIKE_DOUBLE_BOTTOM(RebornModels.FERN_FOLIAGE_BOTTOM, ModelTemplates.TINTED_FLOWER_POT_CROSS, false),
        NOT_TINTED(RebornModels.GRASS_FOLIAGE, ModelTemplates.FLOWER_POT_CROSS, false),
        EMISSIVE_NOT_TINTED(RebornModels.EMISSIVE_FOLIAGE, ModelTemplates.FLOWER_POT_CROSS_EMISSIVE, true);

        private final ModelTemplate blockTemplate;
        private final ModelTemplate flowerPotTemplate;
        private final boolean isEmissive;

        private RPlantType(ModelTemplate blockTemplate, ModelTemplate flowerPotTemplate, boolean isEmissive) {
            this.blockTemplate = blockTemplate;
            this.flowerPotTemplate = flowerPotTemplate;
            this.isEmissive = isEmissive;
        }

        public ModelTemplate getCross() {
            return this.blockTemplate;
        }

        public ModelTemplate getCrossPot() {
            return this.flowerPotTemplate;
        }

        public ResourceLocation createItemModel(BlockModelGenerators generator, Block block) {
            Item item = block.asItem();
            return this.isEmissive
                    ? generator.createFlatItemModelWithBlockTextureAndOverlay(item, block, "_emissive")
                    : generator.createFlatItemModelWithBlockTexture(item, block);
        }

        public TextureMapping getTextureMapping(Block block) {
            if (this == FERN_LIKE_DOUBLE_BOTTOM){
                return RebornMapping.crossBottom(block);
            }
            return this.isEmissive ? TextureMapping.crossEmissive(block) : TextureMapping.cross(block);
        }

        public TextureMapping getPlantTextureMapping(Block block) {
            return this.isEmissive ? TextureMapping.plantEmissive(block) : TextureMapping.plant(block);
        }
    }



}
