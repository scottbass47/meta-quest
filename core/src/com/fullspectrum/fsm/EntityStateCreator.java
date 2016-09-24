package com.fullspectrum.fsm;

public class EntityStateCreator implements StateCreator<EntityState>{
	@Override
	public EntityState getInstance() {
		return new EntityState();
	}
}
