package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RotationComponent implements Component, Poolable {

	public float rotation = 0.0f;
	public boolean automatic = false;
	
	public RotationComponent set(float rotation){
		this.rotation = rotation;
		return this;
	}
	
	@Override
	public void reset() {
		rotation = 0.0f;
		automatic = false;
	}
}