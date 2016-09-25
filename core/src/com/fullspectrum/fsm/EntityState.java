package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.fsm.transition.Transition;

public class EntityState extends StateObject{

	// Data
	private Array<Component> components;
	
	// Animations
	protected AnimationStateMachine animations;
	protected State initialAnim;
	
	protected EntityState(){
		components = new Array<Component>();
		animations = new AnimationStateMachine(entity, new StateObjectCreator());
		animations.setDebugName("Animation State Machine");
	}
	
	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
		animations.entity = entity;
	}
	
	public EntityState addAnimation(State anim){
		if(initialAnim == null) initialAnim = anim;
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
		initialAnim = anim;
		return this;
	}
	
	public void reset(){
		animations.currentState = animations.states.get(initialAnim);
		animations.time = 0;
	}
	
	public State getCurrentAnimation(){
		return animations.states.getKey(animations.currentState, false);
	}
	
	public float getAnimationTime(){
		return animations.getAnimationTime();
	}
	
	public void addAnimationTime(float time){
		animations.time += time;
	}
	
	public EntityState add(Component c){
		components.add(c);
		return this;
	}
	
	public Array<Component> getComponents(){
		return components;
	}
	
}
