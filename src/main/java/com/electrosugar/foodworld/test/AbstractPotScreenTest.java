package com.electrosugar.foodworld.test;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.blocks.Pot;
import com.electrosugar.foodworld.container.PotContainer;
import com.electrosugar.foodworld.tileentity.PotTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbstractPotScreenTest extends ContainerScreen<PotContainerTest> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(FoodWorld.MOD_ID, "textures/gui/pot.png");

    public AbstractPotScreenTest(PotContainerTest screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer,inv,titleIn);
        this.guiLeft=0;
        this.guiTop=0;
        this.xSize = 176;
        this.ySize = 166;

    }


    @Override
    public void render(final int mouseX,final int mouseY,final float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //8 pixeli x si 6 pixeli y
        this.font.drawString(this.title.getFormattedText(),8.0f,6.0f,4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),8.0f,73.0f,4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f,1.0f,1.0f,1.0f);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int x = (this.width -this.xSize)/2;
        int y = (this.height-this.ySize)/2;
        this.blit(x,y,0,0,this.xSize,this.ySize);

    }

    public void drawFluidLevel(PotTileEntity potTileEntity){
        int ratio=0;
        if(potTileEntity.fluidTank.getFluidAmount()!=0){
            ratio = potTileEntity.fluidTank.getCapacity()/potTileEntity.fluidTank.getFluidAmount();
        }
        this.blit(10,10,176,17,18,36*ratio);
    }

}
