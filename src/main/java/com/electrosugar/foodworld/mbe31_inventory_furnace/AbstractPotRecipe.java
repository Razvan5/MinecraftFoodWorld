package com.electrosugar.foodworld.mbe31_inventory_furnace;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;

public abstract class AbstractPotRecipe implements IShapedRecipe<PotZoneContents> {
    protected final IRecipeType<?> type;
    protected final ResourceLocation id;
    protected final String group;
    protected final int recipeWidth;
    protected final int recipeHeight;
    protected final NonNullList<Ingredient> ingredientList;
    protected final ItemStack result;
    protected final float experience;
    protected final int cookTime;

    @Override
    public int getRecipeWidth() {
        return 3;
    }

    @Override
    public int getRecipeHeight() {
        return 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return StartupCommon.BOILING_RECIPE_SERIALIZER;
    }

    public AbstractPotRecipe(IRecipeType<?> typeIn, ResourceLocation idIn, String groupIn,int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> ingredientListIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
        this.type = typeIn;
        this.id = idIn;
        this.group = groupIn;
        this.recipeWidth = recipeWidthIn;
        this.recipeHeight = recipeHeightIn;
        this.ingredientList = ingredientListIn;
        this.result = resultIn;
        this.experience = experienceIn;
        this.cookTime = cookTimeIn;
    }

    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(PotZoneContents potZoneContents, World worldIn) {
        for(int i = 0; i <= potZoneContents.getWidth() - this.recipeWidth; ++i) {
            for(int j = 0; j <= potZoneContents.getHeight() - this.recipeHeight; ++j) {
                if (this.checkMatch(potZoneContents, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(potZoneContents, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(PotZoneContents potZoneContents, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
        for(int i = 0; i < potZoneContents.getWidth(); ++i) {
            for(int j = 0; j < potZoneContents.getHeight(); ++j) {
                int k = i - p_77573_2_;
                int l = j - p_77573_3_;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (p_77573_4_) {
                        ingredient = this.ingredientList.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
                    } else {
                        ingredient = this.ingredientList.get(k + l * this.recipeWidth);
                    }
                }

                if (!ingredient.test(potZoneContents.getStackInSlot(i + j * potZoneContents.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(PotZoneContents inv) {
        return this.getRecipeOutput().copy();
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredientList;
    }


    /**
     * Gets the experience of this recipe
     */
    public float getExperience() {
        return this.experience;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Gets the cook time in ticks
     */
    public int getCookTime() {
        return this.cookTime;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public IRecipeType<?> getType() {
        return this.type;
    }
}
