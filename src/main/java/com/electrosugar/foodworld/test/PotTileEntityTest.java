package com.electrosugar.foodworld.test;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PotTileEntityTest extends AbstractPotTileEntityTest {

    public PotTileEntityTest() {
        super(ModTileEntityTypesTest.POT_TE.get(), ModRecipeTypesTest.BOILING);
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.furnace");
    }

    protected Container createMenu(int id, PlayerInventory player) {
        return new FurnaceContainer(id, player, this, this.furnaceData);
    }
}
