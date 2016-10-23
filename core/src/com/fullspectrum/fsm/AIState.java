package com.fullspectrum.fsm;


public enum AIState implements State{
	
	WANDERING;

	@Override
	public int getIndex() {
		return ordinal();
	}

	@Override
	public int numStates() {
		return values().length;
	}

	@Override
	public String getName() {
		return name();
	}
}
