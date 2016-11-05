package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EngineComponent implements Component, Poolable{

	public Engine engine;

	public EngineComponent set(Engine engine){
		this.engine = engine;
		return this;
	}

	@Override
	public void reset() {
		engine = null;
	}
	
}
