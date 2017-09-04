package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LadderComponent implements Component, Poolable{

	public float speedX;
	public float speedY;
	
	public LadderComponent set(float speedX, float speedY){
		this.speedX = speedX;
		this.speedY = speedY;
		return this;
	}
	
	@Override
	public void reset() {
		speedX = 0;
		speedY = 0;
	}
}
