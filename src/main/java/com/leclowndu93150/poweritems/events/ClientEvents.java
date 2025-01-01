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


    @EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    static class ClientGameEvents {
        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ShaderInstance shader = NightVisionManager.INSTANCE.getShader();
                    if (shader != null) {
                        NightVisionManager.INSTANCE.updateUniforms(player);

                        ShaderInstance oldShader = RenderSystem.getShader();

                        RenderSystem.setShader(() -> shader);
                        RenderSystem.setShaderTexture(0, Minecraft.getInstance().getMainRenderTarget().getColorTextureId());

                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
                        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                        bufferbuilder.vertex(-1.0D, -1.0D, 0.0D).uv(0.0F, 0.0F).endVertex();
                        bufferbuilder.vertex(1.0D, -1.0D, 0.0D).uv(1.0F, 0.0F).endVertex();
                        bufferbuilder.vertex(1.0D, 1.0D, 0.0D).uv(1.0F, 1.0F).endVertex();
                        bufferbuilder.vertex(-1.0D, 1.0D, 0.0D).uv(0.0F, 1.0F).endVertex();
                        BufferUploader.drawWithShader(bufferbuilder.end());

                        // Restore previous state
                        RenderSystem.setShader(() -> oldShader);
                        RenderSystem.disableBlend();
                    }
                }
            }
        }
    }
}