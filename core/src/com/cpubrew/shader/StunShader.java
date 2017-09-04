package com.cpubrew.shader;

import com.badlogic.ashley.core.Entity;

public class StunShader extends Shader{

	public StunShader(){
		super("stun.vert", "stun.frag");
	}

	@Override
	public void passUniforms(Entity entity) {
	}
}