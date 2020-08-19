package com.electrosugar.foodworld.init;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.ExampleChestBlock;
import com.electrosugar.foodworld.blocks.Pot;
import com.electrosugar.foodworld.blocks.PotBlock;
import com.electrosugar.foodworld.test.PotBlockTest;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInitNew {

    public static final DeferredRegister<Block> BLOCKS =new DeferredRegister<>(ForgeRegistries.BLOCKS, FoodWorld.MOD_ID);

    public static final RegistryObject<Block> EXAMPLE_CHEST = BLOCKS.register("example_chest", ()-> new ExampleChestBlock(Block.Properties.create(Material.IRON)));

    public static final RegistryObject<Block> POT = BLOCKS.register("pot", ()-> new PotBlock(Block.Properties.create(Material.IRON)));

    public static final RegistryObject<Block> POT_BLOCK_TEST = BLOCKS.register("pot_test",()-> new PotBlockTest(Block.Properties.create(Material.IRON).hardnessAndResistance(3.5F).lightValue(13)));


}
