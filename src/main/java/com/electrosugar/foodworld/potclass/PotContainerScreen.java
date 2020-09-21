package com.electrosugar.foodworld.potclass;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: brandon3055
 * Date: 06/01/2015
 *
 * ContainerScreenFurnace is a gui similar to that of a furnace. It has a progress bar and a burn time indicator.
 * Both indicators have mouse over text
 *
 * The Screen is drawn in several layers, most importantly:
 * Background - renderBackground() - eg a grey fill
 * Background texture - drawGuiContainerBackgroundLayer() (eg the frames for the slots)
 * Foreground layer - typically text labels
 * renderHoveredToolTip - for tool tips when the mouse is hovering over something of interest
 */
public class PotContainerScreen extends ContainerScreen<PotContainer> {

	private PotContainer potContainer;
	public PotContainerScreen(PotContainer potContainer, PlayerInventory playerInventory, ITextComponent title) {
		super(potContainer, playerInventory, title);
    this.potContainer = potContainer;

		// Set the width and height of the gui.  Should match the size of the texture!
		xSize = 176;
		ySize = 207;
	}

	// some [x,y] coordinates of graphical elements
	final int COOK_BAR_XPOS = 103;
	final int COOK_BAR_YPOS = 35;
	final int COOK_BAR_ICON_U = 176;   // texture position of white arrow icon [u,v]
	final int COOK_BAR_ICON_V = 0;
	final int COOK_BAR_WIDTH = 24;
	final int COOK_BAR_HEIGHT = 17;

	final int FLAME_XPOS = 7;
	final int FLAME_YPOS = 15;
	final int FLAME_ICON_U = 176;   // texture position of flame icon [u,v]
	final int FLAME_ICON_V = 17;
	final int FLAME_WIDTH = 18;
	final int FLAME_HEIGHT = 37;
	final int FLAME_X_SPACING = 18;

  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  // Draw the Tool tip text if hovering over something of interest on the screen
  protected void renderHoveredToolTip(int mouseX, int mouseY) {
    if (!this.minecraft.player.inventory.getItemStack().isEmpty()) return;  // no tooltip if the player is dragging something

    List<String> hoveringText = new ArrayList<String>();

    // If the mouse is over the progress bar add the progress bar hovering text
    if (isInRect(guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_WIDTH, COOK_BAR_HEIGHT, mouseX, mouseY)){
      hoveringText.add("Progress:");
      int cookPercentage =(int)(potContainer.fractionOfCookTimeComplete() * 100);
      hoveringText.add(cookPercentage + "%");
    }

    // If the mouse is over one of the burn time indicators, add the burn time indicator hovering text
    for (int i = 0; i < PotContainer.FUEL_SLOTS_COUNT; ++i) {
      if (isInRect(guiLeft + FLAME_XPOS + FLAME_X_SPACING * i, guiTop + FLAME_YPOS, FLAME_WIDTH, FLAME_HEIGHT, mouseX, mouseY)) {
        hoveringText.add("Fluid: "+potContainer.getFluid().getRegistryName().toString());
        hoveringText.add("Time: " +potContainer.secondsOfFuelRemaining(i) + "s");
      }
    }

    // If hoveringText is not empty draw the hovering text.  Otherwise, use vanilla to render tooltip for the slots
    if (!hoveringText.isEmpty()){
      renderTooltip(hoveringText, mouseX, mouseY);
    } else {
      super.renderHoveredToolTip(mouseX, mouseY);
    }
  }


  @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(TEXTURE);

    // width and height are the size provided to the window when initialised after creation.
    // xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
    // The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
    //  the available window
    int edgeSpacingX = (this.width - this.xSize) / 2;
    int edgeSpacingY = (this.height - this.ySize) / 2;
    this.blit(edgeSpacingX, edgeSpacingY, 0, 0, this.xSize, this.ySize);

    	// draw the cook progress bar
		double cookProgress = potContainer.fractionOfCookTimeComplete();
		blit(guiLeft + COOK_BAR_XPOS, guiTop + COOK_BAR_YPOS, COOK_BAR_ICON_U, COOK_BAR_ICON_V,
         (int)(cookProgress * COOK_BAR_WIDTH), COOK_BAR_HEIGHT);

		// draw the fuel remaining bar for each fuel slot flame
		for (int i = 0; i < PotContainer.FUEL_SLOTS_COUNT; ++i) {
//			if(PotTileEntity.getFluidTexture()!=null) this.minecraft.getTextureManager().bindTexture(PotTileEntity.getFluidTexture());
//			Minecraft.getInstance().getTextureManager().bindTexture(Fluids.WATER.getAttributes().getStillTexture())

			ResourceLocation stillLocation = potContainer.getFluidTexture();
			int color = potContainer.getFluid().getAttributes().getColor();
			float r = ((color >> 16) & 0xFF) / 255f; // red
			float g = ((color >> 8) & 0xFF) / 255f; // green
			float b = ((color >> 0) & 0xFF) / 255f; // blue
			float a = ((color >> 24) & 0xFF) / 255f; // alpha
			GlStateManager.color4f(r,g,b,a);
			TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(stillLocation);
			ResourceLocation spriteLocation = sprite.getName();
			ResourceLocation texture = new ResourceLocation(spriteLocation.getNamespace(), "textures/" + spriteLocation.getPath() + ".png");
			minecraft.getTextureManager().bindTexture(texture);
			double burnRemaining = potContainer.fractionOfFuelRemaining(i);
			int yOffset = (int)((1.0 - burnRemaining) * FLAME_HEIGHT);
			blit(guiLeft + FLAME_XPOS + FLAME_X_SPACING * i, guiTop + FLAME_YPOS + yOffset,
					10,0 + yOffset, FLAME_WIDTH, FLAME_HEIGHT - yOffset);
//			blit(100,100,100,100,100,sprite);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(this.title.getFormattedText(),8.0f,6.0f,Color.darkGray.getRGB());
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),8.0f,73.0f,Color.darkGray.getRGB());
	}

	// Returns true if the given x,y coordinates are within the given rectangle
	public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY){
		return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
	}

  // This is the resource location for the background image
  private static final ResourceLocation TEXTURE = new ResourceLocation("foodworld", "textures/gui/pot.png");
}
