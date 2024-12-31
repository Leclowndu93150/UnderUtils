package com.leclowndu93150.poweritems.register;

import com.leclowndu93150.poweritems.PowerItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PowerItems.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("undertab", () -> CreativeModeTab.builder().title(Component.literal("UnderUtils")).icon(() -> Items.IRON_INGOT.getDefaultInstance()).displayItems((parameters, output) -> {
        for(DeferredHolder<Item, ? extends Item> item : PItems.ITEMS.getEntries()) {
            output.accept(item.get().getDefaultInstance());
        }
    }).build());

}
