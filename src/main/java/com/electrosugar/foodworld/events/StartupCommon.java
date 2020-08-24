package com.electrosugar.foodworld.events;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.PotBlock;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StartupCommon
{

    public static Block blockPot;  // this holds the unique instance of your block
    public static BlockItem itemBlockFurnace; // and the corresponding item form that block

    public static TileEntityType<PotTileEntity> potTileEntity;  // Holds the type of our tile entity; needed for the TileEntityData constructor
    public static ContainerType<PotContainer> potContainerContainerType;

    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        blockPot = new PotBlock().setRegistryName("pot");
        blockRegisterEvent.getRegistry().register(blockPot);
    }


    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        // We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
        final int MAXIMUM_STACK_SIZE = 1;  // player can only hold 1 of this block in their hand at once

        Item.Properties itemSimpleProperties = new Item.Properties()
                .maxStackSize(MAXIMUM_STACK_SIZE)
                .group(ItemGroup.BUILDING_BLOCKS);  // which inventory tab?
        itemBlockFurnace = new BlockItem(blockPot, itemSimpleProperties);

        itemBlockFurnace.setRegistryName(blockPot.getRegistryName());
        itemRegisterEvent.getRegistry().register(itemBlockFurnace);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
        potTileEntity = TileEntityType.Builder.create(PotTileEntity::new, blockPot)
                .build(null);
        // you probably don't need a datafixer --> null should be fine
        potTileEntity.setRegistryName("pot");
        event.getRegistry().register(potTileEntity);
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        potContainerContainerType = IForgeContainerType.create(PotContainer::createContainerClientSide);
        potContainerContainerType.setRegistryName("pot_container");
        event.getRegistry().register(potContainerContainerType);
    }

}

