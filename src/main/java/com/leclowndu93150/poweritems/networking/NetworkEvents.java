package com.leclowndu93150.poweritems.networking;

import com.leclowndu93150.poweritems.PowerItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = PowerItems.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PowerItems.MODID);
        registrar.playToServer(
                TogglePayload.TYPE,
                TogglePayload.STREAM_CODEC,
                TogglePayload::keyPressedAction
        );
    }
}
