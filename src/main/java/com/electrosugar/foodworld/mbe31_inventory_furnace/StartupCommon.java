package com.electrosugar.foodworld.mbe31_inventory_furnace;


import com.electrosugar.foodworld.FoodWorld;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: brandon3055
 * Date: 06/01/2015
 *
 * The Startup class for this example is called during startup
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
  public static Block POT_BLOCK;  // this holds the unique instance of your block
  public static BlockItem POT_BLOCK_ITEM; // and the corresponding item form that block

  public static TileEntityType<PotTileEntity> POT_TILE_ENTITY;  // Holds the type of our tile entity; needed for the TileEntityData constructor
  public static ContainerType<PotContainer> POT_CONTAINER;

  public static IRecipeType<PotRecipe> POT_RECIPE_TYPE = new PotRecipeType();
  public static BoilingRecipeSerializer<PotRecipe> BOILING_RECIPE_SERIALIZER;

  @SubscribeEvent
  public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
    POT_BLOCK = new PotInventoryBlock().setRegistryName("pot");
    blockRegisterEvent.getRegistry().register(POT_BLOCK);
  }

  @SubscribeEvent
  public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
    // We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
    final int MAXIMUM_STACK_SIZE = 1;  // player can only hold 1 of this block in their hand at once

    Item.Properties itemSimpleProperties = new Item.Properties()
            .maxStackSize(MAXIMUM_STACK_SIZE)
            .group(FoodWorld.FW_FOOD);  // which inventory tab?
    POT_BLOCK_ITEM = new BlockItem(POT_BLOCK, itemSimpleProperties);
    POT_BLOCK_ITEM.setRegistryName(POT_BLOCK.getRegistryName().toString());
    itemRegisterEvent.getRegistry().register(POT_BLOCK_ITEM);
  }

  @SubscribeEvent
  public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
    POT_TILE_ENTITY = TileEntityType.Builder.create(PotTileEntity::new, POT_BLOCK)
            .build(null);
    // you probably don't need a datafixer --> null should be fine
    POT_TILE_ENTITY.setRegistryName("pot_tile_entity");
    event.getRegistry().register(POT_TILE_ENTITY);
  }

  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
  {
    POT_CONTAINER = IForgeContainerType.create(PotContainer::createContainerClientSide);
    POT_CONTAINER.setRegistryName("pot_container");
    event.getRegistry().register(POT_CONTAINER);
  }

  @SubscribeEvent
  public static void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event){

    BOILING_RECIPE_SERIALIZER = IRecipeSerializer.register("foodworld:boiling",new BoilingRecipeSerializer<PotRecipe>(PotRecipe::new,100));
//    boilingRecipeSerializer.setRegistryName("boiling");
    Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(POT_RECIPE_TYPE.toString()), POT_RECIPE_TYPE);

    event.getRegistry().register(BOILING_RECIPE_SERIALIZER);
  }


}
