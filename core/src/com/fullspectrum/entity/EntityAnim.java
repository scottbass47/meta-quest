package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum EntityAnim implements State{

	IDLE,
	RANDOM_IDLE,
	RUNNING,
	JUMP, // Jump refers to the initial few frames of jumping
	RISE, // Rise refers to the still frame once in the air 
	FALLING,
	JUMP_APEX,
	CLIMBING,
	SWING,
	WALL_SLIDING,
	DROP_IDLE,
	DROP_DISAPPEAR;

	@Override
	public int numStates() {
		return values().length;
	}

	@Override
	public int getIndex() {
		return ordinal();
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
