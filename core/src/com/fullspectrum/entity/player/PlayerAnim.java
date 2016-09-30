package com.fullspectrum.entity.player;

import com.fullspectrum.fsm.State;

public enum PlayerAnim implements State{
	IDLE,
	RANDOM_IDLE,
	RUNNING,
	JUMP, // Jump refers to the initial few frames of jumping
	RISE, // Rise refers to the still frame once in the air 
	FALLING,
	JUMP_APEX;

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
		return name();
	}
}
