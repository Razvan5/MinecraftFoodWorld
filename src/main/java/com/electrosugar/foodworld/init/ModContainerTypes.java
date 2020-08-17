package com.electrosugar.foodworld.init;

import com.electrosugar.foodworld.FoodWorld;
import com.electrosugar.foodworld.container.ExampleChestContainer;
import com.electrosugar.foodworld.container.PotContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, FoodWorld.MOD_ID);

    public static RegistryObject<ContainerType<ExampleChestContainer>> EXAMPLE_CHEST = CONTAINER_TYPES
            .register("example_chest",() -> IForgeContainerType.create(ExampleChestContainer::new));

    public static RegistryObject<ContainerType<PotContainer>> POT = CONTAINER_TYPES
            .register("pot",() -> IForgeContainerType.create(PotContainer::new));

}

