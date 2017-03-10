varying vec2 v_texCoord0;
varying vec4 v_color;

uniform sampler2D u_sampler2D;

void main()
{
	vec4 color = texture2D(u_sampler2D, v_texCoord0);
    gl_FragColor = color;
}