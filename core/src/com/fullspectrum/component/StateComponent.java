package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.fsm.State;

public class StateComponent implements Component, Poolable{

	public State state;
	public float time = 0;
	
	@Override
	public void reset() {
		state = null;
		time = 0;
	}
	
}
