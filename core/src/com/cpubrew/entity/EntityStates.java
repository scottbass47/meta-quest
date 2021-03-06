package com.cpubrew.entity;

import com.cpubrew.fsm.State;
import com.cpubrew.utils.StringUtils;

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
	EXECUTE,
	BOW_ATTACK,
	WIND_BURST,
	INSTA_WALL,
	ROLL,
	DOUBLE_JUMP,
	CHARGE,
	INIT,
	ATTACK_ANTICIPATION,
	ATTACK_COOLDOWN,
	RECOVER,
	TUNNEL,
	TRIP;
	
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
