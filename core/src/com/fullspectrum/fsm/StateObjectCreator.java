package com.fullspectrum.fsm;

public class StateObjectCreator implements StateCreator<StateObject>{

	@Override
	public StateObject getInstance() {
		return new StateObject();
	}

}
