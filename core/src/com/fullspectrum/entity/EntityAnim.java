package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum EntityAnim implements State{

	IDLE,
	RANDOM_IDLE,
	RUN,
	BACK_PEDAL,
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
	PARRY_BLOCK,
	PARRY_SWING,
	FLAPPING,
	KICK,
	OVERHEAD_SWING,
	SLAM,
	DASH,
	SPIN_SLICE,
	TORNADO_INIT,
	TORNADO_SWING,
	PROJECTILE_INIT,
	PROJECTILE_FLY,
	PROJECTILE_DEATH,
	
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
	
	// Rogue Upper Body Defaults
	RUN_ARMS,
	BACK_PEDAL_ARMS,
	JUMP_ARMS,
	RISE_ARMS,
	APEX_ARMS,
	FALL_ARMS,
	
	// Rogue Upper Body Abilities
	RUN_THROW,
	BACK_PEDAL_THROW,
	AIR_THROW,
	SLINGHOT_ARMS,
	FLASH_POWDER_THROW,
	BOOMERANG_THROW,
	
	// Special Init State for resolutions
	INIT;
	
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
