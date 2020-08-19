package com.electrosugar.foodworld.test;

import com.electrosugar.foodworld.FoodWorld;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypesTest {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, FoodWorld.MOD_ID);

    public static RegistryObject<ContainerType<PotContainerTest>> POT_CONTAINER = CONTAINER_TYPES
            .register("pot_test",() -> IForgeContainerType.create(PotContainerTest::new));
}
