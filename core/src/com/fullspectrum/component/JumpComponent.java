package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class JumpComponent implements Component, Poolable{

	public float maxForce;
	public float multiplier = 1.0f;
	
	@Override
	public void reset() {
		maxForce = 0.0f;
		multiplier = 1.0f;
	}
	
	public JumpComponent set(float maxForce){
		this.maxForce = maxForce;
		return this;
	}
	
}
