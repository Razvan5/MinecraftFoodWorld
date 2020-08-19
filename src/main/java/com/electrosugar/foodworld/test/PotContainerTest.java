package com.electrosugar.foodworld.test;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;

public class PotContainerTest extends AbstractPotContainerTest {

//    public PotContainerTest(int id, PlayerInventory playerInventoryIn) {
//        super(ModContainerTypesTest.POT_CONTAINER.get(), ModRecipeTypesTest.BOILING, id, playerInventoryIn);
//    }
//
//    public PotContainerTest(int id, PlayerInventory playerInventoryIn, IInventory potInventoryIn, IIntArray p_i50083_4_) {
//        super(ModContainerTypesTest.POT_CONTAINER.get(), ModRecipeTypesTest.BOILING, id, playerInventoryIn, potInventoryIn, p_i50083_4_);
//    }

    public PotContainerTest(int id, PlayerInventory playerInventoryIn, PacketBuffer packetBuffer) {
        super(ModContainerTypesTest.POT_CONTAINER.get(),ModRecipeTypesTest.BOILING,id,playerInventoryIn);
    }
}
