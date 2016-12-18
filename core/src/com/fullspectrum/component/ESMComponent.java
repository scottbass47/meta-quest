package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.EntityStateMachine;

public class ESMComponent implements Component, Poolable {

	public EntityStateMachine esm;
	
	public ESMComponent set(EntityStateMachine esm){
		this.esm = esm;
		return this;
	}
	
	@Override
	public void reset() {
		if(esm == null) return;
		esm.reset();
		esm = null;
	}

}
