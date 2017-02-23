package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderComponent implements Component, Poolable{

	public float rotation = 0.0f;
	
	@Override
	public void reset() {
		rotation = 0.0f;
	}

}
