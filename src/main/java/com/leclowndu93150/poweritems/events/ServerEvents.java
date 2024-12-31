package com.leclowndu93150.poweritems.events;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.FlashlightItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEvents {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if(event.getEntity().isShiftKeyDown() && event.getItemStack().has(PDataComponents.ENABLED) && !(event.getItemStack().getItem() instanceof FlashlightItem)){
            event.getItemStack().set(PDataComponents.ENABLED, !event.getItemStack().getOrDefault(PDataComponents.ENABLED,false));
            event.getEntity().displayClientMessage(Component.literal("Item is now " + (event.getItemStack().get(PDataComponents.ENABLED).booleanValue() ? "enabled" : "disabled")), true);
            event.getLevel().playSound(event.getEntity(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.4f, event.getItemStack().getOrDefault(PDataComponents.ENABLED,false) ? 0.01f : 0.09f);
        }
    }
}
