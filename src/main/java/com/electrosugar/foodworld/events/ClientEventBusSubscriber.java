package com.electrosugar.foodworld.events;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.client.gui.ExampleChestScreen;
import com.electrosugar.foodworld.client.gui.PotScreen;
import com.electrosugar.foodworld.init.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FoodWorld.MOD_ID,bus= Mod.EventBusSubscriber.Bus.MOD,value= Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        ScreenManager.registerFactory(ModContainerTypes.EXAMPLE_CHEST.get(), ExampleChestScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.POT.get(), PotScreen::new);
    }
}
