package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.State;

public class StateComponent implements Component{

	public State state;
	public float time = 0;
	
	public StateComponent(State state){
		this.state = state;
	}
	
}
