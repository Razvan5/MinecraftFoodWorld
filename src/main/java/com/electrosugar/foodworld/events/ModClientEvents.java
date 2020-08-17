package com.electrosugar.foodworld.events;

import com.electrosugar.foodworld.FoodWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FoodWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ModClientEvents {

    public static void openPotGUI(GuiContainerEvent event){

    }
}
