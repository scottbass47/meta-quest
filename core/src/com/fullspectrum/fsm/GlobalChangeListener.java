package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public interface GlobalChangeListener {

	public void onChange(Entity entity, State oldState, State newState);	
	
}
