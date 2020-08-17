package com.electrosugar.foodworld.container;

import com.electrosugar.foodworld.init.BlockInitNew;
import com.electrosugar.foodworld.init.ModContainerTypes;
import com.electrosugar.foodworld.tileentity.ExampleChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;


public class ExampleChestContainer extends Container {

    public final ExampleChestTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;

    public ExampleChestContainer(final int windowId, final PlayerInventory playerInventory, final ExampleChestTileEntity tileEntity) {
       super(ModContainerTypes.EXAMPLE_CHEST.get(),windowId);
       this.tileEntity=tileEntity;
       this.canInteractWithCallable=IWorldPosCallable.of(tileEntity.getWorld(),tileEntity.getPos());

       //Main Inventory //this is where we map the tiles
        int startX= 8;
        int startY= 18;
        int slotSizePlus2 = 18;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<9;++column){
                this.addSlot(new Slot(tileEntity,(row*9)+column,startX+(column*slotSizePlus2),startY+(row*slotSizePlus2)));
            }
        }
        //Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<9;++column){
                this.addSlot(new Slot(playerInventory,9+(row*9)+column,startX+(column*slotSizePlus2),startPlayerInvY+(row*slotSizePlus2)));
            }
        }

        //HotBar
        int hotbarY= 142;

        for(int column=0;column<9;++column){
                this.addSlot(new Slot(playerInventory,column,startX+(column*slotSizePlus2),hotbarY));
        }

    }

    private static ExampleChestTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data){
        Objects.requireNonNull(playerInventory,"playerInventory cant be null");
        Objects.requireNonNull(data,"data cant be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if(tileAtPos instanceof ExampleChestTileEntity){
            return (ExampleChestTileEntity)tileAtPos;
        }
        throw new IllegalStateException("Tile Entity from mod foodworld is not correct!"+tileAtPos);
    }

    public ExampleChestContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data){
        this(windowId,playerInventory,getTileEntity(playerInventory,data));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(canInteractWithCallable,playerIn, BlockInitNew.EXAMPLE_CHEST.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot!=null && slot.getHasStack()){
            ItemStack itemStack1 = slot.getStack();
            itemStack=itemStack1.copy();
            if(index<36){
                if(!this.mergeItemStack(itemStack1,36,this.inventorySlots.size(),true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.mergeItemStack(itemStack1,0,36,false)){
                return ItemStack.EMPTY;
            }

            if(itemStack1.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }
            else{
                slot.onSlotChanged();
            }
        }
        return itemStack;

    }

}
