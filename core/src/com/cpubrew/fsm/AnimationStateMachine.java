package com.cpubrew.fsm;

import com.badlogic.ashley.core.Entity;

public class AnimationStateMachine extends StateMachine<State, StateObject>{
	
	// Time
	private float time = 0.0f;

	public AnimationStateMachine(Entity entity, StateCreator<StateObject> creator) {
		super(entity, creator, State.class, StateObject.class);
	}
	
	@Override
	public void changeState(State identifier) {
		if(identifier == null) return;
		super.changeState(identifier);
		time = 0;
	}
	
	public float getAnimationTime(){
		return time;
	}
	
	public void setTime(float time){
		this.time = time;
	}

	public void addTime(float time){
		this.time += time;
	}
	
	public State getCurrentAnimation(){
		return getCurrentState();
	}
	
}
