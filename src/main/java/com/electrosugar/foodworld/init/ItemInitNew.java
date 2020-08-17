package com.electrosugar.foodworld.init;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.BlockItemBase;
import com.sun.javafx.geom.transform.BaseTransform;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInitNew {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, FoodWorld.MOD_ID);
//    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, FoodWorld.MOD_ID);
    //Example Item
    public static final RegistryObject<Item> DEF_ITEM = ITEMS.register("def_item", ()->new Item(new Item.Properties().group(FoodWorld.FW_FOOD)));
    //Example Block Item
    public static final RegistryObject<Item> POT_ITEM = ITEMS.register("pot",() -> new BlockItemBase(BlockInitNew.POT.get()));

//    public static final RegistryObject<Fluid> FLUID_SIMPLE = FLUIDS.register("cum", ()->new Fluid(new Fluid ))

}
