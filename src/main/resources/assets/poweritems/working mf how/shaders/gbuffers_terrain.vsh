#version 330 compatibility
#include "/lib/settings.glsl"

out vec2 texCoord;
out vec2 lightCoord;
out vec4 vertexColor;
out float vertexDistance;
out vec3 fragPos;
out float moonLighting;

uniform int heldBlockLightValue;
uniform vec3 playerLookVector;
uniform int moonPhase;
uniform mat4 gbufferModelView;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    texCoord = vec2(gl_MultiTexCoord0.x, gl_MultiTexCoord0.y);
    lightCoord = (gl_TextureMatrix[1] * gl_MultiTexCoord1).xy;
    vertexColor = gl_Color;
    vertexDistance = length((gl_ModelViewMatrix * gl_Vertex).xyz);
    fragPos = gl_Vertex.xyz;

    int moonBrightness;
    if (moonPhase <= 4) {
        moonBrightness = 4 - moonPhase;
    } else {
        moonBrightness = moonPhase - 4;
    }
    moonLighting = moonBrightness / 4.0;
}