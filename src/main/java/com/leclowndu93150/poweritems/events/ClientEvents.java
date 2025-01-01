package com.leclowndu93150.poweritems.events;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.shader.NightVisionManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Objects;

import static com.leclowndu93150.poweritems.shader.NightVisionShader.createNightVisionShader;

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            PowerItems.LOGGER.info("Registering Night Vision shader");
            event.registerShader(createNightVisionShader(event.getResourceProvider()),
                    NightVisionManager.INSTANCE::setShader);
            PowerItems.LOGGER.info("Night Vision shader registered successfully");
        } catch (Exception e) {
            PowerItems.LOGGER.error("Failed to load night vision shader", e);
        }
    }
}