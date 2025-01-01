package com.leclowndu93150.poweritems.shader;

import com.leclowndu93150.poweritems.PowerItems;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import java.io.IOException;

public class FlashlightShader extends ShaderInstance {
    private static final ResourceLocation SHADER_LOCATION = ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "flashlight");

    public FlashlightShader(ResourceProvider resourceProvider) throws IOException {
        super(resourceProvider, SHADER_LOCATION, DefaultVertexFormat.BLOCK);
    }

    public static ShaderInstance createFlashlightShader(ResourceProvider resourceProvider) throws IOException {
        return new FlashlightShader(resourceProvider);
    }
}