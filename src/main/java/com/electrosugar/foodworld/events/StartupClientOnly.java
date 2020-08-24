package com.electrosugar.foodworld.events;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.client.gui.PotScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

//@Mod.EventBusSubscriber(modid = FoodWorld.MOD_ID,bus= Mod.EventBusSubscriber.Bus.MOD,value= Dist.CLIENT)
public class StartupClientOnly
{
    // register the factory that is used on the client to generate a ContainerScreen corresponding to our Container
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(StartupCommon.potContainerContainerType, PotScreen::new);
    }
}
