package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TintComponent implements Component, Poolable{

	public Color tint;
	
	public TintComponent set(Color tint){
		this.tint = tint;
		return this;
	}
	
	@Override
	public void reset() {
		tint = null;
	}
	
}
