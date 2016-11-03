package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;

public class EngineComponent implements Component{

	public PooledEngine engine;

	public EngineComponent set(PooledEngine engine){
		this.engine = engine;
		return this;
	}
	
}
