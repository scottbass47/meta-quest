package com.fullspectrum.shader;

import com.badlogic.ashley.core.Entity;

public class PoisonShader extends Shader {

	public PoisonShader() {
		super("poison.vert", "poison.frag");
	}

	@Override
	public void passUniforms(Entity entity) {
		
	}

}
