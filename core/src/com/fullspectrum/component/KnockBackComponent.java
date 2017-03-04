package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class KnockBackComponent implements Component, Poolable {

	public float distance;
	public float angle;
	
	public KnockBackComponent set(float distance, float angle){
		this.distance = distance;
		this.angle = angle;
		return this;
	}
	
	@Override
	public void reset() {
		distance = 0.0f;
		angle = 0.0f;
	}
}