package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ForceComponent implements Component, Poolable{

	public float fx = 0.0f;
	public float fy = 0.0f;
	
	public ForceComponent set(float fx, float fy){
		this.fx = fx;
		this.fy = fy;
		return this;
	}
	
	@Override
	public void reset() {
		fx = 0.0f;
		fy = 0.0f;
	}
}
