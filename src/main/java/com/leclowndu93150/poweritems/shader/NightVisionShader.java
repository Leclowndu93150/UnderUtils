package com.leclowndu93150.poweritems.shader;

import com.leclowndu93150.poweritems.PowerItems;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;

public class NightVisionShader extends ShaderInstance {
    private static final ResourceLocation SHADER_LOCATION = ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "night-vision-wp");

    public NightVisionShader(ResourceProvider resourceProvider) throws IOException {
        super(resourceProvider, SHADER_LOCATION, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
    }

    public static ShaderInstance createNightVisionShader(ResourceProvider resourceProvider) throws IOException {
        return new NightVisionShader(resourceProvider);
    }
}