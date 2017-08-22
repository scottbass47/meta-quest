package com.fullspectrum.shader;

import com.badlogic.ashley.core.Entity;

public class WhiteShader extends Shader {

	public WhiteShader() {
		super("white.vert", "white.frag");
	}

	@Override
	public void passUniforms(Entity entity) {
	}

}
