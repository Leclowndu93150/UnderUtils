package com.leclowndu93150.poweritems.api;

import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ITimeBasedItem {
	default Component[] getTimeTooltip(ItemStack stack) {
		int currentTicks = stack.getOrDefault(PDataComponents.TIME.get(), 0);
		boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);

		return new Component[] {
				Component.literal("Time Remaining: " + PowerUtils.formatTimeRemaining(currentTicks))
						.withStyle(ChatFormatting.AQUA),
				Component.literal("Status: " + (isEnabled ? "Active" : "Inactive"))
						.withStyle(isEnabled ? ChatFormatting.GREEN : ChatFormatting.RED)
		};
	}

	default boolean isTimeBarVisible(ItemStack stack) {
		return true;
	}

	default int getTimeBarWidth(ItemStack stack, int maxTicks) {
		return PowerUtils.getTimeBarWidth(
				stack.getOrDefault(PDataComponents.TIME.get(), 0),
				maxTicks
		);
	}

	int getMaxTime();
}