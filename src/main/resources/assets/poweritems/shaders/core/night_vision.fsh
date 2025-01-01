#version 150

in vec2 texCoord;
in vec2 lightCoord;
in vec4 vertexColor;
in float vertexDistance;

uniform sampler2D gtexture;
uniform sampler2D lightmap;
uniform float GameTime;
uniform int NVEnabled;

out vec4 fragColor;

const float MAX_DISTANCE = 12.0;
const vec3 NV_COLOR = vec3(0.0, 0.6, 0.0); // Dark green tint

void main() {
    vec4 texColor = texture(gtexture, texCoord);
    if (texColor.a < 0.1) discard;

    vec4 lightColor = texture(lightmap, lightCoord);

    if (NVEnabled == 1) {
        // Calculate distance-based intensity
        float distanceFactor = 1.0 - clamp(vertexDistance / MAX_DISTANCE, 0.0, 1.0);
        distanceFactor = pow(distanceFactor, 2.0); // Square for more realistic falloff

        // Add some noise to simulate image intensifier grain
        float noise = fract(sin(dot(texCoord + GameTime * 0.01, vec2(12.9898, 78.233))) * 43758.5453);
        float grain = mix(0.95, 1.05, noise);

        // Increase brightness and add green tint
        vec3 brightenedColor = texColor.rgb * vertexColor.rgb * 2.0;
        vec3 nvColor = mix(brightenedColor, NV_COLOR, 0.4) * grain;

        // Apply distance-based intensity
        vec3 finalColor = mix(texColor.rgb, nvColor, distanceFactor);
        fragColor = vec4(finalColor, texColor.a);
    } else {
        fragColor = texColor * vertexColor * lightColor;
    }
}