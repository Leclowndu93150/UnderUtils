package com.leclowndu93150.poweritems.shader;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import com.leclowndu93150.poweritems.items.NightVisionGogglesItem;
import com.leclowndu93150.poweritems.register.PDataComponents;

public class NightVisionManager {
    public static final NightVisionManager INSTANCE = new NightVisionManager();
    private ShaderInstance shader;

    private NightVisionManager() {}

    public void setShader(ShaderInstance shader) {
        this.shader = shader;
    }

    public ShaderInstance getShader() {
        return shader;
    }

    public void updateUniforms(Player player) {
        if (shader != null && player != null) {
            System.out.println("Updating uniforms");
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            boolean isEnabled = helmet.getItem() instanceof NightVisionGogglesItem &&
                    helmet.getOrDefault(PDataComponents.ENABLED.get(), false);

            shader.safeGetUniform("NVEnabled").set(isEnabled ? 1 : 0);
            shader.safeGetUniform("GameTime").set((float) (System.currentTimeMillis() % 1000000) / 1000.0f);
        }
    }
}