package com.leclowndu93150.poweritems.client;


import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.NightVisionGogglesItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public class NightVisionClientHandler {
    public static final KeyMapping TOGGLE_NIGHT_VISION = new KeyMapping(
            "key.poweritems.toggle_night_vision",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.poweritems"
    );

    public static void init() {
        NeoForge.EVENT_BUS.addListener(NightVisionClientHandler::onKeyInput);
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
}
