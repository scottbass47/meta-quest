package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum EntityAnim implements State{

	IDLE,
	RANDOM_IDLE,
	RUNNING,
	BACK_PEDALING,
	JUMP, // Jump refers to the initial few frames of jumping
	RISE, // Rise refers to the still frame once in the air 
	FALLING,
	FLYING,
	JUMP_APEX,
	CLIMBING,
	SWING,
	ATTACK,
	LAND,
	DYING,
	WALL_SLIDING,
	DROP_IDLE,
	DROP_DISAPPEAR,
	
	// Knight stuff (so ugly!!)
	SWING_IDLE_ANTIPATION_1,
	SWING_IDLE_ANTIPATION_2,
	SWING_IDLE_ANTIPATION_3,
	SWING_IDLE_ANTIPATION_4,
	SWING_ANTICIPATION_1,
	SWING_ANTICIPATION_2,
	SWING_ANTICIPATION_3,
	SWING_ANTICIPATION_4,
	SWING_1,
	SWING_2,
	SWING_3,
	SWING_4,
	
	// Rogue stuff
	THROWING,
	ARMS_RUNNING,
	ARMS_BACK_PEDALING;
	
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
