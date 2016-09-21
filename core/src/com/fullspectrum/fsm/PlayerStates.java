package com.fullspectrum.fsm;


public enum PlayerStates implements StateIdentifier{
	IDLING,
	RANDOM_IDLING,
	RUNNING,
	JUMPING,
	RISING,
	FALLING;

	@Override
	public int getIndex() {
		return ordinal();
	}

	@Override
	public int numStates() {
		return values().length;
	}
	
	@Override
	public String toString(){
		return name();
	}

}
