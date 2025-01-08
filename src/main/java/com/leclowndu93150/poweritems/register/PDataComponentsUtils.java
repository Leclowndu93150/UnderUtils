package com.leclowndu93150.poweritems.register;

import com.leclowndu93150.poweritems.capabilities.BatteryStorage;
import net.minecraft.world.item.ItemStack;

public final class PDataComponentsUtils {
	public static BatteryStorage getBatteryStorageComponent(ItemStack stack) {
		return stack.get(PDataComponents.BATTERY.get()).batteryStorage();
	}
}
