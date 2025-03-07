package com.monsoon.underutils.register;

import com.monsoon.underutils.UnderUtils;
import com.monsoon.underutils.capabilities.IBatteryStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class PCapabilities {
	public static final class BatteryStorage {
		public static final BlockCapability<IBatteryStorage, @Nullable Direction> BLOCK = BlockCapability.createSided(create("battery"), IBatteryStorage.class);
		public static final ItemCapability<IBatteryStorage, @Nullable Void> ITEM = ItemCapability.createVoid(create("battery"), IBatteryStorage.class);
		public static final EntityCapability<IBatteryStorage, @Nullable Direction> ENTITY = EntityCapability.createSided(create("battery"), IBatteryStorage.class);
	}

	private static ResourceLocation create(String path) {
		return ResourceLocation.fromNamespaceAndPath(UnderUtils.MODID, path);
	}
}
