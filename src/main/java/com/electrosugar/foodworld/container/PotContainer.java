package com.electrosugar.foodworld.container;

import com.electrosugar.foodworld.customslots.PotFluidSlot;
import com.electrosugar.foodworld.customslots.PotResultSlot;
import com.electrosugar.foodworld.init.BlockInitNew;
import com.electrosugar.foodworld.init.ModContainerTypes;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.Objects;

public class PotContainer extends Container {
//    AbstractPotContainer

//    public PotContainer(int id, PlayerInventory playerInventoryIn) {
//        super(ContainerType.FURNACE, ModRecipeTypes.BOILING, id, playerInventoryIn);
//    }
//
//    public PotContainer(int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray p_i50083_4_) {
//        super(ContainerType.FURNACE, ModRecipeTypes.BOILING, id, playerInventoryIn, furnaceInventoryIn, p_i50083_4_);
//    }

    public final PotTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;
//    public final IInventory potInventory;
//    private final IIntArray potData;
//    protected final World world;
//    private final IRecipeType<? extends AbstractBoilingRecipe> recipeType;


    public PotContainer(final int windowId, final PlayerInventory playerInventoryIn, final PotTileEntity tileEntity) {
        super(ModContainerTypes.POT.get(),windowId);

        this.tileEntity=tileEntity;
        this.canInteractWithCallable=IWorldPosCallable.of(tileEntity.getWorld(),tileEntity.getPos());

//        assertInventorySize(potInventoryIn, 11);
//        assertIntArraySize(potDataIn, 12);
//        this.potInventory=potInventoryIn;
//        this.potData=potDataIn;
//        this.world=world;
//        this.recipeType=recipeType;

        //Main Inventory //this is where we map the tiles for the tile entity
        int startX=8;
        int startY=53;
        //liquid fuel slot 0
        //nu uita de reparat is fluid sa nu verifice daca e fuel
        this.addSlot(new PotFluidSlot(this,tileEntity,0,startX,startY));
        //crafting table like grid slots 1-9
        startX= 44;
        startY= 17;
        int slotSizePlus2 = 18;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<3;++column){
                this.addSlot(new Slot(tileEntity,1+(row*3)+column,startX+(column*slotSizePlus2),startY+(row*slotSizePlus2)));
            }
        }
        //Result slot 10
        int lastX =138;
        int lastY =35;
        this.addSlot(new PotResultSlot(playerInventoryIn.player,tileEntity,10,lastX,lastY));

        //Main Player Inventory
        int startPlayerInvY = 84;
        int startPlayerInvX = 8;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<9;++column){
                this.addSlot(new Slot(playerInventoryIn,9+(row*9)+column,startPlayerInvX+(column*slotSizePlus2),startPlayerInvY+(row*slotSizePlus2)));
            }
        }
        //HotBar
        int hotbarY= 142;
        int hotbarX= 8;

        for(int column=0;column<9;++column){
            this.addSlot(new Slot(playerInventoryIn,column,hotbarX+(column*slotSizePlus2),hotbarY));
        }

//        this.trackIntArray(potDataIn);

    }

    private static PotTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data){
        Objects.requireNonNull(playerInventory,"playerInventory cant be null");
        Objects.requireNonNull(data,"data cant be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if(tileAtPos instanceof PotTileEntity){
            return (PotTileEntity)tileAtPos;
        }
        throw new IllegalStateException("Tile Entity Pot from mod foodworld is not correct!"+tileAtPos);
    }

    //    public PotContainer(
    //    final int windowId,
    //    final PlayerInventory playerInventoryIn,
    //    final PotTileEntity tileEntity,
    //    IInventory potInventoryIn,
    //    IIntArray potDataIn,
    //    World world,
    //    IRecipeType<? extends AbstractBoilingRecipe> recipeType)
    public PotContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data){
        this(windowId,playerInventory,getTileEntity(playerInventory,data));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
//       return this.potInventory.isUsableByPlayer(playerIn);
        return isWithinUsableDistance(canInteractWithCallable,playerIn, BlockInitNew.POT.get());
    }

    public boolean isFluid(ItemStack stack) {
        return AbstractFurnaceTileEntity.isFuel(stack);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot!=null && slot.getHasStack()){
            ItemStack itemStack1 = slot.getStack();
            itemStack=itemStack1.copy();
            if(index<11){
                if(!this.mergeItemStack(itemStack1,11,this.inventorySlots.size(),true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.mergeItemStack(itemStack1,0,11,false)){
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
