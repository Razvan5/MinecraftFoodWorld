package com.electrosugar.foodworld;

import com.electrosugar.foodworld.init.BlockInitNew;
import com.electrosugar.foodworld.init.ItemInitNew;
import com.electrosugar.foodworld.init.ModContainerTypes;
import com.electrosugar.foodworld.init.ModTileEntityTypes;
import com.electrosugar.foodworld.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod("foodworld")
public class FoodWorld
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "foodworld";

    public FoodWorld() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        //TurtyWurty 1.15.2 register type
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        //ends here

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        ItemInitNew.ITEMS.register(modEventBus);
        BlockInitNew.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
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
