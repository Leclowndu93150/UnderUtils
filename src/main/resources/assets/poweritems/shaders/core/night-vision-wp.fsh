#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D NoiseSampler;
uniform float Time;
uniform float NightVisionEnabled;
uniform float VignetteEnabled;
uniform float VignetteRadius;
uniform float Brightness;
uniform float NoiseAmplification;
uniform float IntensityAdjust;

in vec2 texCoord;
out vec4 fragColor;

const float SOFTNESS = 0.25;
const float contrast = 0.8;

void main() {
    vec4 texColor = texture(DiffuseSampler, texCoord);
    texColor.rgb *= Brightness;
    
    vec2 uv;
    uv.x = 0.35 * sin(Time * 10);
    uv.y = 0.35 * cos(Time * 10);
    vec3 noise = texture(NoiseSampler, texCoord + uv).rgb * NoiseAmplification;
    texColor.xy += noise.xy * 0.005;

    if(VignetteEnabled > 0) {
        float dist = distance(texCoord, vec2(0.5));
        float vignette = smoothstep(VignetteRadius, VignetteRadius - SOFTNESS, dist);
        texColor.rgb *= vignette;
    }

    const vec3 lumvec = vec3(0.30, 0.59, 0.11);
    float intensity = dot(lumvec, texColor.rgb);
    intensity = clamp(contrast * (intensity - 0.5) + 0.5, 0.0, 1.0);
    float green = clamp(intensity / 0.59, 0.0, 1.0) * IntensityAdjust;
    
    vec4 visionColor = vec4(green * 0.7, green * 1, green * 1, 1.0);
    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    vec4 grayColor = vec4(gray);
    
    fragColor = grayColor * visionColor;
}
