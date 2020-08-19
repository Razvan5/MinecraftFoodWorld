package com.electrosugar.foodworld.test;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.container.ExampleChestContainer;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.init.BlockInitNew;
import com.electrosugar.foodworld.tileentity.ExampleChestTileEntity;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypesTest {



    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, FoodWorld.MOD_ID);


    public static final RegistryObject<TileEntityType<PotTileEntityTest>> POT_TE = TILE_ENTITY_TYPES
            .register("pot_test", ()->TileEntityType.Builder
                    .create(PotTileEntityTest::new,ModBlockTypesTest.POT_BLOCK_TEST.get()).build(null));

}
