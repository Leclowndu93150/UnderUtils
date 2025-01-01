package com.leclowndu93150.poweritems.shader;

public class FlashlightManager {
    private static final FlashlightManager INSTANCE = new FlashlightManager();
    private boolean flashlightEnabled = false;
    private float flashlightPower = 0.0f;

    private FlashlightManager() {}

    public static FlashlightManager getInstance() {
        return INSTANCE;
    }

    public boolean isFlashlightEnabled() {
        return flashlightEnabled;
    }

    public void setFlashlightEnabled(boolean enabled) {
        this.flashlightEnabled = enabled;
    }

    public float getFlashlightPower() {
        return flashlightPower;
    }

    public void setFlashlightPower(float power) {
        this.flashlightPower = power;
    }
}

