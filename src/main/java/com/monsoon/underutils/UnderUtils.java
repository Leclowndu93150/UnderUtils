package com.monsoon.underutils;

import com.monsoon.underutils.client.ClientEvents;
import com.monsoon.underutils.register.PCreativeTabs;
import com.monsoon.underutils.register.PDataComponents;
import com.monsoon.underutils.register.PItems;
import com.monsoon.underutils.register.PKeyMappings;
import com.monsoon.underutils.util.ShaderPackDownloader;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(UnderUtils.MODID)
public class UnderUtils {
    public static final String MODID = "underutils";
    public static final Logger LOGGER = LogUtils.getLogger();
    public UnderUtils(IEventBus modEventBus, ModContainer modContainer) {
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
