package com.cpubrew.fsm;

import com.badlogic.ashley.core.Entity;

public interface StateChangeListener {

	public void onEnter(State prevState, Entity entity);
	public void onExit(State nextState, Entity entity);
	
}
