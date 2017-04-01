package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

// INCOMPLETE Allow an option to test out transitions when resolving
public interface StateChangeResolver {

	public State resolve(Entity entity, State oldState);
	
}
