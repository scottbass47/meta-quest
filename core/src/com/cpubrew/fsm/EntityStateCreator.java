package com.cpubrew.fsm;

import com.badlogic.ashley.core.Entity;

public class EntityStateCreator implements StateCreator<EntityState>{

	@Override
	public EntityState getInstance(Entity entity, StateMachine<? extends State, ? extends StateObject> machine) {
		return new EntityState(entity, machine);
	}
}
