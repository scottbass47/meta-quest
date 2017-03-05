varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

void main(){
	vec4 color = texture2D(u_texture, v_texCoord0) * v_color;
	//color.xyz = min(min(color.x, color.y), color.z);
	color.xyz = color.xyz + 0.25;
	if(color.x > 1.0) color.x = 1.0;
	if(color.y > 1.0) color.y = 1.0;
	if(color.z > 1.0) color.z = 1.0;
	gl_FragColor = color;
}