package com.electrosugar.foodworld.tileentity;

import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.datacontents.PotStateData;
import com.electrosugar.foodworld.datacontents.PotZoneContents;
import com.electrosugar.foodworld.events.StartupCommon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PotTileEntity extends TileEntity implements IFluidHandler, INamedContainerProvider, ITickableTileEntity {

    //Fluid Tank inside
    public static final int FLUID_TANK_NUMBER = 1; 
    public FluidTank fluidTank = new FluidTank(8000);//added
    public static final int FLUID_SLOTS_COUNT =1;
    public static final int INPUT_SLOTS_COUNT =9;
    public static final int OUTPUT_SLOTS_COUNT =1;
    public static final int TOTAL_SLOTS_COUNT =FLUID_SLOTS_COUNT+INPUT_SLOTS_COUNT+OUTPUT_SLOTS_COUNT;

    private PotZoneContents fluidZoneContents;
    private PotZoneContents inputZoneContents;
    private PotZoneContents outputZoneContents;

    private final PotStateData potStateData = new PotStateData();

    public PotTileEntity(){
        super(StartupCommon.potTileEntity);
        fluidZoneContents = PotZoneContents.createForTileEntity(FLUID_SLOTS_COUNT,
                this::canPlayerAccessInventory, this::markDirty);
        inputZoneContents = PotZoneContents.createForTileEntity(INPUT_SLOTS_COUNT,
                this::canPlayerAccessInventory, this::markDirty);
        outputZoneContents = PotZoneContents.createForTileEntity(OUTPUT_SLOTS_COUNT,
                this::canPlayerAccessInventory, this::markDirty);
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    public boolean canPlayerAccessInventory(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }
    
    // This method is called every tick to update the tile entity, i.e.
    // - see if the fuel has run out, and if so turn the furnace "off" and slowly uncook the current item (if any)
    // - see if the current smelting input item has finished smelting; if so, convert it to output
    // - burn fuel slots
    // It runs both on the server and the client but we only need to do updates on the server side.
    @Override
    public void tick() {
        if (world.isRemote) return; // do nothing on client.
        ItemStack currentlySmeltingItem = getCurrentlyBoilingInputItem();

        // if user has changed the input slots, reset the smelting time
        if (!ItemStack.areItemsEqual(currentlySmeltingItem, currentlySmeltingItemLastTick)) {  // == and != don't work!
            potStateData.heatingTimeElapsed = 0;
        }
        currentlySmeltingItemLastTick = currentlySmeltingItem.copy();

        if (!currentlySmeltingItem.isEmpty()) {
            int numberOfFuelBurning = burnFuel();

            // If fuel is available, keep cooking the item, otherwise start "uncooking" it at double speed
            if (numberOfFuelBurning > 0) {
                potStateData.heatingTimeElapsed += numberOfFuelBurning;
            }	else {
                potStateData.heatingTimeElapsed -= 2;
            }
            if (potStateData.heatingTimeElapsed < 0) potStateData.heatingTimeElapsed = 0;

            int cookTimeForCurrentItem = getCookTime(this.world, currentlySmeltingItem);
            potStateData.heatingTimeForCompletion = cookTimeForCurrentItem;
            // If cookTime has reached maxCookTime smelt the item and reset cookTime
            if (potStateData.heatingTimeElapsed >= cookTimeForCurrentItem) {
                boilFirstSuitableInputItem();
                potStateData.heatingTimeElapsed = 0;
            }
        }	else {
            potStateData.heatingTimeElapsed = 0;
        }
        

    }

    /**
     * 	for each fuel slot: decreases the burn time, checks if boilTimeRemainings = 0 and tries to consume a new piece of fuel if one is available
     * @return the number of fuel slots which are burning
     */
    private int burnFuel() {
        int burningCount = 0;
        boolean inventoryChanged = false;

        // i keep this in case i want multiple fluid inputs
        for (int fuelIndex = 0; fuelIndex < FLUID_SLOTS_COUNT; fuelIndex++) {
            if (potStateData.boilTimeRemainings > 0) {
                --potStateData.boilTimeRemainings;
                ++burningCount;
            }

            if (potStateData.boilTimeRemainings == 0) {
                ItemStack fuelItemStack = fluidZoneContents.getStackInSlot(fuelIndex);
                if (!fuelItemStack.isEmpty() && getItemBurnTime(this.world, fuelItemStack) > 0) {
                    // If the stack in this slot isn't empty and is fuel, set boilTimeRemainings & burnTimeInitialValues to the
                    // item's burn time and decrease the stack size
                    int burnTimeForItem = getItemBurnTime(this.world, fuelItemStack);
                    potStateData.boilTimeRemainings = burnTimeForItem;
                    potStateData.boilTimeInitialValues = burnTimeForItem;
                    fluidZoneContents.decrStackSize(fuelIndex, 1);
                    ++burningCount;
                    inventoryChanged = true;

                    // If the stack size now equals 0 set the slot contents to the item container item. This is for fuel
                    // item such as lava buckets so that the bucket is not consumed. If the item dose not have
                    // a container item, getContainerItem returns ItemStack.EMPTY which sets the slot contents to empty
                    if (fuelItemStack.isEmpty()) {
                        ItemStack containerItem = fuelItemStack.getContainerItem();
                        fluidZoneContents.setInventorySlotContents(fuelIndex, containerItem);
                    }
                }
            }
        }
        if (inventoryChanged) markDirty();
        return burningCount;
    }

    /**
     * Check if any of the input item are smeltable and there is sufficient space in the output slots
     * @return the ItemStack of the first input item that can be smelted; ItemStack.EMPTY if none
     */
    private ItemStack getCurrentlyBoilingInputItem() {return boilFirstSuitableInputItem(false);}

    /**
     * Smelt an input item into an output slot, if possible
     */
    private void boilFirstSuitableInputItem() {
        boilFirstSuitableInputItem(true);
    }

    /**
     * checks that there is an item to be smelted in one of the input slots and that there is room for the result in the output slots
     * If desired, performs the smelt
     * @param performBoil if true, perform the smelt.  if false, check whether smelting is possible, but don't change the inventory
     * @return a copy of the ItemStack of the input item smelted or to-be-smelted
     */
    private ItemStack boilFirstSuitableInputItem(boolean performBoil)
    {
        Integer firstSuitableInputSlot = null;
        Integer firstSuitableOutputSlot = null;
        ItemStack result = ItemStack.EMPTY;

        // finds the first input slot which is smeltable and whose result fits into an output slot (stacking if possible)
        // !SHOULD USE SOME SORT OF RECIPE MAKING
        for (int inputIndex = 0; inputIndex < INPUT_SLOTS_COUNT; inputIndex++)	{
            //obtain item stack from all the input index
            ItemStack itemStackToBoil = inputZoneContents.getStackInSlot(inputIndex);
            if (!itemStackToBoil.isEmpty()) {
                result = getBoilingResultForItem(this.world, itemStackToBoil);
                if (!result.isEmpty()) {
                    // find the first suitable output slot- either empty, or with identical item that has enough space
                        if (willItemStackFit(outputZoneContents, 10, result)) {
                            firstSuitableInputSlot = inputIndex;
                            firstSuitableOutputSlot = 10;
                        }
                    if (firstSuitableInputSlot != null) break;
                }
            }
        }

        if (firstSuitableInputSlot == null) return ItemStack.EMPTY;

        ItemStack returnvalue = inputZoneContents.getStackInSlot(firstSuitableInputSlot).copy();
        if (!performBoil) return returnvalue;

        // alter input and output
        inputZoneContents.decrStackSize(firstSuitableInputSlot, 1);
        // be carful, should be altered to allow more than one output per input
        outputZoneContents.increaseStackSize(firstSuitableOutputSlot, result);

        markDirty();
        return returnvalue;
    }

    /**
     * Will the given ItemStack fully fit into the target slot?
     * @param potZoneContents
     * @param slotIndex
     * @param itemStackOrigin
     * @return true if the given ItemStack will fit completely; false otherwise
     */
    public boolean willItemStackFit(PotZoneContents potZoneContents, int slotIndex, ItemStack itemStackOrigin) {
        ItemStack itemStackDestination = potZoneContents.getStackInSlot(slotIndex);

        if (itemStackDestination.isEmpty() || itemStackOrigin.isEmpty()) {
            return true;
        }

        if (!itemStackOrigin.isItemEqual(itemStackDestination)) {
            return false;
        }

        int sizeAfterMerge = itemStackDestination.getCount() + itemStackOrigin.getCount();
        if (sizeAfterMerge <= potZoneContents.getInventoryStackLimit() && sizeAfterMerge <= itemStackDestination.getMaxStackSize()) {
            return true;
        }
        return false;
    }

    // returns the smelting result for the given stack. Returns ItemStack.EMPTY if the given stack can not be smelted
    //!!!!!!!!!!!!!!IMPORTANT THIS USES THE RECIPE NOTATION CREATE 2 new classes for this
    public static ItemStack getBoilingResultForItem(World world, ItemStack itemStack) {
        Optional<FurnaceRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        if (!matchingRecipe.isPresent()){
            return ItemStack.EMPTY;
        }
        return matchingRecipe.get().getRecipeOutput().copy();  // beware! You must deep copy otherwise you will alter the recipe itself really? modding is soooooooo "fun"!
    }

    // returns the number of ticks the given item will burn. Returns 0 if the given item is not a valid fuel
    public static int getItemBurnTime(World world, ItemStack stack)
    {
        int burntime = net.minecraftforge.common.ForgeHooks.getBurnTime(stack);
        return burntime;
    }

    // gets the recipe which matches the given input, or Missing if none.
    public static Optional<FurnaceRecipe> getMatchingRecipeForInput(World world, ItemStack itemStack) {
        RecipeManager recipeManager = world.getRecipeManager();
        Inventory singleItemInventory = new Inventory(itemStack);
        Optional<FurnaceRecipe> matchingRecipe = recipeManager.getRecipe(IRecipeType.SMELTING, singleItemInventory, world);
        return matchingRecipe;
    }

    /**
     * Gets the cooking time for this recipe input
     * @param world
     * @param itemStack the input item to be smelted
     * @return cooking time (ticks) or 0 if no matching recipe
     */
    public static int getCookTime(World world, ItemStack itemStack) {
        Optional<FurnaceRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        if (!matchingRecipe.isPresent()) return 0;
        return matchingRecipe.get().getCookTime();
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    static public boolean isItemValidForFuelSlot(ItemStack itemStack)
    {
        return true;
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the input slots
    static public boolean isItemValidForInputSlot(ItemStack itemStack)
    {
        return true;
    }

    // Return true if the given stack is allowed to be inserted in the given slot
    static public boolean isItemValidForOutputSlot(ItemStack itemStack)
    {
        return false;
    }

    //------------------------------
    private final String FUEL_SLOTS_NBT = "fluidSlots";
    private final String INPUT_SLOTS_NBT = "inputSlots";
    private final String OUTPUT_SLOTS_NBT = "outputSlots";

    // This is where you save any data that you don't want to lose when the tile entity unloads
    // In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
    @Override
    public CompoundNBT write(CompoundNBT parentNBTTagCompound)
    {
        super.write(parentNBTTagCompound); // The super call is required to save and load the tile's location

        potStateData.putIntoNBT(parentNBTTagCompound);
        parentNBTTagCompound.put(FUEL_SLOTS_NBT, fluidZoneContents.serializeNBT());
        parentNBTTagCompound.put(INPUT_SLOTS_NBT, inputZoneContents.serializeNBT());
        parentNBTTagCompound.put(OUTPUT_SLOTS_NBT, outputZoneContents.serializeNBT());
        return parentNBTTagCompound;
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    public void read(CompoundNBT nbtTagCompound)
    {
        super.read(nbtTagCompound); // The super call is required to save and load the tile's location

        potStateData.readFromNBT(nbtTagCompound);

        CompoundNBT inventoryNBT = nbtTagCompound.getCompound(FUEL_SLOTS_NBT);
        fluidZoneContents.deserializeNBT(inventoryNBT);

        inventoryNBT = nbtTagCompound.getCompound(INPUT_SLOTS_NBT);
        inputZoneContents.deserializeNBT(inventoryNBT);

        inventoryNBT = nbtTagCompound.getCompound(OUTPUT_SLOTS_NBT);
        outputZoneContents.deserializeNBT(inventoryNBT);

        if (fluidZoneContents.getSizeInventory() != FLUID_SLOTS_COUNT
                || inputZoneContents.getSizeInventory() != INPUT_SLOTS_COUNT
                || outputZoneContents.getSizeInventory() != OUTPUT_SLOTS_COUNT
        )
            throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
    }

    //	// When the world loads from disk, the server needs to send the TileEntity information to the client
//	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this
    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT updateTagDescribingTileEntityState = getUpdateTag();
        final int METADATA = 42; // arbitrary.
        return new SUpdateTileEntityPacket(this.pos, METADATA, updateTagDescribingTileEntityState);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT updateTagDescribingTileEntityState = pkt.getNbtCompound();
        handleUpdateTag(updateTagDescribingTileEntityState);
    }

    /* Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
       Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
     */
    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
     *  The vanilla default is suitable for this example but I've included an explicit definition anyway.
     */
    @Override
    public void handleUpdateTag(CompoundNBT tag) { read(tag); }

    /**
     * When this tile entity is destroyed, drop all of its contents into the world
     * @param world
     * @param blockPos
     */
    public void dropAllContents(World world, BlockPos blockPos) {
        InventoryHelper.dropInventoryItems(world, blockPos, fluidZoneContents);
        InventoryHelper.dropInventoryItems(world, blockPos, inputZoneContents);
        InventoryHelper.dropInventoryItems(world, blockPos, outputZoneContents);
    }

    // -------------  The following two methods are used to make the TileEntity perform as a NamedContainerProvider, i.e.
    //  1) Provide a name used when displaying the container, and
    //  2) Creating an instance of container on the server, and linking it to the inventory items stored within the TileEntity

    /**
     *  standard code to look up what the human-readable name is.
     *  Can be useful when the tileentity has a customised name (eg "David's footlocker")
     */
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.foodworld.pot");
    }

    /**
     * The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only
     * @param windowID
     * @param playerInventory
     * @param playerEntity
     * @return
     */
    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return PotContainer.createContainerServerSide(windowID, playerInventory,
                inputZoneContents, outputZoneContents, fluidZoneContents, potStateData);
    }

    private ItemStack currentlySmeltingItemLastTick = ItemStack.EMPTY;
    
    
    // FLUID TANK NEEDED
    @Override
    public int getTanks() {
        return FLUID_TANK_NUMBER;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.fluidTank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.fluidTank.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
       return this.fluidTank.fill(resource,action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return this.fluidTank.drain(resource,action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return this.fluidTank.drain(maxDrain,action);
    }
    
}
