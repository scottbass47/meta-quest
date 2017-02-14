package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionData;
import com.fullspectrum.fsm.transition.TransitionTag;

public class EntityState extends StateObject{

	// Animations
	protected AnimationStateMachine animations;
	protected State initialAnim;
	
	protected EntityState(Entity entity, StateMachine<? extends State, ? extends StateObject> machine){
		super(entity, machine);
		animations = new AnimationStateMachine(entity, new StateObjectCreator());
		addSubstateMachine(animations);
		animations.setDebugName("Animation State Machine");
		animations.entity = entity;
	}
	
	public EntityState addAnimations(State... anims){
		for(State anim : anims) addAnimation(anim);
		return this;
	}
	
	// CLEANUP Hacky fix to make adding knight animations easier.
	public EntityState addAnimations(Array<EntityAnim> anims){
		for(State anim : anims) addAnimation(anim);
		return this;
	}
	
	public EntityState addAnimation(State anim){
		animations.createState(anim);
		return this;
	}
	
	public EntityState addAnimTransition(State fromState, Transition transition, TransitionData data, State toState){
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
	
	public State getCurrentAnimation(){
		return animations.states.getKey(animations.currentState, false);
	}
	
	public float getAnimationTime(){
		return animations.getAnimationTime();
	}
	
	public void addAnimationTime(float time){
		animations.time += time;
	}
	
	public AnimationStateMachine getASM(){
		return animations;
	}
	
	/**
	 * Convenient override to allow method chaining.
	 */
	@Override
	public EntityState add(Component c) {
		super.add(c);
		return this;
	}
	
	/**
	 * Convenient override to allow method chaining.
	 */
	@Override
	public EntityState addTag(TransitionTag tag) {
		super.addTag(tag);
		return this;
	}
	
	/**
	 * Convenient override to allow method chaining.
	 */
	@Override
	public EntityState addChangeListener(StateChangeListener listener) {
		super.addChangeListener(listener);
		return this;
	}
}
