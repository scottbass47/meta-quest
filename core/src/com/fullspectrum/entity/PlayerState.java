package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.utils.StringUtils;

public enum PlayerState implements State{

	KNIGHT,
	MAGE,
	ROGUE;

	@Override
	public int getIndex() {
		return ordinal();
	}

	@Override
	public int numStates() {
		return values().length;
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
