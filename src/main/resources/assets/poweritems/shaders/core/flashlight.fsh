#version 150

in vec2 texCoord;
in vec2 lightCoord;
in vec4 vertexColor;
in float vertexDistance;
in float flashlightStrength;
in float moonLighting;

uniform sampler2D gtexture;
uniform sampler2D lightmap;
uniform vec3 FlashlightColor;
uniform float nightVision;
uniform int megaDarknessEnabled;
uniform int FlashlightEnabled;
uniform float FlashlightDistance;  // Added this uniform

out vec4 fragColor;

void makeTexturesBw(inout vec4 color) {
    float average = (color.r + color.g + color.b) / 3.0;
    color.rgb = vec3(average);
}

void main() {
    vec4 texColor = texture(gtexture, texCoord);
    if (texColor.a < 0.1) discard;

    vec4 lightColor = texture(lightmap, lightCoord);
    float lightBrightness = (lightColor.r + lightColor.g + lightColor.b) / 3.0;
    vec4 baseColor = vertexColor;

    // Night vision effect
    if (nightVision > 0.0) {
        makeTexturesBw(baseColor);
        makeTexturesBw(texColor);
        lightColor = vec4(1.0);
    }
    // Mega darkness handling
    else if (megaDarknessEnabled == 1) {
        if (FlashlightEnabled > 0) {
            lightBrightness += flashlightStrength;
        }

        if (lightBrightness < 0.2) {
            lightBrightness = 0.0;
        } else if (lightBrightness < 0.4) {
            if (vertexDistance < moonLighting * 50.0) {
                float maxBrightness = 0.6 * moonLighting;
                float distanceAway = vertexDistance / (moonLighting * 50.0);
                lightBrightness = maxBrightness * (1.0 - distanceAway);

                if (flashlightStrength == 0.0) {
                    makeTexturesBw(baseColor);
                    makeTexturesBw(texColor);
                }
            } else {
                lightBrightness = 0.0;
            }
        }
        lightColor *= lightBrightness;
    }

    // Apply flashlight effect
    if (FlashlightEnabled > 0 && flashlightStrength > 0.0) {
        vec3 lightContribution = FlashlightColor * flashlightStrength;

        if (lightBrightness < 0.4) {
            float attenuatedStrength = smoothstep(0.0, 1.0, 1.0 - (vertexDistance / FlashlightDistance));
            lightColor.rgb *= attenuatedStrength;
        }

        lightColor.rgb += lightContribution;
    }

    fragColor = texColor * baseColor * lightColor;
}