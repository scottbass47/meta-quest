package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.EntityStateMachine;

public class FSMComponent implements Component, Poolable{

	public EntityStateMachine fsm;
	
	@Override
	public void reset() {
		fsm.reset();
		fsm = null;
	}
	
	public FSMComponent set(EntityStateMachine fsm){
		this.fsm = fsm;
		return this;
	}
}
