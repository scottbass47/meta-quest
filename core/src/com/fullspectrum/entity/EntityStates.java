package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;

public enum EntityStates implements State{

	IDLING,
	RUNNING,
	JUMPING,
	FALLING,
	DIVING,
	ATTACK,
	DYING,
	CLEAN_UP;
	
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

	@Override
	public String getName() {
		return name();
	}
	
}
