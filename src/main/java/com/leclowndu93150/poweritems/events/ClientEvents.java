package com.leclowndu93150.poweritems.events;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.shader.FlashlightManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.io.IOException;

import static com.leclowndu93150.poweritems.shader.FlashlightShader.createFlashlightShader;

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            PowerItems.LOGGER.info("Registering Flashlight shader");
            event.registerShader(createFlashlightShader(event.getResourceProvider()), FlashlightManager.INSTANCE::setShader);
            PowerItems.LOGGER.info("Flashlight shader registered successfully");
        } catch (IOException e) {
            PowerItems.LOGGER.error("Failed to load flashlight shader", e);
        }
    }


    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderLevelStageEvent.class, event2 -> {
            if (event2.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    FlashlightManager.INSTANCE.updateUniforms(player);
                }
            }
        });
    }
}

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
class ClientGameEvents{
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ShaderInstance shader = FlashlightManager.INSTANCE.getShader();
                if (shader != null) {
                    PowerItems.LOGGER.debug("Applying flashlight shader");
                    ShaderInstance oldShader = RenderSystem.getShader();

                    // Update and apply shader
                    FlashlightManager.INSTANCE.updateUniforms(player);
                    RenderSystem.setShader(() -> shader);

                    // Render a full-screen quad to apply the effect
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    // Draw fullscreen quad
                    Minecraft.getInstance().renderBuffers().bufferSource().endBatch(RenderType.translucent());

                    // Restore previous state
                    RenderSystem.setShader(() -> oldShader);
                    RenderSystem.disableBlend();
                }
            }
        }
    }
}
