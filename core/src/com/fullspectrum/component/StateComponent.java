package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.fullspectrum.fsm.AnimState;

public class StateComponent implements Component{

	public AnimState state;
	public float time = 0;
	
	public StateComponent(AnimState state){
		this.state = state;
	}
	
}
