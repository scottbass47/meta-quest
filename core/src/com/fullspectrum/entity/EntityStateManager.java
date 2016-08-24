package com.fullspectrum.entity;

import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.input.GameInput;

public class EntityStateManager {

	private EntityState currentState;
	private Entity e;

	public EntityState getCurrentState() {
		return currentState;
	}

	protected void handleInput(GameInput input){
		if(currentState != null){ 
			EntityState newState = currentState.handleInput(input);
			if(newState != null){
				setCurrentState(newState);
			}
		}
	}
	
	protected void setEntity(Entity e){
		this.e = e;
	}
	
	protected void handlePhysics(PhysicsComponent physics){
		if(currentState != null) currentState.handlePhysics(physics);
	}
	
	public void setCurrentState(EntityState newState) {
		if(newState == null) return;
		if(this.currentState != null) this.currentState.exit();
		this.currentState = newState;
		newState.init(e);
	}
}