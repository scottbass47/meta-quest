package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.entity.EntityStates;

public class EntityStateMachine extends StateMachine<EntityStates, EntityState> {

	public EntityStateMachine(Entity entity/*, String bodyPath*/) {
		super(entity, new EntityStateCreator(), EntityStates.class, EntityState.class);
		this.states = new ArrayMap<EntityStates, EntityState>();
	}
	
	@Override
	public EntityState createState(EntityStates key) {
		EntityState state = super.createState(key);
		return state;
	}
	
	@Override
	public void changeState(State identifier) {
		if(!(identifier instanceof EntityStates)) throw new IllegalArgumentException("Invalid input. Must be of type EntityStates.");
		EntityState currState = currentState;
		EntityStates state = (EntityStates)identifier;
		EntityState newState = states.get(state);
		super.changeState(identifier);
		if (newState == currState) return;
	}
	
	@Override
	public void reset() {
		super.reset();
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