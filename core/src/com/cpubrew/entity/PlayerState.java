package com.cpubrew.entity;

import com.cpubrew.fsm.State;
import com.cpubrew.utils.StringUtils;

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
