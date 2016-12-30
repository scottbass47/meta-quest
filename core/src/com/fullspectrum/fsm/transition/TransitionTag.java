package com.fullspectrum.fsm.transition;

public enum TransitionTag implements Tag{
	ALL,
	GROUND_STATE,
	AIR_STATE,
	STATIC_STATE;

	@Override
	public int getIndex() {
		return ordinal();
	}
}
