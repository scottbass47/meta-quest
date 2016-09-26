package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public class StateObjectCreator implements StateCreator<StateObject>{

	@Override
	public StateObject getInstance(Entity entity, StateMachine<? extends State, ? extends StateObject> machine) {
		return new StateObject(entity, machine);
	}


}
