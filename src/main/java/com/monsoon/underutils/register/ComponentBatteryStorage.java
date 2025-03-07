package com.monsoon.underutils.register;

import com.monsoon.underutils.capabilities.BatteryStorage;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record ComponentBatteryStorage(BatteryStorage batteryStorage) {
    public static final ComponentBatteryStorage EMPTY = new ComponentBatteryStorage(BatteryStorage.EMPTY);

    public static final Codec<ComponentBatteryStorage> CODEC =
            BatteryStorage.CODEC.xmap(ComponentBatteryStorage::new, ComponentBatteryStorage::batteryStorage);
    public static final StreamCodec<FriendlyByteBuf, ComponentBatteryStorage> STREAM_CODEC =
            BatteryStorage.STREAM_CODEC.map(ComponentBatteryStorage::new, ComponentBatteryStorage::batteryStorage);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComponentBatteryStorage(BatteryStorage instance))) return false;
        return Objects.equals(batteryStorage, instance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(batteryStorage);
    }
}
