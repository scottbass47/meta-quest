package com.fullspectrum.fsm.transition;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

public class TransitionSystem extends EntitySystem{

	protected Array<Entity> entities;
	
	public TransitionSystem() { 
		entities = new Array<Entity>(); 
	}
	
	public void addEntity(Entity e){
		entities.add(e);
	}
	
	public void removeEntity(Entity e){
		entities.removeIndex(entities.indexOf(e, true));
	}
	
}
