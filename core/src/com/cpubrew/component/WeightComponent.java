package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class WeightComponent implements Component, Poolable{

	public float weight;
	
	public WeightComponent set(float weight) {
		this.weight = weight;
		return this;
	}
	
	@Override
	public void reset() {
		weight = 0.0f;
	}

}
