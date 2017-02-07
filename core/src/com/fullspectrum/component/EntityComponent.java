package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EntityComponent implements Component, Poolable{

	public String name;
	
	public EntityComponent set(String name){
		this.name = name;
		return this;
	}
	
	@Override
	public void reset() {
		name = null;
	}
}
