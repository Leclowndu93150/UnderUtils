package com.leclowndu93150.poweritems;

import com.leclowndu93150.poweritems.client.ClientEvents;
import com.leclowndu93150.poweritems.register.PCreativeTabs;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.register.PItems;
import com.leclowndu93150.poweritems.register.PKeyMappings;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(PowerItems.MODID)
public class PowerItems {
    public static final String MODID = "poweritems";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PowerItems(IEventBus modEventBus, ModContainer modContainer) {
        PItems.ITEMS.register(modEventBus);

        modEventBus.addListener(PKeyMappings::register);
        modEventBus.addListener(ClientEvents::onFMLClientSetupEvent);

        PCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        PDataComponents.COMPONENTS.register(modEventBus);
        //ShaderPackExtractor.extractShaderPack(MODID, "Flashlight-Shader-v1.0.1");

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
