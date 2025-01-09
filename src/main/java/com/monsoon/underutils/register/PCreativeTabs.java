package com.monsoon.underutils.register;

import com.monsoon.underutils.UnderUtils;
import com.monsoon.underutils.api.IBatteryBasedItem;
import com.monsoon.underutils.api.ITimeBasedItem;
import com.monsoon.underutils.capabilities.BatteryStorage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UnderUtils.MODID);

    public static ItemStack getEnabledFlashlight(){
        ItemStack itemStack = new ItemStack(PItems.FLASHLIGHT.get().asItem());
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        itemStack.set(PDataComponents.ENABLED.get(), true);
        itemStack.set(PDataComponents.BATTERY.get(), new ComponentBatteryStorage(new BatteryStorage(1, false)));
        return itemStack;
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UNDERTAB = CREATIVE_MODE_TABS.register("undertab", () -> CreativeModeTab.builder()
        .title(Component.literal("UnderUtils"))
        .icon(PCreativeTabs::getEnabledFlashlight)
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
