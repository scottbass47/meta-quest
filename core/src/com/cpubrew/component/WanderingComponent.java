package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class WanderingComponent implements Component, Poolable{

	public int radius;
	public float idleTime;
	public boolean wandering = true;
	public float timeElapsed = 0.0f;
	
	@Override
	public void reset() {
		radius = 0;
		idleTime = 0.0f;
		wandering = true;
		timeElapsed = 0.0f;
	}
	
	public WanderingComponent set(int radius, float idleTime){
		this.radius = radius;
		this.idleTime = idleTime;
		return this;
	}
	
}
