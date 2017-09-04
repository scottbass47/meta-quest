package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.fsm.State;

public class StateComponent implements Component, Poolable{

	public State state;
	public float time = 0;
	
	public StateComponent set(State state){
		this.state = state;
		return this;
	}
	
	@Override
	public void reset() {
		state = null;
		time = 0;
	}
	
}
