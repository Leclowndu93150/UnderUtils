#include "/lib/settings.glsl"

uniform int randSeed;
uniform float NIGHT_VISION_ENABLED;
uniform float flashlightEnabled;

void toGrayScale(inout vec4 newVertexColor, inout vec4 texColor) {
    float blackWhiteTexCol = (texColor.r + texColor.g + texColor.b) / 3.0;
    float blackWhiteVertexCol = (newVertexColor.r + newVertexColor.g + newVertexColor.b) / 3.0;

    newVertexColor = vec4(blackWhiteVertexCol, blackWhiteVertexCol, blackWhiteVertexCol, newVertexColor.a);
    texColor = vec4(blackWhiteTexCol, blackWhiteTexCol, blackWhiteTexCol, texColor.a);
}

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

vec4 applyNightVision(vec4 color, vec2 texCoord, float vertexDistance) {
    float luminance = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec2 noiseCoord = texCoord * vec2(1920, 1080) / 4.0;
    float noise = random(noiseCoord + vec2(mod(float(randSeed), 100.0))) * NOISE_INTENSITY;
    vec2 center = texCoord - 0.5;
    float vignette = 1.0 - length(center) * VIGNETTE_STRENGTH;

    float distanceBoost = 1.0;
    if (vertexDistance < NV_FALLOFF) {
        float distanceAttenuation = smoothstep(0.0, 1.0, (1.0 - ((vertexDistance / 2) / NV_FALLOFF)));
        float closeBonus = smoothstep(5.0, 0.0, vertexDistance) * 3;
        distanceBoost = distanceAttenuation + closeBonus;
    }

    vec3 nvColor = vec3(luminance) * NV_TINT * max(distanceBoost, 1);
    nvColor += noise;
    nvColor *= vignette;

    return vec4(nvColor, color.a);
}

vec4 applyNightVisionSky(vec4 color, vec2 texCoord) {
    vec2 center = texCoord - 0.5;
    float vignette = 1.0 - length(center) * VIGNETTE_STRENGTH;

    vec2 noiseCoord = texCoord * vec2(1920, 1080) / 4.0;
    float noise = random(noiseCoord + vec2(mod(float(randSeed), 100.0))) * NOISE_INTENSITY;
    return vec4((color.rgb - 0.5) * NV_TINT * vignette, color.a);
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
)  {
    vec4 pixelColor;
    vec4 texColor = texture(gtexture, texCoord);
    if (texColor.a < 0.1) discard;

    vec4 lightColor = texture(lightmap, lightCoord);
    float lightBrightness = (lightColor.r + lightColor.g + lightColor.b)/3.0;
    vec4 newVertexColor = vertexColor;

    if (MEGA_DARKNESS_ENABLED == 1 && NIGHT_VISION_ENABLED < 0.5) {
        lightBrightness -= MEGA_DARKNESS_INTENSITY;
        lightColor = lightColor * lightBrightness;

        if (lightBrightness < 0.02) {
            lightBrightness = 0.0;
        }
    }

    if (flashlightEnabled > 0.5 && flashlightLightStrength > 0.0) {
        vec4 modifiedFlashlightColor = vec4(flashlightColor.rgb * flashlightLightStrength, 1.0);
        lightColor = lightColor + modifiedFlashlightColor;
    }

    vec4 unFogColor = texColor * newVertexColor * lightColor;

    if (NIGHT_VISION_ENABLED > 0.5) {
        return applyNightVision(unFogColor, texCoord, vertexDistance);
    }

    if (vertexDistance > fogStart) {
        float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
        return(vec4(mix(unFogColor.rgb, fogColor, fogValue), unFogColor.a));
    } else {
        return(unFogColor);
    }
}