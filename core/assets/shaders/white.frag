varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

void main(){
	vec4 color = vec4(1.0,1.0,1.0,1.0);
	gl_FragColor = color;
}