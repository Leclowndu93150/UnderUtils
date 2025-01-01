package com.leclowndu93150.poweritems.shader;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.items.FlashlightItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FlashlightManager {
    public static final FlashlightManager INSTANCE = new FlashlightManager();
    private ShaderInstance shader;
    private boolean lastFlashlightState = false;
    private Vec3 lastLookVec = Vec3.ZERO;
    private static final float LOOK_VEC_THRESHOLD = 0.01f;

    private static final float FLASHLIGHT_BEAM_WIDTH = 0.3f;
    private static final float FLASHLIGHT_DISTANCE = 40.0f;
    private static final float[] FLASHLIGHT_COLOR = new float[]{0.8f, 0.8f, 0.0f};

    private FlashlightManager() {}

    public void setShader(ShaderInstance shader) {
        this.shader = shader;
        if (shader != null) {
            PowerItems.LOGGER.info("Setting up Flashlight shader uniforms");
            shader.safeGetUniform("FlashlightBeamWidth").set(FLASHLIGHT_BEAM_WIDTH);
            shader.safeGetUniform("FlashlightDistance").set(FLASHLIGHT_DISTANCE);
            shader.safeGetUniform("FlashlightColor").set(FLASHLIGHT_COLOR[0], FLASHLIGHT_COLOR[1], FLASHLIGHT_COLOR[2]);
            shader.safeGetUniform("FlashlightEnabled").set(0);
            shader.safeGetUniform("megaDarknessEnabled").set(1);
            PowerItems.LOGGER.info("Flashlight shader uniforms set");
        }
    }

    public void updateUniforms(Player player) {
        if (shader == null || player == null) return;

        // Check flashlight state
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean hasFlashlightMain = mainHand.getItem() instanceof FlashlightItem;
        boolean hasFlashlightOff = offHand.getItem() instanceof FlashlightItem;

        boolean mainEnabled = hasFlashlightMain && mainHand.getOrDefault(PDataComponents.ENABLED.get(), false);
        boolean offEnabled = hasFlashlightOff && offHand.getOrDefault(PDataComponents.ENABLED.get(), false);

        boolean currentFlashlightState = mainEnabled || offEnabled;

        Vec3 currentLookVec = player.getLookAngle();
        boolean lookVecChanged = needsLookVecUpdate(currentLookVec);

        // Debug logging
        if (currentFlashlightState != lastFlashlightState) {
            PowerItems.LOGGER.info("Flashlight state changed: " + currentFlashlightState);
            PowerItems.LOGGER.info("Main hand flashlight: " + hasFlashlightMain + ", enabled: " + mainEnabled);
            PowerItems.LOGGER.info("Off hand flashlight: " + hasFlashlightOff + ", enabled: " + offEnabled);
        }

        if (currentFlashlightState != lastFlashlightState || lookVecChanged) {
            shader.safeGetUniform("FlashlightEnabled").set(currentFlashlightState ? 1 : 0);
            shader.safeGetUniform("PlayerLookVec").set((float)currentLookVec.x, (float)currentLookVec.y, (float)currentLookVec.z);

            lastFlashlightState = currentFlashlightState;
            lastLookVec = currentLookVec;
        }
    }

    private boolean needsLookVecUpdate(Vec3 currentLookVec) {
        return Math.abs(currentLookVec.x - lastLookVec.x) > LOOK_VEC_THRESHOLD
                || Math.abs(currentLookVec.y - lastLookVec.y) > LOOK_VEC_THRESHOLD
                || Math.abs(currentLookVec.z - lastLookVec.z) > LOOK_VEC_THRESHOLD;
    }

    public ShaderInstance getShader() {
        return shader;
    }
}