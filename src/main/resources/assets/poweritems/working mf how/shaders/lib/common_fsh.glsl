#include "/lib/settings.glsl"

void toGrayScale(inout vec4 newVertexColor, inout vec4 texColor) {
    float blackWhiteTexCol = (texColor.r + texColor.g + texColor.b) / 3.0;
    float blackWhiteVertexCol = (newVertexColor.r + newVertexColor.g + newVertexColor.b) / 3.0;

    newVertexColor = vec4(blackWhiteVertexCol, blackWhiteVertexCol, blackWhiteVertexCol, newVertexColor.a);
    texColor = vec4(blackWhiteTexCol, blackWhiteTexCol, blackWhiteTexCol, texColor.a);
}

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

vec4 applyNightVision(vec4 color, vec2 texCoord) {
    // Convert to grayscale with human perception weights
    float luminance = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    // Add noise
    vec2 noiseCoord = texCoord * vec2(1920, 1080) / 4.0;
    float noise = random(noiseCoord + vec2(mod(float(frameCounter), 100.0))) * NOISE_INTENSITY;

    // Add vignette
    vec2 center = texCoord - 0.5;
    float vignette = 1.0 - length(center) * VIGNETTE_STRENGTH;

    // Combine effects
    vec3 nvColor = vec3(luminance) * NV_TINT;
    nvColor += noise;
    nvColor *= vignette;

    return vec4(nvColor, color.a);
}

vec4 commonFsh(
    vec2 texCoord,
    vec2 lightCoord,
    vec4 vertexColor,
    float vertexDistance,
    float flashlightLightStrength,
    float moonLighting,
    sampler2D gtexture,
    sampler2D lightmap,
    float nightVision,
    float fogStart,
    float fogEnd,
    vec3 fogColor
) {
    vec4 texColor = texture(gtexture, texCoord);
    if (texColor.a < 0.1) discard;

    vec4 lightColor = texture(lightmap, lightCoord);
    float lightBrightness = (lightColor.r + lightColor.g + lightColor.b)/3.0;

    vec4 newVertexColor = vertexColor;

    if (nightVision > 0.0) {
        toGrayScale(newVertexColor, texColor);
        lightColor = vec4(1.0, 1.0, 1.0, 1.0);
    } else if (MEGA_DARKNESS_ENABLED == 1) {
        lightBrightness -= MEGA_DARKNESS_INTENSITY;
        lightColor = lightColor * lightBrightness;
    }

    if (flashlightLightStrength > 0.0) {
        vec4 modifiedFlashlightColor = vec4(flashlightColor.rgb * flashlightLightStrength, 1.0);
        lightColor = lightColor + modifiedFlashlightColor;
    }

    vec4 unFogColor = texColor * newVertexColor * lightColor;

    if (vertexDistance > fogStart) {
        float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
        return(vec4(mix(unFogColor.rgb, fogColor, fogValue), unFogColor.a));
    } else {
        return(unFogColor);
    }

    if (MEGA_DARKNESS_ENABLED == 1) {
        if (lightBrightness < 0.02) {
            lightBrightness = 0.0;
        }
    }
}