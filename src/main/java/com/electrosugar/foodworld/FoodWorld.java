package com.electrosugar.foodworld;

import com.electrosugar.foodworld.client.gui.PotScreen;
import com.electrosugar.foodworld.events.BlockInit;
import com.electrosugar.foodworld.events.StartupCommon;
import com.electrosugar.foodworld.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("foodworld")
public class FoodWorld
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "foodworld";

    public FoodWorld() {
//        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();
        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();


//        BlockInit.BLOCKS.register(MOD_EVENT_BUS);
//        BlockInit.ITEMS.register(MOD_EVENT_BUS);
//        BlockInit.TILE_ENTITY_TYPES.register(MOD_EVENT_BUS);
//        BlockInit.CONTAINER_TYPES.register(MOD_EVENT_BUS);

        registerCommonEvents();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> FoodWorld::registerClientOnlyEvents);
        MinecraftForge.EVENT_BUS.register(this);
    }
    public static IEventBus MOD_EVENT_BUS;

    public static void registerCommonEvents() {

        MOD_EVENT_BUS.register(com.electrosugar.foodworld.mbe31_inventory_furnace.StartupCommon.class);
        MOD_EVENT_BUS.register(com.electrosugar.foodworld.events.StartupCommon.class);


        //----------------
    }

    public static void registerClientOnlyEvents() {

        MOD_EVENT_BUS.register(com.electrosugar.foodworld.mbe31_inventory_furnace.StartupClientOnly.class);
        MOD_EVENT_BUS.register(com.electrosugar.foodworld.events.StartupClientOnly.class);


        //----------------
    }

    private void setup(final FMLCommonSetupEvent event)
    {
//        ScreenManager.registerFactory(StartupCommon.potContainerContainerType, PotScreen::new);

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    public static final ItemGroup FW_FOOD = new ItemGroup("foodWorld"){

        @Override
        public ItemStack createIcon(){
            return new ItemStack(RegistryHandler.PARSNIP.get());
        }
        //item for the first group
    };

}
