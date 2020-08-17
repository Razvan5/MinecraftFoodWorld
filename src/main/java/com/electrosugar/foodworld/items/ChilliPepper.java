package com.electrosugar.foodworld.items;

import com.electrosugar.foodworld.FoodWorld;
import com.sun.scenario.effect.Effect;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ChilliPepper extends Item {

    public ChilliPepper() {
        super(new Item.Properties()
                .group(FoodWorld.FW_FOOD)
                .food(new Food.Builder()
                        .hunger(2)
                        .saturation(1.3f)
                        .effect(new EffectInstance(Effects.FIRE_RESISTANCE,600,1),1)
                        .effect(new EffectInstance(Effects.NAUSEA,100,1),0.3f)
                        .effect(new EffectInstance(Effects.INSTANT_DAMAGE,1,1),0.3f)
                        .setAlwaysEdible()
                        .build())


        );
    }
}
