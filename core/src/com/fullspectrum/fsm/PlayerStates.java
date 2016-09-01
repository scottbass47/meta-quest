package com.fullspectrum.fsm;

public enum PlayerStates implements IStateIdentifier{
	IDLING,
	RANDOM_IDLING,
	RUNNING,
	JUMPING,
	RISING,
	FALLING
}
