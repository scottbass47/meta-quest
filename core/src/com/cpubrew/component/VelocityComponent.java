package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class VelocityComponent implements Component, Poolable{

	public float dx = 0.0f;
	public float dy = 0.0f;

	public VelocityComponent set(float dx, float dy){
		this.dx = dx;
		this.dy = dy;
		return this;
	}
	
	public VelocityComponent set(Vector2 vec){
		this.dx = vec.x;
		this.dy = vec.y;
		return this;
	}	
	
	@Override
	public void reset() {
		dx = 0.0f;
		dy = 0.0f;
	}
	
}
