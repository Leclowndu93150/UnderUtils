package com.leclowndu93150.poweritems.capabilities;

import com.leclowndu93150.poweritems.api.BatteryInstance;

public interface IBatteryStorage {
    void setBattery(int slot, BatteryInstance BatteryInstance);

    BatteryInstance getBattery(int slot);

    int getBatterySlots();

    void addCharge(int pcharge);

    void removeCharge(int pcharge);

    int getTotalCharge();

    int getEmptyOrLowest();

    int getMostCharged();

    IBatteryStorage copy();
}
