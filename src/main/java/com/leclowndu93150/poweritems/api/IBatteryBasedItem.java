package com.leclowndu93150.poweritems.api;

import com.leclowndu93150.poweritems.Config;
import com.leclowndu93150.poweritems.capabilities.BatteryStorage;
import com.leclowndu93150.poweritems.capabilities.IBatteryStorage;
import com.leclowndu93150.poweritems.register.ComponentBatteryStorage;
import com.leclowndu93150.poweritems.register.PCapabilities;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public interface IBatteryBasedItem {
    default IBatteryStorage getBatteryStorage(ItemStack stack) {
        return stack.get(PDataComponents.BATTERY.get()).batteryStorage();
    }

    default Component[] getBatteryTooltip(ItemStack stack) {
        BatteryStorage batteryStorage = stack.get(PDataComponents.BATTERY).batteryStorage();
        boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);

        MutableComponent[] component = new MutableComponent[batteryStorage.getBatterySlots() + 2];

        if (batteryStorage.getBatterySlots() != 0) {
            for (int i = 0; i < batteryStorage.getBatterySlots(); i++) {
                var batteryInstance = batteryStorage.getBattery(i);
                var charge = batteryInstance.getCharge();
                if (batteryInstance.isEmpty())
                    component[i] = Component.literal("[EMPTY]").withStyle(ChatFormatting.GRAY);
                else
                    component[i] = Component.literal("Charge: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal("" + charge).withStyle(ChatFormatting.WHITE));
            }
        }

        component[batteryStorage.getBatterySlots()] = Component.literal("Time Remaining: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(PowerUtils.formatTimeRemaining(getTimeLeft(stack)))).withStyle(ChatFormatting.WHITE);
        component[batteryStorage.getBatterySlots() + 1] = Component.literal("Status: " + (isEnabled ? "Active" : "Inactive"))
                .withStyle(isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED);

        return component;
    }

    default int getTimeLeft(ItemStack stack) {
        BatteryStorage batteryStorage = stack.get(PDataComponents.BATTERY).batteryStorage();
        int time = 0;

        for (int i = 0; i < batteryStorage.getBatterySlots(); i++) {
            var batteryInstance = batteryStorage.getBattery(i);
            time += batteryInstance.getCharge();
        }
        return time;
    }

    default boolean isTimeBarVisible(ItemStack stack) {
        return true;
    }

    default int getTimeBarWidth(ItemStack stack) {
        return PowerUtils.getTimeBarWidth(
                stack.get(PDataComponents.BATTERY.get()).batteryStorage().getTotalCharge(),
                stack.get(PDataComponents.BATTERY.get()).batteryStorage().getBatterySlots() * Config.batteryCapacity
        );
    }
}

