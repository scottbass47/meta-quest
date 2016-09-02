package com.fullspectrum.fsm;

import com.fullspectrum.fsm.transition.ITag;

public enum PlayerStates implements IStateIdentifier, ITag{
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
