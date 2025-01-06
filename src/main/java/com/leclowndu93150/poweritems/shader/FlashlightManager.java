package com.leclowndu93150.poweritems.shader;

public class FlashlightManager {
    private static final FlashlightManager INSTANCE = new FlashlightManager();
    private boolean flashlightEnabled = false;
    private float flashlightPower = 0.0f;
    private Object activeSource;

    private FlashlightManager() {}

    public static FlashlightManager getInstance() {
        return INSTANCE;
    }

    public boolean isFlashlightEnabled() {
        return flashlightEnabled;
    }

    public void setFlashlightEnabled(boolean enabled, Object source) {
        if (enabled) {
            activeSource = source;
            flashlightEnabled = true;
        } else if (source == activeSource) {
            activeSource = null;
            flashlightEnabled = false;
        }
    }

    public float getFlashlightPower() {
        return flashlightPower;
    }

    public void setFlashlightPower(float power) {
        this.flashlightPower = power;
    }
}