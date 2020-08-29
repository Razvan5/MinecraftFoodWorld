package com.electrosugar.foodworld.mbe31_inventory_furnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class PotRecipe extends AbstractPotRecipe {
    public PotRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
        super(StartupCommon.POT_RECIPE_TYPE, idIn, groupIn,recipeWidthIn,recipeHeightIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
    }

    public ItemStack getIcon() {
        return new ItemStack(StartupCommon.POT_BLOCK);
    }

    public IRecipeSerializer<?> getSerializer() {
        return StartupCommon.BOILING_RECIPE_SERIALIZER;
    }
}
