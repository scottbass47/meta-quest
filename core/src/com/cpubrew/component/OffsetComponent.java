package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class OffsetComponent implements Component, Poolable{

	public float xOff = 0.0f;
	public float yOff = 0.0f;
	public boolean canFlip = true;
	
	public OffsetComponent set(float xOff, float yOff, boolean canFlip){
		this.xOff = xOff;
		this.yOff = yOff;
		this.canFlip = canFlip;
		return this;
	}
	
	@Override
	public void reset() {
		xOff = 0.0f;
		yOff = 0.0f;
		canFlip = true;
	}
	
}
