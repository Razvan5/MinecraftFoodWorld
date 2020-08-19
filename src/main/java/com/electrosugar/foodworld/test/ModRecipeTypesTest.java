package com.electrosugar.foodworld.test;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.ExampleChestBlock;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.item.crafting.IRecipeType.register;

public class ModRecipeTypesTest {

//   public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_TYPES = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, FoodWorld.MOD_ID);
   static IRecipeType<? extends AbstractBoilingRecipeTest> BOILING = register("boiling");
//   public static final RegistryObject<IRecipeSerializer<? extends AbstractBoilingRecipeTest>> BOILING = RECIPE_TYPES.register("boiling",()-> new AbstractBoilingRecipeTest());
}