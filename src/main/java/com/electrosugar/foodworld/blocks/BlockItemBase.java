package com.electrosugar.foodworld.blocks;

import com.electrosugar.foodworld.FoodWorld;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockItemBase extends BlockItem {

    public BlockItemBase(Block block) {
        super(block, new Item.Properties().group(FoodWorld.FW_FOOD));
    }
}
