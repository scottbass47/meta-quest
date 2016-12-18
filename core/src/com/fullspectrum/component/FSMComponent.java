package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class FSMComponent implements Component, Poolable{

	public StateMachine<? extends State, ? extends StateObject> fsm;
	
	@Override
	public void reset() {
		if(fsm == null) return;
		fsm.reset();
		fsm = null;
	}
	
	public FSMComponent set(StateMachine<? extends State, ? extends StateObject> fsm){
		this.fsm = fsm;
		return this;
	}
}
