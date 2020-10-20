#version 120

varying vec4 texCoord;
varying vec3 normal, color, pos;

void main(void) {
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_TexCoord[1] = gl_MultiTexCoord1;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    texCoord = gl_Vertex;
    normal = gl_Normal;

    color = gl_Color.rgb;
    normal = normalize(gl_NormalMatrix * gl_Normal);
    pos = vec3(gl_ModelViewMatrix * gl_Vertex);
}
