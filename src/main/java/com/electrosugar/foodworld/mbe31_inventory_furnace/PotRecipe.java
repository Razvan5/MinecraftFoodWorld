package com.electrosugar.foodworld.mbe31_inventory_furnace;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class PotRecipe extends AbstractPotRecipe {
    public PotRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
        super(IRecipeType.SMELTING, idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
    }

    public ItemStack getIcon() {
        return new ItemStack(StartupCommon.blockFurnace);
    }

    public IRecipeSerializer<?> getSerializer() {
        return IRecipeSerializer.SMELTING;
    }
}
