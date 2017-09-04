package com.cpubrew.fsm;

import com.badlogic.ashley.core.Entity;

public interface StateCreator<T extends StateObject> {

	public T getInstance(Entity entity, StateMachine<? extends State, ? extends StateObject> machine);
	
}