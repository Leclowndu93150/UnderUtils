package com.leclowndu93150.poweritems.register;

import com.leclowndu93150.poweritems.client.ClientEvents;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class PKeyMappings {
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ClientEvents.TOGGLE_NIGHT_VISION);
        event.register(ClientEvents.TOGGLE_FLASHLIGHT);
    }
}
