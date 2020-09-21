package com.electrosugar.foodworld.potclass;

import net.minecraft.item.crafting.IRecipeType;

// I made a new class because I don't like to use anonymous inner classes.
// That's more of a preference than a requirement.
public class PotRecipeType implements IRecipeType<PotRecipe> {

    @Override
    public String toString () {

        // All vanilla recipe types return their ID in toString. I am not sure how vanilla uses
        // this, or if it does. Modded types should follow this trend for the sake of
        // consistency. I am also using it during registry to create the ResourceLocation ID.
        return "foodworld:boiling";
    }
}
