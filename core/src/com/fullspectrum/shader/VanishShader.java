package com.fullspectrum.shader;

import com.badlogic.ashley.core.Entity;

public class VanishShader extends Shader{

	public VanishShader(){
		super("vanish.vert", "vanish.frag");
	}

	@Override
	public void passUniforms(Entity entity) {
		
	}
	
}
