package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityType;

public class EntityComponent implements Component, Poolable{

	public EntityType type;
	
	public EntityComponent set(EntityType type){
		this.type = type;
		return this;
	}
	
	@Override
	public void reset() {
		type = null;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
