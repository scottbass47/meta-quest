package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FacingComponent implements Component, Poolable {

	public boolean facingRight = true;
	public boolean locked = false;

	public FacingComponent set(boolean facingRight){
		this.facingRight = facingRight;
		return this;
	}
	
	@Override
	public void reset() {
		facingRight = true;
		locked = false;
	}
	
}
