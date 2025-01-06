package com.leclowndu93150.poweritems.shader;

public class NightVisionManager {
    private static final NightVisionManager INSTANCE = new NightVisionManager();
    private boolean nightVisionEnabled = false;
    private float nightVisionPower = 0.0f;

    private NightVisionManager() {}

    public static NightVisionManager getInstance() {
        return INSTANCE;
    }

    public boolean isNightVisionEnabled() {
        return nightVisionEnabled;
    }

    public void setNightVisionEnabled(boolean enabled) {
        this.nightVisionEnabled = enabled;
    }

    public float getNightVisionPower() {
        return nightVisionPower;
    }

    public void setNightVisionPower(float power) {
        this.nightVisionPower = power;
    }
}