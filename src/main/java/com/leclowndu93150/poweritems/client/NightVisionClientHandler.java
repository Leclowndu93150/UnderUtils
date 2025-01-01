package com.leclowndu93150.poweritems.client;


import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.NightVisionGogglesItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public class NightVisionClientHandler {
    private static final ResourceLocation NIGHT_VISION_SHADER = ResourceLocation.fromNamespaceAndPath("poweritems", "night_vision");
    private static GameRenderer gameRenderer;

    public static final KeyMapping TOGGLE_NIGHT_VISION = new KeyMapping(
            "key.poweritems.toggle_night_vision",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.poweritems"
    );

    public static void init() {
        NeoForge.EVENT_BUS.addListener(NightVisionClientHandler::onKeyInput);
        NeoForge.EVENT_BUS.addListener(NightVisionClientHandler::onRenderLevel);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            gameRenderer = Minecraft.getInstance().gameRenderer;
        });
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_NIGHT_VISION);
    }

    private static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && TOGGLE_NIGHT_VISION.consumeClick()) {
            ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet.getItem() instanceof NightVisionGogglesItem) {
                helmet.set(PDataComponents.ENABLED.get(), !helmet.getOrDefault(PDataComponents.ENABLED.get(), false));
                mc.player.displayClientMessage(Component.literal(String.valueOf(helmet.getOrDefault(PDataComponents.ENABLED.get(), false))), true);
            }
        }
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof NightVisionGogglesItem) {
                if (player.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(PDataComponents.ENABLED.get(), false)) {
                    gameRenderer.loadEffect(NIGHT_VISION_SHADER);
                }
            } else {
                gameRenderer.shutdownEffect();
            }
        }
    }
}
