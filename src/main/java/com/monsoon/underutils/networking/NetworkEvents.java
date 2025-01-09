package com.monsoon.underutils.networking;

import com.monsoon.underutils.UnderUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = UnderUtils.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(UnderUtils.MODID);
        registrar.playToServer(
                TogglePayload.TYPE,
                TogglePayload.STREAM_CODEC,
                TogglePayload::keyPressedAction
        );
    }
}
