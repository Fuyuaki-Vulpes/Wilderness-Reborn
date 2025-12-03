package com.fuyuaki.r_wilderness.data.generation.other;

import com.fuyuaki.r_wilderness.world.item.RItems;
import com.fuyuaki.r_wilderness.world.block.RBlocks;
import com.fuyuaki.r_wilderness.world.block.ModFamilies;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class GenRecipes  extends RecipeProvider {


    public GenRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        this.generateForEnabledBlockFamilies(FeatureFlagSet.of(FeatureFlags.VANILLA));

        twoByTwoPacker(RecipeCategory.BUILDING_BLOCKS, RBlocks.CHALK, RItems.CHALK_DUST);
        twoByTwoPacker(RecipeCategory.BUILDING_BLOCKS, RBlocks.PEAT, RItems.PEAT_BALL);
        shapeless(RecipeCategory.BUILDING_BLOCKS, RBlocks.CHALKY_SOIL)
                .requires(Blocks.DIRT)
                .requires(RItems.CHALK_DUST)
                .group("brown_dye")
                .unlockedBy("has_chalk_dust", this.has(RItems.CHALK_DUST))
                .save(this.output);
        shapeless(RecipeCategory.BUILDING_BLOCKS, RBlocks.CHALKY_FARMLAND)
                .requires(RBlocks.CHALKY_SOIL)
                .requires(Tags.Items.SEEDS)
                .group("brown_dye")
                .unlockedBy("has_chalk_dust", this.has(RItems.CHALK_DUST))
                .save(this.output);
    }





    protected void cuttingStoneSetRecipe(Block ing, Block slab, Block stair, Block wall) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ing), RecipeCategory.BUILDING_BLOCKS, slab, 2)
                .unlockedBy(getHasName(ing), has(ing)).save(this.output,getConversionRecipeName(ing,slab) + "_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ing), RecipeCategory.BUILDING_BLOCKS, stair)
                .unlockedBy(getHasName(ing), has(ing)).save(this.output,getConversionRecipeName(ing,stair) + "_stonecutter");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ing), RecipeCategory.BUILDING_BLOCKS, wall)
                .unlockedBy(getHasName(ing), has(ing)).save(this.output,getConversionRecipeName(ing,wall) + "_stonecutter");
    }

    protected void cuttingTerracottaSetRecipe(Block ing, Block tiles, Block slab, Block tSlab, Block stair, Block tStair, Block wall, Block tWall) {
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, tiles, ing);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, slab, ing, 2);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, tSlab, ing, 2);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, stair, ing);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, tStair, ing);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, wall, ing);
        stonecutterResultFromBase(RecipeCategory.DECORATIONS, tWall, ing);
    }

    public void upgradeTemplate(Item template, Block frame, String unlock) {
        shaped(RecipeCategory.MISC, template, 2)
                .define('X', template)
                .define('O', frame)
                .define('D', Items.DIAMOND)
                .pattern("DOD")
                .pattern("DXD")
                .pattern("DDD")
                .unlockedBy(unlock, has(template)).save(this.output,getItemName(frame) + "_duplication");
    }




    @Override
    protected void generateForEnabledBlockFamilies(FeatureFlagSet enabledFeatures) {
        ModFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(p_359455_ -> this.generateRecipes(p_359455_, enabledFeatures));
    }


    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new GenRecipes(lookupProvider, output);
        }

        @Override
        public String getName() {
            return "More Than Adventure recipes";
        }
    }
}
