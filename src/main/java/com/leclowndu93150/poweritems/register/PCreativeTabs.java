package com.leclowndu93150.poweritems.register;

import com.leclowndu93150.poweritems.Config;
import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.api.BatteryInstance;
import com.leclowndu93150.poweritems.api.IBatteryBasedItem;
import com.leclowndu93150.poweritems.api.ITimeBasedItem;
import com.leclowndu93150.poweritems.capabilities.BatteryStorage;
import com.leclowndu93150.poweritems.capabilities.IBatteryStorage;
import com.leclowndu93150.poweritems.items.BatteryItem;
import com.leclowndu93150.poweritems.items.WindupFlashlightItem;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PowerItems.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UNDERTAB = CREATIVE_MODE_TABS.register("undertab", () -> CreativeModeTab.builder()
        .title(Component.literal("UnderUtils"))
        .icon(() -> Items.IRON_INGOT.getDefaultInstance())
        .displayItems((parameters, output) -> {
            for (DeferredHolder<Item, ? extends Item> item : PItems.ITEMS.getEntries()) {
                output.accept(item.get().getDefaultInstance());
                if (item.get() instanceof IBatteryBasedItem) {
                    addPowered(output, item.get().asItem());
                }

                if (item.get() instanceof ITimeBasedItem) {
                    addTimeBased(output, item.get().asItem());
                }
            }
    }).build());

    public static void addPowered(CreativeModeTab.Output output, Item item) {
        ItemStack itemStack = new ItemStack(item);
        BatteryStorage storage = itemStack.get(PDataComponents.BATTERY).batteryStorage();
        if (storage != null) {
            int slots = storage.getBatterySlots();

            itemStack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(new BatteryStorage(slots, false)));

            output.accept(itemStack);
            output.accept(item.getDefaultInstance());
        }
    }

    public static void addTimeBased(CreativeModeTab.Output output, Item item) {
        ItemStack itemStack = new ItemStack(item);

        if (item instanceof ITimeBasedItem) {
            itemStack.set(PDataComponents.TIME, ((ITimeBasedItem) item).getMaxTime());
        }

        output.accept(itemStack);
        output.accept(item.getDefaultInstance());
    }
}
