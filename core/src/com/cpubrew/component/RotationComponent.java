package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RotationComponent implements Component, Poolable {

	public float rotation = 0.0f;
	public float angularVel = 0.0f;
	public boolean automatic = false;
	
	public RotationComponent set(float rotation){
		return set(rotation, 0.0f);
	}
	
	public RotationComponent set(float rotation, float angularVel) {
		this.rotation = rotation;
		this.angularVel = angularVel;
		return this;
	}
	
	@Override
	public void reset() {
		rotation = 0.0f;
		angularVel = 0.0f;
		automatic = false;
	}
}