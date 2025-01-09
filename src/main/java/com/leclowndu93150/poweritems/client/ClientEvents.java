package com.leclowndu93150.poweritems.client;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.api.IBatteryBasedItem;
import com.leclowndu93150.poweritems.capabilities.ItemBatteryWrapper;
import com.leclowndu93150.poweritems.items.FlashlightItem;
import com.leclowndu93150.poweritems.networking.TogglePayload;
import com.leclowndu93150.poweritems.register.PCapabilities;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.register.PItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

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
            PacketDistributor.sendToServer(new TogglePayload(EquipmentSlot.HEAD.getName()));
        }

        if (TOGGLE_FLASHLIGHT.consumeClick()) {
            PacketDistributor.sendToServer(new TogglePayload(EquipmentSlot.MAINHAND.getName()));
        }
    }

    private static ItemStack findFlashlight(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return mainHand.getItem() instanceof FlashlightItem ? mainHand :
                offHand.getItem() instanceof FlashlightItem ? offHand :
                        ItemStack.EMPTY;
    }

    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(PItems.FLASHLIGHT.get(), ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "enabled"),
                    (stack, level, living, id) -> isEnabledNBT(stack));
            ItemProperties.register(PItems.NIGHT_VISION_GOGGLES.get(), ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "enabled"),
                    (stack, level, living, id) -> isEnabledNBT(stack));
            ItemProperties.register(PItems.WINDUP_FLASHLIGHT.get(), ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "enabled"),
                    (stack, level, living, id) -> isEnabledNBT(stack));
        });
    }

    private static void registerItemCaps(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof IBatteryBasedItem) {
                event.registerItem(PCapabilities.BatteryStorage.ITEM, (stack, ctx) -> new ItemBatteryWrapper(PDataComponents.BATTERY, stack), item);
            }
        }
    }

    public static float isEnabledNBT(ItemStack stack) {
        return stack.getOrDefault(PDataComponents.ENABLED.get(),false) ? 1 : 0;
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return HumanoidModel.ArmPose.EMPTY;
            }
        }, PItems.NIGHT_VISION_GOGGLES);
    }

}