package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public class AnimationStateMachine extends StateMachine<State, StateObject>{
	
	// Time
	protected float time = 0.0f;

	public AnimationStateMachine(Entity entity, StateCreator<StateObject> creator) {
		super(entity, creator, State.class, StateObject.class);
	}
	
	@Override
	public void changeState(State identifier) {
		super.changeState(identifier);
		time = 0;
	}
	
	public float getAnimationTime(){
		return time;
	}

}
