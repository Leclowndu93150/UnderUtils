package com.monsoon.underutils.mixin;

import com.monsoon.underutils.shader.FlashlightManager;
import com.monsoon.underutils.shader.NightVisionManager;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

@Mixin(value = CommonUniforms.class, remap = false)
public class CommonUniformsMixin {

    @Inject(method = "generalCommonUniforms", at = @At("RETURN"))
    private static void injectFlashlightUniform(UniformHolder uniforms, FrameUpdateNotifier updateNotifier, PackDirectives directives, CallbackInfo ci) {
        uniforms.uniform1f(PER_FRAME, "flashlightEnabled", () -> FlashlightManager.getInstance().isFlashlightEnabled() ? 1.0f : 0.0f);
        uniforms.uniform1f(PER_FRAME,"NIGHT_VISION_ENABLED", () -> NightVisionManager.getInstance().isNightVisionEnabled() ? 1.0f : 0.0f);
    }
}
