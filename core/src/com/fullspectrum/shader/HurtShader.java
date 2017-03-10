package com.fullspectrum.shader;

import com.badlogic.ashley.core.Entity;

public class HurtShader extends Shader{

	public HurtShader() {
		super("hurt.vert", "hurt.frag");
	}

	@Override
	public void passUniforms(Entity entity) {
		
	}

}
