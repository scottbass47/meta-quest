package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PositionComponent implements Component, Poolable{

	public float x = 0.0f;
	public float y = 0.0f;
	
	@Override
	public void reset() {
		x = 0.0f;
		y = 0.0f;
	}
	
	public PositionComponent set(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}
	
}
