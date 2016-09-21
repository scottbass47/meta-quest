package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;

public class EntityStateMachine extends StateMachine<StateIdentifier, EntityState> {

	// State
	private EntityState currentState;
	private Entity entity;

	// Animation
	public float animationTime;

	public EntityStateMachine(Entity entity) {
		super(entity, EntityState.class);
		this.entity = entity;
		this.states = new ArrayMap<StateIdentifier, EntityState>();
	}

	@Override
	public void changeState(StateIdentifier identifier) {
		super.changeState(identifier);
		EntityState newState = states.get(identifier);
		if (newState == currentState) return;
		if (currentState != null) {
			for (Component c : currentState.getComponents()) {
				entity.remove(c.getClass());
			}
		}
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		animationTime = 0;
		currentState = newState;
	}
	
	public AnimState getAnimation(){
		return currentState.getCurrentAnimation();
	}
	
	@Override
	public EntityState getCurrentState() {
		return currentState;
	}
}