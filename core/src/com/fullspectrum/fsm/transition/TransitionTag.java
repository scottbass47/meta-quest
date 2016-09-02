package com.fullspectrum.fsm.transition;

public enum TransitionTag implements ITag{
	GROUND_STATE,
	AIR_STATE;

	@Override
	public int getIndex() {
		return ordinal();
	}
}
