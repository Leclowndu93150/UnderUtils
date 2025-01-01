#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 PlayerLookVec;
uniform int FlashlightEnabled;
uniform float FlashlightBeamWidth;
uniform float FlashlightDistance;
uniform int moonPhase;

out vec2 texCoord;
out vec2 lightCoord;
out vec4 vertexColor;
out float vertexDistance;
out float flashlightStrength;
out float moonLighting;

void main() {
    vec4 pos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * pos;

    texCoord = UV0;
    lightCoord = UV2;
    vertexColor = Color;
    vertexDistance = length(pos.xyz);

    vec3 normalizedPos = normalize(Position);
    float distanceToView = length(normalizedPos - PlayerLookVec);

    // Calculate flashlight strength
    if (FlashlightEnabled > 0 && distanceToView < FlashlightBeamWidth && vertexDistance < FlashlightDistance) {
        flashlightStrength = smoothstep(0.0, 1.0, (1.0 - (vertexDistance / FlashlightDistance)));
    } else {
        flashlightStrength = 0.0;
    }

    // Calculate moon lighting
    if (moonPhase <= 4) {
        moonLighting = (4.0 - float(moonPhase)) / 4.0;
    } else {
        moonLighting = (float(moonPhase) - 4.0) / 4.0;
    }
}