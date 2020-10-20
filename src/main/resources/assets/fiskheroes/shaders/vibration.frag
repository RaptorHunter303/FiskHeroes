#version 120

varying vec4 texCoord;
varying vec3 normal, color, pos;

uniform float time;
uniform sampler2D textureSampler;

void main(void) {

//	vec3 nlight = normalize(gl_LightSource[0].position.xyz - pos);
//	vec3 neye = normalize(-pos);
//	vec3 nnormal = normalize(normal);
//	vec3 nhalf = normalize(neye + nlight);
//	float diff = max(0.0, dot(nlight, nnormal));
//	float spec = diff > 0.0 ? pow(dot(nhalf, nnormal), 0.0) : 0.0;

	gl_FragColor = texture2D(textureSampler, gl_TexCoord[0].xy) * vec4(texture2D(textureSampler, gl_TexCoord[1].xy).rgb, 1.0) * vec4(color, 1);
//    float timeScaled = time * 0.1;
//    vec4 distortedPosition = texCoord;
//
//    vec2 scale = vec2(0.1, 0.1);
//
//    vec2 distortedTexCoords = texture2D(textureSampler, vec2(texCoord.x + timeScaled, texCoord.y - timeScaled)).rg * 0.1;
//    distortedTexCoords = (texCoord.xy) + (vec2(distortedTexCoords.x, distortedTexCoords.y + timeScaled));
//    vec2 rippleDirection = (texture2D(textureSampler, distortedTexCoords * scale).rg * 2.0 - 1.0) * 0.06;
//
//    distortedPosition += vec4(rippleDirection.x * 5.0, rippleDirection.y * 5.0, 0.0, 0.0);
//
//    float centerDistanceDistorted = (distortedPosition.x * distortedPosition.x + distortedPosition.y * distortedPosition.y + (cos(distortedPosition.z * distortedPosition.z) * 2));
//    float centerDistance = (texCoord.x * texCoord.x + texCoord.y * texCoord.y + (cos(texCoord.z * texCoord.z) * 2));
//
//    float brightness = sin(rippleDirection.x + rippleDirection.y);
//    float brightnessEdge = clamp(sin(clamp((centerDistanceDistorted - 0.7) / 2, 0.0, 4.0)) + clamp((cos(timeScaled) + 1.0) * 0.15, 0.0, 0.3), 0.7, 1.0);
//    float alphaEdge = 1.0 - clamp(sin((centerDistanceDistorted - 3.3) / 2), 0.0, 1.0);
//    float alphaEdgeNonDistorted = 1.0 - clamp(sin((centerDistance - 3.3) / 2) * 2, 0.0, 1.0);
//    float greenPulse = clamp(sin(timeScaled + (centerDistanceDistorted * 0.3)) * 0.1, -0.1, 0.1);
//
//    float mixedBrightness = clamp(brightnessEdge + brightness, 0.0, 1.0);
//    gl_FragColor = texture2D(textureSampler, gl_TexCoord[0].xy) * vec4(0.4 - greenPulse, 0.8, 0.7 - greenPulse, 1.0) * vec4(brightnessEdge, mixedBrightness, brightnessEdge, alphaEdge * alphaEdgeNonDistorted);
}
