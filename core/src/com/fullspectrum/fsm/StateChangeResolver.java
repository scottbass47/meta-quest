package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public interface StateChangeResolver {

	public State resolve(Entity entity, State oldState);
	
}
