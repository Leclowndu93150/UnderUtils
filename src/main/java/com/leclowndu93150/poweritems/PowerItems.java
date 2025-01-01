package com.leclowndu93150.poweritems;

import com.leclowndu93150.poweritems.client.NightVisionClientHandler;
import com.leclowndu93150.poweritems.register.PCreativeTabs;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.register.PItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(PowerItems.MODID)
public class PowerItems {
    public static final String MODID = "poweritems";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PowerItems(IEventBus modEventBus, ModContainer modContainer) {
        PItems.ITEMS.register(modEventBus);
        PCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        PDataComponents.COMPONENTS.register(modEventBus);
        modEventBus.addListener(NightVisionClientHandler::onClientSetup);
        NightVisionClientHandler.init();

    }

}
