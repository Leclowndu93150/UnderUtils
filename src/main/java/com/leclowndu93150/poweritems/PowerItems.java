package com.leclowndu93150.poweritems;

import com.leclowndu93150.poweritems.client.ClientEvents;
import com.leclowndu93150.poweritems.register.PCreativeTabs;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.register.PItems;
import com.leclowndu93150.poweritems.register.PKeyMappings;
import com.leclowndu93150.poweritems.util.ShaderPackDownloader;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(PowerItems.MODID)
public class PowerItems {
    public static final String MODID = "poweritems";
    public static final String MODNAME = "Power Items";
    public static final Logger LOGGER = LogUtils.getLogger();
    public PowerItems(IEventBus modEventBus, ModContainer modContainer) {
        PItems.ITEMS.register(modEventBus);

        modEventBus.addListener(PKeyMappings::register);
        modEventBus.addListener(ClientEvents::onFMLClientSetupEvent);
        modEventBus.addListener(ClientEvents::registerClientExtensions);

        PCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        PDataComponents.COMPONENTS.register(modEventBus);
        ShaderPackDownloader.downloadShaderPack();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
