package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.fsm.transition.Transition;

public class EntityState extends StateObject{

	// Data
	private Array<Component> components;
	
	// Animations
	private StateMachine<State, StateObject> animations;
	
	protected EntityState(){
		components = new Array<Component>();
		animations = new StateMachine<State, StateObject>(entity, new StateObjectCreator());
	}
	
	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
		animations.entity = entity;
	}
	
	public EntityState addAnimation(State anim){
		animations.createState(anim);
		return this;
	}
	
	public EntityState addAnimTransition(State fromState, Transition transition, Object data, State toState){
		animations.addTransition(fromState, transition, data, toState);
		return this;
	}
	
	public EntityState addAnimTransition(State fromState, Transition transition, State toState){
		animations.addTransition(fromState, transition, null, toState);
		return this;
	}
	
	public EntityState setInitialAnimation(State anim){
		animations.changeState(anim);
		return this;
	}
	
	public State getCurrentAnimation(){
		return animations.states.getKey(animations.currentState, false);
	}
	
	public EntityState add(Component c){
		components.add(c);
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
	}
	
}
