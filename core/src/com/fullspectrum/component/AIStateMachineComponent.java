package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.AIStateMachine;

public class AIStateMachineComponent implements Component, Poolable{

	public AIStateMachine aism;
	
	@Override
	public void reset() {
		aism.reset();
		aism = null;
	}
	
	public AIStateMachineComponent set(AIStateMachine aism){
		this.aism = aism;
		return this;
	}
}
