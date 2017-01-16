package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TypeComponent implements Component, Poolable{

	public EntityType type = EntityType.NEUTRAL;
	
	public TypeComponent set(EntityType type){
		this.type = type;
		return this;
	}
	
	@Override
	public void reset() {
		type = EntityType.NEUTRAL;
	}
	
	public enum EntityType{
		FRIENDLY,
		ENEMY,
		NEUTRAL
	}

}
