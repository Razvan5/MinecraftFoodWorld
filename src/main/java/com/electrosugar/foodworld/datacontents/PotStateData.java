package com.electrosugar.foodworld.datacontents;

import com.electrosugar.foodworld.tileentity.PotTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraftforge.fluids.FluidStack;

public class PotStateData implements IIntArray {

    public static final int FLUID_SLOTS_COUNT = PotTileEntity.FLUID_SLOTS_COUNT;

    /**The number of ticks that the current item has been cooking*/
    public int heatingTimeElapsed;
    // The number of ticks required to cook the current item (i.e complete when cookTimeElapsed == cookTimeForCompletion
    public int heatingTimeForCompletion;

    /** The initial fuel value of the currently burning fuel in each slot (in ticks of burn duration) */
    public int boilTimeInitialValues;
    /** The number of burn ticks remaining on the current piece of fluid in each slot */
    public int boilTimeRemainings;

//    public FluidStack fluidStack;

    // --------- read/write to NBT for permanent storage (on disk, or packet transmission) - used by the TileEntity only

    public void putIntoNBT(CompoundNBT nbtTagCompound) {
        nbtTagCompound.putInt("heatTimeElapsed", heatingTimeElapsed);
        nbtTagCompound.putInt("heatTimeForCompletion", heatingTimeForCompletion);
        nbtTagCompound.putInt("boilTimeRemainings", boilTimeRemainings);
        nbtTagCompound.putInt("boilTimeInitial", boilTimeInitialValues);
//        nbtTagCompound.put("fluidStack",fluidStack.getTag());
    }

    public void readFromNBT(CompoundNBT nbtTagCompound) {
        // Trim the arrays (or pad with 0) to make sure they have the correct number of elements
        heatingTimeElapsed = nbtTagCompound.getInt("heatTimeElapsed");
        heatingTimeForCompletion = nbtTagCompound.getInt("heatTimeForCompletion");
        boilTimeRemainings = nbtTagCompound.getInt("boilTimeRemainings");
        boilTimeInitialValues = nbtTagCompound.getInt("boilTimeInitial");
//        fluidStack.setTag((CompoundNBT) nbtTagCompound.get("fluidStack"));
    }

    // -------- used by vanilla, not intended for mod code
//  * The ints are mapped (internally) as:
//  * 0 = heatTimeElapsed
//  * 1 = heatTimeForCompletion
//  * 2 = boilTimeInitialValue
//  * 3 = boilTimeRemaining
//  * 4 = fluidStack size?
//  (before:2 .. FLUID_SLOTS_COUNT+1 = boilTimeInitialValues)
//  * FLUID_SLOTS_COUNT + 2 .. 2*FLUID_SLOTS_COUNT +1 = burnTimeRemainings
//  *

    private final int HEATTIME_INDEX = 0;
    private final int HEATTIME_FOR_COMPLETION_INDEX = 1;
    private final int BOILTIME_INITIAL_VALUE_INDEX = 2;
    private final int BOILTIME_REMAINING_INDEX = 3;

    @Override
    public int get(int index) {
        validateIndex(index);
        if (index == HEATTIME_INDEX) {
            return heatingTimeElapsed;
        } else if (index == HEATTIME_FOR_COMPLETION_INDEX) {
            return heatingTimeForCompletion;
        } else if (index == BOILTIME_INITIAL_VALUE_INDEX) {
            return boilTimeInitialValues;
        } else
//            if (index == BOILTIME_REMAINING_INDEX)
        {
            return boilTimeRemainings;
        }
//        else{
//            return fluidStack.getAmount();
//        }
    }

    @Override
    public void set(int index, int value) {
        validateIndex(index);
        if (index == HEATTIME_INDEX) {
            heatingTimeElapsed = value;
        } else if (index == HEATTIME_FOR_COMPLETION_INDEX) {
            heatingTimeForCompletion = value;
        } else if (index == BOILTIME_INITIAL_VALUE_INDEX) {
            boilTimeInitialValues = value;
        } else{
//        if (index == BOILTIME_REMAINING_INDEX)
            boilTimeRemainings = value;
        }
//        else {
//            fluidStack.setAmount(value);
//        }
    }

    @Override
    public int size() {
        //    max=4(fluidstack size)
        int END_OF_DATA_INDEX_PLUS_ONE = BOILTIME_REMAINING_INDEX + FLUID_SLOTS_COUNT + 1;
        return END_OF_DATA_INDEX_PLUS_ONE;
    }

    private void validateIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index out of bounds:"+index);
        }
    }

}
