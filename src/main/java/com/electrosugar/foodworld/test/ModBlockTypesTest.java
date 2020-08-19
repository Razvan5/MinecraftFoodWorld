package com.electrosugar.foodworld.test;

import com.electrosugar.foodworld.FoodWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockTypesTest {

    public static final DeferredRegister<Block> BLOCK_TYPES = new DeferredRegister<>(ForgeRegistries.BLOCKS, FoodWorld.MOD_ID);

    public static final RegistryObject<Block> POT_BLOCK_TEST = BLOCK_TYPES.register("pot_test",()-> new PotBlockTest(Block.Properties.create(Material.IRON).hardnessAndResistance(3.5F).lightValue(13)));

}
