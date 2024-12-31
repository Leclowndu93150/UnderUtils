package com.leclowndu93150.poweritems;

import com.leclowndu93150.poweritems.register.PDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class RandomUtils {

    public static int powerForDurabilityBar(ItemStack itemStack) {
        IEnergyStorage powerStorage = (IEnergyStorage) itemStack.getItem();
        int powerStored = itemStack.getOrDefault(PDataComponents.ENERGY.get(), 0);
        int powerCapacity = powerStorage.getMaxEnergyStored();
        float chargeRatio = (float) powerStored / powerCapacity;
        return Math.round(13.0F - ((1 - chargeRatio) * 13.0F));
    }

}
