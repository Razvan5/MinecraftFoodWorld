package com.electrosugar.foodworld.container;

import com.electrosugar.foodworld.datacontents.PotStateData;
import com.electrosugar.foodworld.datacontents.PotZoneContents;
import com.electrosugar.foodworld.events.StartupCommon;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotContainer extends Container {

    public static PotContainer createContainerServerSide(int windowID, PlayerInventory playerInventory,
                                                             PotZoneContents inputZoneContents,
                                                             PotZoneContents outputZoneContents,
                                                             PotZoneContents fluidZoneContents,
                                                             PotStateData potStateData) {
        return new PotContainer(windowID, playerInventory,
                inputZoneContents, outputZoneContents, fluidZoneContents, potStateData);
    }

    public static PotContainer createContainerClientSide(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        //  don't need extraData for this example; if you want you can use it to provide extra information from the server, that you can use
        //  when creating the client container
        //  eg String detailedDescription = extraData.readString(128);
        PotZoneContents inputZoneContents = PotZoneContents.createForClientSideContainer(INPUT_SLOTS_COUNT);
        PotZoneContents outputZoneContents = PotZoneContents.createForClientSideContainer(OUTPUT_SLOTS_COUNT);
        PotZoneContents fuelZoneContents = PotZoneContents.createForClientSideContainer(FLUID_SLOTS_COUNT);
        PotStateData furnaceStateData = new PotStateData();

        // on the client side there is no parent TileEntity to communicate with, so we:
        // 1) use dummy inventories and furnace state data (tracked ints)
        // 2) use "do nothing" lambda functions for canPlayerAccessInventory and markDirty
        return new PotContainer(windowID, playerInventory,
                inputZoneContents, outputZoneContents, fuelZoneContents, furnaceStateData);
    }

    // must assign a slot index to each of the slots used by the GUI.
    // For this container, we can see the furnace fuel, input, and output slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container using addSlotToContainer(), it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 39 = fuel slots (furnaceStateData 0 - 3)
    //  40 - 44 = input slots (furnaceStateData 4 - 8)
    //  45 - 49 = output slots (furnaceStateData 9 - 13)

    //9 slots
    private static final int HOTBAR_SLOT_COUNT = 9;
    //3 slots
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    //9 slots
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    //27 slots
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    //36 slots total
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    //1 slot
    public static final int FLUID_SLOTS_COUNT = PotTileEntity.FLUID_SLOTS_COUNT;
    //9 slots
    public static final int INPUT_SLOTS_COUNT = PotTileEntity.INPUT_SLOTS_COUNT;
    //1 slot
    public static final int OUTPUT_SLOTS_COUNT = PotTileEntity.OUTPUT_SLOTS_COUNT;
    //11 slots total
    public static final int POT_SLOTS_COUNT = FLUID_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    // slot index is the unique index for all slots in this container i.e. 0 - 35 for invPlayer then 36 - 49 for furnaceContents
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX;
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;

    //36
    private static final int FIRST_FLUID_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    //37,38,39,40,41,42,43,44,45,46
    private static final int FIRST_INPUT_SLOT_INDEX = FIRST_FLUID_SLOT_INDEX + FLUID_SLOTS_COUNT;
    //47
    private static final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    // slot number is the slot number within each component;
    // i.e. invPlayer slots 0 - 35 (hotbar 0 - 8 then main inventory 9 to 35)
    // and furnace: inputZone slots 0 - 4, outputZone slots 0 - 4, fuelZone 0 - 3
    // actual slots : fluid =+0, input zone+(1..9), outputZone=+10

    public PotContainer(int windowID, PlayerInventory invPlayer,
                            PotZoneContents inputZoneContents,
                            PotZoneContents outputZoneContents,
                            PotZoneContents fluidZoneContents,
                            PotStateData furnaceStateData) {
        
        super(StartupCommon.potContainerContainerType, windowID);
        
        if (StartupCommon.potContainerContainerType == null)
            throw new IllegalStateException("Must initialise potContainerContainerType before constructing a PotContainer!");
        this.inputZoneContents = inputZoneContents;
        this.outputZoneContents = outputZoneContents;
        this.fluidZoneContents = fluidZoneContents;
        this.furnaceStateData = furnaceStateData;
        this.world = invPlayer.player.world;

        trackIntArray(furnaceStateData);    // tell vanilla to keep the furnaceStateData synchronised between client and server Containers

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 125;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlot(new Slot(invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 84;
        // Add the rest of the players inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(invPlayer, slotNumber,  xpos, ypos));
            }
        }
        
        int startX=8;
        int startY=53;
        //liquid fuel slot 0
        //nu uita de reparat is fluid sa nu verifice daca e fuel
        addSlot(new SlotFuel(fluidZoneContents, 0, startX, startY));
        
        //crafting table like grid slots 1-9
        startX= 44;
        startY= 17;
        int slotSizePlus2 = 18;
        for(int row =0 ;row<3;++row){
            for(int column=0;column<3;++column){
                addSlot(new SlotSmeltableInput(inputZoneContents, 1+(row*3)+column,startX+(column*slotSizePlus2),startY+(row*slotSizePlus2) ));
            }
        }
        //Result slot 10
        int lastX =138;
        int lastY =35;
        addSlot(new SlotOutput(outputZoneContents, 10, lastX, lastY));
        
    }

    // Checks each tick to make sure the player is still able to access the inventory and if not closes the gui
    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return fluidZoneContents.isUsableByPlayer(player) && inputZoneContents.isUsableByPlayer(player)
                && outputZoneContents.isUsableByPlayer(player);
    }

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory - either input or fuel as appropriate for the item you clicked)
    // At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot.
    // returns ItemStack.EMPTY if the source slot is empty, or if none of the source slot item could be moved.
    //   otherwise, returns a copy of the source stack
    //  Code copied & refactored from vanilla furnace AbstractFurnaceContainer
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int sourceSlotIndex)
    {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
        ItemStack sourceItemStack = sourceSlot.getStack();
        ItemStack sourceStackBeforeMerge = sourceItemStack.copy();
        boolean successfulTransfer = false;

        SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

        switch (sourceZone) {
            case OUTPUT_ZONE: // taking out of the output zone - try the hotbar first, then main inventory.  fill from the end.
                successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, true);
                if (!successfulTransfer) {
                    successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, true);
                }
                if (successfulTransfer) {  // removing from output means we have just crafted an item -> need to inform
                    sourceSlot.onSlotChange(sourceItemStack, sourceStackBeforeMerge);
                }
                break;

            case INPUT_ZONE:
            case FUEL_ZONE: // taking out of input zone or fuel zone - try player main inv first, then hotbar.  fill from the start
                successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, false);
                if (!successfulTransfer) {
                    successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, false);
                }
                break;

            case PLAYER_HOTBAR:
            case PLAYER_MAIN_INVENTORY: // taking out of inventory - find the appropriate furnace zone
                if (!PotTileEntity.getBoilingResultForItem(world, sourceItemStack).isEmpty()) { // smeltable -> add to input
                    successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceItemStack, false);
                }
                if (!successfulTransfer && PotTileEntity.getItemBurnTime(world, sourceItemStack) > 0) { //burnable -> add to fuel from the bottom slot first
                    successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceItemStack, true);
                }
                if (!successfulTransfer) {  // didn't fit into furnace; try player main inventory or hotbar
                    if (sourceZone == SlotZone.PLAYER_HOTBAR) { // main inventory
                        successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, false);
                    } else {
                        successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, false);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
        }
        if (!successfulTransfer) return ItemStack.EMPTY;

        // If source stack is empty (the entire stack was moved) set slot contents to empty
        if (sourceItemStack.isEmpty()) {
            sourceSlot.putStack(ItemStack.EMPTY);
        } else {
            sourceSlot.onSlotChanged();
        }

        // if source stack is still the same as before the merge, the transfer failed somehow?  not expected.
        if (sourceItemStack.getCount() == sourceStackBeforeMerge.getCount()) {
            return ItemStack.EMPTY;
        }
        sourceSlot.onTake(player, sourceItemStack);
        return sourceStackBeforeMerge;
    }

    /**
     * Try to merge from the given source ItemStack into the given SlotZone.
     * @param destinationZone the zone to merge into
     * @param sourceItemStack the itemstack to merge from
     * @param fillFromEnd if true: try to merge from the end of the zone instead of from the start
     * @return true if a successful transfer occurred
     */
    private boolean mergeInto(SlotZone destinationZone, ItemStack sourceItemStack, boolean fillFromEnd) {
        return mergeItemStack(sourceItemStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
    }

    // -------- methods used by the ContainerScreen to render parts of the display

    /**
     * Returns the amount of fuel remaining on the currently burning item in the given fuel slot.
     * @fuelSlot the number of the fuel slot (0..3)
     * @return fraction remaining, between 0.0 - 1.0
     */
    public double fractionOfFuelRemaining(int fuelSlot) {
        if (furnaceStateData.boilTimeInitialValues <= 0 ) return 0;
        double fraction = furnaceStateData.boilTimeRemainings / (double)furnaceStateData.boilTimeInitialValues;
        return MathHelper.clamp(fraction, 0.0, 1.0);
    }

    /**
     * return the remaining burn time of the fuel in the given slot
     * @param fuelSlot the number of the fuel slot (0..3)
     * @return seconds remaining
     */
    public int secondsOfFuelRemaining(int fuelSlot)	{
        if (furnaceStateData.boilTimeRemainings <= 0 ) return 0;
        return furnaceStateData.boilTimeRemainings / 20; // 20 ticks per second
    }

    /**
     * Returns the amount of cook time completed on the currently cooking item.
     * @return fraction remaining, between 0 - 1
     */
    public double fractionOfCookTimeComplete() {
        if (furnaceStateData.heatingTimeForCompletion == 0) return 0;
        double fraction = furnaceStateData.heatingTimeElapsed / (double)furnaceStateData.heatingTimeForCompletion;
        return MathHelper.clamp(fraction, 0.0, 1.0);
    }

    // --------- Customise the different slots (in particular - what items they will accept)


    // SlotFuel is a slot for fuel items
    public class SlotFuel extends Slot {
        public SlotFuel(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return PotTileEntity.isItemValidForFuelSlot(stack);
        }
    }

    // SlotSmeltableInput is a slot for input item
    public class SlotSmeltableInput extends Slot {
        public SlotSmeltableInput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return PotTileEntity.isItemValidForInputSlot(stack);
        }
    }

    // SlotOutput is a slot that will not accept any item
    public class SlotOutput extends Slot {
        public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return PotTileEntity.isItemValidForOutputSlot(stack);
        }
    }

    private PotZoneContents inputZoneContents;
    private PotZoneContents outputZoneContents;
    private PotZoneContents fluidZoneContents;
    private PotStateData furnaceStateData;

    private World world; //needed for some helper methods
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Helper enum to make the code more readable
     */
    private enum SlotZone {
        FUEL_ZONE(FIRST_FLUID_SLOT_INDEX, FLUID_SLOTS_COUNT),
        INPUT_ZONE(FIRST_INPUT_SLOT_INDEX, INPUT_SLOTS_COUNT),
        OUTPUT_ZONE(FIRST_OUTPUT_SLOT_INDEX, OUTPUT_SLOTS_COUNT),
        PLAYER_MAIN_INVENTORY(PLAYER_INVENTORY_FIRST_SLOT_INDEX, PLAYER_INVENTORY_SLOT_COUNT),
        PLAYER_HOTBAR(HOTBAR_FIRST_SLOT_INDEX, HOTBAR_SLOT_COUNT);

        SlotZone(int firstIndex, int numberOfSlots) {
            this.firstIndex = firstIndex;
            this.slotCount = numberOfSlots;
            this.lastIndexPlus1 = firstIndex + numberOfSlots;
        }

        public final int firstIndex;
        public final int slotCount;
        public final int lastIndexPlus1;

        public static SlotZone getZoneFromIndex(int slotIndex) {
            for (SlotZone slotZone : SlotZone.values()) {
                if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
            }
            throw new IndexOutOfBoundsException("Unexpected slotIndex");
        }
    }

   
}
