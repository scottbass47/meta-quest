package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityType;

public class EntityComponent implements Component, Poolable{

	public EntityType type;
	private int id;
	
	public EntityComponent set(EntityType type, int id){
		this.type = type;
		this.id = id;
		return this;
	}
	
	@Override
	public void reset() {
		type = null;
		id = 0;
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
