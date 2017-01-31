package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FacingComponent implements Component, Poolable {

	public boolean facingRight = true;

	public FacingComponent set(boolean facingRight){
		this.facingRight = facingRight;
		return this;
	}
	
	@Override
	public void reset() {
		facingRight = true;
	}
	
}
