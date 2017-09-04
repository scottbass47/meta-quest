package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class NoMovementComponent implements Component, Poolable{

	public boolean ignoreGravity = false;
	
	public NoMovementComponent set(boolean ignoreGravity) {
		this.ignoreGravity = ignoreGravity;
		return this;
	}
	
	@Override
	public void reset() {
		ignoreGravity = false;
	}
}
