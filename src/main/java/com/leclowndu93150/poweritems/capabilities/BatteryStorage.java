package com.leclowndu93150.poweritems.capabilities;

import com.leclowndu93150.poweritems.Config;
import com.leclowndu93150.poweritems.PowerItems;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.leclowndu93150.poweritems.api.BatteryInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class BatteryStorage implements IBatteryStorage, INBTSerializable<Tag> {
    public static final Codec<BatteryStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BatteryInstance.CODEC.listOf().fieldOf("batteries").forGetter(BatteryStorage::getBatteries),
            Codec.INT.fieldOf("slots").forGetter(BatteryStorage::getBatterySlots)
    ).apply(instance, BatteryStorage::new));

    public static final StreamCodec<FriendlyByteBuf, BatteryStorage> STREAM_CODEC = StreamCodec.composite(
            BatteryInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            BatteryStorage::getBatteries,
            ByteBufCodecs.INT,
            BatteryStorage::getBatterySlots,
            BatteryStorage::new
    );

    public static final BatteryStorage EMPTY = new BatteryStorage(1, true);

    private List<BatteryInstance> batteries;
    private int slots;

    public BatteryStorage(int slots, boolean empty) {
        this.batteries = new ArrayList<>(slots);
        this.slots = slots;

        for (int i = 0; i < slots; i++) {
            if (empty) this.batteries.add(BatteryInstance.EMPTY);
            else this.batteries.add(new BatteryInstance(Config.batteryCapacity));
        }
    }

    private BatteryStorage(List<BatteryInstance> batteries, int slots) {
        this.batteries = batteries;
        this.slots = slots;
    }

    @Override
    public @UnknownNullability Tag serializeNBT(HolderLookup.Provider provider) {
        DataResult<Tag> tagDataResult = CODEC.encodeStart(NbtOps.INSTANCE, this);
        if (tagDataResult.isSuccess()) {
            return tagDataResult.result().get();
        }
        PowerItems.LOGGER.error("Error encoding BacteriaStorage: {}", tagDataResult.error().get().message());
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        DataResult<Pair<BatteryStorage, Tag>> dataResult = CODEC.decode(NbtOps.INSTANCE, nbt);
        if (dataResult.isSuccess()) {
            BatteryStorage newThis = dataResult.getOrThrow().getFirst();
            this.batteries = newThis.batteries;
            this.slots = newThis.slots;
        } else {
            PowerItems.LOGGER.error("Error decoding BacteriaStorage: {}", dataResult.error().get().message());
        }
    }

    public List<BatteryInstance> getBatteries() {
        return batteries;
    }

    @Override
    public void setBattery(int slot, BatteryInstance BatteryInstance) {
        this.batteries.set(slot, BatteryInstance);
    }

    @Override
    public BatteryInstance getBattery(int slot) {
        return this.batteries.get(slot);
    }

    @Override
    public int getBatterySlots() {
        return slots;
    }

    @Override
    public void addCharge(int pcharge) {
        for (BatteryInstance battery : batteries) {
            if (pcharge > 0) {
                int charge = battery.getCharge();
                int maxCharge = Config.batteryCapacity;
                int toAdd = Math.min(maxCharge - charge, pcharge);
                battery.addCharge(toAdd);
                pcharge -= toAdd;
                if (pcharge <= 0) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void removeCharge(int pcharge) {
        for (BatteryInstance battery : batteries) {
            if (pcharge > 0) {
                int charge = battery.getCharge();
                int toRemove = Math.min(charge, pcharge);
                battery.removeCharge(toRemove);
                pcharge -= toRemove;
            } else {
                break;
            }
        }
    }

    @Override
    public int getTotalCharge() {
        int totalCharge = 0;
        for (BatteryInstance battery : batteries) {
            totalCharge += battery.getCharge();
        }
        return totalCharge;
    }

    @Override
    public int getEmptyOrLowest() {
        int lowestCharge = Config.batteryCapacity;
        int j = 0;

        for (int i = 0; i < slots; i++) {
            int charge = getBattery(i).getCharge();
            if (charge == 0) {
                return i;
            } else if (charge < lowestCharge) {
                lowestCharge = charge;
                j = i;
            }
        }
        return j;
    }

    @Override
    public int getMostCharged() {
        int highestCharge = 0;
        int j = 0;

        for (int i = 0; i < slots; i++) {
            int charge = getBattery(i).getCharge();
            if (charge == Config.batteryCapacity) {
                return i;
            } else if (charge > highestCharge) {
                highestCharge = charge;
                j = i;
            }
        }
        return j;
    }

    public BatteryStorage copy() {
        return new BatteryStorage(new ArrayList<>(batteries), slots);
    }
}
