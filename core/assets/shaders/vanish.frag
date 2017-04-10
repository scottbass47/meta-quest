varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

void main(){
	vec4 color = texture2D(u_texture, v_texCoord0) * v_color;
	if(color.w < 0.1) discard;
	color.w = 0.5;
	gl_FragColor = color;
}