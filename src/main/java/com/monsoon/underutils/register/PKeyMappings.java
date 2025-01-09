package com.monsoon.underutils.register;

import com.monsoon.underutils.client.ClientEvents;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class PKeyMappings {
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ClientEvents.TOGGLE_NIGHT_VISION);
        event.register(ClientEvents.TOGGLE_FLASHLIGHT);
    }
}
