package com.electrosugar.foodworld.init;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import com.electrosugar.foodworld.test.ModBlockTypesTest;
import com.electrosugar.foodworld.test.PotTileEntityTest;
import com.electrosugar.foodworld.tileentity.ExampleChestTileEntity;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, FoodWorld.MOD_ID);

    public static final RegistryObject<TileEntityType<ExampleChestTileEntity>> EXAMPLE_CHEST = TILE_ENTITY_TYPES
            .register("example_chest", ()->TileEntityType.Builder
                    .create(ExampleChestTileEntity::new,BlockInitNew.EXAMPLE_CHEST.get()).build(null));

    public static final RegistryObject<TileEntityType<PotTileEntity>> POT = TILE_ENTITY_TYPES
            .register("pot", ()->TileEntityType.Builder
                    .create(PotTileEntity::new,BlockInitNew.POT.get()).build(null));

    public static final RegistryObject<TileEntityType<PotTileEntityTest>> POT_TE = TILE_ENTITY_TYPES
            .register("pot_test", ()->TileEntityType.Builder
                    .create(PotTileEntityTest::new, BlockInitNew.POT_BLOCK_TEST.get()).build(null));
}
