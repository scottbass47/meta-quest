package com.fullspectrum.entity;

import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.input.GameInput;

public abstract class EntityState {

	protected EntityStateManager manager;
	protected Entity entity;
	
	public EntityState(EntityStateManager manager){
		this.manager = manager;
	}
	
	public abstract void init(Entity e);
	public abstract EntityState handleInput(GameInput input);
	public abstract void handlePhysics(PhysicsComponent physics);
	public abstract void exit();
}
