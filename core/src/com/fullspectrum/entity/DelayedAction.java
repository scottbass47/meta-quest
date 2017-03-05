package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;

public abstract class DelayedAction {

	private Entity entity;
	
	public DelayedAction(Entity entity){
		this.entity = entity;
	}
	
	public abstract void onAction();
	
	public Entity getEntity() {
		return entity;
	}
}
