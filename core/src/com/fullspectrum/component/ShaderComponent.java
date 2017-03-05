package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.shader.Shader;

public class ShaderComponent implements Component, Poolable{

	public Shader shader = null;
	
	public ShaderComponent set(Shader shader) {
		this.shader = shader;
		return this;
	}
	
	@Override
	public void reset() {
		shader = null;
	}

}
