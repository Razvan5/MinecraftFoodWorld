package com.electrosugar.foodworld.items;

import com.electrosugar.foodworld.FoodWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemBase extends Item {
    public ItemBase() {
        super(new Item.Properties().group(FoodWorld.FW_FOOD));
    }
}
