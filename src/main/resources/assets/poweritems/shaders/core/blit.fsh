#version 150

uniform sampler2D DiffuseSampler;
uniform vec4 ColorModulate;

in vec2 texCoord;
out vec4 fragColor;1

void main() {
    fragColor = texture(DiffuseSampler, texCoord) * ColorModulate;
}