package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

public class EngineComponent implements Component{

	public Engine engine;

	public EngineComponent set(Engine engine){
		this.engine = engine;
		return this;
	}
	
}
