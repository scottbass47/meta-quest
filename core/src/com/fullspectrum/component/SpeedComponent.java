package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SpeedComponent implements Component, Poolable {

	public float maxSpeed;
	public float multiplier = 1.0f;
	
	@Override
	public void reset() {
		maxSpeed = 0.0f;
		multiplier = 1.0f;
	}
	
	public SpeedComponent set(float maxSpeed){
		this.maxSpeed = maxSpeed;
		return this;
	}
	
}
