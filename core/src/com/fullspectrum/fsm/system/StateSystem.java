package com.fullspectrum.fsm.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

public abstract class StateSystem extends EntitySystem{

	protected Array<Entity> entities;
	
	protected StateSystem() { 
		entities = new Array<Entity>(); 
	}
	
	public void addEntity(Entity entity){
		entities.add(entity);
	}
	
	public void removeEntity(Entity entity){
		entities.removeIndex(entities.indexOf(entity, false));
	}
}
