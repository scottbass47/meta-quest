package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.physics.EntityFixtures;

public class EntityState extends StateObject{

	// Animations
	protected AnimationStateMachine animations;
	protected State initialAnim;
	
	// Physics
	protected EntityFixtures fixtures;
	
	protected EntityState(Entity entity, StateMachine<? extends State, ? extends StateObject> machine){
		super(entity, machine);
		animations = new AnimationStateMachine(entity, new StateObjectCreator());
		machine.addSubstateMachine(this, animations);
		animations.setDebugName("Animation State Machine");
		animations.entity = entity;
	}
	
	public EntityState addAnimation(State anim){
		animations.createState(anim);
		animations.reset();
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
	
	public EntityFixtures getFixtures(){
		return fixtures;
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
	
	/**
	 * Convenient override to allow method chaining.
	 */
	public EntityState add(Component c) {
		super.add(c);
		return this;
	}
}
