package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum EntityStates implements State{

	IDLING,
	RUNNING,
	JUMPING,
	FALLING,
	DIVING,
	SWING_ATTACK,
	BASE_ATTACK,
	WALL_SLIDING,
	WALL_JUMP,
	DASH,
	DYING,
	CLIMBING,
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
		return getName();
	}

	@Override
	public String getName() {
		return StringUtils.toTitleCase(name());
	}
	
}
