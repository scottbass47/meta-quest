package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class KnockBackComponent implements Component, Poolable {

	public float distance;
	public float speed;
	public float angle;
	public boolean knockedUp = false;
	
	public KnockBackComponent set(float distance, float speed, float angle){
		this.distance = distance;
		this.speed = speed;
		this.angle = angle;
		return this;
	}
	
	@Override
	public void reset() {
		distance = 0.0f;
		speed = 0.0f;
		angle = 0.0f;
		knockedUp = false;
	}
}