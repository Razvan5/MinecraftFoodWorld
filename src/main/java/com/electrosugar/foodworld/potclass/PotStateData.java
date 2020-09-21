package com.electrosugar.foodworld.potclass;

import com.electrosugar.foodworld.util.IStringArray;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Created by TGG on 4/04/2020.
 * This class is used to store some state data for the furnace (eg burn time, smelting time, etc)
 * 1) The Server TileEntity uses it to store the data permanently, including NBT serialisation and deserialisation
 * 2) The server container uses it to
 *    a) read/write permanent data back into the TileEntity
 *    b) synchronise the server container data to the client container using the IIntArray interface (via Container::trackIntArray)
 * 3) The client container uses it to store a temporary copy of the data, for rendering / GUI purposes
 * The TileEntity and the client container both use it by poking directly into its member variables.  That's not good
 *   practice but it's easier to understand than the vanilla method which uses an anonymous class/lambda functions
 *
 *  The IIntArray interface collates all the separate member variables into a single array for the purposes of transmitting
 *     from server to client (handled by Vanilla)
 */
public class PotStateData implements IIntArray {

  private static final Logger LOGGER = LogManager.getLogger();

  public static final int FUEL_SLOTS_COUNT = PotTileEntity.FUEL_SLOTS_COUNT;

  /**The number of ticks that the current item has been cooking*/
  public int cookTimeElapsed;
  // The number of ticks required to cook the current item (i.e complete when cookTimeElapsed == cookTimeForCompletion
  public int cookTimeForCompletion;

  /** The initial fuel value of the currently burning fuel in each slot (in ticks of burn duration) */
  public int burnTimeInitialValues;
  /** The number of burn ticks remaining on the current piece of fuel in each slot */
  public int burnTimeRemainings;

  public String fluidRegistryName = "empty";

  // --------- read/write to NBT for permanent storage (on disk, or packet transmission) - used by the TileEntity only

  public void putIntoNBT(CompoundNBT nbtTagCompound) {
    nbtTagCompound.putInt("CookTimeElapsed", cookTimeElapsed);
    nbtTagCompound.putInt("CookTimeForCompletion", cookTimeElapsed);
    nbtTagCompound.putInt("burnTimeRemainings", burnTimeRemainings);
    nbtTagCompound.putInt("burnTimeInitial", burnTimeInitialValues);
    nbtTagCompound.putString("fluidRegistryName", fluidRegistryName);

  }

  public void readFromNBT(CompoundNBT nbtTagCompound) {
      // Trim the arrays (or pad with 0) to make sure they have the correct number of elements
    cookTimeElapsed = nbtTagCompound.getInt("CookTimeElapsed");
    cookTimeForCompletion = nbtTagCompound.getInt("CookTimeForCompletion");
    burnTimeRemainings = nbtTagCompound.getInt("burnTimeRemainings");
    burnTimeInitialValues = nbtTagCompound.getInt("burnTimeInitialValues");
    fluidRegistryName = nbtTagCompound.getString("fluidRegistryName");
//    LOGGER.info("PotState:-"+nbtTagCompound.getString("fluidRegistryName"));

  }

  // -------- used by vanilla, not intended for mod code
//  * The ints are mapped (internally) as:
//  * 0 = cookTimeElapsed
//  * 1 = cookTimeForCompletion
//  * 2 .. FUEL_SLOTS_COUNT+1 = burnTimeInitialValues[]
//  * FUEL_SLOTS_COUNT + 2 .. 2*FUEL_SLOTS_COUNT +1 = burnTimeRemainings[]
//  *

  private final int COOKTIME_INDEX = 0;
  private final int COOKTIME_FOR_COMPLETION_INDEX = 1;
  private final int BURNTIME_INITIAL_VALUE_INDEX = 2;
  private final int BURNTIME_REMAINING_INDEX = BURNTIME_INITIAL_VALUE_INDEX + FUEL_SLOTS_COUNT;
  private final int FLUID_NAME_INDEX = BURNTIME_REMAINING_INDEX + 1 ;
  private final int END_OF_DATA_INDEX_PLUS_ONE = BURNTIME_REMAINING_INDEX + FUEL_SLOTS_COUNT;

  @Override
  public int get(int index) {
    validateIndex(index);
    LOGGER.info(fluidRegistryName);

    if (index == COOKTIME_INDEX) {
      return cookTimeElapsed;
    } else if (index == COOKTIME_FOR_COMPLETION_INDEX) {
      return cookTimeForCompletion;
    } else if (index >= BURNTIME_INITIAL_VALUE_INDEX && index < BURNTIME_REMAINING_INDEX) {
      return burnTimeInitialValues;
    } else {
      return burnTimeRemainings;
    }
//    else{
//      return int(fluidRegistryName);
//    }
  }

  @Override
  public void set(int index, int value) {
    validateIndex(index);
    if (index == COOKTIME_INDEX) {
      cookTimeElapsed = value;
    } else if (index == COOKTIME_FOR_COMPLETION_INDEX) {
      cookTimeForCompletion = value;
    } else if (index >= BURNTIME_INITIAL_VALUE_INDEX && index < BURNTIME_REMAINING_INDEX) {
      burnTimeInitialValues = value;
    } else if (index >= BURNTIME_REMAINING_INDEX && index<FLUID_NAME_INDEX){
      burnTimeRemainings = value;
    }
    else{
      fluidRegistryName = String.valueOf(value);
    }
  }

  @Override
  public int size() {
    return END_OF_DATA_INDEX_PLUS_ONE;
  }

  private void validateIndex(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException("Index out of bounds:"+index);
    }
  }
}
