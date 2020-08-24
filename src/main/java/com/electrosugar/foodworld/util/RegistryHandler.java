package com.electrosugar.foodworld.util;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.BlockItemBase;
import com.electrosugar.foodworld.items.ChilliPepper;
import com.electrosugar.foodworld.items.ItemBase;
import com.electrosugar.foodworld.items.WaterCup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS =new  DeferredRegister<>(ForgeRegistries.ITEMS, FoodWorld.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =new  DeferredRegister<>(ForgeRegistries.BLOCKS,FoodWorld.MOD_ID);

    public static void init(){

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Items only
    public static final RegistryObject<Item> PARSNIP = ITEMS.register("parsnip", ItemBase::new);
    public static final RegistryObject<ChilliPepper> CHILLI_PEPPER = ITEMS.register("chilli_pepper", ChilliPepper::new);
    public static final RegistryObject<WaterCup> WATER_CUP = ITEMS.register("water_cup", WaterCup::new);

    //Blocks only

    //Block items only

}
