package com.electrosugar.foodworld.container.abstractcontainer;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.containerrecipe.abstractrecipe.AbstractBoilingRecipe;
import com.electrosugar.foodworld.customslots.PotFluidSlot;
import com.electrosugar.foodworld.customslots.PotResultSlot;
import com.electrosugar.foodworld.recipeplacer.ServerRecipePlacerPot;
import com.electrosugar.foodworld.tileentity.abstracttileentity.AbstractPotTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractPotContainer extends RecipeBookContainer<IInventory> {
    
    public final IInventory potInventory;
    private final IIntArray potData;
    protected final World world;
    private final IRecipeType<? extends AbstractBoilingRecipe> recipeType;

    protected AbstractPotContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractBoilingRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn) {
        this(containerTypeIn, recipeTypeIn, id, playerInventoryIn, new Inventory(11), new IntArray(12));
    }

    protected AbstractPotContainer(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractBoilingRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn, IInventory potInventoryIn, IIntArray potDataIn) {
        super(containerTypeIn, id);
        this.recipeType = recipeTypeIn;
        assertInventorySize(potInventoryIn, 11);
        assertIntArraySize(potDataIn, 12);
        this.potInventory = potInventoryIn;
        this.potData = potDataIn;
        this.world = playerInventoryIn.player.world;

//        this.addSlot(new Slot(potInventoryIn, 0, 56, 17));//replaced with Pot 9 Crafting SLots
//
//        this.addSlot(new FurnaceFuelSlot(this, potInventoryIn, 1, 56, 53));//Pot Fluid Slot
//
//        this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, potInventoryIn, 2, 116, 35));//replaced with pot result

        //Main Inventory //this is where we map the tiles for the tile entity
        int startX=8;
        int startY=53;
        //liquid fuel slot 0
        //nu uita de reparat is fluid sa nu verifice daca e fuel
        this.addSlot(new PotFluidSlot(this,potInventoryIn,0,startX,startY));
        //crafting table like grid slots 1-9
        startX= 44;
        startY= 17;
        int slotSizePlus2 = 18;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<3;++column){
                this.addSlot(new Slot(potInventoryIn,1+(row*3)+column,startX+(column*slotSizePlus2),startY+(row*slotSizePlus2)));
            }
        }
        //Result slot 10
        int lastX =138;
        int lastY =35;
        this.addSlot(new PotResultSlot(playerInventoryIn.player,potInventoryIn,10,lastX,lastY));

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

        this.trackIntArray(potDataIn);
    }

    public void fillStackedContents(RecipeItemHelper itemHelperIn) {
        if (this.potInventory instanceof IRecipeHelperPopulator) {
            ((IRecipeHelperPopulator)this.potInventory).fillStackedContents(itemHelperIn);
        }

    }

    public void clear() {
        this.potInventory.clear();
    }

    public void func_217056_a(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
        (new ServerRecipePlacerPot<>(this)).place(p_217056_3_, (IRecipe<IInventory>)p_217056_2_, p_217056_1_);
    }

    public boolean matches(IRecipe<? super IInventory> recipeIn) {
        return recipeIn.matches(this.potInventory, this.world);
    }

    public int getOutputSlot() {
        //id of result slot
        return 10;
    }

    public int getWidth() {
        //what is this??
        return 1;
    }

    public int getHeight() {
        //what is this??
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 11;
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.potInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 10) {
                //cazul in care e result slotul
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index>9) {
                if (this.hasRecipe(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFluid(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    //recipe manager nonmodificat
    protected boolean hasRecipe(ItemStack stack) {
        return this.world.getRecipeManager().getRecipe((IRecipeType)this.recipeType, new Inventory(stack), this.world).isPresent();
    }
    //isFuel-> isFluid modificat
    public boolean isFluid(ItemStack stack) {
        return AbstractPotTileEntity.isFluid(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public int getBoilingProgressionScaled() {
        int i = this.potData.get(2);
        FoodWorld.LOGGER.info(this.potData.get(2));
        int j = this.potData.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFluidLeftScaled() {
        int i = this.potData.get(1);//i think that potData index is equal to the index+1 of slots
        if (i == 0) {
            i = 200;
        }

        return this.potData.get(0) * 13 / i;
    }

    //modified the names of all the functions with dist
    @OnlyIn(Dist.CLIENT)
    public boolean isBoiling() {
        FoodWorld.LOGGER.info(this.potData.get(0));
        return this.potData.get(0) > 0;
    }
}