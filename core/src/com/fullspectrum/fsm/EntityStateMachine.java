package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;

public class EntityStateMachine extends StateMachine<State, EntityState> {

	// State
	private Entity entity;

	public EntityStateMachine(Entity entity) {
		super(entity, new EntityStateCreator());
		this.entity = entity;
		this.states = new ArrayMap<State, EntityState>();
	}

	@Override
	public void changeState(State identifier) {
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
		currentState = newState;
	}
	
	public float getAnimationTime(){
		return currentState.getAnimationTime();
	}
	
	public void addAnimationTime(float time){
		currentState.addAnimationTime(time);
	}
	
	public State getAnimation(){
		return currentState.getCurrentAnimation();
	}
	
	@Override
	public EntityState getCurrentState() {
		return currentState;
	}
}