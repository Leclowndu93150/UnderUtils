package com.leclowndu93150.poweritems.api;

import com.leclowndu93150.poweritems.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class BatteryInstance {
    public static final BatteryInstance EMPTY = new BatteryInstance(0);
    public static final BatteryInstance FULL = new BatteryInstance(Config.batteryCapacity);

    public static final Codec<BatteryInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("charge").forGetter(BatteryInstance::getCharge)
    ).apply(instance, BatteryInstance::new));

    public static final StreamCodec<FriendlyByteBuf, BatteryInstance> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            BatteryInstance::getCharge,
            BatteryInstance::new
    );

    private int charge;

    public BatteryInstance(int pcharge) {
        this.charge = pcharge;
    }

    public void setCharge(int pcharge) {
        this.charge = pcharge;
    }

    public int getCharge() {
        return this.charge;
    }

    public BatteryInstance copy() {
        return new BatteryInstance(this.charge);
    }

    public boolean isEmpty() {
        return this.charge == 0;
    }

    public void addCharge(int pcharge) {
        if (this.charge + pcharge > Config.batteryCapacity) {
            this.charge = Config.batteryCapacity;
            return;
        }
        this.charge += pcharge;
    }

    public void removeCharge(int pcharge) {
        if (this.charge - pcharge < 0) {
            this.charge = 0;
            return;
        }
        this.charge -= pcharge;
    }
}
