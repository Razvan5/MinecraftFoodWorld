package com.electrosugar.foodworld.customslots;

import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.container.abstractcontainer.AbstractPotContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PotFluidSlot extends Slot {
    private final PotContainer field_216939_a;

    public PotFluidSlot(PotContainer p_i50084_1_, IInventory p_i50084_2_, int p_i50084_3_, int p_i50084_4_, int p_i50084_5_) {
        super(p_i50084_2_, p_i50084_3_, p_i50084_4_, p_i50084_5_);
        this.field_216939_a = p_i50084_1_;
    }

    /**
     * Check if the stack is allowed to be placed in this slot
     */
    public boolean isItemValid(ItemStack stack) {
        return this.field_216939_a.isFluid(stack) || isBucket(stack);
    }

    public int getItemStackLimit(ItemStack stack) {
        return isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.getItem() == Items.BUCKET;
    }
}
