package com.leclowndu93150.poweritems.capabilities;

import com.leclowndu93150.poweritems.api.BatteryInstance;
import com.leclowndu93150.poweritems.register.ComponentBatteryStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.function.Supplier;

public record ItemBatteryWrapper(Supplier<DataComponentType<ComponentBatteryStorage>> componentType, ItemStack itemStack) implements IBatteryStorage {
    @Override
    public void setBattery(int slot, BatteryInstance BatteryInstance) {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        bs.setBattery(slot, BatteryInstance);

        itemStack.set(componentType, new ComponentBatteryStorage(bs));
    }

    @Override
    public BatteryInstance getBattery(int slot) {
        return itemStack.get(componentType).batteryStorage().getBattery(slot);
    }

    @Override
    public int getBatterySlots() {
        return itemStack.get(componentType).batteryStorage().getBatterySlots();
    }

    @Override
    public void addCharge(int pcharge) {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        bs.addCharge(pcharge);

        itemStack.set(componentType, new ComponentBatteryStorage(bs));
    }

    @Override
    public void removeCharge(int pcharge) {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        bs.removeCharge(pcharge);

        itemStack.set(componentType, new ComponentBatteryStorage(bs));
    }

    @Override
    public int getTotalCharge() {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        return bs.getTotalCharge();
    }

    @Override
    public int getEmptyOrLowest() {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        return bs.getEmptyOrLowest();
    }

    @Override
    public int getMostCharged() {
        BatteryStorage bs = itemStack.get(componentType).batteryStorage();
        return bs.getMostCharged();
    }

    @Override
    public BatteryStorage copy() {
        return itemStack.get(componentType).batteryStorage().copy();
    }
}
