package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component{

	public IAnimState state;
	public float time = 0;
	
	public StateComponent(IAnimState state){
		this.state = state;
	}
	
}
