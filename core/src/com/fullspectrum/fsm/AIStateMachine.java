package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public class AIStateMachine extends StateMachine<AIState, StateObject>{

	public AIStateMachine(Entity entity){
		super(entity, new AIStateCreator(), AIState.class, StateObject.class);
	}
	
}
