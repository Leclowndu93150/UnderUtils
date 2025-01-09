package com.monsoon.underutils.datagen;

import com.monsoon.underutils.UnderUtils;
import com.monsoon.underutils.register.PItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RecipesProvider extends RecipeProvider {
    public RecipesProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PItems.FLASHLIGHT.asItem())
                .pattern(" II")
                .pattern("ISG")
                .pattern(" II")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.REDSTONE)
                .define('G', Items.GLASS)
                .unlockedBy("has_item", has(PItems.BATTERY))
                .save(pRecipeOutput, UnderUtils.rl("flashlight"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PItems.WINDUP_FLASHLIGHT.asItem())
                .pattern("I I")
                .pattern("IIR")
                .pattern("  I")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_item", has(PItems.BATTERY))
                .save(pRecipeOutput, UnderUtils.rl("windup_flashlight"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PItems.BATTERY_PACK.asItem())
                .pattern("RIR")
                .pattern("BBB")
                .pattern("BBB")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('B', PItems.BATTERY)
                .unlockedBy("has_item", has(PItems.BATTERY))
                .save(pRecipeOutput, UnderUtils.rl("battery_pack"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PItems.NIGHT_VISION_GOGGLES.asItem())
                .pattern("III")
                .pattern("IGI")
                .define('I', Items.IRON_INGOT)
                .define('G', Items.GREEN_STAINED_GLASS_PANE)
                .unlockedBy("has_item", has(PItems.BATTERY))
                .save(pRecipeOutput, UnderUtils.rl("night_vision_goggles"));
    }
}
