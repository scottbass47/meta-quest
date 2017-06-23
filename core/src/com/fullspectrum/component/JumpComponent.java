package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class JumpComponent implements Component, Poolable{

	public float maxForce;
	public float floatAmount;
	public float multiplier = 1.0f;
	public float timeDown = 0.5f;
	public boolean jumpReady = true;
	
	@Override
	public void reset() {
		floatAmount = 0.0f;
		maxForce = 0.0f;
		multiplier = 1.0f;
		timeDown = 0.5f;
		jumpReady = true;
	}
	
	public JumpComponent set(float maxForce, float floatAmount){
		this.maxForce = maxForce;
		this.floatAmount = floatAmount;
		return this;
	}
	
}
