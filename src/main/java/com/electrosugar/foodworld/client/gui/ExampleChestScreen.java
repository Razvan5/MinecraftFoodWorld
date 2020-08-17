package com.electrosugar.foodworld.client.gui;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.container.ExampleChestContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExampleChestScreen extends ContainerScreen<ExampleChestContainer> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(FoodWorld.MOD_ID, "textures/gui/example_chest.png");

    public ExampleChestScreen(ExampleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer,inv,titleIn);
        this.guiLeft=0;
        this.guiTop=0;
        this.xSize = 175;
        this.ySize = 183;
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
        //8pixeli x si 6 pixeli y
        this.font.drawString(this.title.getFormattedText(),8.0f,6.0f,4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),8.0f,73.0f,4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f,1.0f,1.0f,1.0f);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int x = (this.width -this.xSize)/2;
        int y = (this.height-this.ySize)/2;
        this.blit(x,y,0,0,this.xSize,this.ySize);
    }
}
