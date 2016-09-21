package com.fullspectrum.entity.player;

import com.fullspectrum.fsm.AnimState;

public enum PlayerAnim implements AnimState{
	IDLE,
	RANDOM_IDLE,
	RUNNING,
	JUMP, // Jump refers to the initial few frames of jumping
	RISE, // Rise refers to the still frame once in the air 
	FALLING;

	@Override
	public int numStates() {
		return values().length;
	}
}
