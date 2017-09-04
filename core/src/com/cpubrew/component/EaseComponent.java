package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EaseComponent implements Component, Poolable{

	public float accel;
	public float prevX;
	public float prevY;
	
	public EaseComponent set(float accel, float prevX, float prevY){
		this.accel = accel;
		this.prevX = prevX;
		this.prevY = prevY;
		return this;
	}

	@Override
	public void reset() {
		accel = 0.0f;
		prevX = 0.0f;
		prevY = 0.0f;
	}
}
