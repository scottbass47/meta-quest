package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
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
	
	public PositionComponent set(Vector2 vec) {
		this.x = vec.x;
		this.y = vec.y;
		return this;
	}
	
}
