package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum EntityStates implements State{

	IDLING,
	RUNNING,
	JUMPING,
	FALLING,
	DIVING,
	LANDING,
	IDLE_TO_SWING,
	SWING_ATTACK,
	SWING_ANTICIPATION,
	PROJECTILE_ATTACK,
	WALL_SLIDING,
	WALL_JUMP,
	DASH,
	DYING,
	CLIMBING,
	KNOCK_BACK,
	BASE_ATTACK,
	CLEAN_UP,
	PARRY_BLOCK,
	PARRY_SWING,
	FLYING,
	KICK,
	OVERHEAD_SWING,
	SLAM,
	SPIN_SLICE,
	TORNADO,
	PROJECTILE_INIT,
	PROJECTILE_FLY,
	PROJECTILE_DEATH,
	HOMING_KNIVES,
	EXECUTE;
	
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
