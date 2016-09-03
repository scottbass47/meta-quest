package com.fullspectrum.fsm;

import com.fullspectrum.fsm.transition.Tag;

public enum PlayerStates implements StateIdentifier, Tag{
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
