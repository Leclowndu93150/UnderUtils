#version 330 compatibility#include "/lib/common_fsh.glsl"in vec2 texCoord;in vec2 lightCoord;in vec4 vertexColor;in vec3 fragPos;in float moonLighting;uniform sampler2D gtexture;uniform sampler2D lightmap;uniform float nightVision;uniform float fogStart;uniform float fogEnd;uniform vec3 fogColor;uniform int heldBlockLightValue;uniform vec3 playerLookVector;layout(location = 0) out vec4 pixelColor;void main() {    vec3 normalizedFragPos = normalize(fragPos);    float distance = length(cross(normalizedFragPos, playerLookVector));    float edgeAttenuation = 1.0 - smoothstep(0.0, FLASHLIGHT_BEAM_WIDTH, distance);    float vertexDistance = length((gl_ModelViewMatrix * vec4(fragPos, 1.0)).xyz);    float flashlightLightStrength = 0.0;    if ((heldBlockLightValue > 0) && (vertexDistance < FLASHLIGHT_DISTANCE)) {        float distanceAttenuation = smoothstep(0.0, 1.0, (1.0 - (vertexDistance / FLASHLIGHT_DISTANCE)));        float closeBonus = smoothstep(5.0, 0.0, vertexDistance) * 2.0; // Multiply by 2.0 to amplify close-range effect        flashlightLightStrength = (distanceAttenuation + closeBonus) * edgeAttenuation;    }    pixelColor = commonFsh(texCoord,                           lightCoord,                           vertexColor,                           vertexDistance,                           flashlightLightStrength,                           moonLighting,                           gtexture,                           lightmap,                           nightVision,                           fogStart,                           fogEnd,                           fogColor);}