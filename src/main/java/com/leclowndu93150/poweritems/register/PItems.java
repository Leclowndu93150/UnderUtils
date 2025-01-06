package com.leclowndu93150.poweritems.register;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.BatteryItem;
import com.leclowndu93150.poweritems.items.FlashlightItem;
import com.leclowndu93150.poweritems.items.NightVisionGogglesItem;
import com.leclowndu93150.poweritems.items.WindupFlashlightItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PowerItems.MODID);

    public static final DeferredItem<FlashlightItem> FLASHLIGHT = ITEMS.register("flashlight", () -> new FlashlightItem(new Item.Properties()));

    public static final DeferredItem<BatteryItem> BATTERY = ITEMS.register("battery", () -> new BatteryItem(new Item.Properties()));

    public static final DeferredItem<NightVisionGogglesItem> NIGHT_VISION_GOGGLES = ITEMS.register("night_vision_goggles", () -> new NightVisionGogglesItem(new Item.Properties()));

    public static final DeferredItem<WindupFlashlightItem> WINDUP_FLASHLIGHT = ITEMS.register("windup_flashlight", () -> new WindupFlashlightItem(new Item.Properties()));
}
