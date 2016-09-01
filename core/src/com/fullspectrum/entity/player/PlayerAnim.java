package com.fullspectrum.entity.player;

import com.fullspectrum.component.IAnimState;

public enum PlayerAnim implements IAnimState{
	IDLE,
	RANDOM_IDLE,
	RUNNING,
	JUMP, // Jump refers to the initial few frames of jumping
	RISE, // Rise refers to the still frame once in the air 
	FALLING
}
