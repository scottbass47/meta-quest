package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Entity;

public interface StateChangeListener {

	public void onEnter(Entity entity);
	public void onExit(Entity entity);
	
}
