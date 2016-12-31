package com.fullspectrum.fsm;

import com.fullspectrum.utils.StringUtils;

public enum AIState implements State{
	
	WANDERING,
	FOLLOWING,
	ATTACKING;

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
		return StringUtils.toTitleCase(name());
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
