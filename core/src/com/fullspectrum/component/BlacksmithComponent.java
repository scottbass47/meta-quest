package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BlacksmithComponent implements Component, Poolable{

	public float shield = 0.0f;
	public float shieldMax = 0.0f;
	
    public BlacksmithComponent set(float shieldMax) {
    	this.shieldMax = shieldMax;
    	return this;
    }
	
	@Override
	public void reset() {
		shield = 0.0f;
		shieldMax = 0.0f;
	}

}
