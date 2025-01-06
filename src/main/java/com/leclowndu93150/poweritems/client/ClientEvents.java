package com.leclowndu93150.poweritems.client;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.FlashlightItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {
    public static final KeyMapping TOGGLE_NIGHT_VISION = new KeyMapping(
            "key.poweritems.toggle_night_vision",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.poweritems"
    );

    public static final KeyMapping TOGGLE_FLASHLIGHT = new KeyMapping(
            "key.poweritems.toggle_flashlight",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "key.categories.poweritems"
    );

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (TOGGLE_NIGHT_VISION.consumeClick()) {
            toggleItem(player.getItemBySlot(EquipmentSlot.HEAD));
        }

        if (TOGGLE_FLASHLIGHT.consumeClick()) {
            toggleItem(findFlashlight(player));
        }
    }

    private static void toggleItem(ItemStack stack) {
        if (stack.has(PDataComponents.ENABLED.get())
                && stack.getOrDefault(PDataComponents.TIME.get(), 0) > 0) {
            stack.set(PDataComponents.ENABLED.get(),
                    !stack.getOrDefault(PDataComponents.ENABLED.get(), false));
        }
    }

    private static ItemStack findFlashlight(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return mainHand.getItem() instanceof FlashlightItem ? mainHand :
                offHand.getItem() instanceof FlashlightItem ? offHand :
                        ItemStack.EMPTY;
    }
}