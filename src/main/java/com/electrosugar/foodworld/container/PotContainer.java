package com.electrosugar.foodworld.container;

import com.electrosugar.foodworld.container.abstractcontainer.AbstractPotContainer;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import com.electrosugar.foodworld.init.BlockInitNew;
import com.electrosugar.foodworld.init.ModContainerTypes;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;


public class PotContainer extends AbstractPotContainer {

//    public final PotTileEntity tileEntity;
//    private final IWorldPosCallable canInteractWithCallable;

    protected PotContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractBoilingRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn) {
        super(containerTypeIn, recipeTypeIn, id, playerInventoryIn);
    }

    protected PotContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractBoilingRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn, IInventory potInventoryIn, IIntArray potDataIn) {
        super(containerTypeIn, recipeTypeIn, id, playerInventoryIn, potInventoryIn, potDataIn);
    }


//    public PotContainer(final int windowId, final PlayerInventory playerInventory, final PotTileEntity tileEntity) {
//        super(ModContainerTypes.POT.get(),windowId);
//        this.tileEntity=tileEntity;
//        this.canInteractWithCallable=IWorldPosCallable.of(tileEntity.getWorld(),tileEntity.getPos());
//
//        //Main Inventory //this is where we map the tiles for the tile entity
//        int startX=8;
//        int startY=53;
//        //liquid fuel slot 0
//        this.addSlot(new Slot(tileEntity,0,startX,startY));
//        //crafting table like grid slots 1-9
//        startX= 44;
//        startY= 17;
//        int slotSizePlus2 = 18;
//        for(int row =0 ;row<3;++row){
//            for(int column=0;column<3;++column){
//                this.addSlot(new Slot(tileEntity,1+(row*3)+column,startX+(column*slotSizePlus2),startY+(row*slotSizePlus2)));
//            }
//        }
//        //Result slot 10
//        int lastX =138;
//        int lastY =35;
//        this.addSlot(new Slot(tileEntity,10,lastX,lastY));
//
//        //Main Player Inventory
//        int startPlayerInvY = 84;
//        int startPlayerInvX = 8;
//        for(int row =0 ;row<3;++row){
//            for(int column=0;column<9;++column){
//                this.addSlot(new Slot(playerInventory,9+(row*9)+column,startPlayerInvX+(column*slotSizePlus2),startPlayerInvY+(row*slotSizePlus2)));
//            }
//        }
//        //HotBar
//        int hotbarY= 142;
//        int hotbarX= 8;
//
//        for(int column=0;column<9;++column){
//            this.addSlot(new Slot(playerInventory,column,hotbarX+(column*slotSizePlus2),hotbarY));
//        }
//
//    }

    private static PotTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data){
        Objects.requireNonNull(playerInventory,"playerInventory cant be null");
        Objects.requireNonNull(data,"data cant be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if(tileAtPos instanceof PotTileEntity){
            return (PotTileEntity)tileAtPos;
        }
        throw new IllegalStateException("Tile Entity Pot from mod foodworld is not correct!"+tileAtPos);
    }

//    public PotContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data){
//        this(windowId,playerInventory,getTileEntity(playerInventory,data));
//    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
       return this.potInventory.isUsableByPlayer(playerIn);
//        return isWithinUsableDistance(canInteractWithCallable,playerIn, BlockInitNew.POT.get());
    }

//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
//        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//        if(slot!=null && slot.getHasStack()){
//            ItemStack itemStack1 = slot.getStack();
//            itemStack=itemStack1.copy();
//            if(index<11){
//                if(!this.mergeItemStack(itemStack1,11,this.inventorySlots.size(),true)){
//                    return ItemStack.EMPTY;
//                }
//            }else if(!this.mergeItemStack(itemStack1,0,11,false)){
//                return ItemStack.EMPTY;
//            }
//
//            if(itemStack1.isEmpty()){
//                slot.putStack(ItemStack.EMPTY);
//            }
//            else{
//                slot.onSlotChanged();
//            }
//        }
//        return itemStack;
//
//    }

}
