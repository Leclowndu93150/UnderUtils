package com.monsoon.underutils.register;

import com.monsoon.underutils.capabilities.BatteryStorage;
import net.minecraft.world.item.ItemStack;

public final class PDataComponentsUtils {
	public static BatteryStorage getBatteryStorageComponent(ItemStack stack) {
		return stack.get(PDataComponents.BATTERY.get()).batteryStorage();
	}
}
