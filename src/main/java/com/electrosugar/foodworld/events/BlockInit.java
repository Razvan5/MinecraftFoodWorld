package com.electrosugar.foodworld.events;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.BlockItemBase;
import com.electrosugar.foodworld.blocks.PotBlock;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {

//    public static final DeferredRegister<Block> BLOCKS =new DeferredRegister<>(ForgeRegistries.BLOCKS, FoodWorld.MOD_ID);
//
//
//    public static final RegistryObject<Block> POT = BLOCKS.register("pot", PotBlock::new);
//
//    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, FoodWorld.MOD_ID);
//    //    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, FoodWorld.MOD_ID);
//    //Example Item
//    public static final RegistryObject<Item> DEF_ITEM = ITEMS.register("def_item", ()->new Item(new Item.Properties().group(FoodWorld.FW_FOOD)));
//
//    public static final RegistryObject<Item> POT_ITEM = ITEMS.register("pot",() -> new BlockItemBase(POT.get()));
//
//    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, FoodWorld.MOD_ID);
//
//    public static final RegistryObject<TileEntityType<PotTileEntity>> POT_TILE_ENTITY = TILE_ENTITY_TYPES
//            .register("pot", ()->TileEntityType.Builder
//                    .create(PotTileEntity::new,POT.get()).build(null));
//
//    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, FoodWorld.MOD_ID);
//    public static RegistryObject<ContainerType<PotContainer>> POT_CONTAINER = CONTAINER_TYPES
//            .register("pot",() -> IForgeContainerType.create(PotContainer::createContainerClientSide));

}
